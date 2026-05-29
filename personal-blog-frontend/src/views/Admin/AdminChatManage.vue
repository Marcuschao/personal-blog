<template>
  <div class="admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">聊天消息管理</h1>
          <p class="ds-page-sub">搜索、删除与撤回</p>
        </div>
        <n-space :size="8">
          <router-link to="/admin/chat/online"><n-button>在线监控</n-button></router-link>
          <router-link to="/admin"><n-button>返回管理</n-button></router-link>
        </n-space>
      </header>

      <n-card :bordered="true" style="margin-bottom: 16px;">
        <n-space :size="12" wrap>
          <n-input v-model:value="filters.username" placeholder="用户名" style="width: 140px;" />
          <n-input v-model:value="filters.keyword" placeholder="内容关键词" style="width: 180px;" />
          <n-button type="primary" @click="reload">搜索</n-button>
          <n-button type="error" secondary :disabled="!checkedIds.length" @click="onBatchDelete">批量删除</n-button>
        </n-space>
      </n-card>

      <n-card :bordered="true">
        <n-data-table
          v-model:checked-row-keys="checkedIds"
          :columns="columns"
          :data="rows"
          :row-key="(row) => row.id"
          :bordered="false"
          :scroll-x="980"
        />
      </n-card>

      <Pagination :total="total" :page-size="pageSize" :current-page="page" @changePage="onPage" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import { NButton, NCard, NDataTable, NInput, NSpace, NTag, useMessage } from 'naive-ui';
import Pagination from '../../components/Pagination.vue';
import {
  deleteAdminChatMessage,
  deleteAdminChatMessages,
  fetchAdminChatMessages,
  muteUser,
  recallAdminChatMessage,
} from '../../api/adminChat';
import { formatShortDateTime } from '../../utils/format';

const message = useMessage();
const rows = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(20);
const checkedIds = ref([]);
const filters = ref({ username: '', keyword: '' });

const columns = [
  { type: 'selection' },
  { title: 'ID', key: 'id', width: 72 },
  { title: '用户', key: 'username', width: 100, ellipsis: { tooltip: true } },
  { title: '内容', key: 'content', minWidth: 200, ellipsis: { tooltip: true } },
  {
    title: '状态',
    key: 'recalled',
    width: 88,
    render(row) {
      return row.recalled === 1
        ? h(NTag, { type: 'default', bordered: false }, () => '已撤回')
        : h(NTag, { type: 'success', bordered: false }, () => '正常');
    },
  },
  {
    title: '时间',
    key: 'createTime',
    width: 140,
    render: (row) => formatShortDateTime(row.createTime),
  },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    fixed: 'right',
    render(row) {
      return h(NSpace, { size: 8 }, () => [
        h(NButton, {
          size: 'small',
          secondary: true,
          disabled: row.recalled === 1,
          onClick: () => onRecall(row.id),
        }, () => '撤回'),
        h(NButton, {
          size: 'small',
          type: 'warning',
          secondary: true,
          onClick: () => onMute(row.userId),
        }, () => '禁言30分'),
        h(NButton, {
          size: 'small',
          type: 'error',
          secondary: true,
          onClick: () => onDelete(row.id),
        }, () => '删除'),
      ]);
    },
  },
];

async function load() {
  try {
    const res = await fetchAdminChatMessages({
      page: page.value,
      size: pageSize.value,
      username: filters.value.username || undefined,
      keyword: filters.value.keyword || undefined,
    });
    rows.value = res.records || [];
    total.value = res.total || 0;
    checkedIds.value = [];
  } catch {
    rows.value = [];
    total.value = 0;
  }
}

function reload() {
  page.value = 1;
  load();
}

function onPage(p) {
  page.value = p;
  load();
}

async function onDelete(id) {
  try {
    await deleteAdminChatMessage(id);
    message.success('已删除');
    await load();
  } catch (err) {
    message.error(err?.message || '删除失败');
  }
}

async function onBatchDelete() {
  try {
    await deleteAdminChatMessages(checkedIds.value);
    message.success('已批量删除');
    await load();
  } catch (err) {
    message.error(err?.message || '删除失败');
  }
}

async function onRecall(id) {
  try {
    await recallAdminChatMessage(id);
    message.success('已撤回');
    await load();
  } catch (err) {
    message.error(err?.message || '撤回失败');
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
