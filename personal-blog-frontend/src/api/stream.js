import request from '../utils/request';

export function fetchStreamPending() {
  return request({ url: '/admin/stream/pending', method: 'get' }).then((res) => res.data);
}

export function fetchStreamDead(limit = 50) {
  return request({ url: '/admin/stream/dead', method: 'get', params: { limit } }).then((res) => res.data);
}

export function retryDeadLetter(recordId) {
  return request({ url: '/admin/stream/dead/retry', method: 'post', data: { recordId } }).then(
    (res) => res.data
  );
}
