import http from '@/common/http'

const BASE_URL = 'custom-rewards'

export default {
  // Obtener todas las recompensas del usuario
  async findAll() {
    return (await http.get(BASE_URL)).data
  },

  // Obtener recompensas filtradas por estado
  async findByStatus(status) {
    return (await http.get(`${BASE_URL}?status=${status}`)).data
  },

  // Obtener recompensas filtradas por categoría
  async findByCategory(category) {
    return (await http.get(`${BASE_URL}?category=${category}`)).data
  },

  // Obtener recompensas desbloqueadas
  async findUnlocked() {
    return (await http.get(`${BASE_URL}/unlocked`)).data
  },

  // Obtener estadísticas
  async getStats() {
    return (await http.get(`${BASE_URL}/stats`)).data
  },

  // Obtener recompensas de una meta básica (Goal)
  async findByGoal(goalId) {
    return (await http.get(`${BASE_URL}/goal/${goalId}`)).data
  },

  // Obtener recompensas de una meta de salud mental (MentalHealthGoal)
  async findByMentalHealthGoal(mentalHealthGoalId) {
    return (await http.get(`${BASE_URL}/mental-health-goal/${mentalHealthGoalId}`)).data
  },

  // Obtener una recompensa por ID
  async findOne(id) {
    return (await http.get(`${BASE_URL}/${id}`)).data
  },

  // Crear una nueva recompensa
  async create(reward) {
    return (await http.post(BASE_URL, reward)).data
  },

  // Actualizar una recompensa
  async update(id, reward) {
    return (await http.put(`${BASE_URL}/${id}`, reward)).data
  },

  // Eliminar una recompensa
  async delete(id) {
    return (await http.delete(`${BASE_URL}/${id}`)).data
  },

  // Categorías disponibles con info para el frontend
  getCategories() {
    return [
      { value: 'MATERIAL', label: 'Material', icon: '🛍️', description: 'Compras, objetos, regalos' },
      { value: 'EXPERIENCE', label: 'Experiencia', icon: '🎭', description: 'Viajes, actividades, aventuras' },
      { value: 'PERSONAL', label: 'Personal', icon: '🧘', description: 'Autocuidado, descanso, tiempo libre' },
      { value: 'SOCIAL', label: 'Social', icon: '👥', description: 'Salidas, reuniones, actividades sociales' },
      { value: 'FOOD', label: 'Comida', icon: '🍽️', description: 'Restaurantes, dulces, cocinar algo especial' },
      { value: 'DIGITAL', label: 'Digital', icon: '📱', description: 'Suscripciones, juegos, apps' },
      { value: 'OTHER', label: 'Otro', icon: '✨', description: 'Cualquier otra recompensa' }
    ]
  },

  // Emojis sugeridos por categoría
  getSuggestedIcons(category) {
    const iconsByCategory = {
      MATERIAL: ['🛍️', '👟', '🎧', '📚', '👗', '⌚', '💍', '🎮'],
      EXPERIENCE: ['✈️', '🎭', '🎪', '🏔️', '🎢', '🏖️', '🎵', '🎬'],
      PERSONAL: ['🧘', '💆', '🛁', '😴', '📖', '🎨', '🌸', '☕'],
      SOCIAL: ['👥', '🎉', '🍻', '🎂', '🤝', '💬', '🎊', '🏠'],
      FOOD: ['🍽️', '🍕', '🍰', '🍣', '🍫', '☕', '🍷', '🧁'],
      DIGITAL: ['📱', '🎮', '🎬', '🎵', '💻', '📺', '🕹️', '📡'],
      OTHER: ['🎁', '⭐', '🎯', '💎', '🏆', '🌟', '✨', '🎊']
    }
    return iconsByCategory[category] || iconsByCategory.OTHER
  }
}
