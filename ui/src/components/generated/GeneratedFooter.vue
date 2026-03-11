<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div v-for="block in footerBlocks" :key="block.title" class="footer-section">
          <h4><i :class="block.iconClass"></i> {{ block.title }}</h4>
          <p v-for="line in block.lines" :key="line">{{ line }}</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 Adaptive Tree Analysis. Built for educational purposes.</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { footerBlocks } from '../../constants/generatedContent';
</script>
