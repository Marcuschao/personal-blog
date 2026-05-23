<template>
  <div class="about-page ds-page">
    <div class="container" style="max-width: 720px;">
      <header class="ds-page-hero" style="margin-bottom: 24px;">
        <h1 class="ds-page-title ds-page-title-md">关于我</h1>
      </header>

      <div v-if="loading">
        <n-card>
          <n-space vertical :size="12">
            <n-skeleton height="20px" width="100%" :sharp="false" />
            <n-skeleton height="20px" width="90%" :sharp="false" />
            <n-skeleton height="20px" width="60%" :sharp="false" />
            <n-skeleton height="20px" width="100%" :sharp="false" />
            <n-skeleton height="20px" width="70%" :sharp="false" />
          </n-space>
        </n-card>
      </div>

      <n-card v-else class="about-content-card">
        <div class="markdown-renderer markdown-prose">
          <MarkdownRenderer :markdown="aboutContent" />
        </div>
      </n-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { NCard, NSkeleton, NSpace } from 'naive-ui';
import { getAboutContent } from '../api/about';
import MarkdownRenderer from '../components/MarkdownRenderer.vue';

const aboutContent = ref('');
const loading = ref(true);

const fetchAboutContent = async () => {
  loading.value = true;
  try {
    const response = await getAboutContent();
    aboutContent.value = response.data.content;
  } catch (error) {
    console.error('Failed to fetch about content:', error);
    aboutContent.value = '加载失败，请稍后再试。';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchAboutContent();
});
</script>

<style scoped>
</style>
