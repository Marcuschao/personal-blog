<template>
  <n-button
    v-if="show"
    size="small"
    :type="isFollowing ? 'default' : 'primary'"
    :loading="busy"
    @click="onToggle"
  >
    {{ isFollowing ? '已关注' : '+ 关注' }}
  </n-button>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { NButton } from 'naive-ui';
import { useAuthStore } from '../stores/auth';
import { toggleFollow } from '../api/interaction';

const props = defineProps({
  userId: { type: Number, required: true },
  following: { type: Boolean, default: false },
});

const emit = defineEmits(['update:following', 'toggled']);

const authStore = useAuthStore();
const busy = ref(false);
const isFollowing = ref(props.following);

watch(
  () => props.following,
  (v) => { isFollowing.value = v; }
);

const show = computed(() => {
  if (!authStore.isLoggedIn || !authStore.user?.id) return false;
  return authStore.user.id !== props.userId;
});

async function onToggle() {
  if (!authStore.isLoggedIn) return;
  busy.value = true;
  try {
    const res = await toggleFollow(props.userId);
    isFollowing.value = res.data?.following ?? !isFollowing.value;
    emit('update:following', isFollowing.value);
    emit('toggled', res.data);
  } finally {
    busy.value = false;
  }
}
</script>
