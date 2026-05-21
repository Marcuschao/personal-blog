<template>
  <div v-if="!online" class="offline-banner" role="status">当前离线，部分内容可能不可用</div>
  <div v-else-if="showRecovered" class="offline-banner offline-banner--ok" role="status">网络已恢复</div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const online = ref(navigator.onLine);
const showRecovered = ref(false);

let hideTimer;

function onOffline() {
  online.value = false;
}

function onOnline() {
  online.value = true;
  showRecovered.value = true;
  clearTimeout(hideTimer);
  hideTimer = setTimeout(() => {
    showRecovered.value = false;
  }, 2800);
}

onMounted(() => {
  window.addEventListener('offline', onOffline);
  window.addEventListener('online', onOnline);
});

onUnmounted(() => {
  window.removeEventListener('offline', onOffline);
  window.removeEventListener('online', onOnline);
  clearTimeout(hideTimer);
});
</script>

<style scoped>
.offline-banner {
  position: sticky;
  top: 0;
  z-index: 11000;
  padding: 0.55rem 1rem;
  text-align: center;
  font-size: 0.82rem;
  font-weight: 650;
  background: var(--color-warn-soft);
  color: var(--color-warn);
  border-bottom: 1px solid var(--color-border);
}

.offline-banner--ok {
  background: var(--color-success-soft);
  color: var(--color-success);
}
</style>
