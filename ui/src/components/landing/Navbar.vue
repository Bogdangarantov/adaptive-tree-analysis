<template>
  <nav class="navbar">
    <div class="nav-container">
      <RouterLink class="nav-logo" to="/">
        <i class="fas fa-project-diagram"></i>
        <span>Adaptive Trees</span>
      </RouterLink>
      <ul class="nav-menu">
        <li><RouterLink to="/">Головна</RouterLink></li>
        <li><RouterLink to="/playground">Пісочниця</RouterLink></li>
        <li><RouterLink to="/benchmark">Бенчмарк</RouterLink></li>
      </ul>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
</script>
