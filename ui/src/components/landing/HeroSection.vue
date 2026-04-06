<template>
  <section class="hero" id="hero">
    <div class="hero-content">
      <div class="hero-text">
        <h1 class="hero-title">
          <span class="gradient-text">Адаптивний аналіз дерев</span>
        </h1>
        <p class="hero-subtitle">Дослідження самобалансувальних структур даних для ефективних алгоритмів</p>
        <button class="cta-button" @click="callGlobalWithArg('scrollToSection', 'overview')">
          Переглянути аналіз <i class="fas fa-arrow-right"></i>
        </button>
      </div>
      <div class="hero-tree">
        <svg id="heroTreeSVG" width="300" height="300" viewBox="0 0 300 300"></svg>
      </div>
    </div>
    <div class="scroll-indicator">
      <i class="fas fa-chevron-down"></i>
    </div>
  </section>
</template>

<script setup lang="ts">
import { callGlobalWithArg } from '../../composables/useGlobalFunction';
</script>
