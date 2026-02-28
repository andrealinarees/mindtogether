/**
 * WellnessPracticeRepository - Usa el microservicio de Hábitos como backend.
 * Las prácticas de bienestar son hábitos con categorías de salud mental.
 */
import HTTP from '@/common/http'

const resource = 'habits'

export default {
  async findAll() {
    return (await HTTP.get(`${resource}`)).data
  },

  async findById(id) {
    return (await HTTP.get(`${resource}/${id}`)).data
  },

  async getDetail(id, latitude = null, longitude = null) {
    const params = {}
    if (latitude !== null && longitude !== null) {
      params.latitude = latitude
      params.longitude = longitude
    }
    return (await HTTP.get(`${resource}/${id}/detail`, { params })).data
  },

  async create(practice) {
    return (await HTTP.post(`${resource}`, practice)).data
  },

  async update(id, practice) {
    return (await HTTP.put(`${resource}/${id}`, practice)).data
  },

  async delete(id) {
    return await HTTP.delete(`${resource}/${id}`)
  },

  async toggleCompletion(id, date = null) {
    const params = date ? { date } : {}
    return (await HTTP.post(`${resource}/${id}/toggle`, null, { params })).data
  },

  async addComment(practiceId, comment) {
    return (await HTTP.post(`${resource}/${practiceId}/comments`, comment)).data
  },

  async updateComment(practiceId, commentId, comment) {
    return (await HTTP.put(`${resource}/${practiceId}/comments/${commentId}`, comment)).data
  },

  async deleteComment(practiceId, commentId) {
    return await HTTP.delete(`${resource}/${practiceId}/comments/${commentId}`)
  },

  async getCategories() {
    return (await HTTP.get(`${resource}/categories`)).data
  },

  // Categorías de bienestar mental predefinidas (idénticas a las de metas de salud mental)
  getWellnessCategories() {
    return [
      { value: 'MINDFULNESS', label: 'Atención Plena', icon: '🧘', description: 'Meditación y presencia', color: '#009688' },
      { value: 'PHYSICAL_ACTIVITY', label: 'Actividad Física', icon: '🏃', description: 'Ejercicio y movimiento', color: '#4CAF50' },
      { value: 'SOCIAL_CONNECTION', label: 'Conexión Social', icon: '👥', description: 'Relaciones y vínculos', color: '#9C27B0' },
      { value: 'EMOTIONAL_REGULATION', label: 'Regulación Emocional', icon: '❤️', description: 'Gestión de emociones', color: '#E91E63' },
      { value: 'SLEEP_QUALITY', label: 'Calidad del Sueño', icon: '😴', description: 'Descanso y sueño', color: '#1A237E' },
      { value: 'NUTRITION', label: 'Nutrición', icon: '🥗', description: 'Alimentación saludable', color: '#8BC34A' },
      { value: 'CREATIVE_EXPRESSION', label: 'Expresión Creativa', icon: '🎨', description: 'Arte y creatividad', color: '#FF5722' },
      { value: 'PERSONAL_GROWTH', label: 'Crecimiento Personal', icon: '🌱', description: 'Desarrollo personal', color: '#2E7D32' },
      { value: 'STRESS_MANAGEMENT', label: 'Manejo del Estrés', icon: '😌', description: 'Reducir el estrés', color: '#FF9800' },
      { value: 'GRATITUDE', label: 'Gratitud', icon: '🙏', description: 'Agradecimiento', color: '#F57C00' },
      { value: 'SELF_CARE', label: 'Autocuidado', icon: '💆', description: 'Cuidado personal', color: '#F06292' },
      { value: 'THERAPY_SUPPORT', label: 'Apoyo Terapéutico', icon: '💬', description: 'Terapia profesional', color: '#3F51B5' },
      { value: 'JOURNALING', label: 'Diario Personal', icon: '📝', description: 'Escritura reflexiva', color: '#00BCD4' },
      { value: 'OTHER', label: 'Otro', icon: '✨', description: 'Otras prácticas', color: '#9E9E9E' }
    ]
  },

  // Ejemplos sugeridos de prácticas de bienestar
  getSuggestedPractices() {
    return [
      { name: 'Meditación matutina', description: '10 minutos de meditación guiada al despertar', frequency: 'DAILY', location: 'INTERIOR' },
      { name: 'Respiración 4-7-8', description: 'Ejercicio de respiración para calmar la ansiedad: inhalar 4s, retener 7s, exhalar 8s', frequency: 'DAILY', location: 'INTERIOR' },
      { name: 'Diario de gratitud', description: 'Escribir 3 cosas por las que estás agradecido/a cada noche', frequency: 'DAILY', location: 'INTERIOR' },
      { name: 'Caminata consciente', description: '20 minutos de caminata prestando atención plena a los sentidos', frequency: 'DAILY', location: 'EXTERIOR' },
      { name: 'Escaneo corporal', description: 'Relajación progresiva recorriendo cada parte del cuerpo antes de dormir', frequency: 'DAILY', location: 'INTERIOR' },
      { name: 'Desconexión digital', description: 'Una hora sin pantallas antes de acostarse', frequency: 'DAILY', location: 'INTERIOR' },
      { name: 'Yoga restaurativo', description: 'Sesión de yoga suave enfocada en relajación y flexibilidad', frequency: 'WEEKLY', location: 'INTERIOR' },
      { name: 'Baño de bosque', description: 'Inmersión en la naturaleza para reducir cortisol y mejorar el ánimo', frequency: 'WEEKLY', location: 'EXTERIOR' }
    ]
  }
}
