<template>
  <n-pagination
    v-if="total > 0"
    v-model:page="page"
    :page-count="totalPages"
    :page-size="pageSize"
    show-quick-jumper
    class="pagination"
    @update:page="onPageChange"
  />
</template>

<script setup>
import { computed } from 'vue';
import { NPagination } from 'naive-ui';

const props = defineProps({
  total: { type: Number, default: 0 },
  pageSize: { type: Number, default: 10 },
  currentPage: { type: Number, default: 1 },
});

const emit = defineEmits(['changePage']);

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize) || 1));

const page = computed({
  get: () => props.currentPage,
  set: (v) => emit('changePage', v),
});

function onPageChange(p) {
  if (p >= 1 && p <= totalPages.value && p !== props.currentPage) {
    emit('changePage', p);
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: var(--space-14);
}

@media (max-width: 767px) {
  .pagination {
    margin-top: var(--space-6);
    padding: 0 var(--space-2);
  }
}
</style>
