import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const base = env.VITE_APP_BASE_URL || '/';
  return {
    base: base.endsWith('/') ? base : `${base}/`,
    plugins: [vue()],
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/upload': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/rss.xml': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/sitemap.xml': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/robots.txt': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
  };
});
