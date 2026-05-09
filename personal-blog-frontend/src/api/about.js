import request from '../utils/request';

export function getAboutContent() {
  return request({
    url: '/about',
    method: 'get',
  });
}
