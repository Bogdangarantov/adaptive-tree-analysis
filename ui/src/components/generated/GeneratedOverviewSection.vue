<template>
  <section class="section overview-section" id="overview">
    <div class="container">
      <h2 class="section-title fade-in">What is Adaptive Tree Analysis?</h2>
      <p class="section-description fade-in">
        Adaptive self-balancing trees such as AVL, Red-Black and extensible trees dynamically maintain structure to ensure optimal performance across operations. This analysis focuses on comparing these adaptive search trees.
      </p>

      <div class="cards-grid">
        <div
          v-for="card in overviewCards"
          :key="card.title"
          class="glass-card fade-in"
          :style="{ animationDelay: card.delay }"
        >
          <div class="card-icon" :style="{ background: card.gradient }">
            <i :class="card.iconClass"></i>
          </div>
          <h3>{{ card.title }}</h3>
          <p>{{ card.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { overviewCards } from '../../constants/generatedContent';
</script>
