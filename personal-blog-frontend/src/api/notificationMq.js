import request from '../utils/request';

export function fetchNotificationMqStatus() {
  return request({ url: '/admin/notification-mq/status', method: 'get' }).then((res) => res.data);
}
