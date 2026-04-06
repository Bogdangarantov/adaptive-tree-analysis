<template>
  <div class="benchmark-root">
    <Navbar />

    <main class="benchmark-page">
      <section class="benchmark-hero">
        <div class="glass-card hero-copy-card">
          <p class="eyebrow">Benchmark Workspace</p>
          <h1>Порівняння продуктивності дерев</h1>
          <p class="hero-copy">
            Налаштовуй сценарій навантаження, запускай повторювані експерименти й оцінюй AVL, Red-Black та
            Splay дерева в єдиному аналітичному просторі.
          </p>

          <div class="hero-meta">
            <div class="hero-pill">
              <span>Обсяг</span>
              <strong>{{ datasetSize.toLocaleString() }} записів</strong>
            </div>
            <div class="hero-pill">
              <span>Розподіл</span>
              <strong>{{ distributionLabel }}</strong>
            </div>
            <div class="hero-pill">
              <span>Повтори</span>
              <strong>{{ repeatCount }}</strong>
            </div>
          </div>
        </div>

        <div class="hero-stack">
          <article class="glass-card hero-stat-card">
            <span>Operation mix</span>
            <strong>{{ totalOperationPercent }}%</strong>
            <p>Insert {{ insertPercent }}% · Search {{ searchPercent }}% · Delete {{ deletePercent }}%</p>
          </article>
          <article class="glass-card hero-stat-card">
            <span>Status</span>
            <strong>{{ loading ? 'Виконується' : result ? 'Готово до аналізу' : 'Готово до запуску' }}</strong>
            <p>
              {{
                loading
                  ? `Минуло ${(elapsedMs / 1000).toFixed(1)}s`
                  : result
                    ? `Experiment ${result.experiment.id}`
                    : 'Останній результат зʼявиться тут після запуску'
              }}
            </p>
          </article>
        </div>
      </section>

      <section class="benchmark-layout">
        <div class="glass-card benchmark-form">
          <div class="section-heading">
            <div>
              <p class="section-kicker">Control Panel</p>
              <h2>Параметри експерименту</h2>
            </div>
            <p class="section-copy">Конфігурація впливає лише на benchmark-run і не змінює Playground або API-контракти.</p>
          </div>

          <form @submit.prevent="runBenchmark">
            <div class="form-section">
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
            </div>

            <div class="form-section">
              <div class="section-heading compact">
                <div>
                  <p class="section-kicker">Load Shape</p>
                  <h3>Operation mix</h3>
                </div>
                <span class="mix-badge" :class="{ valid: totalOperationPercent === 100 }">
                  {{ totalOperationPercent }}%
                </span>
              </div>

              <div class="mix-grid">
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
              <p class="hint">Оптимально 100%. Якщо всі значення нульові, backend застосує свій режим за замовчуванням.</p>
            </div>

            <div class="form-section">
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
            </div>

            <div class="actions">
              <button type="submit" class="btn primary run-btn" :disabled="loading">
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

        <div class="benchmark-results">
          <template v-if="result">
            <div class="results-header">
              <div>
                <p class="section-kicker">Latest Run</p>
                <h2>Результати останньої сесії</h2>
              </div>
              <p class="hint">
                Experiment ID: <code>{{ result.experiment.id }}</code>
              </p>
            </div>

            <div class="results-overview">
              <article
                v-for="card in overviewCards"
                :key="card.label"
                class="glass-card overview-card"
              >
                <span>{{ card.label }}</span>
                <strong>{{ card.value }}</strong>
                <p>{{ card.caption }}</p>
              </article>
            </div>

            <section class="chart-panel">
              <div class="chart-panel-header">
                <div>
                  <h3>Порівняльний графік</h3>
                  <p class="hint">Швидкий огляд того, яке дерево лідирує за обраною метрикою в поточному запуску.</p>
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
                  <p class="hint">Окремі часи вставки, пошуку та видалення для кожного типу дерева.</p>
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

            <section class="table-panel">
              <div class="table-panel-head">
                <div>
                  <h3>Табличний зріз</h3>
                  <p class="hint">Сирі метрики поточного запуску для точного порівняння між деревами.</p>
                </div>
              </div>

              <div class="table-scroll">
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
          </template>

          <section v-else class="glass-card empty-state">
            <p class="section-kicker">No Results Yet</p>
            <h2>Запусти benchmark, щоб побачити аналітику</h2>
            <p>
              Після першого запуску тут з’являться ключові метрики, порівняльний графік і таблиця для останньої сесії.
            </p>
          </section>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { Client } from '@stomp/stompjs';
import Navbar from '../components/landing/Navbar.vue';
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

