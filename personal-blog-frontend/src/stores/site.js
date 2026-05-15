import { defineStore } from 'pinia';
import { fetchPublicConfig } from '../api/site';

export const useSiteStore = defineStore('site', {
  state: () => ({
    chatbotVisibility: 'NONE',
    loaded: false,
  }),
  actions: {
    async loadPublicConfig() {
      try {
        const res = await fetchPublicConfig();
        const data = res?.data ?? res;
        const mode = data?.chatbotVisibility || 'NONE';
        this.chatbotVisibility = ['NONE', 'GUEST', 'AUTH'].includes(mode) ? mode : 'NONE';
      } catch {
        this.chatbotVisibility = 'NONE';
      } finally {
        this.loaded = true;
      }
    },
  },
});
