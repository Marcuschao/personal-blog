<template>
  <div class="admin-stream-page admin-page">
    <div class="container">
      <header class="dash-header ds-admin-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="ds-page-title">消息监控</h1>
          <p class="ds-page-sub">RabbitMQ 通知队列状态</p>
        </div>
        <router-link to="/admin">
          <n-button>返回文章管理</n-button>
        </router-link>
      </header>

      <n-card class="stream-panel" style="max-width: 36rem;">
        <p v-if="!status" style="color: var(--color-text-muted);">加载中…</p>
        <template v-else>
          <n-space vertical :size="12">
            <div>
              通知 MQ：
              <n-tag :type="status.enabled ? 'success' : 'default'" :bordered="false">
                {{ status.enabled ? '已启用' : '未启用' }}
              </n-tag>
            </div>
            <div>
              连接状态：
              <n-tag :type="status.connected ? 'success' : 'error'" :bordered="false">
                {{ status.connected ? '正常' : '不可用' }}
              </n-tag>
            </div>
            <div style="color: var(--color-text-muted);">
              交换机：{{ status.exchange }}
            </div>
            <div v-if="status.queues?.length">
              <div style="font-weight: 600; margin-bottom: 8px;">监控队列：</div>
              <n-list bordered>
                <n-list-item v-for="q in status.queues" :key="q">
                  {{ q }}
                </n-list-item>
              </n-list>
            </div>
          </n-space>
        </template>
      </n-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { NButton, NCard, NList, NListItem, NSpace, NTag } from 'naive-ui';
import { fetchNotificationMqStatus } from '../../api/notificationMq';

const status = ref(null);

onMounted(async () => {
  try {
    status.value = await fetchNotificationMqStatus();
  } catch {
    status.value = { enabled: false, connected: false, exchange: '-', queues: [] };
  }
});
</script>

<style scoped>
</style>
