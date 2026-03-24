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

        <section class="chart-panel">
          <div class="chart-panel-header">
            <div>
              <h3>Порівняльний графік</h3>
              <p class="hint">Швидко показує, яке дерево виглядає краще по вибраній метриці.</p>
            </div>

            <div class="metric-switcher">
              <button
                v-for="option in metricOptions"
                :key="option.key"
                type="button"
                class="metric-btn"
                :class="{ active: selectedMetric === option.key }"
                @click="selectedMetric = option.key"
              >
                {{ option.label }}
              </button>
            </div>
          </div>

          <div class="chart-summary" v-if="chartLeader">
            <div class="summary-pill">
              <span>Лідер</span>
              <strong>{{ chartLeader.treeType }}</strong>
            </div>
            <div class="summary-pill">
              <span>Метрика</span>
              <strong>{{ activeMetricLabel }}</strong>
            </div>
            <div class="summary-pill">
              <span>Значення</span>
              <strong>{{ formatMetricValue(chartLeader.value, selectedMetric) }}</strong>
            </div>
          </div>

          <svg
            class="comparison-chart"
            viewBox="0 0 760 320"
            role="img"
            :aria-label="`Benchmark comparison chart for ${activeMetricLabel}`"
          >
            <line x1="72" y1="24" x2="72" y2="264" class="chart-axis" />
            <line x1="72" y1="264" x2="724" y2="264" class="chart-axis" />

            <g v-for="tick in chartTicks" :key="`tick-${tick.value}`">
              <line
                x1="72"
                :y1="tick.y"
                x2="724"
                :y2="tick.y"
                class="chart-grid"
              />
              <text x="62" :y="tick.y + 4" class="chart-tick">
                {{ formatMetricValue(tick.value, selectedMetric) }}
              </text>
            </g>

            <g v-for="bar in chartBars" :key="bar.treeType">
              <rect
                :x="bar.x"
                :y="bar.y"
                :width="bar.width"
                :height="bar.height"
                rx="16"
                class="chart-bar"
                :style="{ '--bar-color': bar.color }"
              />
              <text :x="bar.x + bar.width / 2" y="288" class="chart-label">
                {{ bar.treeType }}
              </text>
              <text :x="bar.x + bar.width / 2" :y="Math.max(18, bar.y - 10)" class="chart-value">
                {{ formatMetricValue(bar.value, selectedMetric) }}
              </text>
            </g>
          </svg>
        </section>

        <section class="chart-panel">
          <div class="chart-panel-header">
            <div>
              <h3>Операційний breakdown</h3>
              <p class="hint">Окремі часи вставки, пошуку та видалення для кожного дерева.</p>
            </div>
          </div>

          <div class="breakdown-grid">
            <article
              v-for="operationChart in operationCharts"
              :key="operationChart.key"
              class="breakdown-card"
            >
              <div class="breakdown-head">
                <h4>{{ operationChart.label }}</h4>
                <span>{{ operationChart.leader }}</span>
              </div>

              <svg
                class="operation-chart"
                viewBox="0 0 320 220"
                role="img"
                :aria-label="`${operationChart.label} benchmark comparison`"
              >
                <line x1="42" y1="20" x2="42" y2="176" class="chart-axis" />
                <line x1="42" y1="176" x2="298" y2="176" class="chart-axis" />

                <g v-for="bar in operationChart.bars" :key="`${operationChart.key}-${bar.treeType}`">
                  <rect
                    :x="bar.x"
                    :y="bar.y"
                    :width="bar.width"
                    :height="bar.height"
                    rx="12"
                    class="chart-bar"
                    :style="{ '--bar-color': bar.color }"
                  />
                  <text :x="bar.x + bar.width / 2" y="196" class="chart-label">
                    {{ bar.treeType }}
                  </text>
                  <text :x="bar.x + bar.width / 2" :y="Math.max(16, bar.y - 8)" class="chart-value">
                    {{ formatMetricValue(bar.value, 'executionTimeNs') }}
                  </text>
                </g>
              </svg>
            </article>
          </div>
        </section>

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
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { Client } from '@stomp/stompjs';
import GeneratedNavbar from '../components/generated/GeneratedNavbar.vue';
import type { BenchmarkQuickRunResponse } from '../api/client';
import { runBenchmarkQuick } from '../api/client';

