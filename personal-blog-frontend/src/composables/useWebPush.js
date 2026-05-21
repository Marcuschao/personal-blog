import { getVapidPublicKey, subscribePush, unsubscribePush } from '../api/push';

const VAPID_STORAGE_KEY = 'blog-push-vapid-public-key';

function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);
  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}

export async function subscribeWebPush() {
  if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
    return { ok: false, reason: 'unsupported' };
  }
  let vapid;
  try {
    vapid = await getVapidPublicKey();
  } catch {
    return { ok: false, reason: 'network' };
  }
  if (!vapid?.enabled || !vapid.publicKey) {
    return { ok: false, reason: 'disabled' };
  }
  const permission = await Notification.requestPermission();
  if (permission !== 'granted') {
    return { ok: false, reason: 'denied' };
  }
  const reg = await navigator.serviceWorker.ready;
  let sub = await reg.pushManager.getSubscription();
  const storedKey = localStorage.getItem(VAPID_STORAGE_KEY);
  if (sub && storedKey && storedKey !== vapid.publicKey) {
    const oldEndpoint = sub.endpoint;
    await sub.unsubscribe();
    try {
      await unsubscribePush(oldEndpoint);
    } catch {
      /* ignore */
    }
    sub = null;
  }
  const key = urlBase64ToUint8Array(vapid.publicKey);
  if (!sub) {
    sub = await reg.pushManager.subscribe({ userVisibleOnly: true, applicationServerKey: key });
  }
  localStorage.setItem(VAPID_STORAGE_KEY, vapid.publicKey);
  const j = sub.toJSON();
  await subscribePush({ endpoint: j.endpoint, keys: j.keys });
  return { ok: true };
}
