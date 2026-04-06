<template>
  <div class="playground-root">
    <Navbar />

    <main class="playground-page">
      <section class="playground-hero">
        <div>
          <p class="eyebrow">Stateless Playground</p>
          <h1>Інтерактивний аналіз дерев</h1>
          <p class="hero-copy">
            Локальне середовище для перевірки операцій `insert`, `search` і `delete` для AVL, Red-Black та
            Splay дерев через backend snapshots.
          </p>
        </div>

        <div class="spawn-panel">
          <button class="spawn-btn avl" @click="spawnTree('avl')">+ AVL</button>
          <button class="spawn-btn red-black" @click="spawnTree('red-black')">+ Red-Black</button>
          <button class="spawn-btn splay" @click="spawnTree('splay')">+ Splay</button>
        </div>
      </section>

      <section class="playground-stage">
        <div class="stage-toolbar">
          <div class="stage-pill">{{ trees.length }} trees on canvas</div>
          <button class="ghost-btn" :disabled="trees.length === 0" @click="clearAll">Очистити сцену</button>
        </div>

        <div ref="stageRef" class="stage-canvas">
          <article
            v-for="tree in trees"
            :key="tree.id"
            class="tree-card"
            :style="{ transform: `translate(${tree.x}px, ${tree.y}px)` }"
          >
            <header class="tree-card-header" @mousedown="startDrag(tree.id, $event)">
              <div>
                <p class="tree-label">{{ formatTreeType(tree.treeType) }}</p>
                <h2>{{ tree.title }}</h2>
              </div>
              <button class="icon-btn" @mousedown.stop @click.stop="removeTree(tree.id)">×</button>
            </header>

            <div class="tree-card-controls">
              <input
                v-model="tree.inputKey"
                class="number-input"
                inputmode="numeric"
                placeholder="Число"
                @keydown.enter.prevent="runOperation(tree.id, 'INSERT')"
              />

              <div class="action-group">
                <button class="action-btn" :disabled="tree.loading" @click="runOperation(tree.id, 'INSERT')">
                  Insert
                </button>
                <button class="action-btn" :disabled="tree.loading" @click="runOperation(tree.id, 'SEARCH')">
                  Search
                </button>
                <button class="action-btn danger" :disabled="tree.loading" @click="runOperation(tree.id, 'DELETE')">
                  Delete
                </button>
              </div>

              <button class="ghost-btn" :disabled="tree.loading" @click="resetTree(tree.id)">Reset</button>
            </div>

            <div class="generator-row">
              <input
                v-model="tree.generateCount"
                class="number-input generator-input"
                inputmode="numeric"
                placeholder="N"
                @keydown.enter.prevent="generateTree(tree.id)"
              />
              <button class="action-btn generator-btn" :disabled="tree.loading" @click="generateTree(tree.id)">
                Generate N
              </button>
              <button class="ghost-btn" :disabled="tree.loading" @click="fillGenerateCount(tree.id, 15)">15</button>
              <button class="ghost-btn" :disabled="tree.loading" @click="fillGenerateCount(tree.id, 31)">31</button>
              <button class="ghost-btn" :disabled="tree.loading" @click="fillGenerateCount(tree.id, 63)">63</button>
            </div>

            <p v-if="tree.statusMessage" class="status-message">{{ tree.statusMessage }}</p>
            <p v-if="tree.error" class="error-message">{{ tree.error }}</p>

            <div class="zoom-row">
              <span class="zoom-label">Zoom</span>
              <button class="ghost-btn zoom-btn" :disabled="tree.loading" @click="adjustZoom(tree.id, -0.15)">-</button>
              <button class="ghost-btn zoom-value" :disabled="tree.loading" @click="resetZoom(tree.id)">
                {{ Math.round(tree.zoom * 100) }}%
              </button>
              <button class="ghost-btn zoom-btn" :disabled="tree.loading" @click="adjustZoom(tree.id, 0.15)">+</button>
            </div>

            <div
              class="visual-shell"
              :class="{ pannable: tree.zoom > 1.01, panning: panState.treeId === tree.id }"
              @wheel.prevent="handleWheelZoom(tree.id, $event)"
              @mousedown="startPan(tree.id, $event)"
            >
              <svg
                v-if="tree.layout.nodes.length > 0"
                class="tree-svg"
                :viewBox="`0 0 ${tree.layout.width} ${tree.layout.height}`"
                role="img"
                :aria-label="`${formatTreeType(tree.treeType)} visualization`"
              >
                <g :transform="svgTransform(tree)">
                  <line
                    v-for="(edge, index) in tree.layout.edges"
                    :key="`edge-${index}`"
                    class="tree-edge"
                    :x1="edge.fromX"
                    :y1="edge.fromY"
                    :x2="edge.toX"
                    :y2="edge.toY"
                  />

                  <g
                    v-for="node in tree.layout.nodes"
                    :key="`node-${node.key}-${node.depth}`"
                    :transform="`translate(${node.x}, ${node.y})`"
                  >
                    <circle
                      class="tree-node"
                      :class="{ active: tree.activeKeys.has(node.key) }"
                      :fill="nodeFill(tree.treeType, node.color)"
                      cx="0"
                      cy="0"
                      r="24"
                    />
                    <text class="tree-node-text" x="0" y="6">{{ node.key }}</text>
                  </g>
                </g>
              </svg>

              <div v-else class="empty-tree">
                <span>Дерево порожнє.</span>
                <small>Спробуй `insert`, щоб побудувати перший snapshot.</small>
              </div>
            </div>

            <div class="tree-meta">
              <span v-if="tree.metrics">height: {{ tree.metrics.treeHeight }}</span>
              <span v-if="tree.metrics">rotations: {{ tree.metrics.rotationCount }}</span>
              <span v-if="tree.metrics">time: {{ formatNs(tree.metrics.executionTimeNs) }}</span>
            </div>

            <div class="steps-panel">
              <h3>Останні кроки</h3>
              <ul v-if="tree.steps.length > 0" class="step-list">
                <li v-for="(step, index) in tree.steps" :key="`${tree.id}-step-${index}`">
                  <strong>{{ step.type }}</strong>
                  <span v-if="typeof step.nodeKey === 'number'">node {{ step.nodeKey }}</span>
                </li>
              </ul>
              <p v-else class="steps-placeholder">Поки що без дій.</p>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, reactive, ref } from 'vue';
