import { ref, onMounted, onUnmounted } from 'vue';

export function useInstallPrompt() {
  const deferred = ref(null);
  const canPrompt = ref(false);

  let handler;

  onMounted(() => {
    handler = (e) => {
      e.preventDefault();
      deferred.value = e;
      canPrompt.value = true;
    };
    window.addEventListener('beforeinstallprompt', handler);
  });

  onUnmounted(() => {
    if (handler) window.removeEventListener('beforeinstallprompt', handler);
  });

  const isIosSafari =
    /iphone|ipad|ipod/i.test(navigator.userAgent) &&
    /safari/i.test(navigator.userAgent) &&
    !/crios|fxios/i.test(navigator.userAgent);

  async function promptInstall() {
    const ev = deferred.value;
    if (!ev) return false;
    ev.prompt();
    const choice = await ev.userChoice;
    deferred.value = null;
    canPrompt.value = false;
    return choice.outcome === 'accepted';
  }

  return { deferred, canPrompt, promptInstall, isIosSafari };
}
