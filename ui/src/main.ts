import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './assets/styles.css';
import './assets/landing.css';

createApp(App).use(router).mount('#app');
