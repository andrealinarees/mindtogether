export default [
  {
    path: '/rewards',
    name: 'RewardList',
    component: () => import('./RewardList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/rewards/new',
    name: 'RewardNew',
    component: () => import('./RewardForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/rewards/:id/edit',
    name: 'RewardEdit',
    component: () => import('./RewardForm.vue'),
    meta: { requiresAuth: true }
  }
]
