import http from '@/common/http'

const BASE_URL = 'achievements'

export default {
  // Obtener todos los logros del usuario
  async findAll() {
    return (await http.get(BASE_URL)).data
  },

  // Obtener logros recientes
  async findRecent(days = 7) {
    return (await http.get(`${BASE_URL}/recent?days=${days}`)).data
  },

  // Obtener logros destacados
  async findFeatured() {
    return (await http.get(`${BASE_URL}/featured`)).data
  },

  // Obtener estadísticas de logros
  async getStats() {
    return (await http.get(`${BASE_URL}/stats`)).data
  },

  // Obtener todos los logros disponibles (desbloqueados y bloqueados)
  async getAvailable() {
    return (await http.get(`${BASE_URL}/available`)).data
  },

  // Marcar/desmarcar logro como destacado
  async toggleFeatured(id) {
    return (await http.post(`${BASE_URL}/${id}/feature`)).data
  },

  // Compartir logro con círculos
  async shareWithCircles(id) {
    return (await http.post(`${BASE_URL}/${id}/share`)).data
  },

  // Marcar notificaciones como enviadas
  async markNotificationsSent() {
    return await http.post(`${BASE_URL}/mark-notifications-sent`)
  },

  // Obtener información de un logro por tipo
  getAchievementInfo(achievementType) {
    const achievementTypes = {
      // Primeros pasos
      FIRST_GOAL_CREATED: {
        name: 'Primera Meta',
        description: 'Crear tu primera meta de bienestar',
        icon: '🎯',
        points: 10,
        color: 'primary'
      },
      FIRST_GOAL_COMPLETED: {
        name: 'Primer Logro',
        description: 'Completar tu primera meta',
        icon: '🏆',
        points: 25,
        color: 'success'
      },
      FIRST_MILESTONE: {
        name: 'Primer Hito',
        description: 'Alcanzar tu primer hito',
        icon: '🎖️',
        points: 15,
        color: 'info'
      },
      
      // Cantidad
      GOALS_COMPLETED_5: {
        name: '5 Metas Completadas',
        description: 'Completar 5 metas de bienestar',
        icon: '⭐',
        points: 50,
        color: 'warning'
      },
      GOALS_COMPLETED_10: {
        name: '10 Metas Completadas',
        description: 'Completar 10 metas de bienestar',
        icon: '🌟',
        points: 100,
        color: 'warning'
      },
      GOALS_COMPLETED_25: {
        name: '25 Metas Completadas',
        description: 'Completar 25 metas de bienestar',
        icon: '💫',
        points: 250,
        color: 'warning'
      },
      
      // Rachas
      STREAK_7_DAYS: {
        name: 'Racha de 7 Días',
        description: 'Trabajar en tus metas 7 días seguidos',
        icon: '🔥',
        points: 50,
        color: 'danger'
      },
      STREAK_30_DAYS: {
        name: 'Racha de 30 Días',
        description: 'Trabajar en tus metas 30 días seguidos',
        icon: '🔥🔥',
        points: 200,
        color: 'danger'
      },
      
      // Maestros de categoría
      MINDFULNESS_MASTER: {
        name: 'Maestro de Mindfulness',
        description: 'Completar 10 metas de atención plena',
        icon: '🧘',
        points: 100,
        color: 'info'
      },
      FITNESS_CHAMPION: {
        name: 'Campeón del Fitness',
        description: 'Completar 10 metas de actividad física',
        icon: '🏃',
        points: 100,
        color: 'success'
      },
      SOCIAL_BUTTERFLY: {
        name: 'Mariposa Social',
        description: 'Completar 10 metas de conexión social',
        icon: '🦋',
        points: 100,
        color: 'primary'
      },
      EMOTIONAL_WARRIOR: {
        name: 'Guerrero Emocional',
        description: 'Completar 10 metas de regulación emocional',
        icon: '❤️',
        points: 100,
        color: 'danger'
      },
      
      // Especiales
      OVERACHIEVER: {
        name: 'Sobresaliente',
        description: 'Completar una meta antes de la fecha límite',
        icon: '⚡',
        points: 30,
        color: 'warning'
      },
      PERFECTIONIST: {
        name: 'Perfeccionista',
        description: 'Completar 5 metas al 100%',
        icon: '💯',
        points: 75,
        color: 'success'
      },
      RESILIENT: {
        name: 'Resiliente',
        description: 'Completar una meta después de fallar otra',
        icon: '💪',
        points: 40,
        color: 'info'
      },
      CONSISTENT: {
        name: 'Consistente',
        description: 'Mantener 3 metas activas simultáneamente',
        icon: '📊',
        points: 60,
        color: 'primary'
      }
    }

    return achievementTypes[achievementType] || {
      name: achievementType,
      description: 'Logro especial',
      icon: '🏅',
      points: 0,
      color: 'secondary'
    }
  }
}
