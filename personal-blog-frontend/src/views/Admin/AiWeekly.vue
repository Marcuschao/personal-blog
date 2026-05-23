<template>
  <div class="ai-weekly-page admin-page">
    <div class="container">
      <header class="weekly-head ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">AI 周报</h1>
          <p class="ds-page-sub">本周热门文章摘要与写作建议</p>
        </div>
        <n-space class="dash-actions" :size="12">
          <router-link to="/admin">
            <n-button>返回管理</n-button>
          </router-link>
          <n-button type="primary" :loading="loading" @click="run">
            一键生成
          </n-button>
        </n-space>
      </header>

      <n-alert v-if="error" type="error" style="margin-bottom: 16px;">{{ error }}</n-alert>

      <n-card v-if="body" class="weekly-body">
        <div class="markdown-renderer markdown-prose">
          <MarkdownRenderer :markdown="body" />
        </div>
      </n-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { NAlert, NButton, NCard, NSpace } from 'naive-ui';
import MarkdownRenderer from '../../components/MarkdownRenderer.vue';
import { agentWeeklyReport } from '../../api/agent';

const loading = ref(false);
const error = ref('');
const body = ref('');

async function run() {
  loading.value = true;
  error.value = '';
  try {
    body.value = await agentWeeklyReport({});
  } catch (e) {
    error.value = e?.message || '生成失败';
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
</style>
