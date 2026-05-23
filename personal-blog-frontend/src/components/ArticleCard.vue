<template>
  <router-link :to="`/article/${article.id}`" class="article-card-link">
    <n-card hoverable class="article-card">
      <h3 class="title">{{ article.title }}</h3>
      <p v-if="reasonLine" class="reason">{{ reasonLine }}</p>
      <p class="excerpt">{{ excerpt }}</p>
      <n-space class="meta" :size="8" align="center">
        <time>{{ formatDate(article.createTime || article.createdAt) }}</time>
        <n-tag
          v-for="tag in (article.tags || []).slice(0, 5)"
          :key="tag.id"
          size="small"
          :bordered="false"
        >{{ tag.name }}</n-tag>
        <span v-if="showLike" class="like-stat" @click.prevent>
          <n-icon :color="displayLiked ? '#2563eb' : undefined" size="14">
            <HeartOutline v-if="!displayLiked" />
            <Heart v-else />
          </n-icon>
          {{ displayLikeCount }}
        </span>
      </n-space>
      <span class="read-hint">阅读正文</span>
    </n-card>
  </router-link>
</template>

<script setup>
import { computed } from 'vue';
import { NCard, NIcon, NSpace, NTag } from 'naive-ui';
import { Heart, HeartOutline } from '@vicons/ionicons5';

const props = defineProps({
  article: { type: Object, required: true },
  reason: { type: String, default: '' },
  showLike: { type: Boolean, default: false },
  likeCount: { type: Number, default: undefined },
  liked: { type: Boolean, default: undefined },
});

const displayLikeCount = computed(() => {
  if (props.likeCount !== undefined) return props.likeCount;
  return props.article.likeCount ?? 0;
});

const displayLiked = computed(() => {
  if (props.liked !== undefined) return props.liked;
  return !!props.article.liked;
});

const reasonLine = computed(() => {
  const r = (props.reason || props.article.reason || '').trim();
  return r;
});

const excerpt = computed(() => {
  const a = props.article;
  if (a.summary && a.summary.trim()) return a.summary.trim();
  const raw = (a.content || '').replace(/\s+/g, ' ').trim();
  return raw.length > 160 ? `${raw.slice(0, 160)}…` : raw;
});

const formatDate = (dateString) => {
  if (!dateString) return '';
  const options = { year: 'numeric', month: 'short', day: 'numeric' };
  return new Date(dateString).toLocaleDateString(undefined, options);
};
</script>

<style scoped>
.article-card-link {
  display: block;
  text-decoration: none;
  color: inherit;
  height: 100%;
}

.article-card {
  height: 100%;
}

.title {
  margin: 0 0 var(--space-3);
  font-size: var(--text-lg);
  font-weight: var(--weight-semibold);
  color: var(--color-text);
  line-height: 1.35;
}

.article-card-link:hover .title {
  color: var(--color-primary);
}

.reason {
  margin: calc(var(--space-1) * -1) 0 var(--space-3);
  font-size: var(--text-xs);
  font-weight: var(--weight-semibold);
  color: var(--color-primary);
}

.excerpt {
  margin: 0 0 var(--space-4);
  font-size: var(--text-base);
  color: var(--color-text-muted);
  line-height: 1.62;
  min-height: 3em;
}

.meta {
  flex-wrap: wrap;
  font-size: var(--text-xs);
  color: var(--color-text-soft);
}

.like-stat {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  margin-left: auto;
}

.read-hint {
  display: inline-block;
  margin-top: var(--space-4);
  font-size: var(--text-xs);
  font-weight: var(--weight-semibold);
  color: var(--color-primary);
  opacity: 0;
  transform: translateY(6px);
  transition: opacity var(--transition-fast), transform var(--transition-fast);
}

.article-card-link:hover .read-hint {
  opacity: 1;
  transform: translateY(0);
}
</style>
