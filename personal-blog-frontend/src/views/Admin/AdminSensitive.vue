<template>
  <div class="admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">敏感词管理</h1>
          <p class="ds-page-sub">聊天与评论通用过滤</p>
        </div>
        <router-link to="/admin">
          <n-button>返回管理</n-button>
        </router-link>
      </header>

      <n-card :bordered="true" style="margin-bottom: 16px;">
        <n-space :size="12" align="center">
          <n-input v-model:value="newWord" placeholder="输入敏感词" style="width: 220px;" @keyup.enter="onAdd" />
          <n-button type="primary" @click="onAdd">添加</n-button>
          <n-button @click="onReload">重新加载</n-button>
        </n-space>
      </n-card>

      <n-card :bordered="true">
        <n-data-table :columns="columns" :data="rows" :bordered="false" :scroll-x="480" />
        <n-empty v-if="!rows.length && !loading" description="暂无敏感词" />
      </n-card>

      <Pagination :total="total" :page-size="pageSize" :current-page="page" @changePage="onPage" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import { NButton, NCard, NDataTable, NEmpty, NInput, NSpace, useMessage } from 'naive-ui';
import Pagination from '../../components/Pagination.vue';
import { addSensitiveWord, deleteSensitiveWord, fetchSensitiveWords, reloadSensitiveWords } from '../../api/sensitive';
import { formatShortDateTime } from '../../utils/format';

const message = useMessage();
const rows = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(20);
const loading = ref(false);
const newWord = ref('');

const columns = [
  { title: 'ID', key: 'id', width: 72 },
  { title: '敏感词', key: 'word', minWidth: 160 },
  {
    title: '创建时间',
    key: 'createTime',
    width: 140,
    render: (row) => formatShortDateTime(row.createTime),
  },
  {
    title: '操作',
    key: 'actions',
    width: 88,
    render(row) {
      return h(
        NButton,
        { size: 'small', type: 'error', secondary: true, onClick: () => onDelete(row.id) },
        () => '删除'
      );
    },
  },
];

async function load() {
  loading.value = true;
  try {
    const res = await fetchSensitiveWords({ page: page.value, size: pageSize.value });
    rows.value = res.records || [];
    total.value = res.total || 0;
  } catch {
    rows.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

async function onAdd() {
  const word = newWord.value.trim();
  if (!word) return;
  try {
    await addSensitiveWord(word);
    newWord.value = '';
    message.success('已添加');
    await load();
  } catch (err) {
    message.error(err?.message || '添加失败');
  }
}

async function onDelete(id) {
  try {
    await deleteSensitiveWord(id);
    message.success('已删除');
    await load();
  } catch (err) {
    message.error(err?.message || '删除失败');
  }
}

async function onReload() {
  try {
    await reloadSensitiveWords();
    message.success('已重新加载');
  } catch (err) {
    message.error(err?.message || '加载失败');
  }
}

function onPage(p) {
  page.value = p;
  load();
}

onMounted(load);
</script>
