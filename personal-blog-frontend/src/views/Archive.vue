<template>
  <div class="archive-page">
    <div class="container">
      <header class="page-hero">
        <h1 class="page-title">文章归档</h1>
        <p class="page-sub">按时间浏览全部文章</p>
      </header>

      <div v-if="loading" class="timeline-skeleton">
        <div v-for="n in 4" :key="'ts-' + n" class="sk-block">
          <div class="ui-skeleton sk-dot" />
          <div class="sk-body">
            <div class="ui-skeleton sk-title" />
            <div class="ui-skeleton sk-line" />
            <div class="ui-skeleton sk-line short" />
          </div>
        </div>
      </div>
      <div v-else-if="archives.length" class="archive-timeline">
        <div v-for="yearData in archives" :key="yearData.year" class="archive-year">
          <div class="year-header">
            <span class="year-dot" aria-hidden="true" />
            <h2 class="year-label">{{ yearData.year }}</h2>
          </div>
          <ul class="month-list">
            <li v-for="monthData in yearData.months" :key="`${yearData.year}-${monthData.month}`" class="month-block">
              <h3 class="month-title">
                {{ monthData.month }}月
                <span class="month-count">{{ monthData.articles.length }} 篇</span>
              </h3>
              <ul class="article-list-in-month">
                <li v-for="article in monthData.articles" :key="article.id" class="article-row">
                  <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
                  <span class="article-date">{{ formatDate(article.createTime || article.createdAt) }}</span>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
      <div v-else class="no-articles">
        <p>暂无归档文章。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getArticles } from '../api/article';

const archives = ref([]);
const loading = ref(true);

const buildArchives = (articles) => {
  const tempArchives = {};
  articles.forEach((article) => {
    const t = article.createTime || article.createdAt;
    if (!t) return;
    const date = new Date(t);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;

    if (!tempArchives[year]) {
      tempArchives[year] = { year, months: {} };
    }
    if (!tempArchives[year].months[month]) {
      tempArchives[year].months[month] = { month, articles: [] };
    }
    tempArchives[year].months[month].articles.push(article);
  });

  return Object.values(tempArchives)
    .map((yearData) => ({
      year: yearData.year,
      months: Object.values(yearData.months).sort((a, b) => b.month - a.month),
    }))
    .sort((a, b) => b.year - a.year);
};

const fetchArchives = async () => {
  loading.value = true;
  try {
    const pageSize = 500;
    let page = 1;
    const all = [];
    let total = Infinity;
    while (all.length < total) {
      const res = await getArticles({ page, size: pageSize });
      const d = res.data;
      const records = d.records || [];
      total = Number(d.total) || 0;
      all.push(...records);
      if (!records.length || all.length >= total) break;
      page++;
    }
    archives.value = buildArchives(all);
  } catch (e) {
    console.error(e);
    archives.value = [];
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

onMounted(() => {
  fetchArchives();
});
</script>

<style scoped>
.archive-page {
  padding: 2.25rem 0 3.75rem;
}

.page-hero {
  text-align: center;
  margin-bottom: 2.5rem;
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
  font-size: 1rem;
}

.timeline-skeleton {
  max-width: 720px;
  margin: 0 auto;
}

.sk-block {
  display: flex;
  gap: 1.25rem;
  margin-bottom: 1.75rem;
}

.sk-dot {
  width: 18px;
  height: 18px;
  border-radius: var(--radius-pill);
  flex-shrink: 0;
  margin-top: 0.35rem;
}

.sk-body {
  flex: 1;
}

.sk-title {
  height: 1.2rem;
  width: 30%;
  margin-bottom: 0.85rem;
}

.sk-line {
  height: 0.72rem;
  margin-bottom: 0.45rem;
}

.sk-line.short {
  width: 70%;
}

.archive-timeline {
  position: relative;
  max-width: 760px;
  margin: 0 auto;
  padding-left: 2.25rem;
}

.archive-timeline::before {
  content: '';
  position: absolute;
  left: 0.4rem;
  top: 0.25rem;
  bottom: 0.25rem;
  width: 3px;
  border-radius: var(--radius-pill);
  background: linear-gradient(
    180deg,
    rgba(99, 102, 241, 0.55),
    rgba(168, 85, 247, 0.25),
    rgba(148, 163, 184, 0.2)
  );
}

.archive-year {
  margin-bottom: 2.75rem;
}

.year-header {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  margin-bottom: 1.25rem;
}

.year-dot {
  position: relative;
  left: -1.75rem;
  margin-right: -0.9rem;
  width: 16px;
  height: 16px;
  border-radius: var(--radius-pill);
  background: var(--gradient-cta);
  box-shadow: 0 0 0 4px var(--color-page), 0 4px 14px rgba(79, 70, 229, 0.4);
  flex-shrink: 0;
}

.year-label {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 750;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.month-list {
  list-style: none;
  padding: 0 0 0 0.15rem;
}

.month-block {
  margin-bottom: 1.5rem;
}

.month-title {
  font-size: 1.05rem;
  font-weight: 650;
  color: var(--color-text-muted);
  margin: 0 0 0.85rem;
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.month-count {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--color-text-soft);
  background: var(--color-primary-soft);
  color: var(--color-primary);
  padding: 0.15rem 0.55rem;
  border-radius: var(--radius-pill);
}

.article-list-in-month {
  list-style: none;
  padding: 0;
  margin: 0;
}

.article-row {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.35rem 0.75rem;
  padding: 0.65rem 1rem;
  margin-bottom: 0.35rem;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-xs);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast),
    border-color var(--transition-fast);
}

.article-row:hover {
  transform: translateX(6px);
  box-shadow: var(--shadow-hover);
  border-color: rgba(79, 70, 229, 0.18);
}

.article-row a {
  flex: 1;
  min-width: 180px;
  color: var(--color-text);
  text-decoration: none;
  font-weight: 620;
}

.article-row a:hover {
  color: var(--color-primary);
}

.article-date {
  font-size: 0.82rem;
  color: var(--color-text-soft);
  font-variant-numeric: tabular-nums;
}

.no-articles {
  text-align: center;
  padding: 3rem 1.5rem;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  color: var(--color-text-muted);
}

@media (prefers-reduced-motion: reduce) {
  .article-row:hover {
    transform: none;
  }
}

</style>
