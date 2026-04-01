<template>
  <section class="section charts-section" id="charts">
    <div class="container">
      <h2 class="section-title fade-in">Аналіз продуктивності</h2>
      <p class="section-description fade-in">Головна сторінка показує лише реальні метрики із збережених benchmark-запусків backend.</p>

      <div v-if="loading" class="chart-state glass-card fade-in">Завантажуємо benchmark-метрики...</div>
      <div v-else-if="error" class="chart-state glass-card fade-in">{{ error }}</div>
      <div v-else-if="!hasBenchmarkData" class="chart-state glass-card fade-in">
        Поки що немає benchmark-даних. Запусти кілька порівнянь у розділі бенчмарку, і графіки заповняться реальними числами.
      </div>

      <div v-else class="charts-grid">
        <div class="chart-card glass-card fade-in">
          <h3><i class="fas fa-chart-bar"></i> Середній час виконання</h3>
          <p class="chart-note">Середній `executionTimeNs` по кожному типу дерева.</p>
          <div class="svg-chart-shell">
            <svg viewBox="0 0 640 320" class="svg-chart" aria-label="Average execution time by tree type">
              <line x1="70" y1="24" x2="70" y2="266" class="chart-axis-line" />
              <line x1="70" y1="266" x2="604" y2="266" class="chart-axis-line" />
              <g v-for="tick in executionTimeTicks" :key="`exec-${tick.value}`">
                <line x1="70" :y1="tick.y" x2="604" :y2="tick.y" class="chart-grid-line" />
                <text x="58" :y="tick.y + 4" class="chart-axis-label">{{ formatNsShort(tick.value) }}</text>
              </g>
              <g v-for="bar in executionTimeBars" :key="bar.treeType">
                <rect :x="bar.x" :y="bar.y" :width="bar.width" :height="bar.height" :fill="bar.color" rx="16" />
                <text :x="bar.x + bar.width / 2" :y="Math.max(16, bar.y - 10)" class="chart-value-label">
                  {{ formatNs(bar.value) }}
                </text>
                <text :x="bar.x + bar.width / 2" y="292" class="chart-name-label">{{ bar.label }}</text>
              </g>
            </svg>
          </div>
        </div>

        <div class="chart-card glass-card fade-in">
          <h3><i class="fas fa-chart-line"></i> Зростання висоти дерева</h3>
          <p class="chart-note">Графік будується з реальних benchmark-результатів, збережених на backend.</p>
          <div class="svg-chart-shell">
            <svg viewBox="0 0 640 320" class="svg-chart" aria-label="Average tree height by dataset size">
              <line x1="70" y1="24" x2="70" y2="266" class="chart-axis-line" />
              <line x1="70" y1="266" x2="604" y2="266" class="chart-axis-line" />
              <g v-for="tick in heightTicks" :key="`height-y-${tick.value}`">
                <line x1="70" :y1="tick.y" x2="604" :y2="tick.y" class="chart-grid-line" />
                <text x="58" :y="tick.y + 4" class="chart-axis-label">{{ tick.value.toFixed(1) }}</text>
              </g>
              <g v-for="tick in datasetTicks" :key="`height-x-${tick}`">
                <line :x1="xForDatasetSize(tick)" y1="266" :x2="xForDatasetSize(tick)" y2="272" class="chart-axis-line" />
                <text :x="xForDatasetSize(tick)" y="292" class="chart-name-label">{{ tick }}</text>
              </g>
              <g v-for="series in heightSeriesPaths" :key="series.treeType">
                <path :d="series.path" fill="none" :stroke="series.color" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" />
                <circle
                  v-for="point in series.points"
                  :key="`${series.treeType}-${point.datasetSize}`"
                  :cx="point.x"
                  :cy="point.y"
                  r="4.5"
                  :fill="series.color"
                />
              </g>
            </svg>
          </div>
          <div class="chart-legend">
            <span v-for="series in heightSeriesPaths" :key="`legend-${series.treeType}`">
              <i :style="{ backgroundColor: series.color }"></i>{{ normalizeTreeType(series.treeType) }}
            </span>
          </div>
        </div>

        <div class="chart-card glass-card fade-in full-width">
          <h3><i class="fas fa-chart-area"></i> Порівняння за кількома метриками</h3>
          <p class="chart-note">Середні значення `insert/search/delete` та висоти дерева з backend summary.</p>
          <div class="metrics-grid">
            <article v-for="tree in normalizedTrees" :key="tree.treeType" class="metric-card">
              <header>
                <strong>{{ tree.label }}</strong>
                <span>{{ tree.runCount.toLocaleString() }} запусків</span>
              </header>
              <div class="metric-row">
                <label>Insert</label>
                <div class="metric-track"><div class="metric-fill insert" :style="{ width: metricBarWidth(tree.averageInsertTimeNs, maxOperationTime) }"></div></div>
                <span>{{ formatNs(tree.averageInsertTimeNs) }}</span>
              </div>
              <div class="metric-row">
                <label>Search</label>
                <div class="metric-track"><div class="metric-fill search" :style="{ width: metricBarWidth(tree.averageSearchTimeNs, maxOperationTime) }"></div></div>
                <span>{{ formatNs(tree.averageSearchTimeNs) }}</span>
              </div>
              <div class="metric-row">
                <label>Delete</label>
                <div class="metric-track"><div class="metric-fill delete" :style="{ width: metricBarWidth(tree.averageDeleteTimeNs, maxOperationTime) }"></div></div>
                <span>{{ formatNs(tree.averageDeleteTimeNs) }}</span>
              </div>
              <div class="metric-row compact">
                <label>Height</label>
                <strong>{{ tree.averageTreeHeight.toFixed(1) }}</strong>
              </div>
            </article>
          </div>
        </div>
      </div>

      <div class="complexity-table-wrapper fade-in">
        <h3><i class="fas fa-table"></i> Порівняння алгоритмічної складності</h3>
        <div class="table-container glass-card">
          <table class="complexity-table">
            <thead>
              <tr>
                <th>Тип дерева</th>
                <th>Пошук</th>
                <th>Вставка</th>
                <th>Видалення</th>
                <th>Пам'ять</th>
                <th>Баланс</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in complexityRows" :key="row.treeType">
                <td><strong>{{ row.treeType }}</strong></td>
                <td><span class="complexity-badge" :class="row.searchClass">{{ row.search }}</span></td>
                <td><span class="complexity-badge" :class="row.insertClass">{{ row.insert }}</span></td>
                <td><span class="complexity-badge" :class="row.deleteClass">{{ row.delete }}</span></td>
                <td><span class="complexity-badge" :class="row.spaceClass">{{ row.space }}</span></td>
                <td><span class="complexity-badge" :class="row.balanceClass">{{ row.balance }}</span></td>
              </tr>
            </tbody>
          </table>
          <p class="table-note">* Амортизована часова складність</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import {
  getBenchmarkHeightGrowth,
  getBenchmarkSummary,
  type BenchmarkHeightGrowthResponse,
  type BenchmarkSummaryResponse
} from '../../api/client';
import { complexityRows } from '../../constants/generatedContent';

