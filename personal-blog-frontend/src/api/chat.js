import request from '../utils/request';

function unwrap(promise) {
  return promise.then((res) => res.data);
}

export function fetchChatHistory(params = {}) {
  return unwrap(request({ url: '/chat/history', method: 'get', params, skipErrorToast: true }));
}

export function fetchOnlineUsers() {
  return unwrap(request({ url: '/chat/online-users', method: 'get', skipErrorToast: true }));
}

export function sendChatMessage(content, clientMsgId) {
  return unwrap(request({
    url: '/chat/send',
    method: 'post',
    data: clientMsgId ? { content, clientMsgId } : { content },
  }));
}

export function pingPresence() {
  return unwrap(request({ url: '/chat/presence', method: 'post', skipErrorToast: true }));
}

export function pingOffline() {
  return unwrap(request({ url: '/chat/offline', method: 'post', skipErrorToast: true }));
}

export function recallChatMessage(id) {
  return unwrap(request({ url: `/chat/recall/${id}`, method: 'post' }));
}

export function fetchMuteStatus() {
  return unwrap(request({ url: '/chat/mute-status', method: 'get', skipErrorToast: true }));
}
