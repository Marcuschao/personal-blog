<template>
  <div class="not-found ds-page">
    <div class="container nf-inner" style="max-width: 480px; margin: 0 auto; text-align: center; padding: 48px 16px;">
      <n-empty size="huge" description="页面迷路了">
        <template #icon>
          <div style="color: var(--color-primary); font-size: 64px; font-weight: 800; font-family: monospace;">
            404
          </div>
        </template>
        <template #extra>
          <p style="color: var(--color-text-muted); margin-bottom: 24px;">链接可能过期，或地址敲错了。</p>
          <router-link to="/" style="text-decoration: none;">
            <n-button type="primary">返回首页</n-button>
          </router-link>
        </template>
      </n-empty>
    </div>
  </div>
</template>

<script setup>
import { NButton, NEmpty } from 'naive-ui';
</script>

<style scoped>
</style>
