<template>
  <section class="hero" id="hero">
    <div class="hero-content">
      <div class="hero-text">
        <h1 class="hero-title">
          <span class="gradient-text">Adaptive Tree Analysis</span>
        </h1>
        <p class="hero-subtitle">Exploring Self-Balancing Data Structures for Optimal Algorithm Efficiency</p>
        <button class="cta-button" @click="callGlobalWithArg('scrollToSection', 'overview')">
          Explore Analysis <i class="fas fa-arrow-right"></i>
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
