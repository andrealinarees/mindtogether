import http from '@/common/http'

const BASE_URL = 'goals'

export default {
  // Obtener todos los objetivos del usuario
  async findAll() {
    return (await http.get(BASE_URL)).data
  },

  // Obtener objetivos filtrados por estado
  async findByStatus(status) {
    return (await http.get(`${BASE_URL}?status=${status}`)).data
  },

  // Obtener objetivos activos ordenados por fecha
  async findActive() {
    return (await http.get(`${BASE_URL}/active`)).data
  },

  // Obtener objetivos vencidos
  async findOverdue() {
    return (await http.get(`${BASE_URL}/overdue`)).data
  },

  // Obtener objetivos de un hábito específico
  async findByHabit(habitId) {
    return (await http.get(`${BASE_URL}/habit/${habitId}`)).data
  },

  // Buscar objetivos por nombre o descripción
  async search(query) {
    return (await http.get(`${BASE_URL}/search?query=${query}`)).data
  },

  // Obtener un objetivo por ID
  async findOne(id) {
    return (await http.get(`${BASE_URL}/${id}`)).data
  },

  // Obtener el progreso de un objetivo
  async getProgress(id) {
    return (await http.get(`${BASE_URL}/${id}/progress`)).data
  },

  // Crear un nuevo objetivo
  async create(goal) {
    return (await http.post(BASE_URL, goal)).data
  },

  // Actualizar un objetivo
  async update(id, goal) {
    return (await http.put(`${BASE_URL}/${id}`, goal)).data
  },

  // Actualizar el progreso de un objetivo
  async updateProgress(id, progress) {
    return (await http.patch(`${BASE_URL}/${id}/progress`, null, { params: { progress } })).data
  },

  // Incrementar el progreso de un objetivo
  async incrementProgress(id, amount = 1) {
    return (await http.post(`${BASE_URL}/${id}/progress/increment`, null, { params: { amount } })).data
  },

  // Marcar un objetivo como completado
  async complete(id) {
    return (await http.post(`${BASE_URL}/${id}/complete`)).data
  },

  // Marcar un objetivo como fallido
  async fail(id) {
    return (await http.post(`${BASE_URL}/${id}/fail`)).data
  },

  // Cancelar un objetivo
  async cancel(id) {
    return (await http.delete(`${BASE_URL}/${id}/cancel`)).data
  },

  // Eliminar un objetivo
  async delete(id) {
    return (await http.delete(`${BASE_URL}/${id}`)).data
  }
}
