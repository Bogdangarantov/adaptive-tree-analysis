<template>
  <div class="landing-root">
    <canvas id="particleCanvas"></canvas>
    <Navbar />
    <HeroSection />
    <OverviewSection />
    <VisualizationSection />
    <ChartsSection />
    <BenchmarkSummarySection />
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, watch } from 'vue';
import Navbar from '../components/landing/Navbar.vue';
import HeroSection from '../components/landing/HeroSection.vue';
import OverviewSection from '../components/landing/OverviewSection.vue';
import VisualizationSection from '../components/landing/VisualizationSection.vue';
import ChartsSection from '../components/landing/ChartsSection.vue';
import BenchmarkSummarySection from '../components/landing/BenchmarkSummarySection.vue';
import Footer from '../components/landing/Footer.vue';
import { useLandingScripts } from '../composables/useLandingScripts';

const props = defineProps<{
  targetSection?: string | null;
}>();

onMounted(async () => {
  await useLandingScripts();
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
.landing-root {
  position: relative;
}
</style>
