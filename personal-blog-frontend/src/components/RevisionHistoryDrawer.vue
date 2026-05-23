<template>
  <n-drawer
    :show="modelValue"
    @update:show="close"
    :width="min(540, 100)"
    placement="right"
    resizable
  >
    <n-drawer-content title="历史版本" closable>
      <div v-if="loading" style="color: var(--color-text-muted);">加载中…</div>
      <div v-else-if="!revisions.length" style="color: var(--color-text-muted);">
        暂无历史版本（保存后将自动生成）。
      </div>

      <template v-else>
        <div style="margin-bottom: 24px;">
          <n-list hoverable clickable bordered>
            <n-list-item
              v-for="r in revisions"
              :key="r.id"
              :style="previewId === r.id ? { backgroundColor: 'var(--color-primary-soft)' } : {}"
              @click="selectPreview(r.id)"
            >
              <n-space justify="space-between" align="center">
                <n-space align="center">
                  <n-tag type="info" size="small" :bordered="false">#{{ r.revisionNo }}</n-tag>
                  <span style="font-size: 0.9em; color: var(--color-text-muted);">{{ formatTime(r.createdAt) }}</span>
                </n-space>
                <span style="font-size: 0.9em; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                  {{ r.remark || '—' }}
                </span>
              </n-space>
            </n-list-item>
          </n-list>
        </div>

        <div v-if="previewDetail" style="margin-bottom: 24px; padding: 16px; background-color: var(--surface-muted); border-radius: 8px;">
          <h3 style="margin-bottom: 12px; font-weight: 600;">预览版本 #{{ revisions.find(r => r.id === previewId)?.revisionNo }}</h3>
          <template v-if="kind === 'article'">
            <p style="margin-bottom: 8px;"><strong>标题:</strong> {{ previewDetail.title || '—' }}</p>
            <p style="margin-bottom: 8px;"><strong>摘要:</strong> {{ previewDetail.summary || '—' }}</p>
            <p style="margin-bottom: 12px;"><strong>标签:</strong> {{ previewDetail.articleTags || '—' }}</p>
          </template>
          <template v-else>
            <p style="margin-bottom: 8px;"><strong>标题:</strong> {{ previewDetail.title || '—' }}</p>
            <p style="margin-bottom: 8px;"><strong>日期:</strong> {{ previewDetail.diaryDate || '—' }}</p>
            <p style="margin-bottom: 12px;"><strong>标签:</strong> {{ previewDetail.diaryTags || '—' }}</p>
          </template>

          <n-input
            type="textarea"
            :rows="10"
            readonly
            :value="previewDetail.content || ''"
            style="margin-bottom: 16px;"
          />

          <n-button type="primary" secondary :loading="restoreBusy" @click="doRestore" block>
            回退到此版本
          </n-button>
        </div>

        <div style="border-top: 1px solid var(--color-border); padding-top: 16px;">
          <p style="font-weight: 600; margin-bottom: 12px;">对比正文（按行）</p>
          <n-space vertical :size="12">
            <n-space justify="space-between" align="center" :wrap="false">
              <n-select
                v-model:value="compareA"
                :options="compareOptions"
                placeholder="版本 A"
                style="width: 45%;"
              />
              <span style="color: var(--color-text-muted);">VS</span>
              <n-select
                v-model:value="compareB"
                :options="compareOptions"
                placeholder="版本 B"
                style="width: 45%;"
              />
            </n-space>
            <n-button
              type="primary"
              block
              :disabled="!compareA || !compareB || compareA === compareB"
              :loading="diffBusy"
              @click="runDiff"
            >
              对比差异
            </n-button>
          </n-space>
        </div>
      </template>

      <n-modal
        v-model:show="showDiffModal"
        preset="card"
        style="width: min(1000px, 100%)"
        title="并排差异对比"
      >
        <RevisionDiffSideBySide :lines="diffLines" @close="diffLines = []" />
      </n-modal>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import {
  NDrawer,
  NDrawerContent,
  NButton,
  NList,
  NListItem,
  NTag,
  NSpace,
  NInput,
  NSelect,
  NModal,
} from 'naive-ui';
import {
  listArticleVersions,
  getArticleVersion,
  restoreArticleVersion,
  diffArticleVersions,
} from '../api/article';
import {
  listDiaryVersions,
  getDiaryVersion,
  restoreDiaryVersion,
  diffDiaryVersions,
} from '../api/diary';
import RevisionDiffSideBySide from './RevisionDiffSideBySide.vue';
import { useToastStore } from '../stores/toast';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  kind: { type: String, required: true },
  resourceId: { type: [Number, String], default: null },
});

