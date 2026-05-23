<template>
  <div class="diff-ss-root">
    <div class="diff-ss-head" style="display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--color-border);">
      <span class="diff-ss-title" style="font-weight: 600; font-size: 1.1em;">并排对比</span>
      <n-button size="small" secondary @click="$emit('close')">
        关闭
      </n-button>
    </div>
    <div class="diff-ss-scroll" style="max-height: 60vh; overflow: auto;">
      <div v-for="(ln, idx) in lines" :key="idx" class="diff-ss-row" :class="rowClass(ln.type)">
        <span class="diff-ss-no">{{ ln.leftLineNo ?? '' }}</span>
        <div class="diff-ss-cell diff-ss-left">{{ ln.type === 'INSERT' ? '' : ln.text }}</div>
        <span class="diff-ss-no">{{ ln.rightLineNo ?? '' }}</span>
        <div class="diff-ss-cell diff-ss-right">{{ ln.type === 'DELETE' ? '' : ln.text }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { NButton } from 'naive-ui';

defineProps({
  lines: {
    type: Array,
    default: () => [],
  },
});

defineEmits(['close']);

function rowClass(t) {
  const x = (t || '').toUpperCase();
  return {
    'diff-ss-equal': x === 'EQUAL',
    'diff-ss-del': x === 'DELETE',
    'diff-ss-ins': x === 'INSERT',
  };
}
</script>

<style scoped>
.diff-ss-root {
  display: flex;
  flex-direction: column;
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.diff-ss-scroll {
  font-family: ui-monospace, 'SF Mono', Menlo, Monaco, Consolas, monospace;
  font-size: 0.78rem;
  line-height: 1.45;
}

.diff-ss-row {
  display: grid;
  grid-template-columns: 2.25rem 1fr 2.25rem 1fr;
  gap: 0;
  border-bottom: 1px solid var(--color-border);
}

.diff-ss-no {
  padding: 0.2rem 0.35rem;
  text-align: right;
  color: var(--color-text-muted);
  background: rgba(248, 250, 252, 0.85);
  user-select: none;
}

.diff-ss-cell {
  padding: 0.25rem 0.5rem;
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--color-text);
}

.diff-ss-equal .diff-ss-cell {
  background: rgba(241, 245, 249, 0.65);
}

.diff-ss-del .diff-ss-left {
  background: rgba(254, 226, 226, 0.85);
}

.diff-ss-ins .diff-ss-right {
  background: rgba(220, 252, 231, 0.9);
}

@media (max-width: 720px) {
  .diff-ss-row {
    grid-template-columns: 1.75rem 1fr 1.75rem 1fr;
    font-size: 0.72rem;
  }
}
</style>
