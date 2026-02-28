export default [
  {
    path: '/mental-health-goals',
    name: 'MentalHealthGoalList',
    component: () => import('./MentalHealthGoalList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mental-health-goals/new',
    name: 'MentalHealthGoalNew',
    component: () => import('./MentalHealthGoalForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mental-health-goals/:id',
    name: 'MentalHealthGoalDetail',
    component: () => import('./MentalHealthGoalDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mental-health-goals/:id/edit',
    name: 'MentalHealthGoalEdit',
    component: () => import('./MentalHealthGoalForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mental-health-goals/analytics',
    name: 'MentalHealthGoalAnalytics',
    component: () => import('./MentalHealthGoalAnalytics.vue'),
    meta: { requiresAuth: true }
  }
]
