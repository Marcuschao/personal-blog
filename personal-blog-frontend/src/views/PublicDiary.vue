<template>
  <div class="pub-diary-page ds-page">
    <div class="container">
      <header class="ds-page-hero">
        <h1 class="ds-page-title ds-page-title-md">公开日记</h1>
        <p class="ds-page-sub">作者选择公开的笔记摘录</p>
      </header>
      <p v-if="err" class="err">{{ err }}</p>
      <div v-if="loading" class="sk-grid">
        <div v-for="n in 5" :key="n" class="ui-skeleton sk-card" />
      </div>
      <div v-else-if="!records.length" class="ds-empty-panel">暂无公开日记</div>
      <ul v-else class="hit-list">
        <li v-for="row in records" :key="row.id" class="hit-card">
          <router-link :to="'/diary/' + row.id" class="hit-link">
            <h2 class="hit-title">{{ row.title }}</h2>
            <p class="hit-sum">{{ row.excerpt || '—' }}</p>
            <time class="hit-time">{{ row.diaryDate }}</time>
          </router-link>
        </li>
      </ul>
      <Pagination
        v-if="total > 0"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @changePage="onPage"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Pagination from '../components/Pagination.vue';
import { listPublicDiaries } from '../api/diary';

const records = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);
const loading = ref(true);
const err = ref('');

async function load() {
  loading.value = true;
  err.value = '';
  try {
    const res = await listPublicDiaries({ page: page.value, size: pageSize.value });
    const data = res.data;
    records.value = data?.records || [];
    total.value = Number(data?.total) || 0;
  } catch (e) {
    err.value = e?.message || '加载失败';
    records.value = [];
  } finally {
    loading.value = false;
  }
}

function onPage(p) {
  page.value = p;
  load();
}

onMounted(load);
</script>

<style scoped>
.hit-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hit-card {
  margin-bottom: var(--space-4);
}

.hit-link {
  display: block;
  padding: var(--space-5);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  text-decoration: none;
  color: inherit;
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-fast);
}

.hit-link:hover {
  box-shadow: var(--shadow-md);
}

.hit-title {
  margin: 0 0 var(--space-2);
  font-size: var(--text-lg);
}

.hit-sum {
  margin: 0 0 var(--space-2);
  font-size: var(--text-base);
  color: var(--color-text-muted);
  line-height: 1.55;
}

.hit-time {
  font-size: var(--text-xs);
  color: var(--color-text-soft);
}

.sk-card {
  height: 5rem;
  margin-bottom: var(--space-3);
}

.err {
  color: var(--color-danger);
  margin-bottom: var(--space-3);
}
</style>
