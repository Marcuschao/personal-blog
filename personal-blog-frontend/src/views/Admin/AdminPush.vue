<template>
  <div class="admin-push-page admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header">
        <div>
          <h1 class="ds-page-title">推送管理</h1>
          <p class="ds-page-sub">订阅数与测试推送（后端需 blog.push.enabled 与 VAPID）</p>
        </div>
        <router-link to="/admin" class="ds-btn ds-btn--secondary ds-btn--pill">返回文章管理</router-link>
      </header>

      <section class="ds-empty-panel push-panel">
        <p>当前 Push 订阅数：<strong>{{ subscriptionCount }}</strong></p>

        <div class="field-row">
          <label class="field-label" for="push-article">按文章推送</label>
          <select id="push-article" v-model.number="articleId" class="ds-input push-select">
            <option :value="null">请选择文章</option>
            <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.title }}</option>
          </select>
          <button
            type="button"
            class="ds-btn ds-btn--primary"
            :disabled="articleId == null || sending"
            @click="sendByArticle"
          >
            发送
          </button>
        </div>

        <div class="field-row">
          <button
            type="button"
            class="ds-btn ds-btn--secondary"
            :disabled="sending"
            @click="sendTest"
          >
            发送测试通知
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useArticleStore } from '../../stores/article';
import { fetchPushStats, adminSendPush } from '../../api/push';
import { appPublicUrl } from '../../utils/appUrl';

const articleStore = useArticleStore();
const articles = ref([]);
const subscriptionCount = ref('…');
const articleId = ref(null);
const sending = ref(false);

async function loadStats() {
  try {
    const s = await fetchPushStats();
    subscriptionCount.value = String(s.subscriptionCount ?? 0);
  } catch {
    subscriptionCount.value = '?';
  }
}

async function loadArticles() {
  await articleStore.fetchArticles(1, 500, null);
  articles.value = articleStore.articles;
}

async function sendByArticle() {
  if (articleId.value == null) return;
  sending.value = true;
  try {
    await adminSendPush({ articleId: articleId.value });
  } finally {
    sending.value = false;
  }
}

async function sendTest() {
  sending.value = true;
  try {
    await adminSendPush({
      title: '测试通知',
      body: '这是一条测试推送',
      url: appPublicUrl('/'),
    });
  } finally {
    sending.value = false;
  }
}

onMounted(() => {
  loadStats();
  loadArticles();
});
</script>

<style scoped>
.push-panel {
  text-align: left;
  max-width: 36rem;
}

.field-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--space-3);
  margin-top: var(--space-4);
}

.field-label {
  flex: 0 0 100%;
  font-weight: 650;
  font-size: var(--text-sm);
}

.push-select {
  flex: 1 1 220px;
  min-width: 0;
}
</style>
