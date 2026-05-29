<template>
  <div class="admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">聊天在线监控</h1>
          <p class="ds-page-sub">当前 STOMP 会话</p>
        </div>
        <n-space :size="8">
          <n-button @click="load">刷新</n-button>
          <router-link to="/admin/chat/messages"><n-button>消息管理</n-button></router-link>
          <router-link to="/admin"><n-button>返回管理</n-button></router-link>
        </n-space>
      </header>

      <n-card :bordered="true">
        <n-data-table :columns="columns" :data="rows" :bordered="false" :scroll-x="860" />
        <n-empty v-if="!rows.length" description="暂无在线会话" />
      </n-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import { NButton, NCard, NDataTable, NEmpty, NSpace, NTag, useMessage } from 'naive-ui';
import { fetchAdminChatOnline, muteUser } from '../../api/adminChat';
import { formatShortDateTime } from '../../utils/format';

const message = useMessage();
const rows = ref([]);

const columns = [
  { title: '用户', key: 'username', width: 100, ellipsis: { tooltip: true } },
  { title: '用户ID', key: 'userId', width: 88 },
  { title: 'Session', key: 'sessionId', minWidth: 160, ellipsis: { tooltip: true } },
  { title: 'IP', key: 'ip', width: 120, render: (row) => row.ip || '—' },
  {
    title: '角色',
    key: 'admin',
    width: 88,
    render(row) {
      return row.admin
        ? h(NTag, { type: 'warning', bordered: false }, () => '管理员')
        : h(NTag, { bordered: false }, () => '用户');
    },
  },
  {
    title: '上线时间',
    key: 'onlineAt',
    width: 140,
    render: (row) => formatShortDateTime(row.onlineAt),
  },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    render(row) {
      return h(
        NButton,
        { size: 'small', type: 'warning', secondary: true, onClick: () => onMute(row.userId) },
        () => '禁言30分'
      );
    },
  },
];

async function load() {
  try {
    const data = await fetchAdminChatOnline();
    rows.value = Array.isArray(data) ? data : [];
  } catch {
    rows.value = [];
  }
}

async function onMute(userId) {
  if (!userId) return;
  try {
    await muteUser(userId, 30);
    message.success('已禁言 30 分钟');
  } catch (err) {
    message.error(err?.message || '禁言失败');
  }
}

onMounted(load);
</script>
