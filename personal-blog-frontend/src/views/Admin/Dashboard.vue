<template>
  <div class="admin-dashboard-page">
    <div class="container">
      <header class="dash-header">
        <div>
          <h1 class="page-title">文章管理</h1>
          <p class="page-sub">创建、编辑与发布内容</p>
        </div>
        <router-link to="/admin/new" class="new-article-button">＋ 新建文章</router-link>
      </header>

      <div v-if="articles.length" class="article-management-list">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th>发布日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="article in articles" :key="article.id">
              <td>{{ article.id }}</td>
              <td class="td-title">{{ article.title }}</td>
              <td class="td-date">{{ formatDate(article.createTime || article.createdAt) }}</td>
              <td class="actions-column">
                <router-link :to="`/admin/edit/${article.id}`" class="edit-button">编辑</router-link>
                <button type="button" class="delete-button" @click="confirmDelete(article.id)">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="no-articles">
        <p>暂无文章，<router-link to="/admin/new">立即新建一篇</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useArticleStore } from '../../stores/article';
import { deleteArticle } from '../../api/article';

const articleStore = useArticleStore();
const articles = ref([]);

const fetchArticles = async () => {
  await articleStore.fetchArticles(1, 500, null);
  articles.value = articleStore.articles;
};

const formatDate = (dateString) => {
  const options = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' };
  return new Date(dateString).toLocaleDateString(undefined, options);
};

const confirmDelete = async (id) => {
  if (confirm('确定要删除这篇文章吗？')) {
    try {
      await deleteArticle(id);
      alert('文章删除成功！');
      fetchArticles();
    } catch (error) {
      console.error('Failed to delete article:', error);
      alert('删除失败，请稍后再试。');
    }
  }
};

onMounted(() => {
  fetchArticles();
});
</script>

<style scoped>
.admin-dashboard-page {
  padding: 2.25rem 0 3.5rem;
}

.dash-header {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1.25rem;
  margin-bottom: 1.75rem;
  position: sticky;
  top: var(--nav-height);
  z-index: 40;
  padding: 1rem 0 0.85rem;
  margin-top: -0.25rem;
  background: linear-gradient(
    180deg,
    var(--color-page) 65%,
    rgba(232, 236, 244, 0)
  );
  backdrop-filter: blur(6px);
}

.page-title {
  margin: 0;
  font-size: clamp(1.85rem, 4vw, 2.35rem);
  font-weight: 760;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.page-sub {
  margin: 0.35rem 0 0;
  font-size: 0.92rem;
  color: var(--color-text-muted);
}

.new-article-button {
  display: inline-flex;
  align-items: center;
  padding: 0.72rem 1.35rem;
  border-radius: var(--radius-pill);
  text-decoration: none;
  font-size: 0.92rem;
  font-weight: 650;
  color: #fff;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 10px 26px rgba(16, 185, 129, 0.32);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);
}

.new-article-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(16, 185, 129, 0.38);
}

.article-management-list {
  background: var(--color-surface);
  padding: 0;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.article-management-list table {
  width: 100%;
  border-collapse: collapse;
}

.article-management-list th,
.article-management-list td {
  padding: 0.95rem 1.15rem;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}

.article-management-list th {
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.07em;
  text-transform: uppercase;
  color: var(--color-text-soft);
  background: linear-gradient(180deg, rgba(248, 250, 252, 1), rgba(241, 245, 249, 0.85));
}

.article-management-list tbody tr {
  transition: background var(--transition-fast);
}

.article-management-list tbody tr:hover {
  background: rgba(79, 70, 229, 0.04);
}

.article-management-list tbody tr:last-child td {
  border-bottom: none;
}

.td-title {
  font-weight: 600;
  color: var(--color-text);
  max-width: 320px;
}

.td-date {
  font-size: 0.88rem;
  color: var(--color-text-muted);
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.actions-column {
  width: 160px;
  text-align: right;
  white-space: nowrap;
}

.edit-button,
.delete-button {
  padding: 0.45rem 0.85rem;
  border-radius: var(--radius-pill);
  cursor: pointer;
  border: none;
  font-size: 0.82rem;
  font-weight: 600;
  font-family: inherit;
  margin: 0 0.2rem;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast),
    opacity var(--transition-fast);
}

.edit-button {
  background: var(--gradient-cta);
  color: #fff;
  text-decoration: none;
  display: inline-block;
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.25);
}

.edit-button:hover {
  transform: translateY(-1px);
}

.delete-button {
  background: rgba(239, 68, 68, 0.12);
  color: #dc2626;
}

.delete-button:hover {
  background: #dc2626;
  color: #fff;
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

.no-articles a {
  color: var(--color-primary);
  font-weight: 600;
}

@media (max-width: 768px) {
  .article-management-list {
    overflow-x: auto;
  }

  .dash-header {
    position: relative;
    top: auto;
    background: none;
    backdrop-filter: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .new-article-button:hover,
  .edit-button:hover {
    transform: none;
  }
}
</style>
