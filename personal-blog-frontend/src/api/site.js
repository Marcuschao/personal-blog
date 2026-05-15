import request from '../utils/request';

export function fetchPublicConfig() {
  return request({ url: '/site/public-config', method: 'get', skipErrorToast: true });
}

export function fetchAdminSiteConfig() {
  return request({ url: '/admin/site/public-config', method: 'get' });
}

export function putChatbotVisibility(mode) {
  return request({
    url: '/admin/site/chatbot-visibility',
    method: 'put',
    data: { mode },
  });
}
