<template>
  <div class="generated-root">
    <canvas id="particleCanvas"></canvas>
    <GeneratedNavbar />
    <GeneratedHeroSection />
    <GeneratedOverviewSection />
    <GeneratedVisualizationSection />
    <GeneratedChartsSection />
    <GeneratedDemoSection />
    <GeneratedFooter />
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, watch } from 'vue';
import GeneratedNavbar from '../components/generated/GeneratedNavbar.vue';
import GeneratedHeroSection from '../components/generated/GeneratedHeroSection.vue';
import GeneratedOverviewSection from '../components/generated/GeneratedOverviewSection.vue';
import GeneratedVisualizationSection from '../components/generated/GeneratedVisualizationSection.vue';
import GeneratedChartsSection from '../components/generated/GeneratedChartsSection.vue';
import GeneratedDemoSection from '../components/generated/GeneratedDemoSection.vue';
import GeneratedFooter from '../components/generated/GeneratedFooter.vue';
import { useGeneratedScripts } from '../composables/useGeneratedScripts';

const props = defineProps<{
  targetSection?: string | null;
}>();

onMounted(async () => {
  await useGeneratedScripts();
  await scrollToTargetSection(props.targetSection);
});

watch(
  () => props.targetSection,
  async (targetSection) => {
    await scrollToTargetSection(targetSection);
  }
);

async function scrollToTargetSection(targetSection?: string | null): Promise<void> {
  if (!targetSection) {
    window.scrollTo({ top: 0, behavior: 'auto' });
    return;
  }

  await nextTick();
  window.requestAnimationFrame(() => {
    const element = document.getElementById(targetSection);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  });
}
</script>

<style>
.generated-root {
  position: relative;
}
</style>