type BenchmarkMetricKey = 'executionTimeNs' | 'rotationCount' | 'treeHeight' | 'avgNodeDepth';
type OperationTimeMetricKey = 'insertTimeNs' | 'searchTimeNs' | 'deleteTimeNs';

const datasetSize = ref(10000);
const distributionType = ref('RANDOM');
const insertPercent = ref(40);
const searchPercent = ref(50);
const deletePercent = ref(10);
const repeatCount = ref(1);

const loading = ref(false);
const error = ref<string | null>(null);
const result = ref<BenchmarkQuickRunResponse | null>(null);
const selectedMetric = ref<BenchmarkMetricKey>('executionTimeNs');

const elapsedMs = ref(0);
const progress = ref<{ done: number; total: number } | null>(null);
const currentExperimentId = ref<string | null>(null);

const progressPercent = computed(() => {
  if (!progress.value || progress.value.total <= 0) {
    return 0;
  }
  return (progress.value.done / progress.value.total) * 100;
});

const metricOptions: Array<{ key: BenchmarkMetricKey; label: string }> = [
  { key: 'executionTimeNs', label: 'Time' },
  { key: 'rotationCount', label: 'Rotations' },
  { key: 'treeHeight', label: 'Height' },
  { key: 'avgNodeDepth', label: 'Avg depth' }
];

const activeMetricLabel = computed(() => {
  return metricOptions.find((option) => option.key === selectedMetric.value)?.label ?? 'Metric';
});

const chartStats = computed(() => result.value?.stats ?? []);

const chartMaxValue = computed(() => {
  const maxValue = Math.max(...chartStats.value.map((row) => metricValue(row, selectedMetric.value)), 0);
  return maxValue > 0 ? maxValue : 1;
});

const chartLeader = computed(() => {
  if (chartStats.value.length === 0) {
    return null;
  }

  return [...chartStats.value]
    .map((row) => ({
      treeType: normalizeTreeType(row.treeType),
      value: metricValue(row, selectedMetric.value)
    }))
    .sort((left, right) => left.value - right.value)[0];
});

const chartTicks = computed(() => {
  const ticks = [];
  for (let index = 0; index < 5; index += 1) {
    const ratio = index / 4;
    ticks.push({
      value: chartMaxValue.value * (1 - ratio),
      y: 24 + 240 * ratio
    });
  }
  return ticks;
});

const chartBars = computed(() => {
  const colors = ['#38bdf8', '#f87171', '#f59e0b'];
  const width = 132;
  const gap = 72;
  const startX = 122;

  return chartStats.value.map((row, index) => {
    const value = metricValue(row, selectedMetric.value);
    const height = Math.max(8, (value / chartMaxValue.value) * 208);
    return {
      treeType: normalizeTreeType(row.treeType),
      value,
      x: startX + index * (width + gap),
      y: 264 - height,
      width,
      height,
      color: colors[index % colors.length]
    };
  });
});

const operationCharts = computed(() => {
  const chartConfigs: Array<{ key: OperationTimeMetricKey; label: string }> = [
    { key: 'insertTimeNs', label: 'Insert time' },
    { key: 'searchTimeNs', label: 'Search time' },
    { key: 'deleteTimeNs', label: 'Delete time' }
  ];
  const colors = ['#38bdf8', '#f87171', '#f59e0b'];

  return chartConfigs.map((config) => {
    const values = chartStats.value.map((row) => operationMetricValue(row, config.key));
    const maxValue = Math.max(...values, 0, 1);
    const ranked = chartStats.value
      .map((row) => ({
        treeType: normalizeTreeType(row.treeType),
        value: operationMetricValue(row, config.key)
      }))
      .sort((left, right) => left.value - right.value);

    return {
      key: config.key,
      label: config.label,
      leader: ranked[0] ? `${ranked[0].treeType}: ${formatMetricValue(ranked[0].value, 'executionTimeNs')}` : 'No data',
      bars: chartStats.value.map((row, index) => {
        const value = operationMetricValue(row, config.key);
        const height = Math.max(8, (value / maxValue) * 126);
        return {
          treeType: normalizeTreeType(row.treeType),
          value,
          x: 62 + index * 82,
          y: 176 - height,
          width: 54,
          height,
          color: colors[index % colors.length]
        };
      })
    };
  });
});

