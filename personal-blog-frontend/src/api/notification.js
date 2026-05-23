import request from '../utils/request';

export function fetchNotifications(params) {
  return request({ url: '/notifications', method: 'get', params });
}

export function fetchUnreadCount() {
  return request({ url: '/notifications/unread-count', method: 'get', skipErrorToast: true });
}

export function markRead(id) {
  return request({ url: `/notifications/${id}/read`, method: 'put' });
}

export function markAllRead() {
  return request({ url: '/notifications/read-all', method: 'put' });
}

export function deleteNotification(id) {
  return request({ url: `/notifications/${id}`, method: 'delete' });
}
