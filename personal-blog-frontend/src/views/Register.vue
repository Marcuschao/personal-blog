<template>
  <div class="login-page">
    <div class="login-bg" aria-hidden="true" />
    <div class="container login-wrap">
      <div class="login-card">
        <div class="login-card-glow" aria-hidden="true" />
        <h1 class="card-title">注册账号</h1>
        <p class="card-hint">创建账号后可登录发表评论</p>
        <form class="login-form" novalidate @submit.prevent="handleRegister">
          <div class="form-group">
            <label class="ds-form-label" for="username">用户名</label>
            <input
              id="username"
              v-model="username"
              class="ds-input"
              :class="{ 'input-invalid': showErr('username') }"
              type="text"
              autocomplete="username"
              @blur="touch('username')"
              @input="touch('username')"
            />
            <p v-if="showErr('username')" class="field-error">{{ fieldErrors.username }}</p>
          </div>
          <div class="form-group">
            <label class="ds-form-label" for="email">邮箱</label>
            <input
              id="email"
              v-model="email"
              class="ds-input"
              :class="{ 'input-invalid': showErr('email') }"
              type="email"
              autocomplete="email"
              @blur="touch('email')"
              @input="touch('email')"
            />
            <p v-if="showErr('email')" class="field-error">{{ fieldErrors.email }}</p>
          </div>
          <div class="form-group password-row">
            <label class="ds-form-label" for="password">密码</label>
            <input
              id="password"
              v-model="password"
              class="ds-input"
              :class="{ 'input-invalid': showErr('password') }"
              type="password"
              autocomplete="new-password"
              @blur="touch('password')"
              @input="onPasswordInput"
            />
            <p v-if="showErr('password')" class="field-error">{{ fieldErrors.password }}</p>
          </div>
          <div class="form-group password-row">
            <label class="ds-form-label" for="confirmPassword">确认密码</label>
            <input
              id="confirmPassword"
              v-model="confirmPassword"
              class="ds-input"
              :class="{ 'input-invalid': showErr('confirmPassword') }"
              type="password"
              autocomplete="new-password"
              @blur="touch('confirmPassword')"
              @input="touch('confirmPassword')"
            />
            <p v-if="showErr('confirmPassword')" class="field-error">{{ fieldErrors.confirmPassword }}</p>
          </div>
          <div class="form-group captcha-row">
            <label class="ds-form-label" for="captchaCode">验证码</label>
            <div class="captcha-line">
              <input
                id="captchaCode"
                v-model="captchaCode"
                class="ds-input"
                :class="{ 'input-invalid': showErr('captchaCode') }"
                type="text"
                autocomplete="off"
                maxlength="8"
                placeholder="右侧图形中的字符"
                @blur="touch('captchaCode')"
                @input="touch('captchaCode')"
              />
              <button type="button" class="captcha-img-btn" title="点击刷新" @click="loadCaptcha">
                <img v-if="captchaSrc" :src="captchaSrc" alt="验证码" class="captcha-img" />
                <span v-else class="captcha-placeholder">加载中…</span>
              </button>
            </div>
            <p v-if="showErr('captchaCode')" class="field-error">{{ fieldErrors.captchaCode }}</p>
          </div>
          <button type="submit" class="submit-button ds-btn ds-btn--primary" :disabled="isLoading">
            <span v-if="isLoading" class="ds-spin-lg" aria-hidden="true" />
            <span>{{ isLoading ? '注册中…' : '注册' }}</span>
          </button>
          <p class="switch-link">已有账号？<router-link to="/login">去登录</router-link></p>
          <p v-if="error" :key="errorTick" class="error-message ds-error-box">{{ error }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { register, fetchCaptcha } from '../api/auth';
import { applyServerFieldErrors, validateRegisterField } from '../utils/formValidation';

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const captchaKey = ref('');
const captchaCode = ref('');
const captchaSrc = ref('');
const isLoading = ref(false);
const error = ref(null);
const errorTick = ref(0);
const router = useRouter();
const authStore = useAuthStore();

const touched = reactive({
  username: false,
  email: false,
  password: false,
  confirmPassword: false,
  captchaCode: false,
});

const fieldErrors = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  captchaCode: '',
});

const FIELDS = ['username', 'email', 'password', 'confirmPassword', 'captchaCode'];

function formValues() {
  return {
    username: username.value,
    email: email.value,
    password: password.value,
    confirmPassword: confirmPassword.value,
    captchaCode: captchaCode.value,
  };
}

