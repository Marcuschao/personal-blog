import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue'),
    props: true,
  },
  {
    path: '/archive',
    name: 'Archive',
    component: () => import('../views/Archive.vue'),
  },
  {
    path: '/tags',
    name: 'Tags',
    component: () => import('../views/Tags.vue'),
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue'),
  },
  {
    path: '/reading-history',
    name: 'ReadingHistory',
    component: () => import('../views/ReadingHistory.vue'),
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue'),
  },
  {
    path: '/links',
    name: 'Links',
    component: () => import('../views/Links.vue'),
  },
  {
    path: '/diary',
    name: 'PublicDiary',
    component: () => import('../views/PublicDiary.vue'),
  },
  {
    path: '/diary/:id(\\d+)',
    name: 'PublicDiaryDetail',
    component: () => import('../views/PublicDiaryDetail.vue'),
    props: true,
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/admin/diary/list',
    name: 'AdminDiaryList',
    component: () => import('../views/Admin/DiaryList.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/diary/edit/:id',
    name: 'AdminDiaryEdit',
    component: () => import('../views/Admin/DiaryEditor.vue'),
    props: true,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/diary/:id(\\d+)',
    name: 'AdminDiaryDetail',
    component: () => import('../views/Admin/DiaryDetail.vue'),
    props: true,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/diary',
    name: 'AdminDiaryNew',
    component: () => import('../views/Admin/DiaryEditor.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/translations',
    name: 'AdminTranslations',
    component: () => import('../views/Admin/AdminTranslations.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/freshness',
    name: 'AdminFreshness',
    component: () => import('../views/Admin/AdminFreshness.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/stream',
    name: 'AdminStream',
    component: () => import('../views/Admin/AdminStream.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/push',
    name: 'AdminPush',
    component: () => import('../views/Admin/AdminPush.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/dashboard',
    name: 'AdminAnalytics',
    component: () => import('../views/Admin/StatsDashboard.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/logs',
    name: 'AdminAuditLogs',
    component: () => import('../views/Admin/AdminAuditLogs.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/settings',
    name: 'AdminSiteSettings',
    component: () => import('../views/Admin/AdminSiteSettings.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/links',
    name: 'AdminFriendLinks',
    component: () => import('../views/Admin/AdminFriendLinks.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/comments',
    name: 'AdminComments',
    component: () => import('../views/Admin/AdminComments.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/Admin/Dashboard.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/new',
    name: 'ArticleNew',
    component: () => import('../views/Admin/ArticleEditor.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/edit/:id',
    name: 'ArticleEdit',
    component: () => import('../views/Admin/ArticleEditor.vue'),
    props: true,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/ai-weekly',
    name: 'AiWeekly',
    component: () => import('../views/Admin/AiWeekly.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'Login' };
  }
});

export default router;