const emit = defineEmits(['update:modelValue', 'restored']);

const toastStore = useToastStore();

const loading = ref(false);
const revisions = ref([]);
const previewId = ref(null);
const previewDetail = ref(null);
const restoreBusy = ref(false);
const diffBusy = ref(false);
const compareA = ref(null);
const compareB = ref(null);
const diffLines = ref([]);

const showDiffModal = computed({
  get: () => diffLines.value.length > 0,
  set: (val) => { if (!val) diffLines.value = []; }
});

const compareOptions = computed(() => {
  return revisions.value.map(r => ({
    label: `#${r.revisionNo} · ${formatTime(r.createdAt)}`,
    value: String(r.id),
  }));
});

function min(val, pct) {
  return typeof window !== 'undefined' ? Math.min(val, window.innerWidth) : val;
}

function close() {
  emit('update:modelValue', false);
}

function formatTime(t) {
  if (!t) return '';
  const s = typeof t === 'string' ? t : '';
  return s.replace('T', ' ').slice(0, 19);
}

async function loadList() {
  const id = props.resourceId;
  if (id == null || id === '') return;
  loading.value = true;
  previewId.value = null;
  previewDetail.value = null;
  compareA.value = null;
  compareB.value = null;
  diffLines.value = [];
  try {
    let res;
    if (props.kind === 'article') {
      res = await listArticleVersions(id);
    } else {
      res = await listDiaryVersions(id);
    }
    revisions.value = Array.isArray(res.data) ? res.data : [];
  } catch {
    revisions.value = [];
  } finally {
    loading.value = false;
  }
}

async function selectPreview(versionId) {
  previewId.value = versionId;
  previewDetail.value = null;
  const id = props.resourceId;
  try {
    let res;
    if (props.kind === 'article') {
      res = await getArticleVersion(id, versionId);
    } else {
      res = await getDiaryVersion(id, versionId);
    }
    previewDetail.value = res.data || null;
  } catch {
    previewDetail.value = null;
  }
}

async function doRestore() {
  if (!previewId.value || !previewDetail.value) return;
  if (!window.confirm('确定将当前内容回退到该版本？将自动生成新的版本记录。')) return;
  restoreBusy.value = true;
  try {
    const id = props.resourceId;
    if (props.kind === 'article') {
      await restoreArticleVersion(id, previewId.value);
    } else {
      await restoreDiaryVersion(id, previewId.value);
    }
    toastStore.push('已回退', 'success');
    emit('restored');
    await loadList();
    previewDetail.value = null;
    previewId.value = null;
  } catch (e) {
    toastStore.push(e?.message || '回退失败', 'error');
  } finally {
    restoreBusy.value = false;
  }
}

async function runDiff() {
  if (!compareA.value || !compareB.value || compareA.value === compareB.value) return;
  diffBusy.value = true;
  diffLines.value = [];
  try {
    const id = props.resourceId;
    let res;
    if (props.kind === 'article') {
      res = await diffArticleVersions(id, compareA.value, compareB.value);
    } else {
      res = await diffDiaryVersions(id, compareA.value, compareB.value);
    }
    const payload = res.data || {};
    diffLines.value = Array.isArray(payload.lines) ? payload.lines : [];
    if (!diffLines.value.length) {
      toastStore.push('无差异或对比失败', 'error');
    }
  } catch (e) {
    toastStore.push(e?.message || '对比失败', 'error');
  } finally {
    diffBusy.value = false;
  }
}

watch(
  () => [props.modelValue, props.resourceId, props.kind],
  ([open]) => {
    if (open) loadList();
  }
);
</script>

<style scoped>
</style>