import type { components } from '@dto/api-types';
import Navbar from '../components/landing/Navbar.vue';
import {
  runPlaygroundOperation,
  type PlaygroundOperationRequest,
  type PlaygroundOperationResponse
} from '../api/client';
import { layoutTree, type TreeLayout } from '../visualization/layoutTree';

type TreeType = components['schemas']['PlaygroundTreeType'];
type TreeOperationType = components['schemas']['TreeOperationType'];
type TreeMetrics = components['schemas']['TreeMetrics'];
type TreeStepEvent = components['schemas']['TreeStepEvent'];
type PlaygroundTreeNode = components['schemas']['PlaygroundTreeNode'];

interface PlaygroundTreeCard {
  id: string;
  title: string;
  treeType: TreeType;
  x: number;
  y: number;
  inputKey: string;
  generateCount: string;
  loading: boolean;
  error: string | null;
  statusMessage: string | null;
  root: PlaygroundTreeNode | null;
  layout: TreeLayout;
  metrics: TreeMetrics | null;
  steps: TreeStepEvent[];
  activeKeys: Set<number>;
  animationToken: number;
  zoom: number;
  panX: number;
  panY: number;
}

const stageRef = ref<HTMLElement | null>(null);
const trees = reactive<PlaygroundTreeCard[]>([]);

const dragState = reactive<{
  treeId: string | null;
  pointerOffsetX: number;
  pointerOffsetY: number;
}>({
  treeId: null,
  pointerOffsetX: 0,
  pointerOffsetY: 0
});

const panState = reactive<{
  treeId: string | null;
  startClientX: number;
  startClientY: number;
  startPanX: number;
  startPanY: number;
}>({
  treeId: null,
  startClientX: 0,
  startClientY: 0,
  startPanX: 0,
  startPanY: 0
});

function spawnTree(treeType: TreeType): void {
  const index = trees.length;
  trees.push({
    id: crypto.randomUUID(),
    title: `Sandbox ${index + 1}`,
    treeType,
    x: 32 + (index % 3) * 32,
    y: 32 + index * 44,
    inputKey: '',
    generateCount: '15',
    loading: false,
    error: null,
    statusMessage: 'Дерево створено. Введи число і вибери дію.',
    root: null,
    layout: layoutTree(null),
    metrics: null,
    steps: [],
    activeKeys: new Set<number>(),
    animationToken: 0,
    zoom: 1,
    panX: 0,
    panY: 0
  });
}

function removeTree(treeId: string): void {
  const index = trees.findIndex((tree) => tree.id === treeId);
  if (index >= 0) {
    trees.splice(index, 1);
  }
}

