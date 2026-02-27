import HTTP from "../common/http";

const resource = "habits";

export default {
  // Obtener todos los hábitos del usuario
  async findAll() {
    return (await HTTP.get(`${resource}`)).data;
  },

  // Obtener un hábito por ID
  async findById(id) {
    return (await HTTP.get(`${resource}/${id}`)).data;
  },

  // Obtener detalle completo del hábito (con metas, comentarios y recomendación del tiempo)
  async getHabitDetail(id, latitude = null, longitude = null) {
    const params = {};
    if (latitude !== null && longitude !== null) {
      params.latitude = latitude;
      params.longitude = longitude;
    }
    return (await HTTP.get(`${resource}/${id}/detail`, { params })).data;
  },

  // Crear un nuevo hábito
  async create(habit) {
    return (await HTTP.post(`${resource}`, habit)).data;
  },

  // Actualizar un hábito
  async update(id, habit) {
    return (await HTTP.put(`${resource}/${id}`, habit)).data;
  },

  // Eliminar un hábito
  async delete(id) {
    return await HTTP.delete(`${resource}/${id}`);
  },

  // Marcar hábito como completado
  async complete(id) {
    return (await HTTP.post(`${resource}/${id}/complete`)).data;
  },

  // Desmarcar hábito como completado
  async uncomplete(id) {
    return await HTTP.delete(`${resource}/${id}/complete`);
  },

  // Alternar completación del hábito
  async toggleCompletion(id, date = null) {
    const params = date ? { date } : {};
    return (await HTTP.post(`${resource}/${id}/toggle`, null, { params })).data;
  },

  // Añadir comentario a un hábito
  async addComment(habitId, comment) {
    return (await HTTP.post(`${resource}/${habitId}/comments`, comment)).data;
  },

  // Actualizar comentario de un hábito
  async updateComment(habitId, commentId, comment) {
    return (await HTTP.put(`${resource}/${habitId}/comments/${commentId}`, comment)).data;
  },

  // Eliminar comentario de un hábito
  async deleteComment(habitId, commentId) {
    return await HTTP.delete(`${resource}/${habitId}/comments/${commentId}`);
  },

  // Filtrar hábitos por fecha
  async filterByDate(date) {
    return (await HTTP.get(`${resource}/filter/date`, { params: { date } })).data;
  },

  // Filtrar hábitos por estado
  async filterByStatus(status) {
    return (await HTTP.get(`${resource}/filter/status`, { params: { status } })).data;
  },

  // Buscar hábitos por texto
  async search(query) {
    return (await HTTP.get(`${resource}/search`, { params: { query } })).data;
  }
};
