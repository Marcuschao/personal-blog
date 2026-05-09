<template>
  <div class="login-page">
    <div class="login-bg" aria-hidden="true" />
    <div class="container login-wrap">
      <div class="login-card">
        <div class="login-card-glow" aria-hidden="true" />
        <h1 class="card-title">管理员登录</h1>
        <p class="card-hint">请输入凭据以管理文章</p>
        <form class="login-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <input id="username" v-model="username" type="text" required autocomplete="username" />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input
              id="password"
              v-model="password"
              type="password"
              required
              autocomplete="current-password"
            />
          </div>
          <button type="submit" class="submit-button" :disabled="isLoading">
            <span v-if="isLoading" class="btn-spinner" aria-hidden="true" />
            <span>{{ isLoading ? '登录中…' : '登录' }}</span>
          </button>
          <p v-if="error" :key="error" class="error-message">{{ error }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { login } from '../api/auth';

const username = ref('');
const password = ref('');
const isLoading = ref(false);
const error = ref(null);
const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await login({
      username: username.value,
      password: password.value,
    });

    if (response.data && response.data.token) {
      authStore.setToken(response.data.token);
      router.push({ name: 'AdminDashboard' });
    } else {
      error.value = '登录失败：未获取到有效的 Token。';
    }
  } catch (err) {
    console.error('Login Error:', err);
    error.value =
      err.response?.data?.message || '登录失败，请检查用户名和密码。';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - var(--nav-height) - 140px);
  padding: 2.5rem 0;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 70% 55% at 20% 30%, rgba(99, 102, 241, 0.14), transparent),
    radial-gradient(ellipse 60% 45% at 85% 65%, rgba(168, 85, 247, 0.11), transparent);
  pointer-events: none;
}

.login-wrap {
  position: relative;
  z-index: 1;
}

.login-card {
  position: relative;
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
  padding: 2.65rem 2.25rem 2.35rem;
  text-align: center;
  background: var(--color-surface-glass);
  backdrop-filter: blur(20px) saturate(165%);
  -webkit-backdrop-filter: blur(20px) saturate(165%);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md), 0 0 0 1px rgba(255, 255, 255, 0.5) inset;
}

.login-card-glow {
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: linear-gradient(
    135deg,
    rgba(99, 102, 241, 0.35),
    rgba(168, 85, 247, 0.12),
    transparent 55%
  );
  opacity: 0.45;
  z-index: -1;
  filter: blur(28px);
}

.card-title {
  margin: 0;
  font-size: 1.85rem;
  font-weight: 720;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.card-hint {
  margin: 0.5rem 0 1.85rem;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}

.login-form .form-group {
  margin-bottom: 1.35rem;
  text-align: left;
}

.login-form label {
  display: block;
  margin-bottom: 0.42rem;
  font-size: 0.82rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}

.login-form input[type='text'],
.login-form input[type='password'] {
  width: 100%;
  padding: 0.78rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 1rem;
  font-family: inherit;
  background: rgba(255, 255, 255, 0.9);
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast),
    background var(--transition-fast);
}

.login-form .form-group:focus-within input {
  border-color: rgba(79, 70, 229, 0.45);
  box-shadow: 0 0 0 4px var(--color-primary-soft);
}

.submit-button {
  width: 100%;
  margin-top: 0.35rem;
  padding: 0.95rem 1.25rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  border: none;
  border-radius: var(--radius-md);
  font-size: 1.02rem;
  font-weight: 650;
  font-family: inherit;
  color: #fff;
  cursor: pointer;
  background: var(--gradient-cta);
  box-shadow: 0 10px 28px rgba(79, 70, 229, 0.38);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast),
    opacity var(--transition-fast);
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 14px 36px rgba(79, 70, 229, 0.45);
}

.submit-button:active:not(:disabled) {
  transform: translateY(0);
}

.submit-button:disabled {
  opacity: 0.75;
  cursor: not-allowed;
  transform: none;
}

.btn-spinner {
  width: 1.05rem;
  height: 1.05rem;
  border-radius: var(--radius-pill);
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  animation: spin 0.72s linear infinite;
}

.error-message {
  margin-top: 1.05rem;
  padding: 0.65rem 0.85rem;
  border-radius: var(--radius-md);
  background: rgba(239, 68, 68, 0.1);
  color: #b91c1c;
  font-size: 0.88rem;
  font-weight: 500;
  animation: shake-soft 0.45s var(--ease-out-soft);
}

@media (prefers-reduced-motion: reduce) {
  .submit-button:hover:not(:disabled) {
    transform: none;
  }

  .btn-spinner {
    animation: spin 1.4s linear infinite;
  }

  .error-message {
    animation: none;
  }
}
</style>
