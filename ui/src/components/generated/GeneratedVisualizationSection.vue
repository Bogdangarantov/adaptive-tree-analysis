<template>
  <section class="section viz-section" id="visualization">
    <div class="container">
      <h2 class="section-title fade-in">Візуалізації дерев</h2>
      <p class="section-description fade-in">Інтерактивні візуальні представлення різних деревних структур</p>

      <div class="viz-grid">
        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-balance-scale"></i> AVL-дерево</h3>
          <svg class="tree-preview" viewBox="0 0 500 260" aria-hidden="true">
            <g stroke="#10b981" stroke-width="2.5" opacity="0.8">
              <line x1="250" y1="42" x2="160" y2="112" />
              <line x1="250" y1="42" x2="340" y2="112" />
              <line x1="160" y1="112" x2="100" y2="188" />
              <line x1="160" y1="112" x2="220" y2="188" />
              <line x1="340" y1="112" x2="400" y2="188" />
            </g>
            <g v-for="node in avlNodes" :key="`avl-${node.value}`">
              <circle :cx="node.x" :cy="node.y" r="20" fill="#1d4f45" stroke="#10b981" stroke-width="2.5" />
              <text :x="node.x" :y="node.y + 5" text-anchor="middle" fill="#ffffff" font-size="15" font-weight="700">
                {{ node.value }}
              </text>
              <text :x="node.x" :y="node.y + 42" text-anchor="middle" fill="#6ee7b7" font-size="12" font-weight="700">
                BF {{ node.balance }}
              </text>
            </g>
          </svg>
          <p class="viz-description">Самобалансування з відображенням факторів балансу</p>
        </div>

        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-circle"></i> Червоно-чорне дерево</h3>
          <svg class="tree-preview" viewBox="0 0 500 260" aria-hidden="true">
            <g stroke="#f5576c" stroke-width="2.5" opacity="0.8">
              <line x1="250" y1="42" x2="160" y2="112" />
              <line x1="250" y1="42" x2="340" y2="112" />
              <line x1="160" y1="112" x2="100" y2="188" />
              <line x1="160" y1="112" x2="220" y2="188" />
              <line x1="340" y1="112" x2="280" y2="188" />
              <line x1="340" y1="112" x2="400" y2="188" />
            </g>
            <g v-for="node in rbNodes" :key="`rb-${node.value}`">
              <circle
                :cx="node.x"
                :cy="node.y"
                r="20"
                :fill="node.color === 'red' ? '#8b1e31' : '#111827'"
                :stroke="node.color === 'red' ? '#f5576c' : '#cbd5e1'"
                stroke-width="2.5"
              />
              <text :x="node.x" :y="node.y + 5" text-anchor="middle" fill="#ffffff" font-size="15" font-weight="700">
                {{ node.value }}
              </text>
            </g>
          </svg>
          <p class="viz-description">Кольорове кодування вузлів із дотриманням red-black інваріантів</p>
        </div>

        <div class="viz-card glass-card fade-in">
          <h3><i class="fas fa-layer-group"></i> Splay-дерево</h3>
          <svg class="tree-preview" viewBox="0 0 500 260" aria-hidden="true">
            <g stroke="#00d4ff" stroke-width="2.5" opacity="0.8">
              <line x1="250" y1="42" x2="175" y2="112" />
              <line x1="175" y1="112" x2="115" y2="188" />
              <line x1="250" y1="42" x2="325" y2="112" />
              <line x1="325" y1="112" x2="385" y2="188" />
            </g>
            <g v-for="node in splayNodes" :key="`splay-${node.value}`">
              <circle :cx="node.x" :cy="node.y" r="20" fill="#253255" stroke="#00d4ff" stroke-width="2.5" />
              <text :x="node.x" :y="node.y + 5" text-anchor="middle" fill="#ffffff" font-size="15" font-weight="700">
                {{ node.value }}
              </text>
            </g>
          </svg>
          <p class="viz-description">Самоналаштовувана структура, що підтягує нещодавно використані вузли до кореня.</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const avlNodes = [
  { x: 250, y: 42, value: 50, balance: 0 },
  { x: 160, y: 112, value: 30, balance: 0 },
  { x: 340, y: 112, value: 70, balance: -1 },
  { x: 100, y: 188, value: 20, balance: 0 },
  { x: 220, y: 188, value: 40, balance: 0 },
  { x: 400, y: 188, value: 80, balance: 0 }
];

const rbNodes = [
  { x: 250, y: 42, value: 40, color: 'black' },
  { x: 160, y: 112, value: 20, color: 'red' },
  { x: 340, y: 112, value: 60, color: 'red' },
  { x: 100, y: 188, value: 10, color: 'black' },
  { x: 220, y: 188, value: 30, color: 'black' },
  { x: 280, y: 188, value: 50, color: 'black' },
  { x: 400, y: 188, value: 70, color: 'black' }
];

const splayNodes = [
  { x: 250, y: 42, value: 60 },
  { x: 175, y: 112, value: 40 },
  { x: 325, y: 112, value: 75 },
  { x: 115, y: 188, value: 20 },
  { x: 385, y: 188, value: 90 }
];
</script>

<style scoped>
.tree-preview {
  width: 100%;
  height: 260px;
  display: block;
}
</style>
