<template>
  <div class="article-editor-page">
    <div class="container editor-shell">
      <header class="editor-hero">
        <h1 class="page-title">{{ isEditMode ? '编辑文章' : '新建文章' }}</h1>
        <p class="page-sub">支持 Markdown，提交后将在后台列表中显示</p>
      </header>

      <div class="editor-layout">
        <form class="article-form" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="title">文章标题</label>
            <input id="title" v-model="article.title" type="text" required />
          </div>

          <div class="form-group">
            <label for="summary">摘要</label>
            <input id="summary" v-model="article.summary" type="text" placeholder="可选，显示在列表卡片中" />
          </div>

          <div class="form-group">
            <label for="tags">标签（逗号分隔）</label>
            <input id="tags" v-model="tagsInput" type="text" placeholder="Vue, JavaScript, CSS" />
          </div>

          <div class="form-group form-group-grow">
            <label for="content">正文（Markdown）</label>
            <textarea
              id="content"
              ref="contentRef"
              v-model="article.content"
              rows="20"
              required
            />
          </div>

          <div class="ai-meta-row">
            <button
              type="button"
              class="ai-meta-btn"
              :disabled="aiSummaryLoading || !article.content.trim()"
              @click="fillSummary"
            >
              <span v-if="aiSummaryLoading" class="meta-spin" aria-hidden="true" />
              AI 生成摘要
            </button>
            <button
              type="button"
              class="ai-meta-btn"
              :disabled="aiTagsLoading || !article.content.trim()"
              @click="fillTags"
            >
              <span v-if="aiTagsLoading" class="meta-spin" aria-hidden="true" />
              AI 推荐标签
            </button>
          </div>

          <button type="submit" class="submit-button" :disabled="isLoading">
            <span v-if="isLoading" class="btn-spinner" aria-hidden="true" />
            <span>{{ isLoading ? '提交中…' : '提交' }}</span>
          </button>
          <p v-if="error" :key="error" class="error-message">{{ error }}</p>
        </form>

        <ArticleAiSidebar
          :title="article.title"
          :tags-hint="tagsInput"
          :get-context="getTextareaContext"
          @apply="applyAiResult"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createArticle, updateArticle, getArticleDetail } from '../../api/article';
import { agentSuggestTags, agentSummary } from '../../api/agent';
import ArticleAiSidebar from './ArticleAiSidebar.vue';

const route = useRoute();
const router = useRouter();

const contentRef = ref(null);

const isEditMode = ref(false);
const article = ref({
  id: null,
  title: '',
  summary: '',
  content: '',
  tags: [],
});
const tagsInput = ref('');
const isLoading = ref(false);
const error = ref(null);
const aiSummaryLoading = ref(false);
const aiTagsLoading = ref(false);

const tagNamesParam = () =>
  tagsInput.value
    .split(',')
    .map((t) => t.trim())
    .filter(Boolean)
    .join(',');

function getTextareaContext() {
  const el = contentRef.value;
  const c = article.value.content || '';
  if (!el) {
    return {
      content: c,
      selectionStart: c.length,
      selectionEnd: c.length,
      selectedText: '',
    };
  }
  const selectionStart = typeof el.selectionStart === 'number' ? el.selectionStart : c.length;
  const selectionEnd = typeof el.selectionEnd === 'number' ? el.selectionEnd : selectionStart;
  return {
    content: c,
    selectionStart,
    selectionEnd,
    selectedText: c.slice(selectionStart, selectionEnd),
  };
}

function applyAiResult({ mode, text }) {
  if (!text) return;
  const el = contentRef.value;
  const c = article.value.content || '';
  if (mode === 'replace') {
    const ctx = getTextareaContext();
    const start = ctx.selectionStart;
    const end = ctx.selectionEnd;
    if (start === end && el) {
      return;
    }
    article.value.content = c.slice(0, start) + text + c.slice(end);
    nextTick(() => {
      if (!el) return;
      el.focus();
      const pos = start + text.length;
      el.setSelectionRange(pos, pos);
    });
    return;
  }
  const insertAt = el ? el.selectionStart : c.length;
  const safeAt = typeof insertAt === 'number' ? insertAt : c.length;
  article.value.content = c.slice(0, safeAt) + text + c.slice(safeAt);
  nextTick(() => {
    if (!el) return;
    el.focus();
    const pos = safeAt + text.length;
    el.setSelectionRange(pos, pos);
  });
}

async function fillSummary() {
  aiSummaryLoading.value = true;
  error.value = null;
  try {
    article.value.summary = await agentSummary({
      title: article.value.title,
      content: article.value.content,
    });
  } catch (err) {
    error.value = err?.message || '摘要生成失败';
  } finally {
    aiSummaryLoading.value = false;
  }
}

async function fillTags() {
  aiTagsLoading.value = true;
  error.value = null;
  try {
    const tags = await agentSuggestTags({
      title: article.value.title,
      content: article.value.content,
    });
    const slice = tags.slice(0, 5);
    tagsInput.value = slice.join(', ');
  } catch (err) {
    error.value = err?.message || '标签推荐失败';
  } finally {
    aiTagsLoading.value = false;
  }
}