let timer: number | null = null;
let stompClient: Client | null = null;
let isProgressSubscribed = false;

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

onMounted(() => {
  ensureProgressSocket();
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

function metricValue(row: BenchmarkQuickRunResponse['stats'][number], metric: BenchmarkMetricKey): number {
  if (metric === 'avgNodeDepth') {
    return row.avgNodeDepth ?? 0;
  }
  return row[metric] ?? 0;
}

function formatMetricValue(value: number, metric: BenchmarkMetricKey): string {
  if (metric === 'executionTimeNs') {
    return `${formatNsToMs(value)} ms`;
  }
  if (metric === 'avgNodeDepth') {
    return value.toFixed(2);
  }
  return value.toLocaleString();
}

function operationMetricValue(
  row: BenchmarkQuickRunResponse['stats'][number],
  metric: OperationTimeMetricKey
): number {
  return row[metric] ?? 0;
}

function normalizeTreeType(treeType: string): string {
  return ({
    avl: 'AVL',
    'red-black': 'Red-Black',
    splay: 'Splay'
  }[treeType] ?? treeType);
}

function ensureProgressSocket(): void {
  if (stompClient?.active || stompClient?.connected) {
    return;
  }

  const websocketUrl = import.meta.env.DEV
    ? `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.hostname}:8080/ws`
    : `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws`;

  stompClient = new Client({
    brokerURL: websocketUrl,
    reconnectDelay: 3000,
    connectionTimeout: 5000
  });

  stompClient.onConnect = () => {
    if (isProgressSubscribed) {
      return;
    }

    stompClient!.subscribe(`/topic/benchmark-progress`, (message) => {
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
    });

    isProgressSubscribed = true;
  };

  stompClient.onWebSocketClose = () => {
    isProgressSubscribed = false;
  };

  stompClient.activate();
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

.chart-panel {
  margin: 18px 0 22px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.16);
}

.chart-panel-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: start;
  margin-bottom: 14px;
}

.chart-panel h3 {
  margin: 0 0 4px;
}

.metric-switcher {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: end;
}

.metric-btn {
  border: 1px solid #334155;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.72);
  color: #cbd5e1;
  padding: 8px 12px;
  font-size: 12px;
  cursor: pointer;
}

.metric-btn.active {
  background: #1d4ed8;
  color: #ffffff;
  border-color: #1d4ed8;
}

.chart-summary {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.summary-pill {
  display: inline-flex;
  flex-direction: column;
  gap: 2px;
  padding: 10px 12px;
  border-radius: 14px;
  background: rgba(148, 163, 184, 0.12);
}

.summary-pill span {
  font-size: 11px;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.summary-pill strong {
  font-size: 14px;
  color: #ffffff;
}

.comparison-chart {
  width: 100%;
  height: auto;
  display: block;
}

.breakdown-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.breakdown-card {
  border-radius: 16px;
  background: rgba(2, 8, 23, 0.3);
  border: 1px solid rgba(148, 163, 184, 0.12);
  padding: 14px;
}

.breakdown-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
  margin-bottom: 10px;
}

.breakdown-head h4 {
  margin: 0;
  font-size: 14px;
}

.breakdown-head span {
  color: #94a3b8;
  font-size: 12px;
  text-align: right;
}

.operation-chart {
  width: 100%;
  height: auto;
  display: block;
}

.chart-axis {
  stroke: rgba(148, 163, 184, 0.65);
  stroke-width: 1.5;
}

.chart-grid {
  stroke: rgba(148, 163, 184, 0.15);
  stroke-width: 1;
}

.chart-tick {
  fill: #94a3b8;
  font-size: 11px;
  text-anchor: end;
}

.chart-bar {
  fill: var(--bar-color);
  opacity: 0.92;
}

.chart-label {
  fill: #ffffff;
  font-size: 13px;
  font-weight: 600;
  text-anchor: middle;
}

.chart-value {
  fill: #e2e8f0;
  font-size: 12px;
  font-weight: 600;
  text-anchor: middle;
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

  .chart-panel-header {
    flex-direction: column;
  }

  .metric-switcher {
    justify-content: start;
  }

  .breakdown-grid {
    grid-template-columns: 1fr;
  }
}
</style>
