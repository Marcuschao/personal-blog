<template>
  <div class="home-page">
    <div class="container">
      <header class="home-hero">
        <h1 class="page-title">最新文章</h1>
        <p class="page-sub">探索想法、笔记与技术碎片</p>
      </header>

      <div class="tag-filter">
        <span
          v-for="tag in displayedHomeTags"
          :key="tag.id"
          :class="['tag-item', { active: currentTagId === tag.id }]"
          @click="filterByTag(tag.id)"
        >
          {{ tag.name }}
        </span>
        <span
          class="tag-item tag-all"
          :class="{ active: currentTagId === null }"
          @click="filterByTag(null)"
        >
          全部
        </span>
      </div>

      <div v-if="listingLoading" class="article-list skeleton-grid">
        <div v-for="n in 6" :key="'sk-' + n" class="card-skeleton">
          <div class="ui-skeleton sk-title" />
          <div class="ui-skeleton sk-line" />
          <div class="ui-skeleton sk-line short" />
          <div class="sk-meta">
            <div class="ui-skeleton sk-dot" />
            <div class="ui-skeleton sk-chip" />
          </div>
        </div>
      </div>
      <div v-else class="article-list">
        <ArticleCard
          v-for="(article, idx) in articleStore.articles"
          :key="article.id"
          class="card-enter"
          :article="article"
          :style="{ '--stagger': `${Math.min(idx, 8) * 45}ms` }"
        />
      </div>

      <Pagination
        :total="articleStore.pagination.total"
        :page-size="articleStore.pagination.pageSize"
        :current-page="articleStore.pagination.currentPage"
        @changePage="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useArticleStore } from '../stores/article';
import ArticleCard from '../components/ArticleCard.vue';
import Pagination from '../components/Pagination.vue';
import { useRoute, useRouter } from 'vue-router';

const articleStore = useArticleStore();
const route = useRoute();
const router = useRouter();

const currentTagId = ref(null);
const listingLoading = ref(false);

const displayedHomeTags = computed(() => {
  const all = articleStore.tags || [];
  const slice = all.slice(0, 10);
  const tid = currentTagId.value;
  if (tid == null) return slice;
  if (slice.some((t) => t.id === tid)) return slice;
  const sel = all.find((t) => t.id === tid);
  if (!sel) return slice;
  return [...slice.slice(0, 9), sel];
});

const fetchArticlesData = async (page, tagId) => {
  listingLoading.value = true;
  try {
    await articleStore.fetchArticles(page, articleStore.pagination.pageSize, tagId);
  } finally {
    listingLoading.value = false;
  }
};

const handlePageChange = (newPage) => {
  const query = { page: newPage };
  if (currentTagId.value != null) query.tag = currentTagId.value;
  router.push({ query });
};

const filterByTag = (tagId) => {
  currentTagId.value = tagId;
  const query = { page: 1 };
  if (tagId != null) query.tag = tagId;
  router.push({ query });
};

watch(
  () => route.query,
  async (newQuery) => {
    const page = Math.max(1, parseInt(newQuery.page, 10) || 1);
    let tag = null;
    const raw = newQuery.tag;
    if (raw !== undefined && raw !== null && raw !== '') {
      const t = parseInt(raw, 10);
      tag = Number.isNaN(t) ? null : t;
    }
    currentTagId.value = tag;
    await fetchArticlesData(page, tag);
  },
  { immediate: true }
);

onMounted(() => {
  articleStore.fetchTags();
});
</script>

<style scoped>
.home-page {
  padding: 2.25rem 0 3.5rem;
}

.home-hero {
  text-align: center;
  margin-bottom: 2.25rem;
  animation: fade-in-soft 0.55s var(--ease-out-soft) both;
}

.page-title {
  margin: 0;
  font-size: clamp(2rem, 5vw, 2.75rem);
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.page-sub {
  margin: 0.65rem 0 0;
  font-size: 1rem;
  color: var(--color-text-muted);
}

.tag-filter {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 2.25rem;
}

.tag-item {
  cursor: pointer;
  padding: 0.45rem 1.05rem;
  border-radius: var(--radius-pill);
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-muted);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-xs);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast),
    border-color var(--transition-fast), color var(--transition-fast),
    background var(--transition-fast);
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
  border-color: rgba(79, 70, 229, 0.25);
  color: var(--color-primary);
}

.tag-item.active {
  color: #fff;
  background: var(--gradient-cta);
  border-color: transparent;
  box-shadow: var(--shadow-hover);
}

.tag-all.active {
  background: var(--color-text);
  background-image: none;
}

.article-list {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: 1fr;
}

.skeleton-grid .card-skeleton {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  padding: 1.35rem 1.5rem;
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.sk-title {
  height: 1.35rem;
  width: 72%;
  margin-bottom: 1rem;
}

.sk-line {
  height: 0.75rem;
  width: 100%;
  margin-bottom: 0.55rem;
}

.sk-line.short {
  width: 55%;
  margin-bottom: 1.1rem;
}

.sk-meta {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.sk-dot {
  width: 3.5rem;
  height: 0.65rem;
  border-radius: var(--radius-pill);
}

.sk-chip {
  width: 3rem;
  height: 1.35rem;
  border-radius: var(--radius-pill);
}

.card-enter {
  animation: fade-in-soft 0.5s var(--ease-out-soft) both;
  animation-delay: var(--stagger, 0ms);
}

@media (min-width: 768px) {
  .article-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .article-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (prefers-reduced-motion: reduce) {
  .tag-item:hover {
    transform: none;
  }

  .card-enter {
    animation: none;
  }

  .home-hero {
    animation: none;
  }
}
</style>
