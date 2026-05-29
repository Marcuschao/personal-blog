import { createApp } from 'vue';
import { createHead } from '@vueuse/head';
import { createPinia } from 'pinia';
import { registerSW } from 'virtual:pwa-register';
import App from './App.vue';
import router from './router';
import 'vfonts/Lato.css';
import 'vfonts/FiraCode.css';
import './assets/styles/global.css';
import { stripTrailingSlashInBrowserUrl } from './utils/url';

const app = createApp(App);
const pinia = createPinia();
const head = createHead();

app.use(pinia);
app.use(router);
app.use(head);

router.isReady().then(() => {
  stripTrailingSlashInBrowserUrl();
});

app.mount('#app');

registerSW({
  onNeedRefresh() {
    if (confirm('新版本可用，是否立即刷新？')) {
      location.reload();
    }
  },
});