const loading = ref(true);
const error = ref<string | null>(null);
const summary = ref<BenchmarkSummaryResponse | null>(null);
const heightGrowth = ref<BenchmarkHeightGrowthResponse | null>(null);

const chartColors: Record<string, string> = {
  avl: '#10b981',
  'red-black': '#00d4ff',
  splay: '#f59e0b'
};

const normalizedTrees = computed(() => {
  return (summary.value?.trees ?? []).map((tree) => ({
    ...tree,
    label: normalizeTreeType(tree.treeType)
  }));
});

const hasBenchmarkData = computed(() => {
  return normalizedTrees.value.length > 0 || (heightGrowth.value?.series.length ?? 0) > 0;
});

const maxExecutionTime = computed(() => {
  return Math.max(...normalizedTrees.value.map((tree) => tree.averageExecutionTimeNs), 1);
});

const executionTimeTicks = computed(() => {
  return [0, 0.25, 0.5, 0.75, 1].map((ratio) => ({
    value: maxExecutionTime.value * (1 - ratio),
    y: 24 + ratio * 242
  }));
});

const executionTimeBars = computed(() => {
  const barWidth = 112;
  const gap = 46;
  const startX = 106;

  return normalizedTrees.value.map((tree, index) => {
    const height = Math.max(10, (tree.averageExecutionTimeNs / maxExecutionTime.value) * 208);
    return {
      treeType: tree.treeType,
      label: tree.label,
      value: tree.averageExecutionTimeNs,
      x: startX + index * (barWidth + gap),
      y: 266 - height,
      width: barWidth,
      height,
      color: chartColors[tree.treeType] ?? '#c084fc'
    };
  });
});

const datasetTicks = computed(() => {
  const allSizes = (heightGrowth.value?.series ?? []).flatMap((series) => series.points.map((point) => point.datasetSize));
  return [...new Set(allSizes)].sort((left, right) => left - right);
});

const maxHeight = computed(() => {
  const heights = (heightGrowth.value?.series ?? []).flatMap((series) => series.points.map((point) => point.averageTreeHeight));
  return Math.max(...heights, 1);
});