const totalOperationPercent = computed(() => insertPercent.value + searchPercent.value + deletePercent.value);

const distributionLabel = computed(() => {
  return (
    {
      RANDOM: 'Random',
      SORTED: 'Sorted',
      REVERSE_SORTED: 'Reverse sorted',
      ALMOST_SORTED: 'Almost sorted',
      ZIPF: 'Zipf'
    }[distributionType.value] ?? distributionType.value
  );
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

const overviewCards = computed(() => {
  if (chartStats.value.length === 0) {
    return [];
  }

  const fastest = [...chartStats.value].sort((left, right) => left.executionTimeNs - right.executionTimeNs)[0];
  const lowestRotations = [...chartStats.value].sort((left, right) => left.rotationCount - right.rotationCount)[0];
  const shortestHeight = [...chartStats.value].sort((left, right) => left.treeHeight - right.treeHeight)[0];

  return [
    {
      label: 'Найшвидше дерево',
      value: normalizeTreeType(fastest.treeType),
      caption: `${formatNsToMs(fastest.executionTimeNs)} ms для повного сценарію`
    },
    {
      label: 'Найменше ротацій',
      value: normalizeTreeType(lowestRotations.treeType),
      caption: `${lowestRotations.rotationCount.toLocaleString()} rotations`
    },
    {
      label: 'Найменша висота',
      value: normalizeTreeType(shortestHeight.treeType),
      caption: `tree height ${shortestHeight.treeHeight}`
    }
  ];
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
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(56, 189, 248, 0.2), transparent 24%),
    radial-gradient(circle at top right, rgba(245, 158, 11, 0.12), transparent 22%),
    linear-gradient(180deg, #07111f 0%, #0d1726 48%, #101827 100%);
  color: #f8fafc;
}

.benchmark-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 108px 16px 36px;
}

.benchmark-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 0.9fr);
  gap: 20px;
  margin-bottom: 24px;
}

.hero-copy-card,
.hero-stat-card,
.benchmark-form,
.empty-state,
.overview-card,
.table-panel {
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.hero-copy-card {
  padding: 28px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  background: linear-gradient(135deg, rgba(10, 18, 34, 0.9), rgba(15, 23, 42, 0.72));
}

.eyebrow,
.section-kicker {
  margin: 0 0 10px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: #7dd3fc;
}

.hero-copy-card h1,
.benchmark-form h2,
.benchmark-results h2,
.empty-state h2 {
  margin: 0;
  color: #ffffff;
}

.hero-copy-card h1 {
  font-size: clamp(32px, 4vw, 54px);
  line-height: 1.02;
  max-width: 10ch;
}

.hero-copy {
  max-width: 62ch;
  margin: 14px 0 0;
  color: #cbd5e1;
  font-size: 15px;
  line-height: 1.7;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 22px;
}

.hero-pill {
  min-width: 150px;
  padding: 12px 14px;
  border-radius: 18px;
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.hero-pill span,
.hero-stat-card span,
.overview-card span,
.summary-pill span {
  display: block;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
}

.hero-pill strong,
.hero-stat-card strong,
.overview-card strong,
.summary-pill strong {
  display: block;
  margin-top: 6px;
  color: #ffffff;
}

.hero-pill strong {
  font-size: 15px;
}

.hero-stack {
  display: grid;
  gap: 16px;
}

.hero-stat-card {
  padding: 22px;
  border-radius: 24px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.84), rgba(15, 23, 42, 0.58));
}

.hero-stat-card strong {
  font-size: 24px;
}

.hero-stat-card p,
.overview-card p,
.empty-state p {
  margin: 10px 0 0;
  color: #cbd5e1;
  line-height: 1.6;
}

.benchmark-layout {
  display: grid;
  grid-template-columns: minmax(320px, 0.95fr) minmax(0, 1.35fr);
  gap: 24px;
  align-items: start;
}

.benchmark-form {
  position: sticky;
  top: 92px;
  padding: 24px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background: linear-gradient(180deg, rgba(8, 15, 30, 0.9), rgba(15, 23, 42, 0.72));
}

.benchmark-results {
  min-width: 0;
}

.section-heading {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: end;
  margin-bottom: 18px;
}

.section-heading.compact {
  align-items: center;
  margin-bottom: 14px;
}

.section-heading h2,
.section-heading h3,
.results-header h2,
.chart-panel h3,
.table-panel h3,
.breakdown-head h4 {
  margin: 0;
}

.section-copy {
  max-width: 28ch;
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.5;
}

.form-section {
  padding: 18px;
  border-radius: 20px;
  border: 1px solid rgba(148, 163, 184, 0.12);
  background: rgba(15, 23, 42, 0.42);
}

.form-section + .form-section {
  margin-top: 14px;
}

