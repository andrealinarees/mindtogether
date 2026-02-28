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

  // Categorías de bienestar mental predefinidas (para filtrado visual en el frontend)
  getWellnessCategories() {
    return [
      { value: 'meditation', label: '🧘 Meditación', description: 'Prácticas de meditación y atención plena', color: '#009688' },
      { value: 'breathing', label: '🌬️ Respiración', description: 'Ejercicios de respiración consciente', color: '#00BCD4' },
      { value: 'journaling', label: '📝 Escritura Terapéutica', description: 'Diario emocional y escritura reflexiva', color: '#3F51B5' },
      { value: 'exercise', label: '🏃 Ejercicio Físico', description: 'Actividad física para la salud mental', color: '#4CAF50' },
      { value: 'sleep', label: '🌙 Higiene del Sueño', description: 'Rutinas para mejorar el descanso', color: '#1A237E' },
      { value: 'gratitude', label: '🙏 Gratitud', description: 'Prácticas de gratitud y aprecio', color: '#FF9800' },
      { value: 'social', label: '👥 Conexión Social', description: 'Fortalecer relaciones interpersonales', color: '#9C27B0' },
      { value: 'nutrition', label: '🥗 Alimentación Consciente', description: 'Hábitos alimentarios saludables', color: '#8BC34A' },
      { value: 'relaxation', label: '🛁 Relajación', description: 'Técnicas de relajación y desconexión', color: '#E91E63' },
      { value: 'creative', label: '🎨 Expresión Creativa', description: 'Arte, música y creatividad como terapia', color: '#FF5722' },
      { value: 'nature', label: '🌳 Contacto con la Naturaleza', description: 'Paseos y actividades al aire libre', color: '#2E7D32' },
      { value: 'selfcare', label: '💆 Autocuidado', description: 'Rutinas de cuidado personal', color: '#F06292' }
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
