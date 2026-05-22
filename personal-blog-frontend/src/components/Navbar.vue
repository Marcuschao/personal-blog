<template>
  <header
    class="navbar"
    :class="{ scrolled: isScrolled, 'nav-hidden': hideNav, 'navbar-menu-open': isMenuOpen }"
  >
    <nav class="nav-inner" aria-label="主导航">
      <div class="container nav-row">
        <router-link to="/" class="logo" @click="closeMenu">晓晓博客</router-link>
        <button
          type="button"
          class="menu-toggle"
          :class="{ open: isMenuOpen }"
          aria-label="菜单"
          :aria-expanded="isMenuOpen"
          aria-controls="primary-nav"
          @click="toggleMenu"
        >
          <span class="bar"></span>
          <span class="bar"></span>
          <span class="bar"></span>
        </button>
        <ul id="primary-nav" class="nav-links" :class="{ open: isMenuOpen }">
          <li><router-link to="/" @click="closeMenu">首页</router-link></li>
          <li><router-link to="/archive" @click="closeMenu">归档</router-link></li>
          <li><router-link to="/tags" @click="closeMenu">标签</router-link></li>
          <li><router-link to="/search" @click="closeMenu">搜索</router-link></li>
          <li><router-link to="/links" @click="closeMenu">友链</router-link></li>
          <li><router-link to="/diary" @click="closeMenu">日记</router-link></li>
          <li><router-link to="/reading-history" @click="closeMenu">阅读记录</router-link></li>
          <li v-if="!authStore.isLoggedIn">
            <router-link to="/login" @click="closeMenu">登录</router-link>
          </li>
          <li v-if="!authStore.isLoggedIn">
            <router-link to="/register" @click="closeMenu">注册</router-link>
          </li>
          <li v-if="authStore.isLoggedIn" class="nav-user-wrap">
            <div ref="navUserWrapRef" class="nav-user-menu">
              <button
                type="button"
                class="nav-user-trigger"
                aria-haspopup="menu"
                :aria-expanded="userMenuOpen"
                @click.stop="toggleUserMenu"
              >
                <span v-if="navAvatarSrc" class="nav-avatar nav-avatar--img"><img :src="navAvatarSrc" alt="" /></span>
                <span v-else class="nav-avatar nav-avatar--letter">{{ navInitial }}</span>
                <span class="nav-username-short">{{ authStore.displayName }}</span>
              </button>
              <ul v-if="userMenuOpen" class="nav-user-dropdown" role="menu" @click.stop>
                <li><router-link to="/user/me" role="menuitem" @click="closeUserMenu">个人主页</router-link></li>
                <li v-if="authStore.isAdmin"><router-link to="/admin" class="nav-dropdown-admin">管理后台</router-link></li>
                <li><button type="button" class="nav-dropdown-logout" @click="handleLogoutFromMenu">退出</button></li>
              </ul>
            </div>
          </li>
          <li v-if="authStore.isAdmin" class="nav-admin-li-desktop">
            <router-link to="/admin" class="nav-admin" @click="closeMenu">管理</router-link>
          </li>
        </ul>
      </div>
    </nav>
    <transition name="backdrop-fade">
      <div
        v-if="isMenuOpen"
        class="nav-backdrop"
        aria-hidden="true"
        @click="closeMenu"
      />
    </transition>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const isMenuOpen = ref(false);
const isScrolled = ref(false);
const hideNav = ref(false);
const userMenuOpen = ref(false);
const navUserWrapRef = ref(null);
let lastY = 0;

const navAvatarSrc = computed(() => authStore.user?.avatar || '');
const navInitial = computed(() => {
  const n = authStore.displayName || authStore.user?.username || '?';
  return String(n).slice(0, 1);
});

const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value;
};

const closeUserMenu = () => {
  userMenuOpen.value = false;
};

const onDocClick = (e) => {
  const el = navUserWrapRef.value;
  if (el && !el.contains(e.target)) {
    userMenuOpen.value = false;
  }
};

const handleLogoutFromMenu = () => {
  authStore.logout();
  userMenuOpen.value = false;
  closeMenu();
  if (route.path.startsWith('/admin')) {
    router.push({ name: 'Home' });
  }
};

