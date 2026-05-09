<template>
  <div v-if="total > 0" class="pagination">
    <button
      type="button"
      class="nav"
      :disabled="currentPage <= 1"
      @click="go(currentPage - 1)"
    >
      上一页
    </button>
    <div class="pages">
      <button
        v-for="p in pageList"
        :key="p"
        type="button"
        :class="['page-btn', { active: p === currentPage }]"
        @click="go(p)"
      >
        {{ p }}
      </button>
    </div>
    <button
      type="button"
      class="nav"
      :disabled="currentPage >= totalPages"
      @click="go(currentPage + 1)"
    >
      下一页
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  total: { type: Number, default: 0 },
  pageSize: { type: Number, default: 10 },
  currentPage: { type: Number, default: 1 },
});

const emit = defineEmits(['changePage']);

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize) || 1));

const pageList = computed(() => {
  const tp = totalPages.value;
  const cur = props.currentPage;
  const s = new Set([1, tp]);
  for (let p = cur - 2; p <= cur + 2; p++) {
    if (p >= 1 && p <= tp) s.add(p);
  }
  return [...s].sort((a, b) => a - b);
});

const go = (page) => {
  if (page < 1 || page > totalPages.value || page === props.currentPage) return;
  emit('changePage', page);
};
</script>

<style scoped>
.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 0.65rem;
  margin-top: 2.75rem;
}

.nav {
  padding: 0.52rem 1.15rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-pill);
  background: var(--color-surface);
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 600;
  font-family: var(--font-ui);
  color: var(--color-text-muted);
  box-shadow: var(--shadow-xs);
  transition: background var(--transition-fast), color var(--transition-fast),
    border-color var(--transition-fast), transform var(--transition-fast),
    box-shadow var(--transition-fast);
}

.nav:hover:not(:disabled) {
  border-color: rgba(79, 70, 229, 0.35);
  color: var(--color-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.nav:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.nav:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.pages {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
  padding: 0.25rem;
  background: rgba(255, 255, 255, 0.65);
  border-radius: var(--radius-pill);
  border: 1px solid var(--color-border);
}

.page-btn {
  min-width: 2.5rem;
  height: 2.5rem;
  padding: 0 0.45rem;
  border: none;
  border-radius: var(--radius-pill);
  background: transparent;
  cursor: pointer;
  font-size: 0.88rem;
  font-weight: 600;
  font-family: var(--font-ui);
  color: var(--color-text-muted);
  transition: background var(--transition-fast), color var(--transition-fast),
    transform var(--transition-fast), box-shadow var(--transition-fast);
}

.page-btn:hover:not(.active) {
  background: var(--color-primary-soft);
  color: var(--color-primary);
}

.page-btn:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.page-btn.active {
  background: var(--gradient-cta);
  color: #fff;
  box-shadow: 0 6px 18px rgba(79, 70, 229, 0.35);
  transform: scale(1.02);
}

@media (prefers-reduced-motion: reduce) {
  .nav:hover:not(:disabled),
  .page-btn.active {
    transform: none;
  }
}
</style>
