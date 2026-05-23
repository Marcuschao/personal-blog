<template>
  <div class="fresh-page admin-page">
    <div class="container">
      <header class="fresh-head ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">内容保鲜</h1>
          <p class="ds-page-sub">陈旧文章扫描与 AI 草稿</p>
        </div>
        <n-space class="fresh-head-actions" :size="12">
          <router-link to="/admin">
            <n-button>返回管理</n-button>
          </router-link>
          <n-button type="primary" :loading="scanBusy" @click="runScan">
            立即全量扫描
          </n-button>
        </n-space>
      </header>

      <n-card v-if="summary" class="sum-card" style="margin-bottom: 24px;">
        <n-space align="center" :size="24">
          <n-tag type="success" :bordered="false">健康 {{ summary.healthyCount }}</n-tag>
          <n-tag type="warning" :bordered="false">预警 {{ summary.warnCount }}</n-tag>
          <n-tag type="error" :bordered="false">严重 {{ summary.severeCount }}</n-tag>
          <span v-if="summary.lastFullScanAt" class="last scan-time">
            上次扫描：{{ summary.lastFullScanAt }}
          </span>
        </n-space>
      </n-card>

      <n-card :bordered="true" style="margin-bottom: 24px;">
        <n-space class="filter-row" align="center" :size="16">
          <div style="display: flex; align-items: center;">
            <span style="margin-right: 8px;">状态</span>
            <n-select v-model:value="statusFilter" style="width: 140px;" :options="statusOptions" @update:value="reload" />
          </div>
        </n-space>
      </n-card>

      <n-modal v-model:show="showDraftModal" preset="card" style="width: min(720px, 100%)" title="AI 草稿预览">
        <div v-if="draftBox" class="draft-box">
          <h3 style="margin-bottom: 8px;">ID: {{ draftBox.id }}</h3>
          <p class="draft-t" style="font-weight: 600; font-size: 1.1em; margin-bottom: 8px;">标题: {{ draftBox.title }}</p>
          <p class="draft-s" style="color: var(--color-text-muted); margin-bottom: 16px;">摘要: {{ draftBox.summary }}</p>
          <pre class="draft-c" style="background-color: var(--surface-muted); padding: 12px; border-radius: 6px; overflow: auto; max-height: 300px; font-family: monospace;">{{ draftBox.content }}</pre>
        </div>
      </n-modal>

      <n-card :bordered="true">
        <n-data-table
          :columns="columns"
          :data="rows"
          :bordered="false"
          :single-line="false"
          :scroll-x="720"
        />
      </n-card>

      <Pagination
        v-if="totalPages > 1"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @changePage="onPageChange"
      />
      
      <n-alert v-if="err" type="error" style="margin-top: 16px;">{{ err }}</n-alert>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue';
import { useRouter } from 'vue-router';
import {
  NAlert,
  NButton,
  NCard,
  NDataTable,
  NModal,
  NSelect,
  NSpace,
  NTag,
} from 'naive-ui';
import Pagination from '../../components/Pagination.vue';
import {
  getFreshnessSummary,
  getFreshnessArticles,
  postFreshnessScanRun,
  postFreshnessAiRefresh,
  postFreshnessForkDraft,
} from '../../api/freshness';

const router = useRouter();
const summary = ref(null);
const rows = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(12);
const statusFilter = ref('');
const scanBusy = ref(false);
const busyId = ref(null);
const draftBox = ref(null);
const err = ref('');

const showDraftModal = computed({
  get: () => !!draftBox.value,
  set: (val) => { if (!val) draftBox.value = null; }
});

const statusOptions = [
  { label: '全部', value: '' },
  { label: '正常', value: '0' },
  { label: '预警', value: '1' },
  { label: '严重', value: '2' },
];

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value) || 1));

const columns = [
  { title: 'ID', key: 'id', width: 56 },
  { title: '标题', key: 'title', minWidth: 140, ellipsis: { tooltip: true } },
  {
    title: '保鲜度',
    key: 'freshnessStatus',
    width: 88,
    render(row) {
      if (row.freshnessStatus === 0) return h(NTag, { type: 'success', bordered: false }, () => '正常');
      if (row.freshnessStatus === 1) return h(NTag, { type: 'warning', bordered: false }, () => '预警');
      if (row.freshnessStatus === 2) return h(NTag, { type: 'error', bordered: false }, () => '严重');
      return h(NTag, { type: 'default', bordered: false }, () => String(row.freshnessStatus));
    },
  },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    fixed: 'right',
    render(row) {
      return h(NSpace, { size: 8 }, () => [
        h(
          NButton,
          {
            size: 'small',
            secondary: true,
            loading: busyId.value === row.id,
            onClick: () => aiRow(row.id),
          },
          () => 'AI'
        ),
        h(
          NButton,
          {
            size: 'small',
            secondary: true,
            type: 'primary',
            loading: busyId.value === row.id,
            onClick: () => forkRow(row.id),
          },
          () => '复制'
        ),
      ]);
    },
  },
];

async function loadSummary() {
  try {
    const res = await getFreshnessSummary();
    summary.value = res.data ?? null;
  } catch {
    summary.value = null;
  }
}

async function reload() {
  err.value = '';
  try {
    const params = { page: page.value, size: pageSize.value };
    if (statusFilter.value !== '') params.freshnessStatus = parseInt(statusFilter.value, 10);
    const res = await getFreshnessArticles(params);
    const pr = res.data;
    rows.value = pr?.records || [];
    total.value = Number(pr?.total) || 0;
  } catch (e) {
    err.value = e?.message || '加载失败';
    rows.value = [];
    total.value = 0;
  }
}

function onPageChange(p) {
  page.value = p;
  reload();
}

async function runScan() {
  scanBusy.value = true;
  err.value = '';
  try {
    await postFreshnessScanRun();
    await loadSummary();
    page.value = 1;
    await reload();
  } catch (e) {
    err.value = e?.message || '扫描失败';
  } finally {
    scanBusy.value = false;
  }
}

async function aiRow(id) {
  busyId.value = id;
  err.value = '';
  try {
    const res = await postFreshnessAiRefresh(id);
    draftBox.value = {
      id,
      title: res.data?.title || '',
      summary: res.data?.summary || '',
      content: res.data?.content || '',
    };
  } catch (e) {
    err.value = e?.message || '生成 AI 草稿失败';
  } finally {
    busyId.value = null;
  }
}

async function forkRow(id) {
  busyId.value = id;
  err.value = '';
  try {
    const res = await postFreshnessForkDraft(id);
    const nid = res.data?.draftArticleId || res.data?.id || res.data;
    if (nid) {
      router.push(`/admin/edit/${nid}`);
    } else {
      err.value = '没有获取到新建草稿 ID';
    }
  } catch (e) {
    err.value = e?.message || '复制失败';
  } finally {
    busyId.value = null;
  }
}

onMounted(() => {
  loadSummary();
  reload();
});
</script>

<style scoped>
.scan-time {
  color: var(--color-text-muted);
  font-size: var(--text-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}
</style>