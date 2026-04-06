import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import BenchmarkView from '../views/BenchmarkView.vue';
import PlaygroundView from '../views/PlaygroundView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
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
