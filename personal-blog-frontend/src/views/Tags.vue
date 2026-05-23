<template>
  <div class="tags-page ds-page">
    <div class="container">
      <header class="ds-page-hero">
        <h1 class="ds-page-title ds-page-title-md">所有标签</h1>
        <p class="ds-page-sub">点击查看该标签下的文章</p>
      </header>

      <n-card v-if="articleStore.tags.length" class="tag-cloud-card">
        <div class="tag-cloud-panel">
          <router-link
            v-for="(tag, idx) in articleStore.tags"
            :key="tag.id"
            :to="{ path: '/', query: { tag: tag.id } }"
            class="tag-cloud-link"
          >
            <n-tag
              type="primary"
              :bordered="true"
              size="large"
              class="tag-cloud-item"
              :style="{ '--enter-delay': `${Math.min(idx, 14) * 52}ms` }"
            >
              {{ tag.name }}
            </n-tag>
          </router-link>
        </div>
      </n-card>
      <n-empty v-else description="暂无标签" />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { NCard, NEmpty, NTag } from 'naive-ui';
import { useArticleStore } from '../stores/article';

const articleStore = useArticleStore();

onMounted(() => {
  articleStore.fetchTags();
});
</script>

<style scoped>
.tag-cloud-card {
  max-width: 880px;
  margin: 0 auto;
}

.tag-cloud-panel {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-4);
}

.tag-cloud-link {
  text-decoration: none;
}

.tag-cloud-item {
  cursor: pointer;
  animation: tag-pop-in 0.55s var(--ease-out-soft) backwards;
  animation-delay: var(--enter-delay, 0ms);
  transition: transform var(--transition-fast);
}

.tag-cloud-item:hover {
  transform: translateY(-2px);
}

@media (prefers-reduced-motion: reduce) {
  .tag-cloud-item {
    animation: none;
  }
  .tag-cloud-item:hover {
    transform: none;
  }
}
</style>
