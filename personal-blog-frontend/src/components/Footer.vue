<template>
  <footer class="footer">
    <div class="container footer-inner">
      <div class="footer-accent" aria-hidden="true" />
      <nav class="footer-actions" aria-label="页脚导航">
        <template v-for="item in footerNavItems" :key="item.key">
          <a
            v-if="item.kind === 'external'"
            :href="item.href"
            class="footer-link"
            target="_blank"
            rel="noopener noreferrer"
          >{{ item.label }}</a>
          <router-link v-else-if="item.kind === 'link'" :to="item.to" class="footer-link">{{ item.label }}</router-link>
          <button v-else type="button" class="footer-link footer-btn" @click="item.onClick">{{ item.label }}</button>
        </template>
      </nav>
      <p v-if="isIosSafari" class="pwa-hint">iOS：Safari 分享 → 添加到主屏幕；16.4+ 主屏幕应用才可能支持推送。</p>
      <form class="sub-form" @submit.prevent="onSubscribe">
        <label class="sr-only" for="sub-email">订阅邮箱</label>
        <input
          id="sub-email"
          v-model.trim="subEmail"
          type="email"
          class="ds-input sub-input"
          placeholder="邮件订阅（可选）"
          autocomplete="email"
        />
        <button type="submit" class="ds-btn ds-btn--secondary sub-btn" :disabled="subBusy">
          {{ subBusy ? '…' : '订阅' }}
        </button>
      </form>
      <p v-if="subMsg" class="sub-msg">{{ subMsg }}</p>
      <p class="copyright">&copy; 2026 晓晓博客 · All rights reserved.</p>
      <p class="uptime">{{ uptimeText }}</p>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { subscribeEmail } from '../api/subscribe';
import { useToastStore } from '../stores/toast';
import { useInstallPrompt } from '../composables/useInstallPrompt';
import { subscribeWebPush } from '../composables/useWebPush';

const { canPrompt, promptInstall, isIosSafari } = useInstallPrompt();

const pushBusy = ref(false);

const uptimeText = ref('');
const subEmail = ref('');
const subBusy = ref(false);
const subMsg = ref('');
const toastStore = useToastStore();

const LAUNCH = new Date(2026, 4, 1, 0, 0, 0);

const footerNavItems = computed(() => {
  const items = [
    { key: 'rss', kind: 'external', href: '/rss.xml', label: 'RSS' },
    { key: 'links', kind: 'link', to: '/links', label: '友链' },
    { key: 'search', kind: 'link', to: '/search', label: '搜索' },
  ];
  if (canPrompt.value) {
    items.push({ key: 'install', kind: 'button', label: '安装到桌面', onClick: onInstall });
  }
  items.push({
    key: 'push',
    kind: 'button',
    label: pushBusy.value ? '…' : '开启通知',
    onClick: onPush,
  });
  return items;
});

async function onInstall() {
  await promptInstall();
}

async function onPush() {
  if (pushBusy.value) return;
  pushBusy.value = true;
  const failMsg = {
    unsupported: '当前浏览器不支持推送',
    network: '无法连接推送服务',
    disabled: '站点暂未开启推送通知',
    denied: '您拒绝了通知权限',
  };
  try {
    const r = await subscribeWebPush();
    if (r.ok) {
      toastStore.push('已开启推送通知', 'success');
    } else {
      toastStore.push(failMsg[r.reason] || '开启推送失败', 'error');
    }
  } catch {
    /* axios 已 toast */
  } finally {
    pushBusy.value = false;
  }
}

async function onSubscribe() {
  subMsg.value = '';
  const e = subEmail.value.trim();
  if (!e) return;
  subBusy.value = true;
  try {
    await subscribeEmail(e);
    toastStore.push('已记录订阅邮箱', 'success');
    subEmail.value = '';
  } catch (err) {
    subMsg.value = err?.message || '';
  } finally {
    subBusy.value = false;
  }
}

function pad(n) {
  return String(n).padStart(2, '0');
}

function tick() {
  const now = Date.now();
  const start = LAUNCH.getTime();
  if (now < start) {
    uptimeText.value = '即将上线';
    return;
  }
  const ms = now - start;
  const sec = Math.floor(ms / 1000) % 60;
  const min = Math.floor(ms / 60000) % 60;
  const hour = Math.floor(ms / 3600000) % 24;
  const day = Math.floor(ms / 86400000);
  uptimeText.value = `博客已运行 ${day} 天 ${pad(hour)} 小时 ${pad(min)} 分 ${pad(sec)} 秒`;
}

let timer;

onMounted(() => {
  tick();
  timer = setInterval(tick, 1000);
});

onUnmounted(() => {
  clearInterval(timer);
});
</script>

<style scoped>
.footer {
  margin-top: auto;
  padding: 2.75rem 0;
  background: var(--gradient-footer);
  border-top: 1px solid var(--color-border);
  position: relative;
}

@media (max-width: 1023px) {
  .footer {
    margin-bottom: calc(25px + env(safe-area-inset-bottom, 0px));
  }
}

.footer-inner {
  text-align: center;
  position: relative;
}

.footer-actions {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
  max-width: 100%;
  overflow-x: auto;
  scrollbar-width: none;
}

.footer-actions::-webkit-scrollbar {
  display: none;
}

.footer-link {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
  font-size: var(--text-sm);
  font-weight: 600;
  line-height: 1.5;
  color: var(--color-primary);
  text-decoration: none;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.footer-link:hover {
  color: var(--color-primary-hover);
  text-decoration: underline;
}

.footer-btn {
  border: none;
  background: none;
  padding: 0;
}

.pwa-hint {
  font-size: 0.72rem;
  color: var(--color-text-soft);
  max-width: 26rem;
  margin: 0 auto 0.75rem;
  line-height: 1.45;
}

.sub-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  justify-content: center;
  align-items: center;
  margin-bottom: 0.65rem;
}

.sub-input {
  flex: 1 1 200px;
  max-width: 280px;
}

.sub-btn {
  flex-shrink: 0;
}

.sub-msg {
  font-size: 0.78rem;
  color: var(--color-danger);
  margin-bottom: 0.5rem;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.footer-accent {
  display: none;
}

.copyright {
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--color-text-muted);
  letter-spacing: 0.02em;
}

.uptime {
  margin-top: 0.65rem;
  font-size: 0.8rem;
  color: var(--color-text-soft);
  opacity: 0.9;
  font-variant-numeric: tabular-nums;
}

.footer p {
  margin: 0;
}
</style>