const heightTicks = computed(() => {
  return [0, 0.25, 0.5, 0.75, 1].map((ratio) => ({
    value: maxHeight.value * (1 - ratio),
    y: 24 + ratio * 242
  }));
});

const heightSeriesPaths = computed(() => {
  return (heightGrowth.value?.series ?? []).map((series) => {
    const points = [...series.points]
      .sort((left, right) => left.datasetSize - right.datasetSize)
      .map((point) => ({
        ...point,
        x: xForDatasetSize(point.datasetSize),
        y: yForHeight(point.averageTreeHeight)
      }));

    return {
      treeType: series.treeType,
      color: chartColors[series.treeType] ?? '#c084fc',
      points,
      path: points
        .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
        .join(' ')
    };
  });
});

const maxOperationTime = computed(() => {
  return Math.max(
    ...normalizedTrees.value.flatMap((tree) => [
      tree.averageInsertTimeNs,
      tree.averageSearchTimeNs,
      tree.averageDeleteTimeNs
    ]),
    1
  );
});

onMounted(async () => {
  try {
    const [summaryResponse, heightGrowthResponse] = await Promise.all([
      getBenchmarkSummary(),
      getBenchmarkHeightGrowth()
    ]);
    summary.value = summaryResponse;
    heightGrowth.value = heightGrowthResponse;
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : 'Не вдалося завантажити benchmark-метрики.';
  } finally {
    loading.value = false;
  }
});

function xForDatasetSize(datasetSize: number): number {
  const sizes = datasetTicks.value;
  if (sizes.length <= 1) {
    return 337;
  }

  const minSize = sizes[0];
  const maxSize = sizes[sizes.length - 1];
  const ratio = maxSize === minSize ? 0.5 : (datasetSize - minSize) / (maxSize - minSize);
  return 70 + ratio * 534;
}

function yForHeight(height: number): number {
  return 266 - (height / maxHeight.value) * 220;
}

function normalizeTreeType(treeType: string): string {
  return ({
    avl: 'AVL',
    'red-black': 'Red-Black',
    splay: 'Splay'
  }[treeType] ?? treeType);
}

function formatNs(ns: number): string {
  if (ns >= 1_000_000) {
    return `${(ns / 1_000_000).toFixed(2)} ms`;
  }
  if (ns >= 1_000) {
    return `${(ns / 1_000).toFixed(1)} μs`;
  }
  return `${ns} ns`;
}

function formatNsShort(ns: number): string {
  if (ns >= 1_000_000) {
    return `${(ns / 1_000_000).toFixed(1)} ms`;
  }
  if (ns >= 1_000) {
    return `${Math.round(ns / 1_000)} μs`;
  }
  return `${Math.round(ns)} ns`;
}

function metricBarWidth(value: number, maxValue: number): string {
  return `${Math.max(10, (value / maxValue) * 100)}%`;
}
</script>

<style scoped>
.chart-state {
  padding: 24px;
  text-align: center;
  color: #cbd5e1;
}

.chart-note {
  margin: -4px 0 12px;
  color: #94a3b8;
  font-size: 13px;
}

.svg-chart-shell {
  height: 350px;
}

.svg-chart {
  width: 100%;
  height: 100%;
  display: block;
}

.chart-axis-line {
  stroke: rgba(148, 163, 184, 0.7);
  stroke-width: 1.5;
}

.chart-grid-line {
  stroke: rgba(148, 163, 184, 0.2);
  stroke-width: 1;
}

.chart-axis-label,
.chart-name-label,
.chart-value-label {
  fill: #cbd5e1;
  font-size: 12px;
  font-weight: 600;
  text-anchor: middle;
}

.chart-axis-label {
  text-anchor: end;
  font-size: 11px;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 10px;
  color: #cbd5e1;
  font-size: 13px;
}

.chart-legend span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.chart-legend i {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  display: inline-block;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 16px;
  background: rgba(15, 20, 36, 0.65);
}

.metric-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
  color: #ffffff;
}

.metric-card header span {
  color: #94a3b8;
  font-size: 12px;
}

.metric-row {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  color: #e2e8f0;
  font-size: 12px;
}

.metric-row.compact {
  grid-template-columns: 48px auto;
  justify-content: start;
}

.metric-track {
  height: 10px;
  background: rgba(148, 163, 184, 0.14);
  border-radius: 999px;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  border-radius: inherit;
}

.metric-fill.insert {
  background: linear-gradient(90deg, #0ea5e9, #38bdf8);
}

.metric-fill.search {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.metric-fill.delete {
  background: linear-gradient(90deg, #f97316, #fb923c);
}

@media (max-width: 980px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
