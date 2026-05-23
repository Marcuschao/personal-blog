<template>
  <div class="rh-page ds-page">
    <div class="container">
      <header class="ds-page-hero rh-head-tight" style="margin-bottom: 24px;">
        <h1 class="ds-page-title ds-page-title-md">阅读记录</h1>
        <p class="ds-page-sub">本地保存，换设备不同步</p>
      </header>

      <div v-if="entries.length">
        <n-space vertical :size="12">
          <n-card v-for="row in entries" :key="row.id" class="rh-card" size="small" hoverable>
            <router-link :to="`/article/${row.id}`" class="rh-link" style="text-decoration: none; color: inherit; display: block;">
              <n-space justify="space-between" align="center" style="width: 100%;">
                <span class="rh-title" style="font-weight: 600;">{{ row.title || '（无标题）' }}</span>
                <n-space class="rh-meta" :size="12" align="center">
                  <time style="color: var(--color-text-muted); font-size: 0.85em;">{{ formatTime(row.visitedAt) }}</time>
                  <n-tag v-if="row.scrollPercent > 0" type="primary" :bordered="false" size="small">
                    已读 {{ row.scrollPercent }}%
                  </n-tag>
                </n-space>
              </n-space>
            </router-link>
          </n-card>
        </n-space>
      </div>

      <div v-else>
        <n-empty description="暂无记录" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { NCard, NEmpty, NSpace, NTag } from 'naive-ui';
import { useReadingHistory } from '../composables/useReadingHistory';

const { listEntries } = useReadingHistory();
const entries = computed(() => listEntries());

function formatTime(ts) {
  if (!ts) return '';
  const d = new Date(ts);
  return d.toLocaleString();
}
</script>

<style scoped>
</style>
