<template>
  <section class="hero" id="hero">
    <div class="hero-content">
      <div class="hero-text">
        <h1 class="hero-title">
          <span class="gradient-text">Адаптивний аналіз дерев</span>
        </h1>
        <p class="hero-subtitle">Дослідження самобалансувальних структур даних для ефективних алгоритмів</p>
        <button class="cta-button" @click="scrollToOverview">
          Переглянути аналіз <i class="fas fa-arrow-right"></i>
        </button>
      </div>
      <div class="hero-tree">
        <svg id="heroTreeSVG" width="300" height="300" viewBox="0 0 300 300" aria-hidden="true">
          <defs>
            <linearGradient id="heroTreeGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stop-color="#667eea" />
              <stop offset="100%" stop-color="#764ba2" />
            </linearGradient>
          </defs>
          <g stroke="#00d4ff" stroke-width="2" opacity="0.65">
            <line x1="150" y1="50" x2="85" y2="120" />
            <line x1="150" y1="50" x2="215" y2="120" />
            <line x1="85" y1="120" x2="45" y2="190" />
            <line x1="85" y1="120" x2="125" y2="190" />
            <line x1="215" y1="120" x2="175" y2="190" />
            <line x1="215" y1="120" x2="255" y2="190" />
          </g>
          <g v-for="node in nodes" :key="node.value" class="hero-node" :style="{ animationDelay: node.delay }">
            <circle :cx="node.x" :cy="node.y" r="20" fill="url(#heroTreeGradient)" stroke="#00d4ff" stroke-width="2" />
            <text :x="node.x" :y="node.y + 5" text-anchor="middle" fill="#ffffff" font-size="14" font-weight="700">
              {{ node.value }}
            </text>
          </g>
        </svg>
      </div>
    </div>
    <div class="scroll-indicator">
      <i class="fas fa-chevron-down"></i>
    </div>
  </section>
</template>

<script setup lang="ts">
const nodes = [
  { x: 150, y: 50, value: 50, delay: '0ms' },
  { x: 85, y: 120, value: 30, delay: '120ms' },
  { x: 215, y: 120, value: 70, delay: '220ms' },
  { x: 45, y: 190, value: 20, delay: '320ms' },
  { x: 125, y: 190, value: 40, delay: '420ms' },
  { x: 175, y: 190, value: 60, delay: '520ms' },
  { x: 255, y: 190, value: 80, delay: '620ms' }
];

function scrollToOverview(): void {
  document.getElementById('overview')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}
</script>

<style scoped>
.hero-node {
  opacity: 0;
  animation: heroNodeAppear 0.55s ease forwards;
}

@keyframes heroNodeAppear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