function resetTree(treeId: string): void {
  const tree = trees.find((item) => item.id === treeId);
  if (!tree) {
    return;
  }

  tree.root = null;
  tree.layout = layoutTree(null);
  tree.metrics = null;
  tree.steps = [];
  tree.activeKeys = new Set<number>();
  tree.animationToken += 1;
  tree.zoom = 1;
  tree.panX = 0;
  tree.panY = 0;
  tree.error = null;
  tree.statusMessage = 'Дерево очищено локально. Наступна дія піде в backend із порожнім snapshot.';
}

function fillGenerateCount(treeId: string, count: number): void {
  const tree = trees.find((item) => item.id === treeId);
  if (!tree) {
    return;
  }

  tree.generateCount = String(count);
}

function clearAll(): void {
  trees.splice(0, trees.length);
}

function adjustZoom(treeId: string, delta: number): void {
  const tree = trees.find((item) => item.id === treeId);
  if (!tree) {
    return;
  }

  const nextZoom = clampZoom(tree.zoom + delta);
  if (nextZoom <= 1) {
    tree.panX = 0;
    tree.panY = 0;
  }
  tree.zoom = nextZoom;
}

function resetZoom(treeId: string): void {
  const tree = trees.find((item) => item.id === treeId);
  if (!tree) {
    return;
  }

  tree.zoom = 1;
  tree.panX = 0;
  tree.panY = 0;
}

function handleWheelZoom(treeId: string, event: WheelEvent): void {
  adjustZoom(treeId, event.deltaY < 0 ? 0.08 : -0.08);
}

function startPan(treeId: string, event: MouseEvent): void {
  if (event.button !== 0) {
    return;
  }

  const tree = trees.find((item) => item.id === treeId);
  if (!tree || tree.zoom <= 1.01) {
    return;
  }

  const target = event.target as HTMLElement | null;
  if (target?.closest('.tree-node')) {
    return;
  }

  panState.treeId = treeId;
  panState.startClientX = event.clientX;
  panState.startClientY = event.clientY;
  panState.startPanX = tree.panX;
  panState.startPanY = tree.panY;

  event.preventDefault();
  window.addEventListener('mousemove', onPanMove);
  window.addEventListener('mouseup', stopPan);
}

function onPanMove(event: MouseEvent): void {
  if (!panState.treeId) {
    return;
  }

  const tree = trees.find((item) => item.id === panState.treeId);
  if (!tree) {
    return;
  }

  const sensitivity = Math.max(1, tree.zoom * 0.9);
  tree.panX = panState.startPanX + (event.clientX - panState.startClientX) * sensitivity;
  tree.panY = panState.startPanY + (event.clientY - panState.startClientY) * sensitivity;
}

function stopPan(): void {
  panState.treeId = null;
  window.removeEventListener('mousemove', onPanMove);
  window.removeEventListener('mouseup', stopPan);
}

async function runOperation(treeId: string, operation: TreeOperationType): Promise<void> {
  const tree = trees.find((item) => item.id === treeId);
  if (!tree) {
    return;
  }

  const key = Number.parseInt(tree.inputKey.trim(), 10);
  if (Number.isNaN(key)) {
    tree.error = 'Введи ціле число перед запуском операції.';
    return;
  }

  tree.loading = true;
  tree.error = null;
  tree.statusMessage = `Виконуємо ${operation}(${key})...`;

  try {
    const payload: PlaygroundOperationRequest = {
      treeType: tree.treeType,
      operation,
      key,
      root: tree.root
    };

    const response = await runPlaygroundOperation(payload);
    await animateAndApplyResponse(tree, response);
  } catch (error) {
    tree.error = error instanceof Error ? error.message : 'Не вдалося виконати playground-операцію.';
    tree.statusMessage = null;
  } finally {
    tree.loading = false;
  }
}

