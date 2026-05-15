import { defineStore } from 'pinia';

export const useToastStore = defineStore('toast', {
  state: () => ({
    items: [],
    seq: 0,
  }),
  actions: {
    push(message, type = 'error') {
      const id = ++this.seq;
      this.items.push({ id, message: String(message || '请求失败'), type });
      const ms = type === 'error' ? 5200 : 3200;
      setTimeout(() => this.dismiss(id), ms);
    },
    dismiss(id) {
      this.items = this.items.filter((x) => x.id !== id);
    },
  },
});