const closeMenu = () => {
  isMenuOpen.value = false;
  userMenuOpen.value = false;
};

const toggleMenu = () => {
  userMenuOpen.value = false;
  isMenuOpen.value = !isMenuOpen.value;
};



const onScroll = () => {
  const y = window.scrollY || document.documentElement.scrollTop;
  isScrolled.value = y > 12;
  if (window.innerWidth <= 1023 || route.path.startsWith('/admin')) {
    hideNav.value = false;
    lastY = y;
    return;
  }
  hideNav.value = y > lastY && y > 96;
  lastY = y;
};

onMounted(() => {
  lastY = window.scrollY || 0;
  window.addEventListener('scroll', onScroll, { passive: true });
  document.addEventListener('click', onDocClick);
});

watch(
  () => route.path,
  (path) => {
    if (path.startsWith('/admin')) {
      hideNav.value = false;
    }
    userMenuOpen.value = false;
  }
);

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll);
  document.removeEventListener('click', onDocClick);
});
</script>

<style scoped>
.navbar {
  position: fixed;
  top: var(--layout-navbar-top);
  left: 0;
  right: 0;
  z-index: var(--z-nav);
  transition: transform var(--transition-smooth), box-shadow var(--transition-fast);
}

@media (min-width: 1024px) {
  .navbar {
    left: var(--nav-float-gap);
    right: var(--nav-float-gap);
    width: auto;
  }
}

.navbar.nav-hidden:not(.navbar-menu-open) {
  transform: translateY(-100%);
}

.navbar.navbar-menu-open .nav-inner {
  z-index: var(--z-nav-menu);
}

.navbar.scrolled .nav-inner {
  box-shadow: var(--shadow-sm);
  background: var(--color-surface);
}

.nav-inner {
  position: relative;
  min-height: var(--nav-height);
  display: flex;
  align-items: center;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  box-shadow: none;
  transition: background var(--transition-fast), box-shadow var(--transition-fast),
    min-height var(--transition-fast);
}

@media (min-width: 1024px) {
  .nav-inner {
    border-radius: var(--radius-xl);
    border: 1px solid var(--color-border);
    border-bottom: 1px solid var(--color-border);
    box-shadow: var(--shadow-md);
  }
}

.navbar.scrolled .nav-inner {
  min-height: 3.5rem;
}

.nav-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.logo {
  font-family: var(--font-display), var(--font-ui);
  font-weight: 700;
  font-size: 1.35rem;
  letter-spacing: 0.02em;
  text-decoration: none;
  color: var(--color-text);
  transition: color var(--transition-fast);
}

.logo:hover {
  color: var(--color-primary);
}

.menu-toggle {
  display: none;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
  width: 44px;
  height: 44px;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: var(--radius-sm);
  color: var(--color-text);
}

.menu-toggle:focus-visible {
  outline: 2px solid var(--color-primary);
}

.bar {
  display: block;
  width: 22px;
  height: 2px;
  margin: 0 auto;
  background: currentColor;
  border-radius: var(--radius-pill);
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.menu-toggle.open .bar:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.menu-toggle.open .bar:nth-child(2) {
  opacity: 0;
}

.menu-toggle.open .bar:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

.nav-links {
  list-style: none;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.nav-links li {
  margin: 0;
}

.nav-links a {
  position: relative;
  display: block;
  padding: 0.5rem 0.85rem;
  color: var(--color-text-muted);
  text-decoration: none;
  font-size: 0.92rem;
  font-weight: 500;
  border-radius: var(--radius-pill);
  cursor: pointer;
  transition: color var(--transition-fast), background var(--transition-fast);
}

.nav-links a::after {
  display: none;
}

.nav-links a:hover {
  color: var(--color-text);
  background: var(--surface-muted);
}

.nav-links a.router-link-active {
  color: var(--color-primary);
  font-weight: var(--weight-semibold);
}

.nav-links a.router-link-active::after {
  width: 0;
}

.nav-links a.nav-admin {
  background: var(--color-text);
  color: #fff;
}

.nav-links a.nav-admin:hover {
  background: var(--color-admin-hover);
  color: #fff;
}

.nav-links a.nav-admin::after {
  display: none;
}

.nav-user-wrap {
  position: relative;
}

@media (max-width: 1023px) {
  .nav-admin-li-desktop {
    display: none;
  }
}

.nav-user-menu {
  position: relative;
}

.nav-user-trigger {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  border-radius: var(--radius-pill);
  padding: var(--space-1) var(--space-2) var(--space-1) var(--space-1);
  cursor: pointer;
  font-family: inherit;
  color: var(--color-text);
}

.nav-user-trigger:hover {
  border-color: var(--color-text-muted);
}

.nav-avatar {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  flex-shrink: 0;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface-muted);
  font-size: var(--text-xs);
  font-weight: var(--weight-semibold);
}

.nav-avatar--img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.nav-username-short {
  max-width: 6.5rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: var(--text-sm);
  font-weight: var(--weight-semibold);
}

.nav-user-dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + var(--space-2));
  margin: 0;
  padding: var(--space-2) 0;
  list-style: none;
  min-width: 10rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  z-index: var(--z-nav-menu);
}

