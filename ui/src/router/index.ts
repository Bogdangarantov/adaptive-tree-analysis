import { createRouter, createWebHistory } from 'vue-router';
import GeneratedUiView from '../views/GeneratedUiView.vue';
import BenchmarkView from '../views/BenchmarkView.vue';
import PlaygroundView from '../views/PlaygroundView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: GeneratedUiView,
      props: {
        targetSection: null
      }
    },
    {
      path: '/playground',
      name: 'playground',
      component: PlaygroundView
    },
    {
      path: '/benchmark',
      name: 'benchmark',
      component: BenchmarkView
    }
  ]
});

export default router;
