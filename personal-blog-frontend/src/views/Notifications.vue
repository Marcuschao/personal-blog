<template>
  <div class="notifications-page ds-page container">
    <header class="page-head">
      <h1 class="ds-page-title">消息中心</h1>
      <n-button
        v-if="items.length"
        secondary
        :loading="markingAll"
        @click="onMarkAllRead"
      >全部已读</n-button>
    </header>

    <n-skeleton v-if="loading" height="128px" :sharp="false" />
    <n-empty v-else-if="!items.length" description="暂无消息" />
    <n-list v-else hoverable clickable>
      <n-list-item
        v-for="item in items"
        :key="item.id"
        :class="{ unread: !item.read }"
      >
        <template #prefix>
          <UserAvatar :src="item.actorAvatar" :name="item.actorNickname" :size="40" />
        </template>
        <div class="notif-body" @click="openItem(item)">
          <span class="content">{{ item.content }}</span>
          <time class="time">{{ formatTime(item.createTime) }}</time>
        </div>
        <template #suffix>
          <n-button quaternary size="small" @click.stop="onDelete(item.id)">删除</n-button>
        </template>
      </n-list-item>
    </n-list>

    <Pagination
      v-if="total > pageSize"
      :total="total"
      :page-size="pageSize"
      :current-page="page"
      @changePage="loadPage"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { NButton, NEmpty, NList, NListItem, NSkeleton } from 'naive-ui';
import Pagination from '../components/Pagination.vue';
import UserAvatar from '../components/UserAvatar.vue';
import {
  fetchNotifications,
  markRead,
  markAllRead,
  deleteNotification,
} from '../api/notification';
import { useNotificationStore } from '../stores/notification';

const router = useRouter();
const notificationStore = useNotificationStore();
const loading = ref(true);
const markingAll = ref(false);
const items = ref([]);
const page = ref(1);
const pageSize = ref(20);
const total = ref(0);

function formatTime(t) {
  if (!t) return '';
  const d = new Date(t);
  const now = Date.now();
  const diff = now - d.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  return d.toLocaleDateString();
}

function targetPath(item) {
  if (item.type === 'FOLLOW' && item.actorUserId) {
    return `/user/${item.actorUserId}`;
  }
  if (item.targetType === 'ARTICLE' && item.targetId) {
    return `/article/${item.targetId}`;
  }
  return null;
}

async function openItem(item) {
  if (!item.read) {
    try {
      await markRead(item.id);
      item.read = true;
      notificationStore.decrementUnread();
    } catch {
      /* ignore */
    }
  }
  const path = targetPath(item);
  if (path) router.push(path);
}

async function loadPage(p = 1) {
  loading.value = true;
  page.value = p;
  try {
    const res = await fetchNotifications({ page: p, size: pageSize.value });
    const d = res.data;
    items.value = d?.records || [];
    total.value = d?.total || 0;
  } catch {
    items.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

async function onMarkAllRead() {
  markingAll.value = true;
  try {
    await markAllRead();
    items.value = items.value.map((i) => ({ ...i, read: true }));
    notificationStore.clearUnread();
  } finally {
    markingAll.value = false;
  }
}

async function onDelete(id) {
  try {
    await deleteNotification(id);
    items.value = items.value.filter((i) => i.id !== id);
    if (!items.value.length && page.value > 1) {
      loadPage(page.value - 1);
    }
  } catch {
    /* ignore */
  }
}

onMounted(() => loadPage(1));
</script>

<style scoped>
.notifications-page {
  padding-top: var(--space-8);
  padding-bottom: var(--space-16);
  max-width: 40rem;
}

.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.unread {
  background: var(--color-primary-soft);
}

.notif-body {
  cursor: pointer;
  min-width: 0;
}

.content {
  display: block;
  font-size: var(--text-sm);
  line-height: 1.5;
}

.time {
  display: block;
  margin-top: var(--space-1);
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}
</style>
