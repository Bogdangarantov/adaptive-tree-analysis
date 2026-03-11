<template>
  <div class="benchmark-root">
    <GeneratedNavbar />

    <main class="benchmark-page">
    <section class="glass-card benchmark-header">
      <h1>Benchmark Lab</h1>
      <p>
        Порівняння AVL, Red-Black та Extensible дерев на великих згенерованих датасетах без візуалізації.
      </p>
    </section>

    <section class="glass-card benchmark-layout">
      <div class="benchmark-form">
        <h2>Параметри експерименту</h2>

        <form @submit.prevent="runBenchmark">
          <div class="field-group">
            <label for="datasetSize">Dataset size</label>
            <div class="inline-inputs">
              <input
                id="datasetSize"
                v-model.number="datasetSize"
                type="number"
                min="1"
                class="text-input"
              />
              <div class="preset-buttons">
                <button type="button" class="btn secondary" @click="datasetSize = 1000">1k</button>
                <button type="button" class="btn secondary" @click="datasetSize = 10000">10k</button>
                <button type="button" class="btn secondary" @click="datasetSize = 50000">50k</button>
                <button type="button" class="btn secondary" @click="datasetSize = 100000">100k</button>
              </div>
            </div>
          </div>

          <div class="field-group">
            <label for="distributionType">Distribution</label>
            <select id="distributionType" v-model="distributionType" class="text-input">
              <option value="RANDOM">Random</option>
              <option value="SORTED">Sorted</option>
              <option value="REVERSE_SORTED">Reverse sorted</option>
              <option value="ALMOST_SORTED">Almost sorted</option>
              <option value="ZIPF">Zipf</option>
            </select>
          </div>

          <div class="field-group">
            <label>Operation mix (%)</label>
            <div class="inline-inputs">
              <div class="percent-input">
                <span>Insert</span>
                <input v-model.number="insertPercent" type="number" min="0" max="100" class="text-input" />
              </div>
              <div class="percent-input">
                <span>Search</span>
                <input v-model.number="searchPercent" type="number" min="0" max="100" class="text-input" />
              </div>
              <div class="percent-input">
                <span>Delete</span>
                <input v-model.number="deletePercent" type="number" min="0" max="100" class="text-input" />
              </div>
            </div>
            <p class="hint">Сума бажано 100, але бекенд сприймає й 0 як “за замовчуванням”.</p>
          </div>

          <div class="field-group">
            <label for="repeatCount">Repeat count</label>
            <input
              id="repeatCount"
              v-model.number="repeatCount"
              type="number"
              min="1"
              class="text-input"
            />
          </div>

          <div class="actions">
            <button type="submit" class="btn primary" :disabled="loading">
              {{ loading ? 'Running…' : 'Run benchmark' }}
            </button>
            <p v-if="error" class="error">{{ error }}</p>
          </div>

          <div v-if="loading" class="progress-wrapper">
            <div class="progress-label">
              <template v-if="progress">
                {{ progress.done.toLocaleString() }} / {{ progress.total.toLocaleString() }} ops
              </template>
              <template v-else>
                Running benchmark… {{ (elapsedMs / 1000).toFixed(1) }}s
              </template>
            </div>
            <div class="progress-bar">
              <div class="progress-bar-fill" :style="{ width: progressPercent + '%' }"></div>
            </div>
          </div>
        </form>
      </div>

      <div class="benchmark-results" v-if="result">
        <h2>Результати (остання сесія)</h2>
        <p class="hint">
          Experiment ID: <code>{{ result.experiment.id }}</code>
        </p>

        <table class="results-table">
          <thead>
            <tr>
              <th>Tree type</th>
              <th>Execution time (ms)</th>
              <th>Operations</th>
              <th>Rotations</th>
              <th>Tree height</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in result.stats" :key="row.id">
              <td><strong>{{ row.treeType }}</strong></td>
              <td>{{ formatNsToMs(row.executionTimeNs) }}</td>
              <td>{{ row.operationCount.toLocaleString() }}</td>
              <td>{{ row.rotationCount.toLocaleString() }}</td>
              <td>{{ row.treeHeight }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onUnmounted, ref, watch } from 'vue';
import { Client } from '@stomp/stompjs';
import GeneratedNavbar from '../components/generated/GeneratedNavbar.vue';
import type { BenchmarkQuickRunResponse } from '../api/client';
import { runBenchmarkQuick } from '../api/client';

const datasetSize = ref(10000);
const distributionType = ref('RANDOM');
const insertPercent = ref(40);
const searchPercent = ref(50);
const deletePercent = ref(10);
const repeatCount = ref(1);

const loading = ref(false);
const error = ref<string | null>(null);
const result = ref<BenchmarkQuickRunResponse | null>(null);

const elapsedMs = ref(0);
const progress = ref<{ done: number; total: number } | null>(null);
const currentExperimentId = ref<string | null>(null);

