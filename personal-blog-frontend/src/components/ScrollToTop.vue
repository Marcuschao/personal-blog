<template>
  <button
    v-show="visible"
    type="button"
    class="scroll-top-btn"
    aria-label="回到顶部"
    @click="scrollTop"
  >
    ↑
  </button>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const visible = ref(false);

function onScroll() {
  visible.value = (window.scrollY || 0) > (window.innerHeight || 600) * 0.85;
}

function scrollTop() {
  const reduce = window.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches;
  window.scrollTo({ top: 0, behavior: reduce ? 'auto' : 'smooth' });
}

onMounted(() => {
  onScroll();
  window.addEventListener('scroll', onScroll, { passive: true });
});
onUnmounted(() => window.removeEventListener('scroll', onScroll));
</script>

<style scoped>
.scroll-top-btn {
  position: fixed;
  right: 1rem;
  bottom: calc(5rem + env(safe-area-inset-bottom, 0px));
  z-index: 1310;
  width: 44px;
  height: 44px;
  border-radius: var(--radius-pill);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  color: var(--color-text);
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: var(--shadow-md);
}

.scroll-top-btn:hover {
  border-color: var(--color-border-strong);
}

@media (min-width: 768px) {
  .scroll-top-btn {
    bottom: calc(1.75rem + env(safe-area-inset-bottom, 0px));
  }
}
</style>
