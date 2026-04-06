<template>
  <section class="section overview-section" id="overview">
    <div class="container">
      <h2 class="section-title fade-in">Що таке адаптивний аналіз дерев?</h2>
      <p class="section-description fade-in">
        Самобалансувальні дерева, такі як AVL, Red-Black і Splay, динамічно підтримують структуру, щоб
        забезпечити стабільну продуктивність для основних операцій. Ця платформа зосереджена на порівнянні
        цих структур пошуку, їхніх властивостей і практичної поведінки.
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
import { overviewCards } from '../../constants/landingContent';
</script>
