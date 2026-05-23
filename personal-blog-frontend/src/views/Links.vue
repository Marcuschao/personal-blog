<template>
  <div class="links-page ds-page">
    <div class="container">
      <header class="ds-page-hero" style="margin-bottom: 24px;">
        <h1 class="ds-page-title ds-page-title-lg">友情链接</h1>
        <p class="ds-page-sub">值得收藏的站点</p>
      </header>
      <n-alert v-if="err" type="error" style="margin-bottom: 16px;">{{ err }}</n-alert>
      
      <div v-if="loading">
        <n-grid cols="2 s:3 m:4 l:6" :x-gap="12" :y-gap="12" responsive="screen">
          <n-gi v-for="n in 6" :key="'l-' + n">
            <n-skeleton height="72px" :sharp="false" />
          </n-gi>
        </n-grid>
      </div>
      
      <div v-else-if="!items.length">
        <n-empty description="暂无友链" />
      </div>
      
      <div v-else>
        <n-grid cols="1 s:2 m:3 l:4" :x-gap="16" :y-gap="16" responsive="screen">
          <n-gi v-for="l in items" :key="l.id">
            <n-card class="link-card" size="small" hoverable>
              <a :href="l.url" class="link-a" target="_blank" rel="noopener noreferrer">
                <n-space align="center" :wrap="false" :size="12">
                  <n-avatar v-if="l.logo" :src="l.logo" :size="36" fallback-src="" />
                  <span class="link-name" style="font-weight: 600;">{{ l.name }}</span>
                </n-space>
              </a>
            </n-card>
          </n-gi>
        </n-grid>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { NAlert, NAvatar, NCard, NEmpty, NGi, NGrid, NSkeleton, NSpace } from 'naive-ui';
import { fetchFriendLinks } from '../api/links';

const items = ref([]);
const loading = ref(true);
const err = ref('');

onMounted(async () => {
  loading.value = true;
  err.value = '';
  try {
    const res = await fetchFriendLinks();
    items.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    err.value = e?.message || '加载失败';
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.link-a {
  text-decoration: none;
  color: inherit;
}
.link-a:hover {
  color: var(--color-primary);
}
</style>
