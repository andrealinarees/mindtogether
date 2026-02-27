export default [
  {
    path: '/goals',
    name: 'GoalList',
    component: () => import('./GoalList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/goals/new',
    name: 'GoalNew',
    component: () => import('./GoalForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/goals/:id/edit',
    name: 'GoalEdit',
    component: () => import('./GoalForm.vue'),
    meta: { requiresAuth: true }
  }
]
