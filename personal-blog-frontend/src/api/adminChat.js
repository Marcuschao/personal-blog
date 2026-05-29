import request from '../utils/request';

function unwrap(promise) {
  return promise.then((res) => res.data);
}

export function fetchAdminChatMessages(params = {}) {
  return unwrap(request({ url: '/admin/chat/messages', method: 'get', params }));
}

export function deleteAdminChatMessage(id) {
  return unwrap(request({ url: `/admin/chat/messages/${id}`, method: 'delete' }));
}

export function deleteAdminChatMessages(ids) {
  return unwrap(request({ url: '/admin/chat/messages', method: 'delete', data: { ids } }));
}

export function recallAdminChatMessage(id) {
  return unwrap(request({ url: `/admin/chat/messages/${id}/recall`, method: 'post' }));
}

export function fetchAdminChatOnline() {
  return unwrap(request({ url: '/admin/chat/online', method: 'get' }));
}

export function muteUser(userId, duration = 30) {
  return unwrap(request({ url: `/admin/user/${userId}/mute`, method: 'post', params: { duration } }));
}
