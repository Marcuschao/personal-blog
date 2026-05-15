import request from '../utils/request';

export function subscribeEmail(email) {
  return request({ url: '/subscribe', method: 'post', data: { email } });
}
