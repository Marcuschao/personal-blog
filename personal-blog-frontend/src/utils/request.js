import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_API_BASE_URL || '/api', // 后端 API 的基础 URL
  timeout: 5000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const { data } = response;
    if (data != null && typeof data.code === 'number') {
      if (data.code !== 200) {
        console.error('API Error:', data.message);
        return Promise.reject(new Error(data.message || 'Error'));
      }
      response.data = data.data;
    }
    return response;
  },
  (error) => {
    console.log('Response Error:', error.response);
    if (error.response && error.response.status === 401) {
      // 认证失败，重定向到登录页或清除认证信息
      const authStore = useAuthStore();
      authStore.clearAuth();
      // 可以考虑跳转到登录页
      // router.push('/login');
    }
    return Promise.reject(error);
  }
);

export default service;
