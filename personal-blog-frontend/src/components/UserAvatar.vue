<template>
  <n-avatar v-if="resolvedSrc" round :size="size" :src="resolvedSrc" />
  <n-avatar v-else round :size="size">{{ initial }}</n-avatar>
</template>

<script setup>
import { computed } from 'vue';
import { NAvatar } from 'naive-ui';
import { resolveMediaUrl } from '../utils/mediaUrl';

const props = defineProps({
  src: { type: String, default: '' },
  name: { type: String, default: '?' },
  size: { type: [Number, String], default: 40 },
});

const resolvedSrc = computed(() => resolveMediaUrl(props.src));
const initial = computed(() => {
  const text = String(props.name || '?').trim();
  return text.slice(0, 1) || '?';
});
</script>
