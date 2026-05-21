import request from '../utils/request';

export function getVapidPublicKey() {
  return request({ url: '/push/vapid-public-key', method: 'get', skipErrorToast: true }).then(
    (res) => res.data
  );
}

export function subscribePush(payload) {
  return request({ url: '/push/subscribe', method: 'post', data: payload }).then((res) => res.data);
}

export function unsubscribePush(endpoint) {
  return request({ url: '/push/unsubscribe', method: 'post', data: { endpoint } }).then((res) => res.data);
}

export function fetchPushStats() {
  return request({ url: '/admin/push/stats', method: 'get' }).then((res) => res.data);
}

export function adminSendPush(body) {
  return request({ url: '/admin/push/send', method: 'post', data: body }).then((res) => res.data);
}
