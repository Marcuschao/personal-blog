import request from '../utils/request';

export function uploadDiaryImage(file) {
  const fd = new FormData();
  fd.append('file', file);
  return request({
    url: '/upload/image',
    method: 'post',
    data: fd,
    timeout: 60000,
  });
}
