<template>
  <nav class="navbar">
    <div class="nav-container">
      <div class="nav-logo">
        <i class="fas fa-project-diagram"></i>
        <span>Adaptive Trees</span>
      </div>
      <ul class="nav-menu">
        <li><a href="#overview">Overview</a></li>
        <li><a href="#visualization">Visualization</a></li>
        <li><a href="#charts">Performance</a></li>
        <li><a href="#demo">Demo</a></li>
        <li><RouterLink to="/benchmark">Benchmark</RouterLink></li>
      </ul>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
</script>
