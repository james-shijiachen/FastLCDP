import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/backlog',
  },
  {
    path: '/backlog',
    name: 'Backlog',
    component: () => import('../views/BacklogView.vue'),
  },
  {
    path: '/board',
    name: 'Board',
    component: () => import('../views/BoardView.vue'),
  },
  {
    path: '/requirements',
    name: 'Requirements',
    component: () => import('../views/RequirementsView.vue'),
  },
  {
    path: '/bugs',
    name: 'Bugs',
    component: () => import('../views/BugsView.vue'),
  },
  {
    path: '/reports',
    name: 'Reports',
    component: () => import('../views/ReportsView.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router; 