async function generateTree(treeId: string): Promise<void> {
  const tree = trees.find((item) => item.id === treeId);
  if (!tree) {
    return;
  }

  const count = Number.parseInt(tree.generateCount.trim(), 10);
  if (Number.isNaN(count) || count <= 0) {
    tree.error = 'Введи додатне число N для генерації дерева.';
    return;
  }

  tree.loading = true;
  tree.error = null;
  tree.animationToken += 1;
  tree.activeKeys = new Set<number>();
  tree.statusMessage = `Генеруємо ${count} унікальних чисел для ${formatTreeType(tree.treeType)}...`;

  try {
    let currentRoot: PlaygroundTreeNode | null = null;
    let lastResponse: PlaygroundOperationResponse | null = null;
    const values = generateUniqueValues(count);

    for (const value of values) {
      const payload: PlaygroundOperationRequest = {
        treeType: tree.treeType,
        operation: 'INSERT',
        key: value,
        root: currentRoot
      };

      lastResponse = await runPlaygroundOperation(payload);
      currentRoot = lastResponse.snapshot.root ?? null;
    }

    if (!lastResponse) {
      tree.statusMessage = 'Генерація не виконалась.';
      return;
    }

    tree.root = currentRoot;
    tree.layout = layoutTree(currentRoot);
    tree.metrics = lastResponse.metrics;
    tree.steps = [
      {
        type: 'bulk_generate',
        nodeKey: null,
        metadata: {
          count,
          minValue: Math.min(...values),
          maxValue: Math.max(...values)
        }
      }
    ];
    tree.statusMessage = `Згенеровано дерево з ${count} унікальних чисел. Тепер можна тестувати search/delete/insert.`;
  } catch (error) {
    tree.error = error instanceof Error ? error.message : 'Не вдалося згенерувати дерево.';
    tree.statusMessage = null;
  } finally {
    tree.loading = false;
  }
}

function applyResponse(tree: PlaygroundTreeCard, response: PlaygroundOperationResponse): void {
  tree.root = response.snapshot.root ?? null;
  tree.layout = layoutTree(tree.root);
  tree.metrics = response.metrics;
  tree.steps = response.steps;
  tree.activeKeys = new Set<number>();
  tree.statusMessage = describeResponse(response);
}

async function animateAndApplyResponse(
  tree: PlaygroundTreeCard,
  response: PlaygroundOperationResponse
): Promise<void> {
  if (tree.layout.nodes.length > 100) {
    applyResponse(tree, response);
    tree.statusMessage = `${describeResponse(response)} Анімацію вимкнено для дерев понад 100 вузлів.`;
    return;
  }

  tree.animationToken += 1;
  const animationToken = tree.animationToken;

  const visitSteps = response.steps.filter(
    (step) => step.type === 'visit_node' && typeof step.nodeKey === 'number'
  );

  if (visitSteps.length === 0) {
    applyResponse(tree, response);
    return;
  }

  tree.steps = response.steps;
  tree.statusMessage = buildAnimationMessage(response, visitSteps.length);
  tree.activeKeys = new Set<number>();

  for (const step of visitSteps) {
    if (tree.animationToken !== animationToken || typeof step.nodeKey !== 'number') {
      return;
    }

    tree.activeKeys = new Set<number>([step.nodeKey]);
    await delay(response.operation === 'SEARCH' ? 420 : 340);
  }

  if (tree.animationToken !== animationToken) {
    return;
  }

  applyResponse(tree, response);
}

function describeResponse(response: PlaygroundOperationResponse): string {
  if (response.operation === 'INSERT') {
    return response.found
      ? `${response.inputKey} уже існував, тому дерево лишилось без дубліката.`
      : `${response.inputKey} вставлено, snapshot оновлено.`;
  }

  if (response.operation === 'DELETE') {
    return response.found
      ? `${response.inputKey} видалено з дерева.`
      : `${response.inputKey} не знайдено, структура не змінилася.`;
  }

  return response.found
    ? `${response.inputKey} знайдено.${response.changed ? ' Для Splay структура могла змінитися.' : ''}`
    : `${response.inputKey} не знайдено.`;
}

function buildAnimationMessage(response: PlaygroundOperationResponse, stepCount: number): string {
  if (response.operation === 'SEARCH') {
    return `Анімація пошуку: проходимо ${stepCount} вузл${pluralizeStep(stepCount)} перед фінальним результатом.`;
  }

  if (response.operation === 'DELETE') {
    return `Анімація видалення: спочатку показуємо шлях пошуку з ${stepCount} крок${pluralizeStep(stepCount)}, потім оновлюємо дерево.`;
  }

  return `Анімація проходу: ${stepCount} крок${pluralizeStep(stepCount)} перед оновленням snapshot.`;
}

function pluralizeStep(count: number): string {
  const mod10 = count % 10;
  const mod100 = count % 100;

  if (mod10 === 1 && mod100 !== 11) {
    return '';
  }
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
    return 'и';
  }
  return 'ів';
}

