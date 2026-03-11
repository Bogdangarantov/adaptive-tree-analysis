<template>
  <section class="panel">
    <h1>Playground</h1>
    <p>Тут буде інтерактивна покрокова візуалізація операцій над деревами.</p>
    <div class="controls">
      <button class="btn" :disabled="isLoading" @click="runInsert">
        {{ isLoading ? 'Виконання...' : 'Запустити INSERT(42)' }}
      </button>
      <span v-if="errorMessage" class="error">{{ errorMessage }}</span>
    </div>
  </section>

  <section class="panel">
    <h2>Область візуалізації</h2>
    <svg class="canvas" viewBox="0 0 960 420" role="img" aria-label="Tree visualization placeholder">
      <circle cx="480" cy="80" r="24" />
      <circle cx="360" cy="180" r="24" />
      <circle cx="600" cy="180" r="24" />
      <line x1="468" y1="95" x2="378" y2="165" />
      <line x1="492" y1="95" x2="582" y2="165" />
    </svg>
  </section>

  <section v-if="lastResponse" class="panel">
    <h2>Остання відповідь API</h2>
    <p>Операція: {{ lastResponse.operation }} | Ключ: {{ lastResponse.inputKey }}</p>
    <p>Кроків: {{ lastResponse.steps.length }} | Час: {{ lastResponse.metrics.executionTimeNs }} ns</p>
    <pre class="response">{{ formattedResponse }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { executeAvlOperation } from '../api/client';
import type { AvlOperationResponse } from '@dto/api-types';

const isLoading = ref(false);
const errorMessage = ref('');
const lastResponse = ref<AvlOperationResponse | null>(null);

const formattedResponse = computed(() => JSON.stringify(lastResponse.value, null, 2));

async function runInsert(): Promise<void> {
  isLoading.value = true;
  errorMessage.value = '';

  try {
    lastResponse.value = await executeAvlOperation({ operation: 'INSERT', key: 42 });
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Unknown error';
    errorMessage.value = `Помилка запиту: ${message}`;
  } finally {
    isLoading.value = false;
  }
}
</script>
