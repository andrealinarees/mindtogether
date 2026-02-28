export default [
  {
    path: '/wellness',
    name: 'WellnessPracticeList',
    component: () => import('./WellnessPracticeList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wellness/new',
    name: 'WellnessPracticeNew',
    component: () => import('./WellnessPracticeForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wellness/guided-content',
    name: 'GuidedContentLibrary',
    component: () => import('./GuidedContentLibrary.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wellness/guided-content/:id',
    name: 'GuidedContentDetail',
    component: () => import('./GuidedContentDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wellness/recommendations',
    name: 'WellnessRecommendations',
    component: () => import('./WellnessRecommendations.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wellness/:id',
    name: 'WellnessPracticeDetail',
    component: () => import('./WellnessPracticeDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wellness/:id/edit',
    name: 'WellnessPracticeEdit',
    component: () => import('./WellnessPracticeForm.vue'),
    meta: { requiresAuth: true }
  }
]
