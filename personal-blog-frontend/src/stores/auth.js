import { defineStore } from 'pinia';
import { fetchMe } from '../api/user';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: null,
    role: localStorage.getItem('role') || null,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
    displayName: (state) => state.user?.nickname || state.user?.username || '',
  },
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem('token', token);
    },
    setRole(role) {
      this.role = role || null;
      if (role) {
        localStorage.setItem('role', role);
      } else {
        localStorage.removeItem('role');
      }
    },
    loginSuccess(token, role, user) {
      this.setToken(token);
      this.setRole(role);
      this.user = user || null;
    },
    async fetchMe() {
      const res = await fetchMe();
      const u = res?.data;
      if (u) {
        this.user = u;
        this.setRole(u.role);
      }
      return u;
    },
    logout() {
      this.token = null;
      this.user = null;
      this.role = null;
      localStorage.removeItem('token');
      localStorage.removeItem('role');
    },
    clearAuth() {
      this.logout();
    },
  },
});
