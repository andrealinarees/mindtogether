export default [
  {
    path: '/support-circles',
    name: 'SupportCircleList',
    component: () => import('./SupportCircleList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/discover',
    name: 'SupportCircleDiscover',
    component: () => import('./SupportCircleDiscover.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/new',
    name: 'SupportCircleNew',
    component: () => import('./SupportCircleForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/:id',
    name: 'SupportCircleDetail',
    component: () => import('./SupportCircleDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/:id/edit',
    name: 'SupportCircleEdit',
    component: () => import('./SupportCircleForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/:id/members',
    name: 'SupportCircleMembers',
    component: () => import('./SupportCircleMembers.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/:id/events',
    name: 'SupportCircleEvents',
    component: () => import('./SupportCircleEvents.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support-circles/:id/resources',
    name: 'SupportCircleResources',
    component: () => import('./SupportCircleResources.vue'),
    meta: { requiresAuth: true }
  }
]
