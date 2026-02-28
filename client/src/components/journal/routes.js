export default [
  {
    path: '/journal',
    name: 'JournalHome',
    component: () => import('./JournalHome.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/calendar',
    name: 'JournalCalendar',
    component: () => import('./JournalCalendar.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/new',
    name: 'JournalEntryNew',
    component: () => import('./JournalEntryForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/:id',
    name: 'JournalEntryDetail',
    component: () => import('./JournalEntryDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/:id/edit',
    name: 'JournalEntryEdit',
    component: () => import('./JournalEntryForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/analysis',
    name: 'JournalAnalysis',
    component: () => import('./JournalAnalysis.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/chatbot',
    name: 'JournalChatbot',
    component: () => import('./JournalChatbot.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/journal/reports',
    name: 'JournalReports',
    component: () => import('./JournalReports.vue'),
    meta: { requiresAuth: true }
  }
]
