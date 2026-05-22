<template>
  <div class="profile-page ds-page container">
    <div v-if="loading" class="profile-skel ui-skeleton" />
    <div v-else-if="!user" class="state-msg">无法加载资料</div>
    <div v-else class="profile-panel">
      <h1 class="profile-title">个人资料</h1>
      <p class="extra muted">
        <span v-if="user.registerRegion">注册地区：{{ user.registerRegion }}</span>
        <span v-if="user.region" class="ml">展示地区：{{ user.region }}</span>
        <span v-if="user.lastLoginTime" class="ml">上次登录：{{ formatTime(user.lastLoginTime) }}</span>
      </p>
      <form class="profile-form" @submit.prevent="save">
        <div class="fg">
          <label class="ds-form-label" for="nickname">昵称</label>
          <input id="nickname" v-model="nickname" class="ds-input" type="text" maxlength="50" />
        </div>
        <div class="fg">
          <label class="ds-form-label" for="avatar">头像 URL</label>
          <input id="avatar" v-model="avatar" class="ds-input" type="url" maxlength="255" />
        </div>
        <div class="fg">
          <label class="ds-form-label" for="gender">性别</label>
          <select id="gender" v-model.number="gender" class="ds-input fg-select">
            <option :value="0">未知</option>
            <option :value="1">男</option>
            <option :value="2">女</option>
          </select>
        </div>
        <div class="fg">
          <label class="ds-form-label" for="bio">简介</label>
          <textarea id="bio" v-model="bio" class="ds-textarea" maxlength="500" rows="4" />
        </div>
        <button type="submit" class="ds-btn ds-btn--primary" :disabled="saving">{{ saving ? '保存中…' : '保存' }}</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchMe, updateProfile } from '../api/user';
import { useAuthStore } from '../stores/auth';
import { useToastStore } from '../stores/toast';

const loading = ref(true);
const saving = ref(false);
const user = ref(null);
const nickname = ref('');
const avatar = ref('');
const gender = ref(0);
const bio = ref('');

const authStore = useAuthStore();
const toast = useToastStore();

const formatTime = (t) => (t ? new Date(t).toLocaleString() : '');

onMounted(async () => {
  loading.value = true;
  try {
    const res = await fetchMe();
    const u = res.data;
    user.value = u;
    nickname.value = u?.nickname ?? u?.username ?? '';
    avatar.value = u?.avatar ?? '';
    gender.value = u?.gender ?? 0;
    bio.value = u?.bio ?? '';
  } catch {
    user.value = null;
  } finally {
    loading.value = false;
  }
});

async function save() {
  saving.value = true;
  try {
    const res = await updateProfile({
      nickname: nickname.value.trim(),
      avatar: avatar.value.trim() || null,
      gender: gender.value,
      bio: bio.value.trim() || null,
    });
    const next = res.data;
    user.value = next;
    nickname.value = next?.nickname ?? next?.username ?? '';
    avatar.value = next?.avatar ?? '';
    gender.value = next?.gender ?? 0;
    bio.value = next?.bio ?? '';
    authStore.user = next;
    toast.push('已保存', 'success');
  } catch {
    /* request toast */
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.profile-page {
  padding-top: var(--space-8);
  padding-bottom: var(--space-16);
}

.profile-skel {
  height: 12rem;
  border-radius: var(--radius-lg);
}

.profile-panel {
  max-width: 32rem;
  margin: 0 auto;
  background: var(--color-surface);
  padding: clamp(1.25rem, 3vw, 1.85rem);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.profile-title {
  margin: 0 0 var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--weight-semibold);
}

.muted {
  color: var(--color-text-muted);
  font-size: var(--text-sm);
}

.extra {
  margin: 0 0 var(--space-8);
}

.ml {
  margin-left: var(--space-4);
}

.fg {
  margin-bottom: var(--space-5);
}

.fg-select {
  width: 100%;
}
</style>
