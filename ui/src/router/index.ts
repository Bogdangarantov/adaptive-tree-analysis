import { createRouter, createWebHistory } from 'vue-router';
import GeneratedUiView from '../views/GeneratedUiView.vue';
import BenchmarkView from '../views/BenchmarkView.vue';

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
      component: GeneratedUiView,
      props: {
        targetSection: 'demo'
      }
    },
    {
      path: '/benchmark',
      name: 'benchmark',
      component: BenchmarkView
    }
  ]
});

export default router;
