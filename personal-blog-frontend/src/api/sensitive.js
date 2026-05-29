import request from '../utils/request';

function unwrap(promise) {
  return promise.then((res) => res.data);
}

export function fetchSensitiveWords(params = {}) {
  return unwrap(request({ url: '/admin/sensitive/list', method: 'get', params }));
}

export function addSensitiveWord(word) {
  return unwrap(request({ url: '/admin/sensitive', method: 'post', data: { word } }));
}

export function deleteSensitiveWord(id) {
  return unwrap(request({ url: `/admin/sensitive/${id}`, method: 'delete' }));
}

export function reloadSensitiveWords() {
  return unwrap(request({ url: '/admin/sensitive/refresh', method: 'put' }));
}
