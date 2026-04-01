<template>
  <div class="generated-root">
    <GeneratedNavbar />
    <GeneratedHeroSection />
    <GeneratedOverviewSection />
    <GeneratedVisualizationSection />
    <GeneratedChartsSection />
    <GeneratedBenchmarkSummarySection />
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
import GeneratedBenchmarkSummarySection from '../components/generated/GeneratedBenchmarkSummarySection.vue';
import GeneratedFooter from '../components/generated/GeneratedFooter.vue';

const props = defineProps<{
  targetSection?: string | null;
}>();

onMounted(async () => {
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
  overflow: clip;
}

.generated-root::before,
.generated-root::after {
  content: '';
  position: fixed;
  inset: auto;
  pointer-events: none;
  z-index: 0;
  filter: blur(60px);
}

.generated-root::before {
  top: 120px;
  left: -80px;
  width: 280px;
  height: 280px;
  background: rgba(0, 212, 255, 0.16);
}

.generated-root::after {
  top: 420px;
  right: -100px;
  width: 340px;
  height: 340px;
  background: rgba(124, 58, 237, 0.12);
}
</style>