.nav-user-dropdown li {
  margin: 0;
}

.nav-user-dropdown a {
  display: block;
  padding: var(--space-2) var(--space-4);
  border-radius: 0;
  font-size: var(--text-sm);
}

.nav-dropdown-logout {
  width: 100%;
  text-align: left;
  border: none;
  background: none;
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
  color: var(--color-text-muted);
  cursor: pointer;
  font-family: inherit;
}

.nav-dropdown-logout:hover {
  color: var(--color-text);
  background: var(--surface-muted);
}

.nav-dropdown-admin {
  color: var(--color-text) !important;
  font-weight: var(--weight-semibold);
}

@media (max-width: 1023px) {
  .nav-user-trigger {
    width: 100%;
    justify-content: flex-start;
  }

  .nav-user-dropdown {
    position: static;
    box-shadow: none;
    border: none;
    padding: 0 0 var(--space-2);
    min-width: 0;
  }
}

.nav-backdrop {
  position: fixed;
  inset: 0;
  top: var(--layout-navbar-bottom);
  z-index: var(--z-nav-backdrop);
  background: var(--color-overlay-nav);
  backdrop-filter: blur(4px);
}

.backdrop-fade-enter-active,
.backdrop-fade-leave-active {
  transition: opacity 0.28s var(--ease-out-soft);
}

.backdrop-fade-enter-from,
.backdrop-fade-leave-to {
  opacity: 0;
}

@media (max-width: 1023px) {
  .nav-backdrop {
    backdrop-filter: none;
    -webkit-backdrop-filter: none;
    background: var(--color-overlay-nav-mobile);
  }

  .menu-toggle {
    display: flex;
  }

  .navbar.nav-hidden:not(.navbar-menu-open) {
    transform: none;
  }

  .nav-links {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    flex-direction: column;
    align-items: stretch;
    gap: 0;
    padding: 0.75rem 1rem 1.25rem;
    background: var(--color-surface);
    border-bottom: 1px solid var(--color-border);
    box-shadow: var(--shadow-md);
    max-height: min(70vh, 440px);
    overflow-y: auto;
    transform-origin: top;
    transform: translateY(-12px) scale(0.98);
    opacity: 0;
    visibility: hidden;
    pointer-events: none;
    transition: transform 0.32s var(--ease-out-soft), opacity 0.28s var(--ease-out-soft),
      visibility 0.32s;
  }

  .nav-links.open {
    transform: translateY(0) scale(1);
    opacity: 1;
    visibility: visible;
    pointer-events: auto;
  }

  .nav-links li {
    border-bottom: 1px solid var(--color-border);
  }

  .nav-links li:last-child {
    border-bottom: none;
  }

  .nav-links a {
    padding: 0.95rem 0.5rem;
    border-radius: var(--radius-sm);
  }

  .nav-links a::after {
    display: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .nav-links {
    transition: none;
  }

  .navbar.nav-hidden:not(.navbar-menu-open) {
    transform: none;
  }
}
</style>
