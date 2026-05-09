<template>
  <div class="tags-page">
    <div class="container">
      <header class="page-hero">
        <h1 class="page-title">所有标签</h1>
        <p class="page-sub">点击查看该标签下的文章</p>
      </header>

      <div v-if="articleStore.tags.length" class="tag-cloud-panel">
        <router-link
          v-for="(tag, idx) in articleStore.tags"
          :key="tag.id"
          :to="{ path: '/', query: { tag: tag.id } }"
          class="tag-cloud-item"
          :style="{ '--enter-delay': `${Math.min(idx, 14) * 52}ms` }"
        >
          {{ tag.name }}
        </router-link>
      </div>
      <div v-else class="no-tags">
        <p>暂无标签。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useArticleStore } from '../stores/article';

const articleStore = useArticleStore();

onMounted(() => {
  articleStore.fetchTags();
});
</script>

<style scoped>
.tags-page {
  padding: 2.5rem 0 3.75rem;
}

.page-hero {
  text-align: center;
  margin-bottom: 2.25rem;
}

.page-title {
  margin: 0;
  font-size: clamp(2rem, 4.6vw, 2.65rem);
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.page-sub {
  margin: 0.65rem 0 0;
  color: var(--color-text-muted);
}

.tag-cloud-panel {
  background: var(--color-surface-glass);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: clamp(1.75rem, 4vw, 2.75rem);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md);
  text-align: center;
  max-width: 880px;
  margin: 0 auto;
}

.tag-cloud-item {
  display: inline-block;
  margin: 0.4rem;
  padding: 0.55rem 1.15rem;
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--color-primary-hover);
  text-decoration: none;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(79, 70, 229, 0.15);
  box-shadow: var(--shadow-xs);
  animation: tag-pop-in 0.55s var(--ease-out-soft) backwards;
  animation-delay: var(--enter-delay, 0ms);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast),
    border-color var(--transition-fast), color var(--transition-fast),
    background var(--transition-fast);
}

.tag-cloud-item:hover {
  transform: scale(1.06) translateY(-2px);
  background: var(--gradient-cta);
  border-color: transparent;
  color: #fff;
  box-shadow: var(--shadow-hover);
}

.no-tags {
  text-align: center;
  padding: 3rem 1.5rem;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  color: var(--color-text-muted);
  max-width: 560px;
  margin: 0 auto;
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