function validateOne(field) {
  fieldErrors[field] = validateRegisterField(field, formValues());
}

function touch(field) {
  touched[field] = true;
  validateOne(field);
  if (field === 'password' && touched.confirmPassword) {
    validateOne('confirmPassword');
  }
}

function onPasswordInput() {
  touch('password');
}

function showErr(field) {
  return touched[field] && fieldErrors[field];
}

function validateAll() {
  FIELDS.forEach((f) => {
    touched[f] = true;
    validateOne(f);
  });
  return FIELDS.every((f) => !fieldErrors[f]);
}

async function loadCaptcha() {
  captchaCode.value = '';
  touched.captchaCode = false;
  fieldErrors.captchaCode = '';
  try {
    const d = await fetchCaptcha();
    captchaKey.value = d?.captchaKey || '';
    const raw = d?.imageBase64 || '';
    captchaSrc.value = raw.startsWith('data:') ? raw : `data:image/png;base64,${raw}`;
  } catch {
    captchaSrc.value = '';
    captchaKey.value = '';
    error.value = '验证码加载失败';
    errorTick.value += 1;
  }
}

onMounted(async () => {
  if (authStore.isLoggedIn) {
    router.replace({ name: 'Home' });
    return;
  }
  await loadCaptcha();
});

async function handleRegister() {
  error.value = null;
  if (!validateAll()) return;
  isLoading.value = true;
  try {
    const res = await register({
      username: username.value.trim(),
      email: email.value.trim(),
      password: password.value,
      confirmPassword: confirmPassword.value,
      captchaKey: captchaKey.value,
      captchaCode: captchaCode.value,
    });
    const data = res?.data;
    if (data?.token) {
      authStore.loginSuccess(data.token, data.role);
      try {
        await authStore.fetchMe();
      } catch {
        /* ignore */
      }
    }
    router.push({ name: 'Home' });
  } catch (err) {
    if (err?.payload && typeof err.payload === 'object') {
      Object.assign(fieldErrors, applyServerFieldErrors(fieldErrors, err.payload));
      FIELDS.forEach((f) => {
        if (fieldErrors[f]) touched[f] = true;
      });
    }
    error.value = err?.message || '注册失败';
    errorTick.value += 1;
    await loadCaptcha();
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - var(--layout-main-pad-top) - 140px);
  padding: 2.5rem 0;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 70% 55% at 20% 30%, rgba(37, 99, 235, 0.14), transparent),
    radial-gradient(ellipse 60% 45% at 85% 65%, rgba(63, 63, 70, 0.08), transparent);
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
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.35), rgba(63, 63, 70, 0.12), transparent 55%);
  opacity: 0.45;
  z-index: -1;
  filter: blur(28px);
}

.card-title {
  margin: 0;
  font-size: var(--text-display-sm);
  font-weight: 720;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.card-hint {
  margin: var(--space-2) 0 var(--space-8);
  font-size: var(--text-base);
  color: var(--color-text-muted);
}

.login-form .form-group {
  margin-bottom: var(--space-5);
  text-align: left;
}

.login-form .ds-input {
  background: rgba(255, 255, 255, 0.9);
}

.input-invalid {
  border-color: var(--color-danger);
}

.field-error {
  margin: var(--space-1) 0 0;
  font-size: var(--text-xs);
  color: var(--color-danger);
  line-height: 1.4;
}

.captcha-line {
  display: flex;
  gap: var(--space-2);
  align-items: stretch;
}

.captcha-line .ds-input {
  flex: 1;
  min-width: 0;
}

.captcha-img-btn {
  flex-shrink: 0;
  padding: 0;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.95);
  cursor: pointer;
  overflow: hidden;
  height: 2.85rem;
  width: 7.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-placeholder {
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}

.login-form .form-group:focus-within .ds-input:not(.input-invalid) {
  border-color: var(--border-focus-input);
  box-shadow: 0 0 0 4px var(--color-primary-soft);
}

.submit-button {
  width: 100%;
  margin-top: var(--space-2);
  padding: 0.95rem var(--space-5);
  font-size: var(--text-lg);
}

.ds-error-box.error-message {
  margin-top: var(--space-5);
}

.switch-link {
  margin-top: var(--space-4);
  font-size: var(--text-sm);
  color: var(--color-text-muted);
}

.switch-link a {
  color: var(--color-primary);
  font-weight: var(--weight-semibold);
}
</style>