const fetchArticleForEdit = async (id) => {
  error.value = null;
  try {
    const response = await getArticleDetail(id);
    const d = response.data;
    article.value = {
      id: d.id,
      title: d.title,
      content: d.content,
      summary: d.summary || '',
      tags: d.tags || [],
    };
    tagsInput.value = (d.tags || []).map((t) => t.name).join(', ');
  } catch (err) {
    console.error('Failed to fetch article for edit:', err);
    error.value = '加载文章失败。';
  }
};

const handleSubmit = async () => {
  isLoading.value = true;
  error.value = null;

  const payload = {
    title: article.value.title,
    content: article.value.content,
    summary: article.value.summary || '',
    status: 1,
  };
  const tags = tagNamesParam();

  try {
    if (isEditMode.value) {
      await updateArticle(article.value.id, payload, tags);
    } else {
      await createArticle(payload, tags);
      article.value = { id: null, title: '', content: '', summary: '', tags: [] };
      tagsInput.value = '';
    }
    router.push('/admin');
  } catch (err) {
    console.error('Submission Error:', err);
    error.value = err.message || err.response?.data?.message || '提交失败，请稍后再试。';
  } finally {
    isLoading.value = false;
  }
};

watch(
  () => route.params.id,
  (newId) => {
    isEditMode.value = !!newId;
    if (isEditMode.value) {
      fetchArticleForEdit(newId);
    } else {
      article.value = { id: null, title: '', summary: '', content: '', tags: [] };
      tagsInput.value = '';
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.article-editor-page {
  padding: 2.25rem 0 3.5rem;
}

.editor-shell {
  max-width: 1280px;
}

.editor-hero {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  margin: 0;
  font-size: clamp(1.85rem, 4vw, 2.35rem);
  font-weight: 760;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.page-sub {
  margin: 0.5rem 0 0;
  font-size: 0.92rem;
  color: var(--color-text-muted);
}

.editor-layout {
  display: flex;
  flex-wrap: wrap;
  gap: 1.35rem;
  align-items: flex-start;
}

.article-form {
  flex: 1;
  min-width: 0;
  background: var(--color-surface);
  padding: clamp(1.65rem, 4vw, 2.35rem);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md);
}

@media (min-width: 961px) {
  .editor-layout .article-form {
    max-width: calc(100% - 360px);
  }
}

.form-group {
  margin-bottom: 1.35rem;
}

.form-group-grow {
  margin-bottom: 1.65rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.45rem;
  font-size: 0.82rem;
  font-weight: 650;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}

.form-group input[type='text'],
.form-group textarea {
  width: 100%;
  padding: 0.78rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 1rem;
  font-family: inherit;
  background: rgba(248, 250, 252, 0.65);
  transition:
    border-color var(--transition-fast),
    box-shadow var(--transition-fast),
    background var(--transition-fast);
}

.form-group textarea {
  font-family: ui-monospace, 'SF Mono', Menlo, Monaco, Consolas, monospace;
  font-size: 0.9rem;
  line-height: 1.55;
  min-height: 320px;
  resize: vertical;
}

.form-group:focus-within input,
.form-group:focus-within textarea {
  outline: none;
  border-color: rgba(79, 70, 229, 0.45);
  box-shadow: 0 0 0 4px var(--color-primary-soft);
  background: #fff;
}

.ai-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  margin-bottom: 1rem;
}

.ai-meta-btn {
  flex: 1;
  min-width: 8rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  padding: 0.62rem 0.85rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: rgba(248, 250, 252, 0.85);
  font-size: 0.86rem;
  font-weight: 650;
  font-family: inherit;
  cursor: pointer;
  color: var(--color-text);
}

.ai-meta-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.meta-spin {
  width: 0.9rem;
  height: 0.9rem;
  border-radius: 50%;
  border: 2px solid rgba(79, 70, 229, 0.2);
  border-top-color: var(--color-primary);
  animation: spin 0.72s linear infinite;
}

.submit-button {
  width: 100%;
  padding: 0.95rem 1.25rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  border: none;
  border-radius: var(--radius-md);
  font-size: 1.05rem;
  font-weight: 650;
  font-family: inherit;
  cursor: pointer;
  color: #fff;
  background: var(--gradient-cta);
  box-shadow: 0 10px 28px rgba(79, 70, 229, 0.35);
  transition:
    transform var(--transition-fast),
    box-shadow var(--transition-fast),
    opacity var(--transition-fast),
    filter var(--transition-fast);
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-2px);
  filter: saturate(1.05);
}

.submit-button:active:not(:disabled) {
  transform: translateY(0);
}

.submit-button:disabled {
  opacity: 0.72;
  cursor: not-allowed;
  transform: none;
}

.btn-spinner {
  width: 1.05rem;
  height: 1.05rem;
  border-radius: var(--radius-pill);
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  animation: spin 0.72s linear infinite;
}

.error-message {
  margin-top: 1rem;
  text-align: center;
  padding: 0.7rem;
  border-radius: var(--radius-md);
  background: rgba(239, 68, 68, 0.1);
  color: #b91c1c;
  font-size: 0.88rem;
  font-weight: 500;
}

@media (prefers-reduced-motion: reduce) {
  .submit-button:hover:not(:disabled) {
    transform: none;
  }

  .btn-spinner,
  .meta-spin {
    animation: spin 1.4s linear infinite;
  }
}
</style>
