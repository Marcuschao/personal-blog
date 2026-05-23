<template>
  <div class="admin-links-page admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">友情链接</h1>
          <p class="ds-page-sub">前台 /links 页面数据源</p>
        </div>
        <router-link to="/admin">
          <n-button>返回管理</n-button>
        </router-link>
      </header>

      <n-grid :cols="1" :x-gap="24" :y-gap="24" responsive="screen" item-responsive>
        <n-gi span="24 m:8">
          <n-card :title="editingId ? '编辑友链' : '新增友链'">
            <n-form-item label="名称">
              <n-input v-model:value="form.name" type="text" />
            </n-form-item>
            <n-form-item label="URL">
              <n-input v-model:value="form.url" type="text" />
            </n-form-item>
            <n-form-item label="Logo URL">
              <n-input v-model:value="form.logo" type="text" placeholder="可选" />
            </n-form-item>
            <n-space justify="space-between">
              <n-form-item label="排序" style="width: 120px;">
                <n-input-number v-model:value="form.sortOrder" :min="0" />
              </n-form-item>
              <n-form-item label="状态" style="width: 120px;">
                <n-select v-model:value="form.status" :options="statusOptions" />
              </n-form-item>
            </n-space>
            <n-space style="margin-top: 20px;">
              <n-button type="primary" @click="save">{{ editingId ? '更新' : '创建' }}</n-button>
              <n-button v-if="editingId" secondary @click="resetForm">取消编辑</n-button>
            </n-space>
          </n-card>
        </n-gi>

        <n-gi span="24 m:16">
          <n-card>
            <n-data-table
              :columns="columns"
              :data="rows"
              :bordered="false"
              :single-line="false"
              :scroll-x="640"
            />
          </n-card>
        </n-gi>
      </n-grid>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import {
  NButton,
  NCard,
  NDataTable,
  NFormItem,
  NGi,
  NGrid,
  NInput,
  NInputNumber,
  NSelect,
  NSpace,
  NTag,
} from 'naive-ui';
import {
  fetchAdminFriendLinks,
  createFriendLink,
  updateFriendLink,
  deleteFriendLink,
} from '../../api/links';

const rows = ref([]);
const editingId = ref(null);
const form = ref({
  name: '',
  url: '',
  logo: '',
  sortOrder: 0,
  status: 1,
});

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 },
];

const columns = [
  { title: '排序', key: 'sortOrder', width: 64, sorter: (a, b) => a.sortOrder - b.sortOrder },
  { title: '名称', key: 'name', width: 100, ellipsis: { tooltip: true } },
  { title: 'URL', key: 'url', minWidth: 140, ellipsis: { tooltip: true } },
  {
    title: '状态',
    key: 'status',
    width: 72,
    render(row) {
      if (row.status === 1) return h(NTag, { type: 'success', bordered: false }, () => '启用');
      return h(NTag, { type: 'error', bordered: false }, () => '停用');
    },
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    fixed: 'right',
    render(row) {
      return h(NSpace, { size: 8 }, () => [
        h(
          NButton,
          {
            size: 'small',
            secondary: true,
            onClick: () => edit(row),
          },
          () => '编辑'
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            secondary: true,
            onClick: () => remove(row.id),
          },
          () => '删除'
        ),
      ]);
    },
  },
];

async function load() {
  const res = await fetchAdminFriendLinks();
  rows.value = Array.isArray(res.data) ? res.data : [];
}

function resetForm() {
  editingId.value = null;
  form.value = { name: '', url: '', logo: '', sortOrder: 0, status: 1 };
}

function edit(row) {
  editingId.value = row.id;
  form.value = {
    name: row.name,
    url: row.url,
    logo: row.logo || '',
    sortOrder: row.sortOrder ?? 0,
    status: row.status ?? 1,
  };
}

async function save() {
  const payload = { ...form.value };
  if (editingId.value) {
    await updateFriendLink(editingId.value, payload);
  } else {
    await createFriendLink(payload);
  }
  resetForm();
  await load();
}

async function remove(id) {
  if (confirm('确定要删除这个友链吗？')) {
    await deleteFriendLink(id);
    await load();
  }
}

onMounted(() => load());
</script>

<style scoped>
</style>