.field-group {
  margin-bottom: 16px;
}

.field-group:last-child {
  margin-bottom: 0;
}

.field-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
  color: #e2e8f0;
}

.text-input {
  width: 100%;
  padding: 11px 12px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  background: rgba(8, 15, 30, 0.72);
  color: #f8fafc;
  font-size: 14px;
  transition: border-color 0.16s ease, box-shadow 0.16s ease, background 0.16s ease;
}

.text-input:focus {
  outline: none;
  border-color: rgba(56, 189, 248, 0.65);
  box-shadow: 0 0 0 4px rgba(56, 189, 248, 0.12);
}

.text-input::placeholder {
  color: #64748b;
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
  border-radius: 999px;
  padding: 9px 14px;
  font-size: 13px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: transform 0.16s ease, opacity 0.16s ease, background 0.16s ease, border-color 0.16s ease;
}

.btn:hover {
  transform: translateY(-1px);
}

.btn.primary {
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  color: #fff;
  border-color: rgba(125, 211, 252, 0.35);
}

.btn.primary:disabled {
  opacity: 0.7;
  cursor: default;
}

.btn.secondary {
  background: rgba(15, 23, 42, 0.75);
  color: #e2e8f0;
  border-color: rgba(148, 163, 184, 0.22);
}

.mix-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.percent-input {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 80px;
}

.percent-input span {
  font-size: 12px;
  color: #94a3b8;
}

.mix-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
  padding: 10px 12px;
  border-radius: 999px;
  background: rgba(245, 158, 11, 0.18);
  color: #fde68a;
  border: 1px solid rgba(245, 158, 11, 0.24);
  font-weight: 700;
}

.mix-badge.valid {
  background: rgba(16, 185, 129, 0.16);
  color: #a7f3d0;
  border-color: rgba(16, 185, 129, 0.22);
}

.actions {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.run-btn {
  width: 100%;
  justify-content: center;
  padding: 12px 18px;
  font-size: 14px;
  font-weight: 700;
}

.error {
  color: #fca5a5;
  font-size: 13px;
}

.hint {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: end;
  margin-bottom: 16px;
}

.results-overview {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.overview-card {
  padding: 18px;
  border-radius: 20px;
  border: 1px solid rgba(148, 163, 184, 0.12);
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.78), rgba(15, 23, 42, 0.52));
}

.overview-card strong {
  font-size: 22px;
}

.chart-panel,
.table-panel {
  margin: 0 0 18px;
  padding: 20px;
  border-radius: 22px;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.16);
}

.table-panel-head {
  margin-bottom: 12px;
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
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.72);
  color: #cbd5e1;
  padding: 8px 12px;
  font-size: 12px;
  cursor: pointer;
}

.metric-btn.active {
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  color: #ffffff;
  border-color: rgba(125, 211, 252, 0.35);
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
  border: 1px solid rgba(148, 163, 184, 0.12);
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
  border-radius: 18px;
  background: rgba(2, 8, 23, 0.34);
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
  filter: drop-shadow(0 8px 16px rgba(15, 23, 42, 0.24));
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
  margin-top: 14px;
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
  min-width: 620px;
}

.table-scroll {
  overflow-x: auto;
}

.results-table th,
.results-table td {
  padding: 12px 10px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
  text-align: left;
  font-size: 13px;
  color: #ffffff;
  background: transparent;
}

.results-table th {
  font-weight: 600;
  color: #cbd5e1;
}

.results-table tbody tr:hover {
  background: rgba(148, 163, 184, 0.06);
}

code {
  font-size: 12px;
  background: rgba(15, 23, 42, 0.72);
  padding: 4px 6px;
  border-radius: 8px;
  color: #e2e8f0;
}

.empty-state {
  padding: 28px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background: linear-gradient(180deg, rgba(8, 15, 30, 0.88), rgba(15, 23, 42, 0.72));
}

@media (max-width: 900px) {
  .benchmark-hero,
  .benchmark-layout {
    grid-template-columns: 1fr;
  }

  .benchmark-form {
    position: static;
  }

  .chart-panel-header {
    flex-direction: column;
  }

  .metric-switcher {
    justify-content: start;
  }

  .results-header,
  .section-heading {
    flex-direction: column;
    align-items: start;
  }

  .results-overview,
  .breakdown-grid {
    grid-template-columns: 1fr;
  }

  .mix-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .benchmark-page {
    padding: 96px 14px 28px;
  }

  .hero-copy-card,
  .benchmark-form,
  .chart-panel,
  .table-panel,
  .empty-state {
    padding: 18px;
  }

  .hero-copy-card h1 {
    font-size: 30px;
  }
}
</style>
