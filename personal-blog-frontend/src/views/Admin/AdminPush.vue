<template>
  <div class="admin-push-page admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">推送管理</h1>
          <p class="ds-page-sub">订阅数与测试推送（后端需 blog.push.enabled 与 VAPID）</p>
        </div>
        <router-link to="/admin">
          <n-button>返回文章管理</n-button>
        </router-link>
      </header>

      <n-card class="push-panel" style="max-width: 36rem;">
        <p style="font-size: 1.1em; margin-bottom: 24px;">
          当前 Push 订阅数：<strong>{{ subscriptionCount }}</strong>
        </p>

        <n-form-item label="按文章推送" style="margin-bottom: 20px;">
          <n-space style="width: 100%">
            <n-select
              v-model:value="articleId"
              :options="articleOptions"
              placeholder="请选择文章"
              style="width: 240px;"
            />
            <n-button
              type="primary"
              :disabled="articleId == null || sending"
              @click="sendByArticle"
            >
              发送
            </n-button>
          </n-space>
        </n-form-item>

        <n-button
          secondary
          :disabled="sending"
          @click="sendTest"
        >
          发送测试通知
        </n-button>
      </n-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { NButton, NCard, NFormItem, NSelect, NSpace } from 'naive-ui';
import { useArticleStore } from '../../stores/article';
import { fetchPushStats, adminSendPush } from '../../api/push';
import { appPublicUrl } from '../../utils/appUrl';

const articleStore = useArticleStore();
const articles = ref([]);
const subscriptionCount = ref('…');
const articleId = ref(null);
const sending = ref(false);

const articleOptions = computed(() => {
  return articles.value.map(a => ({
    label: a.title,
    value: a.id,
  }));
});

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
</style>