const progressPercent = computed(() => {
  if (!progress.value || progress.value.total <= 0) {
    return 0;
  }
  return (progress.value.done / progress.value.total) * 100;
});

let timer: number | null = null;
let stompClient: Client | null = null;
let progressSocketPromise: Promise<void> | null = null;

watch(loading, (isLoading) => {
  if (isLoading) {
    elapsedMs.value = 0;
    if (timer !== null) {
      window.clearInterval(timer);
    }
    timer = window.setInterval(() => {
      elapsedMs.value += 200;
    }, 200);
  } else {
    if (timer !== null) {
      window.clearInterval(timer);
      timer = null;
    }
  }
});

onUnmounted(() => {
  if (timer !== null) {
    window.clearInterval(timer);
  }
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
});

async function runBenchmark() {
  loading.value = true;
  error.value = null;
  progress.value = null;
  currentExperimentId.value = null;

  try {
    await ensureProgressSocket();

    const payload = {
      datasetSize: datasetSize.value,
      distributionType: distributionType.value,
      insertPercent: insertPercent.value,
      searchPercent: searchPercent.value,
      deletePercent: deletePercent.value,
      repeatCount: repeatCount.value
    };

    result.value = await runBenchmarkQuick(payload);
    currentExperimentId.value = result.value.experiment.id;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to run benchmark';
  } finally {
    loading.value = false;
  }
}

function formatNsToMs(ns: number): string {
  return (ns / 1_000_000).toFixed(2);
}

function ensureProgressSocket(): Promise<void> {
  if (stompClient?.connected) {
    return Promise.resolve();
  }

  if (progressSocketPromise) {
    return progressSocketPromise;
  }

  progressSocketPromise = new Promise((resolve) => {
    let isResolved = false;
    const finish = () => {
      if (isResolved) {
        return;
      }
      isResolved = true;
      progressSocketPromise = null;
      resolve();
    };

    const subscriptionHandler = (message: { body: string }) => {
      const body = JSON.parse(message.body) as {
        experimentId: string;
        doneOperations: number;
        totalOperations: number;
      };

      if (!loading.value) {
        return;
      }

      if (currentExperimentId.value && body.experimentId !== currentExperimentId.value) {
        return;
      }

      progress.value = {
        done: body.doneOperations,
        total: body.totalOperations
      };
    };

    if (!stompClient) {
      stompClient = new Client({
        brokerURL: `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws`,
        reconnectDelay: 3000,
        connectionTimeout: 1500
      });
    }

    stompClient.onConnect = () => {
      stompClient!.subscribe(`/topic/benchmark-progress`, subscriptionHandler);
      finish();
    };

    stompClient.onStompError = finish;
    stompClient.onWebSocketError = finish;
    window.setTimeout(finish, 1500);

    if (!stompClient.active) {
      stompClient.activate();
    }
  });

  return progressSocketPromise;
}
</script>

<style scoped>
.benchmark-root {
  position: relative;
}

.benchmark-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 96px 16px 24px; /* 96px зверху, щоб не залазити під fixed navbar */
}

.benchmark-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1.4fr);
  gap: 24px;
}

.benchmark-form h2,
.benchmark-results h2 {
  margin-top: 0;
}

.benchmark-header {
  margin-bottom: 24px;
}

.field-group {
  margin-bottom: 16px;
}

.field-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 4px;
}

.text-input {
  width: 100%;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid #d6e1f0;
  font-size: 14px;
}

.inline-inputs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.preset-buttons {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.btn {
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  border: 1px solid transparent;
}

.btn.primary {
  background: #1e72d8;
  color: #fff;
  border-color: #1e72d8;
}

.btn.primary:disabled {
  opacity: 0.7;
  cursor: default;
}

.btn.secondary {
  background: #f3f6fc;
  color: #1e293b;
  border-color: #d6e1f0;
}

.percent-input {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 80px;
}

.percent-input span {
  font-size: 12px;
  color: #64748b;
}

.actions {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.error {
  color: #b42318;
  font-size: 13px;
}

.hint {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

.benchmark-results .hint {
  margin-bottom: 8px;
}

.progress-wrapper {
  margin-top: 12px;
}

.progress-label {
  font-size: 12px;
  color: #c7d2fe;
  margin-bottom: 4px;
}

.progress-bar {
  position: relative;
  width: 100%;
  height: 6px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.35);
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #38bdf8, #6366f1);
  transition: width 0.2s ease-out;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
}

.results-table th,
.results-table td {
  padding: 8px 10px;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
  font-size: 13px;
  color: #ffffff;
  background: transparent;
}

.results-table th {
  font-weight: 600;
}

code {
  font-size: 12px;
  background: #f3f6fc;
  padding: 2px 4px;
  border-radius: 4px;
}

@media (max-width: 900px) {
  .benchmark-layout {
    grid-template-columns: 1fr;
  }
}
</style>
