import http from '@/common/http'

const BASE_URL = 'mental-health-goals'

export default {
  // ==================== CRUD Operations ====================
  
  // Obtener todas las metas del usuario
  async findAll(status = null, category = null) {
    let url = BASE_URL
    const params = []
    if (status) params.push(`status=${status}`)
    if (category) params.push(`category=${category}`)
    if (params.length > 0) url += `?${params.join('&')}`
    
    return (await http.get(url)).data
  },

  // Obtener metas activas
  async findActive() {
    return (await http.get(`${BASE_URL}/active`)).data
  },

  // Obtener metas vencidas
  async findOverdue() {
    return (await http.get(`${BASE_URL}/overdue`)).data
  },

  // Buscar metas
  async search(query) {
    return (await http.get(`${BASE_URL}/search?query=${query}`)).data
  },

  // Obtener meta por ID
  async findById(id) {
    return (await http.get(`${BASE_URL}/${id}`)).data
  },

  // Crear nueva meta
  async create(goal) {
    return (await http.post(BASE_URL, goal)).data
  },

  // Actualizar meta
  async update(id, goal) {
    return (await http.put(`${BASE_URL}/${id}`, goal)).data
  },

  // Eliminar meta
  async delete(id) {
    return await http.delete(`${BASE_URL}/${id}`)
  },

  // ==================== Progress Management ====================

  // Obtener progreso de una meta
  async getProgress(id) {
    return (await http.get(`${BASE_URL}/${id}/progress`)).data
  },

  // Actualizar progreso manualmente
  async updateProgress(id, progress, moodRating = null) {
    return (await http.put(`${BASE_URL}/${id}/progress`, { 
      progress, 
      moodRating 
    })).data
  },

  // Incrementar progreso
  async incrementProgress(id, amount = 1, moodRating = null) {
    return (await http.post(`${BASE_URL}/${id}/increment`, { 
      amount, 
      moodRating 
    })).data
  },

  // ==================== Milestones ====================

  // Obtener hitos de una meta
  async getMilestones(id) {
    return (await http.get(`${BASE_URL}/${id}/milestones`)).data
  },

  // Agregar hito a una meta
  async addMilestone(id, milestone) {
    return (await http.post(`${BASE_URL}/${id}/milestones`, milestone)).data
  },

  // ==================== Progress History ====================

  // Obtener historial de progreso
  async getProgressHistory(id) {
    return (await http.get(`${BASE_URL}/${id}/history`)).data
  },

  // Obtener análisis de progreso
  async getAnalytics(id, days = 30) {
    return (await http.get(`${BASE_URL}/${id}/analytics?days=${days}`)).data
  },

  // ==================== Statistics ====================

  // Obtener estadísticas del usuario
  async getStatistics() {
    return (await http.get(`${BASE_URL}/statistics`)).data
  },

  // ==================== Categories ====================

  // Obtener todas las categorías disponibles
  getCategories() {
    return [
      { value: 'MINDFULNESS', label: 'Atención Plena', icon: '🧘', description: 'Meditación y presencia' },
      { value: 'PHYSICAL_ACTIVITY', label: 'Actividad Física', icon: '🏃', description: 'Ejercicio y movimiento' },
      { value: 'SOCIAL_CONNECTION', label: 'Conexión Social', icon: '👥', description: 'Relaciones y vínculos' },
      { value: 'EMOTIONAL_REGULATION', label: 'Regulación Emocional', icon: '❤️', description: 'Gestión de emociones' },
      { value: 'SLEEP_QUALITY', label: 'Calidad del Sueño', icon: '😴', description: 'Descanso y sueño' },
      { value: 'NUTRITION', label: 'Nutrición', icon: '🥗', description: 'Alimentación saludable' },
      { value: 'CREATIVE_EXPRESSION', label: 'Expresión Creativa', icon: '🎨', description: 'Arte y creatividad' },
      { value: 'PERSONAL_GROWTH', label: 'Crecimiento Personal', icon: '🌱', description: 'Desarrollo personal' },
      { value: 'STRESS_MANAGEMENT', label: 'Manejo del Estrés', icon: '😌', description: 'Reducir el estrés' },
      { value: 'GRATITUDE', label: 'Gratitud', icon: '🙏', description: 'Agradecimiento' },
      { value: 'SELF_CARE', label: 'Autocuidado', icon: '💆', description: 'Cuidado personal' },
      { value: 'THERAPY_SUPPORT', label: 'Apoyo Terapéutico', icon: '💬', description: 'Terapia profesional' },
      { value: 'JOURNALING', label: 'Diario Personal', icon: '📝', description: 'Escritura reflexiva' },
      { value: 'OTHER', label: 'Otro', icon: '✨', description: 'Otras metas' }
    ]
  },

  // Obtener objetivo por categoría
  getCategoryInfo(categoryValue) {
    const categories = this.getCategories()
    return categories.find(c => c.value === categoryValue) || categories[categories.length - 1]
  }
}
