import request from '../utils/request';

export function fetchFriendLinks() {
  return request({ url: '/links', method: 'get', skipErrorToast: true });
}

export function fetchAdminFriendLinks() {
  return request({ url: '/admin/links', method: 'get' });
}

export function createFriendLink(data) {
  return request({ url: '/admin/links', method: 'post', data });
}

export function updateFriendLink(id, data) {
  return request({ url: `/admin/links/${id}`, method: 'put', data });
}

export function deleteFriendLink(id) {
  return request({ url: `/admin/links/${id}`, method: 'delete' });
}
