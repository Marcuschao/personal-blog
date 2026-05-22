import request from '../utils/request';

export function fetchMe() {
  return request({
    url: '/user/me',
    method: 'get',
  });
}

export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data,
  });
}

export function fetchPublicUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'get',
  });
}
