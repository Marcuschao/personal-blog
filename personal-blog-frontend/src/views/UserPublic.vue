<template>
  <div class="public-user ds-page container">
    <div v-if="loading" class="pub-skel ui-skeleton" />
    <div v-else-if="!u" class="state-msg">用户不存在</div>
    <div v-else class="pub-panel">
      <div class="pub-head">
        <img v-if="u.avatar" :src="u.avatar" alt="" class="pub-avatar" />
        <div v-else class="pub-letter">{{ initials }}</div>
        <div>
          <h1 class="pub-name">{{ u.nickname || '用户' }}</h1>
          <p v-if="genderLabel || u.region" class="pub-meta muted">
            <span v-if="genderLabel">{{ genderLabel }}</span>
            <span v-if="u.region">{{ u.region }}</span>
          </p>
        </div>
      </div>
      <p v-if="u.bio" class="pub-bio">{{ u.bio }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useHead } from '@vueuse/head';
import { fetchPublicUser } from '../api/user';

const route = useRoute();
const loading = ref(true);
const u = ref(null);

const initials = computed(() => {
  const n = u.value?.nickname || '?';
  return n.slice(0, 1);
});

const genderLabel = computed(() => {
  const g = u.value?.gender;
  if (g === 1) return '男';
  if (g === 2) return '女';
  return '';
});

useHead(() => ({
  title: u.value?.nickname ? `${u.value.nickname} · 用户` : '用户',
}));

async function load(id) {
  loading.value = true;
  try {
    const res = await fetchPublicUser(id);
    u.value = res.data;
  } catch {
    u.value = null;
  } finally {
    loading.value = false;
  }
}

watch(
  () => Number(route.params.id),
  (id) => {
    if (Number.isFinite(id)) load(id);
  },
  { immediate: true }
);
</script>

<style scoped>
.public-user {
  padding-top: var(--space-8);
  padding-bottom: var(--space-16);
}

.pub-skel {
  height: 8rem;
  border-radius: var(--radius-lg);
}

.pub-panel {
  max-width: 40rem;
  margin: 0 auto;
  background: var(--color-surface);
  padding: clamp(1.25rem, 3vw, 1.85rem);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.pub-head {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.pub-avatar {
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.pub-letter {
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
  background: var(--surface-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: var(--weight-semibold);
  font-size: var(--text-lg);
}

.pub-name {
  margin: 0;
  font-size: var(--text-xl);
}

.pub-meta span + span::before {
  content: ' · ';
}

.muted {
  color: var(--color-text-muted);
  font-size: var(--text-sm);
}

.pub-bio {
  margin-top: var(--space-6);
  white-space: pre-wrap;
}
</style>
