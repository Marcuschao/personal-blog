<template>
  <div class="admin-stream-page admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header">
        <div>
          <h1 class="ds-page-title">消息监控</h1>
          <p class="ds-page-sub">Redis Stream 待处理与死信队列</p>
        </div>
        <router-link to="/admin" class="ds-btn ds-btn--secondary ds-btn--pill">返回文章管理</router-link>
      </header>

      <section class="ds-empty-panel stream-panel">
        <p v-if="!pending.enabled">Stream 未启用（blog.stream.enabled=false）</p>
        <template v-else>
          <p>待处理消息：<strong>{{ pending.pendingCount }}</strong></p>
          <p v-if="pending.oldestIdleMs != null" class="sub">
            最久未 ACK：{{ pending.oldestIdleMs }} ms
          </p>
        </template>
        <p class="sub">死信数量：{{ deadList.length }}</p>
      </section>

      <section v-if="deadList.length" class="ds-table-shell stream-table">
        <table>
          <thead>
            <tr>
              <th>Record ID</th>
              <th>错误</th>
              <th>重试次数</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in deadList" :key="row.recordId">
              <td class="mono">{{ row.recordId }}</td>
              <td>{{ row.error || '—' }}</td>
              <td>{{ row.retryCount }}</td>
              <td>
                <button
                  type="button"
                  class="ds-btn ds-btn--secondary"
                  :disabled="retrying === row.recordId"
                  @click="onRetry(row.recordId)"
                >
                  {{ retrying === row.recordId ? '…' : '重试' }}
                </button>
                <button type="button" class="ds-btn ds-btn--secondary" @click="toggle(row.recordId)">
                  {{ expanded === row.recordId ? '收起' : '详情' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <pre v-for="row in deadList" v-show="expanded === row.recordId" :key="'d-' + row.recordId" class="json-pre">{{ row.eventJson }}</pre>
      </section>
      <div v-else class="ds-empty-panel">暂无死信</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { fetchStreamPending, fetchStreamDead, retryDeadLetter } from '../../api/stream';

const pending = ref({ enabled: true, pendingCount: 0, oldestIdleMs: null });
const deadList = ref([]);
const retrying = ref('');
const expanded = ref('');
let timer;

async function load() {
  try {
    pending.value = await fetchStreamPending();
  } catch {
    pending.value = { enabled: false, pendingCount: 0, oldestIdleMs: null };
  }
  try {
    deadList.value = await fetchStreamDead(50);
  } catch {
    deadList.value = [];
  }
}

async function onRetry(recordId) {
  retrying.value = recordId;
  try {
    await retryDeadLetter(recordId);
    await load();
  } finally {
    retrying.value = '';
  }
}

function toggle(id) {
  expanded.value = expanded.value === id ? '' : id;
}

onMounted(() => {
  load();
  timer = setInterval(load, 30000);
});

onUnmounted(() => clearInterval(timer));
</script>

<style scoped>
.stream-panel {
  text-align: left;
  margin-bottom: var(--space-4);
}

.sub {
  font-size: var(--text-sm);
  color: var(--color-text-soft);
  margin-top: var(--space-2);
}

.mono {
  font-family: ui-monospace, monospace;
  font-size: var(--text-xs);
}

.json-pre {
  margin: var(--space-3);
  padding: var(--space-3);
  background: var(--surface-muted);
  border-radius: 6px;
  font-size: var(--text-xs);
  overflow: auto;
  max-height: 240px;
}
</style>
