<template>
  <n-list-item>
    <template #prefix>
      <router-link :to="`/user/${user.id}`">
        <UserAvatar :src="user.avatar" :name="user.nickname" :size="40" />
      </router-link>
    </template>
    <router-link :to="`/user/${user.id}`" class="name">{{ user.nickname || '用户' }}</router-link>
    <template #suffix>
      <FollowButton :user-id="user.id" :following="user.following" @toggled="$emit('follow-changed')" />
    </template>
  </n-list-item>
</template>

<script setup>
import { NListItem } from 'naive-ui';
import FollowButton from './FollowButton.vue';
import UserAvatar from './UserAvatar.vue';

defineProps({
  user: { type: Object, required: true },
});

defineEmits(['follow-changed']);
</script>

<style scoped>
.name {
  font-weight: var(--weight-semibold);
  font-size: var(--text-sm);
  text-decoration: none;
  color: var(--color-text);
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 12rem;
}
</style>
