import request from '../utils/request';

export function fetchCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get',
  }).then((res) => res.data);
}

export function login(body) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: body,
  });
}

export function register(body) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: body,
  });
}
