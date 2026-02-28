import http from '@/common/http'

const BASE_URL = 'wellness-practices'

export default {
  // ==================== Wellness Practices ====================

  // Obtener todas las prácticas de bienestar
  async findAll(type = null, category = null) {
    let url = `${BASE_URL}/practices`
    const params = []
    if (type) params.push(`type=${type}`)
    if (category) params.push(`category=${category}`)
    if (params.length > 0) url += `?${params.join('&')}`
    
    return (await http.get(url)).data
  },

  // Obtener prácticas activas
  async findActive() {
    return (await http.get(`${BASE_URL}/practices/active`)).data
  },

  // Obtener prácticas recomendadas
  async findRecommended() {
    return (await http.get(`${BASE_URL}/practices/recommended`)).data
  },

  // Obtener práctica por ID
  async findById(id) {
    return (await http.get(`${BASE_URL}/practices/${id}`)).data
  },

  // Crear nueva práctica
  async create(practice) {
    return (await http.post(`${BASE_URL}/practices`, practice)).data
  },

  // Actualizar práctica
  async update(id, practice) {
    return (await http.put(`${BASE_URL}/practices/${id}`, practice)).data
  },

  // Eliminar práctica
  async delete(id) {
    return await http.delete(`${BASE_URL}/practices/${id}`)
  },

  // Completar una sesión
  async completeSession(id, session) {
    return (await http.post(`${BASE_URL}/practices/${id}/complete`, session)).data
  },

  // ==================== Tracking & Progress ====================

  // Obtener historial de sesiones
  async getSessions(practiceId, days = 30) {
    return (await http.get(`${BASE_URL}/practices/${practiceId}/sessions?days=${days}`)).data
  },

  // Obtener estadísticas
  async getStatistics(practiceId) {
    return (await http.get(`${BASE_URL}/practices/${practiceId}/statistics`)).data
  },

  // Obtener racha actual
  async getStreak(practiceId) {
    return (await http.get(`${BASE_URL}/practices/${practiceId}/streak`)).data
  },

  // Obtener tendencia de impacto
  async getImpactTrend(practiceId, days = 30) {
    return (await http.get(`${BASE_URL}/practices/${practiceId}/impact?days=${days}`)).data
  },

  // ==================== Guided Content ====================

  // Obtener contenido guiado disponible
  async getGuidedContent(category = null) {
    let url = `${BASE_URL}/guided-content`
    if (category) url += `?category=${category}`
    return (await http.get(url)).data
  },

  // Obtener contenido guiado por ID
  async getGuidedContentById(id) {
    return (await http.get(`${BASE_URL}/guided-content/${id}`)).data
  },

  // Marcar contenido como completado
  async markContentCompleted(contentId, rating = null, feedback = null) {
    return (await http.post(`${BASE_URL}/guided-content/${contentId}/complete`, {
      rating,
      feedback
    })).data
  },

  // ==================== Recommendations ====================

  // Obtener recomendaciones personalizadas
  async getRecommendations() {
    return (await http.get(`${BASE_URL}/recommendations`)).data
  },

  // Obtener práctica del día
  async getPracticeOfTheDay() {
    return (await http.get(`${BASE_URL}/recommendations/daily`)).data
  },

  // ==================== Categories ====================

  // Obtener categorías de prácticas
  getCategories() {
    return [
      {
        value: 'MINDFULNESS',
        label: 'Mindfulness',
        icon: '🧘',
        description: 'Prácticas de atención plena y meditación',
        color: '#9C27B0'
      },
      {
        value: 'MEDITATION',
        label: 'Meditación',
        icon: '🕉️',
        description: 'Meditación guiada y contemplación',
        color: '#673AB7'
      },
      {
        value: 'BREATHING',
        label: 'Respiración',
        icon: '💨',
        description: 'Ejercicios de respiración consciente',
        color: '#3F51B5'
      },
      {
        value: 'YOGA',
        label: 'Yoga',
        icon: '🧘‍♀️',
        description: 'Posturas y secuencias de yoga',
        color: '#2196F3'
      },
      {
        value: 'PHYSICAL_ACTIVITY',
        label: 'Actividad Física',
        icon: '🏃',
        description: 'Ejercicio y movimiento',
        color: '#03A9F4'
      },
      {
        value: 'JOURNALING',
        label: 'Escritura',
        icon: '✍️',
        description: 'Escritura terapéutica y reflexión',
        color: '#00BCD4'
      },
      {
        value: 'GRATITUDE',
        label: 'Gratitud',
        icon: '🙏',
        description: 'Prácticas de agradecimiento',
        color: '#009688'
      },
      {
        value: 'SLEEP_HYGIENE',
        label: 'Higiene del Sueño',
        icon: '😴',
        description: 'Rutinas para mejor descanso',
        color: '#4CAF50'
      },
      {
        value: 'NUTRITION',
        label: 'Nutrición',
        icon: '🥗',
        description: 'Alimentación consciente',
        color: '#8BC34A'
      },
      {
        value: 'CREATIVE_EXPRESSION',
        label: 'Expresión Creativa',
        icon: '🎨',
        description: 'Arte y creatividad terapéutica',
        color: '#CDDC39'
      },
      {
        value: 'SOCIAL_CONNECTION',
        label: 'Conexión Social',
        icon: '💬',
        description: 'Actividades sociales y comunitarias',
        color: '#FFC107'
      },
      {
        value: 'NATURE',
        label: 'Naturaleza',
        icon: '🌿',
        description: 'Conexión con el entorno natural',
        color: '#FF9800'
      },
      {
        value: 'SELF_COMPASSION',
        label: 'Autocompasión',
        icon: '💝',
        description: 'Amabilidad hacia uno mismo',
        color: '#FF5722'
      },
      {
        value: 'OTHER',
        label: 'Otro',
        icon: '⭐',
        description: 'Otras prácticas personalizadas',
        color: '#795548'
      }
    ]
  },

  // Obtener tipos de práctica
  getPracticeTypes() {
    return [
      { value: 'DAILY', label: 'Diaria', icon: '📅' },
      { value: 'WEEKLY', label: 'Semanal', icon: '📆' },
      { value: 'AS_NEEDED', label: 'Cuando sea necesario', icon: '🔔' },
      { value: 'CUSTOM', label: 'Personalizada', icon: '⚙️' }
    ]
  },

  // Obtener niveles de dificultad
  getDifficultyLevels() {
    return [
      { value: 'BEGINNER', label: 'Principiante', icon: '🌱', color: 'success' },
      { value: 'INTERMEDIATE', label: 'Intermedio', icon: '🌿', color: 'info' },
      { value: 'ADVANCED', label: 'Avanzado', icon: '🌳', color: 'warning' },
      { value: 'EXPERT', label: 'Experto', icon: '🏆', color: 'danger' }
    ]
  }
}