function startDrag(treeId: string, event: MouseEvent): void {
  const tree = trees.find((item) => item.id === treeId);
  const stage = stageRef.value;
  if (!tree || !stage || event.button !== 0) {
    return;
  }

  const rect = stage.getBoundingClientRect();
  dragState.treeId = treeId;
  dragState.pointerOffsetX = event.clientX - rect.left - tree.x;
  dragState.pointerOffsetY = event.clientY - rect.top - tree.y;

  event.preventDefault();
  window.addEventListener('mousemove', onDragMove);
  window.addEventListener('mouseup', stopDrag);
}

function onDragMove(event: MouseEvent): void {
  if (!dragState.treeId) {
    return;
  }

  const tree = trees.find((item) => item.id === dragState.treeId);
  const stage = stageRef.value;
  if (!tree || !stage) {
    return;
  }

  const rect = stage.getBoundingClientRect();
  tree.x = Math.max(12, event.clientX - rect.left - dragState.pointerOffsetX);
  tree.y = Math.max(12, event.clientY - rect.top - dragState.pointerOffsetY);
}

function stopDrag(): void {
  dragState.treeId = null;
  window.removeEventListener('mousemove', onDragMove);
  window.removeEventListener('mouseup', stopDrag);
}

onBeforeUnmount(() => {
  stopDrag();
  stopPan();
});

function formatTreeType(treeType: TreeType): string {
  return {
    avl: 'AVL Tree',
    'red-black': 'Red-Black Tree',
    splay: 'Splay Tree'
  }[treeType];
}

function nodeFill(treeType: TreeType, color?: string | null): string {
  if (treeType === 'red-black') {
    return color === 'RED' ? '#d64545' : '#111827';
  }

  if (treeType === 'splay') {
    return '#d97706';
  }

  return '#2563eb';
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

function svgTransform(tree: PlaygroundTreeCard): string {
  return `translate(${tree.panX} ${tree.panY}) scale(${tree.zoom})`;
}

function delay(ms: number): Promise<void> {
  return new Promise((resolve) => {
    window.setTimeout(resolve, ms);
  });
}

function generateUniqueValues(count: number): number[] {
  const values = new Set<number>();

  while (values.size < count) {
    values.add(Math.floor(Math.random() * Math.max(count * 20, 100)));
  }

  return Array.from(values);
}

function clampZoom(value: number): number {
  return Math.max(0.45, Number(value.toFixed(2)));
}
</script>

<style scoped>
.playground-root {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(56, 189, 248, 0.22), transparent 28%),
    radial-gradient(circle at top right, rgba(245, 158, 11, 0.18), transparent 24%),
    linear-gradient(180deg, #07111f 0%, #0d1726 46%, #101827 100%);
  color: #f8fafc;
}

.playground-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 108px 20px 36px;
}

.playground-hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: end;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: #7dd3fc;
}

.playground-hero h1 {
  margin: 0;
  font-size: clamp(26px, 3vw, 40px);
  line-height: 1.05;
}

.hero-copy {
  max-width: 680px;
  margin: 12px 0 0;
  color: #cbd5e1;
  font-size: 14px;
  line-height: 1.5;
}

.spawn-panel {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: end;
}

.spawn-btn,
.ghost-btn,
.action-btn,
.icon-btn {
  border: 0;
  cursor: pointer;
  transition: transform 0.16s ease, opacity 0.16s ease, background 0.16s ease;
}

.spawn-btn:hover,
.ghost-btn:hover,
.action-btn:hover,
.icon-btn:hover {
  transform: translateY(-1px);
}

.spawn-btn:disabled,
.ghost-btn:disabled,
.action-btn:disabled {
  opacity: 0.6;
  cursor: default;
}

.spawn-btn {
  padding: 14px 18px;
  border-radius: 18px;
  color: #f8fafc;
  font-weight: 700;
}

.spawn-btn.avl {
  background: linear-gradient(135deg, #2563eb, #38bdf8);
}

.spawn-btn.red-black {
  background: linear-gradient(135deg, #7f1d1d, #ef4444);
}

.spawn-btn.splay {
  background: linear-gradient(135deg, #b45309, #f59e0b);
}

.playground-stage {
  border-radius: 28px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(12px);
  padding: 18px;
}

.stage-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.stage-pill {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.14);
  color: #cbd5e1;
  font-size: 13px;
}

.ghost-btn {
  padding: 10px 14px;
  border-radius: 12px;
  background: rgba(148, 163, 184, 0.12);
  color: #f8fafc;
}

.stage-canvas {
  position: relative;
  min-height: 860px;
  overflow: auto;
  border-radius: 22px;
  background:
    linear-gradient(rgba(148, 163, 184, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px),
    linear-gradient(180deg, rgba(15, 23, 42, 0.45), rgba(15, 23, 42, 0.2));
  background-size: 28px 28px, 28px 28px, auto;
}

.tree-card {
  position: absolute;
  width: 440px;
  border-radius: 24px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(15, 23, 42, 0.92);
  box-shadow: 0 20px 50px rgba(2, 8, 23, 0.35);
  overflow: hidden;
}

.tree-card-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 12px;
  padding: 16px 18px 14px;
  cursor: grab;
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.96), rgba(15, 23, 42, 0.96));
}

