<template>
  <div class="admin-comments-page admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">评论审核</h1>
          <p class="ds-page-sub">待审核与已通过</p>
        </div>
        <router-link to="/admin">
          <n-button>返回管理</n-button>
        </router-link>
      </header>
      
      <n-card :bordered="true" style="margin-bottom: 24px;">
        <n-space class="filters" align="center" :size="16">
          <div class="fil" style="display: flex; align-items: center;">
            <span style="margin-right: 8px;">状态</span>
            <n-select v-model:value="statusFilter" style="width: 140px;" :options="statusOptions" @update:value="reload" />
          </div>
          <div class="fil" style="display: flex; align-items: center;">
            <span style="margin-right: 8px;">用户 ID</span>
            <n-input v-model:value="userIdFilter" type="text" placeholder="可选" style="width: 140px;" @keyup.enter="reload" />
          </div>
          <n-button type="primary" @click="reload">筛选</n-button>
        </n-space>
      </n-card>

      <n-card :bordered="true">
        <n-data-table
          :columns="columns"
          :data="rows"
          :bordered="false"
          :single-line="false"
          :scroll-x="900"
        />
      </n-card>

      <Pagination
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @changePage="onPage"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import { NButton, NCard, NDataTable, NInput, NSelect, NSpace, NTag } from 'naive-ui';
import Pagination from '../../components/Pagination.vue';
import { fetchAdminComments, approveComment, deleteAdminComment } from '../../api/comments';
import { formatShortDateTime } from '../../utils/format';

const rows = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(20);
const statusFilter = ref(null);
const userIdFilter = ref('');

const statusOptions = [
  { label: '全部', value: null },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
];

const columns = [
  { title: 'ID', key: 'id', width: 56 },
  { title: '文章', key: 'articleId', width: 72 },
  { title: '用户', key: 'userId', width: 72, render: row => row.userId ?? '—' },
  { title: '作者', key: 'author', width: 88, ellipsis: { tooltip: true } },
  { title: '内容', key: 'content', minWidth: 160, ellipsis: { tooltip: true } },
  {
    title: '状态',
    key: 'status',
    width: 90,
    render(row) {
      if (row.status === 0) return h(NTag, { type: 'warning', bordered: false }, () => '待审核');
      if (row.status === 1) return h(NTag, { type: 'success', bordered: false }, () => '已通过');
      return h(NTag, { type: 'default', bordered: false }, () => String(row.status));
    },
  },
  {
    title: '时间',
    key: 'createTime',
    width: 120,
    render: row => formatShortDateTime(row.createTime),
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    fixed: 'right',
    render(row) {
      const actions = [];
      if (row.status === 0) {
        actions.push(
          h(
            NButton,
            {
              size: 'small',
              type: 'success',
              secondary: true,
              onClick: () => approve(row.id),
            },
            () => '通过'
          )
        );
      }
      actions.push(
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            secondary: true,
            onClick: () => remove(row.id),
          },
          () => '删除'
        )
      );
      return h(NSpace, { size: 8 }, () => actions);
    },
  },
];

async function reload() {
  const params = { page: page.value, size: pageSize.value };
  if (statusFilter.value !== null) params.status = statusFilter.value;
  const uid = Number(userIdFilter.value);
  if (Number.isFinite(uid) && uid > 0) params.userId = uid;
  const res = await fetchAdminComments(params);
  const data = res.data;
  rows.value = data?.records || [];
  total.value = Number(data?.total) || 0;
}

function onPage(p) {
  page.value = p;
  reload();
}

async function approve(id) {
  await approveComment(id);
  reload();
}

async function remove(id) {
  if (!confirm('确定要删除这条评论吗？')) return;
  await deleteAdminComment(id);
  reload();
}

onMounted(() => reload());
</script>

<style scoped>
</style>
