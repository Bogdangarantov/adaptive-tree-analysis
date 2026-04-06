<template>
  <section class="section benchmark-summary-section" id="benchmark-summary">
    <div class="container">
      <h2 class="section-title fade-in">Зведені метрики з бенчмарків</h2>
      <p class="section-description fade-in">
        Агреговані результати з усіх збережених benchmark-запусків, щоб на головній сторінці одразу бачити
        реальну картину продуктивності.
      </p>

      <div v-if="loading" class="summary-state glass-card">Завантажуємо зведену статистику...</div>
      <div v-else-if="error" class="summary-state glass-card">{{ error }}</div>
      <div v-else-if="!summary || summary.totalRuns === 0" class="summary-state glass-card">
        Поки що немає жодного benchmark-запуску. Запусти кілька порівнянь у розділі бенчмарку.
      </div>
      <template v-else>
        <div class="summary-top-grid">
          <article class="summary-card glass-card">
            <span class="summary-label">Усього запусків</span>
            <strong>{{ summary.totalRuns.toLocaleString() }}</strong>
          </article>
          <article class="summary-card glass-card">
            <span class="summary-label">Усього операцій</span>
            <strong>{{ summary.totalOperations.toLocaleString() }}</strong>
          </article>
          <article class="summary-card glass-card">
            <span class="summary-label">Середній час</span>
            <strong>{{ formatNs(summary.averageExecutionTimeNs) }}</strong>
          </article>
          <article class="summary-card glass-card">
            <span class="summary-label">Найкращий час</span>
            <strong>{{ formatNs(summary.bestExecutionTimeNs) }}</strong>
          </article>
        </div>

        <div class="tree-summary-grid">
          <article
            v-for="tree in normalizedTrees"
            :key="tree.treeType"
            class="tree-summary-card glass-card"
          >
            <div class="tree-summary-head">
              <h3>{{ tree.label }}</h3>
              <span>{{ tree.runCount.toLocaleString() }} запусків</span>
            </div>

            <div class="tree-summary-metrics">
              <div>
                <span>Середній час</span>
                <strong>{{ formatNs(tree.averageExecutionTimeNs) }}</strong>
              </div>
              <div>
                <span>Найкращий час</span>
                <strong>{{ formatNs(tree.bestExecutionTimeNs) }}</strong>
              </div>
              <div>
                <span>Середня висота</span>
                <strong>{{ tree.averageTreeHeight.toFixed(1) }}</strong>
              </div>
              <div>
                <span>Операцій усього</span>
                <strong>{{ tree.totalOperations.toLocaleString() }}</strong>
              </div>
            </div>

            <div class="micro-bars">
              <div class="micro-bar-row">
                <span>Insert</span>
                <div class="micro-bar-track">
                  <div class="micro-bar-fill insert" :style="{ width: barWidth(tree.averageInsertTimeNs) }"></div>
                </div>
                <strong>{{ formatNs(tree.averageInsertTimeNs) }}</strong>
              </div>
              <div class="micro-bar-row">
                <span>Search</span>
                <div class="micro-bar-track">
                  <div class="micro-bar-fill search" :style="{ width: barWidth(tree.averageSearchTimeNs) }"></div>
                </div>
                <strong>{{ formatNs(tree.averageSearchTimeNs) }}</strong>
              </div>
              <div class="micro-bar-row">
                <span>Delete</span>
                <div class="micro-bar-track">
                  <div class="micro-bar-fill delete" :style="{ width: barWidth(tree.averageDeleteTimeNs) }"></div>
                </div>
                <strong>{{ formatNs(tree.averageDeleteTimeNs) }}</strong>
              </div>
            </div>
          </article>
        </div>
      </template>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { getBenchmarkSummary, type BenchmarkSummaryResponse } from '../../api/client';

const loading = ref(true);
const error = ref<string | null>(null);
const summary = ref<BenchmarkSummaryResponse | null>(null);

const normalizedTrees = computed(() => {
  return (summary.value?.trees ?? []).map((tree) => ({
    ...tree,
    label: normalizeTreeType(tree.treeType)
  }));
});

const maxOperationTime = computed(() => {
  return Math.max(
    ...((summary.value?.trees ?? []).flatMap((tree) => [
      tree.averageInsertTimeNs,
      tree.averageSearchTimeNs,
      tree.averageDeleteTimeNs
    ])),
    1
  );
});

onMounted(async () => {
  try {
    summary.value = await getBenchmarkSummary();
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : 'Не вдалося завантажити summary бенчмарків.';
  } finally {
    loading.value = false;
  }
});

function formatNs(ns: number): string {
  if (ns >= 1_000_000) {
    return `${(ns / 1_000_000).toFixed(2)} ms`;
  }
  if (ns >= 1_000) {
    return `${(ns / 1_000).toFixed(1)} μs`;
  }
  return `${ns} ns`;
}

function normalizeTreeType(treeType: string): string {
  return ({
    avl: 'AVL-дерево',
    'red-black': 'Червоно-чорне дерево',
    splay: 'Splay-дерево'
  }[treeType] ?? treeType);
}

function barWidth(value: number): string {
  return `${Math.max(8, (value / maxOperationTime.value) * 100)}%`;
}
</script>

<style scoped>
.summary-state {
  padding: 22px;
  text-align: center;
  color: #cbd5e1;
}

.summary-top-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  padding: 18px;
}

.summary-label {
  display: block;
  margin-bottom: 8px;
  color: #94a3b8;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.summary-card strong {
  color: #ffffff;
  font-size: 28px;
}

.tree-summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.tree-summary-card {
  padding: 18px;
}

.tree-summary-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
  margin-bottom: 14px;
}

.tree-summary-head h3 {
  margin: 0;
}

.tree-summary-head span {
  color: #94a3b8;
  font-size: 12px;
}

.tree-summary-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.tree-summary-metrics span {
  display: block;
  margin-bottom: 4px;
  color: #94a3b8;
  font-size: 12px;
}

.tree-summary-metrics strong {
  color: #ffffff;
  font-size: 16px;
}

.micro-bars {
  display: grid;
  gap: 10px;
}

.micro-bar-row {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
}

.micro-bar-row span,
.micro-bar-row strong {
  font-size: 12px;
  color: #e2e8f0;
}

.micro-bar-track {
  height: 10px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.14);
  overflow: hidden;
}

.micro-bar-fill {
  height: 100%;
  border-radius: inherit;
}

.micro-bar-fill.insert {
  background: linear-gradient(90deg, #38bdf8, #2563eb);
}

.micro-bar-fill.search {
  background: linear-gradient(90deg, #34d399, #059669);
}

.micro-bar-fill.delete {
  background: linear-gradient(90deg, #f59e0b, #dc2626);
}

@media (max-width: 960px) {
  .summary-top-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .tree-summary-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .summary-top-grid {
    grid-template-columns: 1fr;
  }

  .tree-summary-metrics {
    grid-template-columns: 1fr;
  }
}
</style>