.tree-card-header h2 {
  margin: 4px 0 0;
  font-size: 18px;
}

.tree-label {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7dd3fc;
}

.icon-btn {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: rgba(148, 163, 184, 0.12);
  color: #f8fafc;
  font-size: 22px;
}

.tree-card-controls {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  gap: 10px;
  padding: 16px 18px 0;
}

.number-input {
  min-width: 0;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: 12px;
  background: rgba(15, 23, 42, 0.7);
  color: #f8fafc;
  padding: 12px 14px;
  font-size: 15px;
}

.action-group {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 0 14px;
  border-radius: 12px;
  background: #1d4ed8;
  color: #f8fafc;
  font-weight: 600;
}

.action-btn.danger {
  background: #b91c1c;
}

.status-message,
.error-message {
  margin: 12px 18px 0;
  font-size: 13px;
}

.generator-row {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  padding: 12px 18px 0;
}

.generator-input {
  width: 92px;
}

.generator-btn {
  min-height: 42px;
}

.zoom-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 18px 0;
}

.zoom-label {
  color: #94a3b8;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.zoom-btn,
.zoom-value {
  min-height: 36px;
}

.zoom-btn {
  min-width: 36px;
  padding: 0 12px;
}

.zoom-value {
  min-width: 72px;
}

.status-message {
  color: #bfdbfe;
}

.error-message {
  color: #fca5a5;
}

.visual-shell {
  margin: 16px 18px 0;
  min-height: 250px;
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background: radial-gradient(circle at top, rgba(30, 41, 59, 0.42), rgba(15, 23, 42, 0.92));
  overflow: auto;
  cursor: default;
}

.visual-shell.pannable {
  cursor: grab;
}

.visual-shell.panning {
  cursor: grabbing;
}

.tree-svg {
  width: 100%;
  min-height: 250px;
}

.tree-edge {
  stroke: rgba(148, 163, 184, 0.75);
  stroke-width: 2;
}

.tree-node {
  stroke: rgba(255, 255, 255, 0.88);
  stroke-width: 2;
  transition: transform 0.18s ease, stroke 0.18s ease;
}

.tree-node.active {
  stroke: #f8fafc;
  stroke-width: 4;
  filter: drop-shadow(0 0 14px rgba(125, 211, 252, 0.7));
}

.tree-node-text {
  fill: #f8fafc;
  font-size: 14px;
  font-weight: 700;
  text-anchor: middle;
}

.empty-tree {
  min-height: 250px;
  display: grid;
  place-items: center;
  text-align: center;
  color: #cbd5e1;
  padding: 18px;
}

.empty-tree small {
  display: block;
  margin-top: 8px;
  color: #94a3b8;
}

.tree-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 14px 18px 0;
  color: #94a3b8;
  font-size: 13px;
}

.steps-panel {
  padding: 16px 18px 18px;
}

.steps-panel h3 {
  margin: 0 0 10px;
  font-size: 14px;
}

.step-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.step-list li {
  padding: 8px 10px;
  border-radius: 12px;
  background: rgba(148, 163, 184, 0.12);
  color: #e2e8f0;
  font-size: 12px;
}

.step-list strong {
  margin-right: 6px;
}

.steps-placeholder {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
}

@media (max-width: 960px) {
  .playground-hero {
    flex-direction: column;
    align-items: start;
  }

  .spawn-panel {
    justify-content: start;
  }

  .tree-card {
    width: min(440px, calc(100vw - 56px));
  }

  .tree-card-controls {
    grid-template-columns: 1fr;
  }

  .generator-row {
    align-items: stretch;
  }

  .zoom-row {
    flex-wrap: wrap;
  }

  .action-group {
    justify-content: stretch;
  }

  .action-group .action-btn {
    flex: 1;
    min-height: 42px;
  }
}
</style>
