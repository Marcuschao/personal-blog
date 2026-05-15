import request from '../utils/request';

export function fetchArticleComments(articleId) {
  return request({
    url: `/articles/${articleId}/comments`,
    method: 'get',
    skipErrorToast: true,
  });
}

export function submitComment(data) {
  return request({ url: '/comments', method: 'post', data });
}

export function fetchMathCaptcha() {
  return request({ url: '/captcha/math', method: 'get', skipErrorToast: true });
}

export function fetchAdminComments(params) {
  return request({ url: '/admin/comments', method: 'get', params });
}

export function approveComment(id) {
  return request({ url: `/admin/comments/${id}/approve`, method: 'put' });
}

export function deleteAdminComment(id) {
  return request({ url: `/admin/comments/${id}`, method: 'delete' });
}
