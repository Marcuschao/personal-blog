<template>
  <div class="tr-page admin-page">
    <div class="container">
      <header class="ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">批量翻译</h1>
          <p class="ds-page-sub">机翻任务 · en / ja / ko</p>
        </div>
        <router-link to="/admin">
          <n-button>返回管理</n-button>
        </router-link>
      </header>

      <n-card class="panel ds-surface-card" style="max-width: 48rem;">
        <n-form-item label="文章 ID（逗号或空格分隔）">
          <n-input v-model:value="idsRaw" type="textarea" :rows="3" placeholder="1, 2, 3" />
        </n-form-item>

        <n-form-item label="语种" style="margin-bottom: 24px;">
          <n-checkbox-group v-model:value="picked">
            <n-space>
              <n-checkbox v-for="loc in locales" :key="loc" :value="loc">
                {{ loc }}
              </n-checkbox>
            </n-space>
          </n-checkbox-group>
        </n-form-item>

        <n-button type="primary" :loading="starting" @click="start">
          启动批量机翻
        </n-button>
      </n-card>

      <n-card v-if="jobId" class="job-panel" style="max-width: 48rem; margin-top: 24px;">
        <div class="job-id" style="font-weight: 600; font-size: 1.1em; margin-bottom: 12px;">任务 {{ jobId }}</div>
        <div v-if="job" class="job-st">
          <n-space align="center" :size="24">
            <n-tag :type="job.state === 'DONE' ? 'success' : job.state === 'FAILED' ? 'error' : 'warning'">
              状态 {{ job.state }}
            </n-tag>
            <n-progress
              type="circle"
              :percentage="job.total ? Math.round((job.processed / job.total) * 100) : 0"
              style="width: 60px;"
            />
            <span>已处理 {{ job.processed }} / 共 {{ job.total }}</span>
          </n-space>
        </div>
        <n-alert v-if="job?.errorMessage" type="error" style="margin-top: 16px;">
          {{ job.errorMessage }}
        </n-alert>
      </n-card>

      <n-alert v-if="err" type="error" style="margin-top: 16px; max-width: 48rem;">{{ err }}</n-alert>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue';
import { NButton, NCard, NCheckboxGroup, NCheckbox, NFormItem, NInput, NSpace, NTag, NProgress, NAlert } from 'naive-ui';
import { batchTranslate, getTranslationJob } from '../../api/translation';

const locales = ['en', 'ja', 'ko'];
const idsRaw = ref('');
const picked = ref(['en']);
const starting = ref(false);
const jobId = ref('');
const job = ref(null);
const err = ref('');
let timer = null;

function parseIds() {
  return idsRaw.value
    .split(/[,，\s]+/)
    .map((s) => parseInt(s.trim(), 10))
    .filter((n) => Number.isFinite(n));
}

async function pollOnce() {
  if (!jobId.value) return;
  try {
    const res = await getTranslationJob(jobId.value);
    job.value = res.data ?? null;
    const st = job.value?.state;
    if (st === 'DONE' || st === 'FAILED') stopPoll();
  } catch {
    stopPoll();
  }
}

function stopPoll() {
  if (timer) clearInterval(timer);
  timer = null;
}

async function start() {
  err.value = '';
  stopPoll();
  job.value = null;
  const articleIds = parseIds();
  const locs = picked.value.slice();
  if (!articleIds.length || !locs.length) {
    err.value = '请填写文章 ID 并选择语种';
    return;
  }
  starting.value = true;
  try {
    const res = await batchTranslate({ articleIds, locales: locs });
    jobId.value = res.data || '';
    await pollOnce();
    timer = setInterval(pollOnce, 2000);
  } catch (e) {
    err.value = e?.message || '启动失败';
  } finally {
    starting.value = false;
  }
}

onUnmounted(stopPoll);
</script>

<style scoped>
</style>
