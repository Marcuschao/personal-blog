import { ref, computed, watch } from 'vue';
import { darkTheme } from 'naive-ui';

const STORAGE_KEY = 'blog-theme-dark';
const isDark = ref(localStorage.getItem(STORAGE_KEY) === '1');

export function useTheme() {
  const naiveTheme = computed(() => (isDark.value ? darkTheme : null));

  function toggleDark(val) {
    isDark.value = typeof val === 'boolean' ? val : !isDark.value;
  }

  watch(isDark, (v) => {
    localStorage.setItem(STORAGE_KEY, v ? '1' : '0');
    document.documentElement.classList.toggle('dark', v);
  }, { immediate: true });

  return { isDark, naiveTheme, toggleDark };
}
