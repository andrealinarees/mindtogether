import http from '@/common/http'

const BASE_URL = 'journal'

export default {
  // ==================== Journal Entries ====================

  // Obtener todas las entradas del diario
  async findAll(year = null, month = null) {
    let url = `${BASE_URL}/entries`
    const params = []
    if (year) params.push(`year=${year}`)
    if (month) params.push(`month=${month}`)
    if (params.length > 0) url += `?${params.join('&')}`
    
    return (await http.get(url)).data
  },

  // Obtener entradas por rango de fechas
  async findByDateRange(startDate, endDate) {
    return (await http.get(`${BASE_URL}/entries/date-range?startDate=${startDate}&endDate=${endDate}`)).data
  },

  // Obtener entradas de una fecha específica
  async findByDate(date) {
    return (await http.get(`${BASE_URL}/entries/date/${date}`)).data
  },

  // Obtener entrada por ID
  async findById(id) {
    return (await http.get(`${BASE_URL}/entries/${id}`)).data
  },

  // Crear nueva entrada
  async create(entry) {
    return (await http.post(`${BASE_URL}/entries`, entry)).data
  },

  // Actualizar entrada
  async update(id, entry) {
    return (await http.put(`${BASE_URL}/entries/${id}`, entry)).data
  },

  // Eliminar entrada
  async delete(id) {
    return await http.delete(`${BASE_URL}/entries/${id}`)
  },

  // Marcar entrada como favorita
  async toggleFavorite(id) {
    return (await http.post(`${BASE_URL}/entries/${id}/favorite`)).data
  },

  // ==================== Calendar ====================

  // Obtener vista de calendario mensual
  async getMonthlyCalendar(year, month) {
    return (await http.get(`${BASE_URL}/calendar/${year}/${month}`)).data
  },

  // Obtener resumen mensual
  async getMonthlySummary(year, month) {
    return (await http.get(`${BASE_URL}/calendar/${year}/${month}/summary`)).data
  },

  // ==================== Search & Analysis ====================

  // Buscar en entradas del diario
  async search(query) {
    return (await http.get(`${BASE_URL}/entries/search?query=${query}`)).data
  },

  // Búsqueda semántica (cuando me sentí así antes)
  async semanticSearch(entryId) {
    return (await http.get(`${BASE_URL}/entries/${entryId}/similar`)).data
  },

  // Obtener patrones emocionales
  async getEmotionalPatterns(days = 30) {
    return (await http.get(`${BASE_URL}/analysis/patterns?days=${days}`)).data
  },

  // Obtener triggers identificados
  async getTriggers() {
    return (await http.get(`${BASE_URL}/analysis/triggers`)).data
  },

  // Obtener análisis de sentimiento histórico
  async getSentimentHistory(days = 30) {
    return (await http.get(`${BASE_URL}/analysis/sentiment?days=${days}`)).data
  },

  // ==================== AI Assistant (Chatbot) ====================

  // Enviar mensaje al chatbot
  async sendChatMessage(message, conversationId = null) {
    return (await http.post(`${BASE_URL}/chat/message`, {
      message,
      conversationId
    })).data
  },

  // Obtener conversaciones del chatbot
  async getChatConversations(limit = 10) {
    return (await http.get(`${BASE_URL}/chat/conversations?limit=${limit}`)).data
  },

  // Obtener una conversación específica
  async getChatConversation(conversationId) {
    return (await http.get(`${BASE_URL}/chat/conversations/${conversationId}`)).data
  },

  // Obtener sugerencias de reflexión
  async getReflectionSuggestions() {
    return (await http.get(`${BASE_URL}/chat/suggestions`)).data
  },

  // ==================== Reports & Insights ====================

  // Generar reporte de bienestar
  async generateReport(type = 'WEEKLY') {
    return (await http.post(`${BASE_URL}/reports/generate`, { reportType: type })).data
  },

  // Obtener reportes generados
  async getReports(limit = 10) {
    return (await http.get(`${BASE_URL}/reports?limit=${limit}`)).data
  },

  // Obtener reporte por ID
  async getReport(reportId) {
    return (await http.get(`${BASE_URL}/reports/${reportId}`)).data
  },

  // Exportar reporte
  async exportReport(reportId, format = 'PDF') {
    return await http.get(`${BASE_URL}/reports/${reportId}/export?format=${format}`, {
      responseType: 'blob'
    })
  },

  // ==================== Wellness Planning ====================

  // Generar plan de bienestar
  async generatePlan() {
    return (await http.post(`${BASE_URL}/planning/generate`)).data
  },

  // Obtener plan actual
  async getCurrentPlan() {
    return (await http.get(`${BASE_URL}/planning/current`)).data
  },

  // Obtener planes anteriores
  async getPastPlans(limit = 5) {
    return (await http.get(`${BASE_URL}/planning/history?limit=${limit}`)).data
  },

  // ==================== Risk Detection ====================

  // Obtener alertas de riesgo
  async getRiskAlerts() {
    return (await http.get(`${BASE_URL}/risk/alerts`)).data
  },

  // Marcar alerta como resuelta
  async resolveRiskAlert(alertId) {
    return (await http.post(`${BASE_URL}/risk/alerts/${alertId}/resolve`)).data
  },

  // Obtener recursos de ayuda
  async getHelpResources() {
    return (await http.get(`${BASE_URL}/risk/help-resources`)).data
  },

  // ==================== Mood Tracking ====================

  // Obtener tendencia de estado de ánimo
  async getMoodTrend(days = 30) {
    return (await http.get(`${BASE_URL}/mood/trend?days=${days}`)).data
  },

  // Obtener gráfica de mood
  async getMoodChart(year, month = null) {
    let url = `${BASE_URL}/mood/chart?year=${year}`
    if (month) url += `&month=${month}`
    return (await http.get(url)).data
  },

  // ==================== Helper Methods ====================

  // Obtener iconos de mood
  getMoodIcons() {
    return {
      1: { emoji: '😢', label: 'Muy triste', color: '#8E24AA' },
      2: { emoji: '😔', label: 'Triste', color: '#5E35B1' },
      3: { emoji: '😕', label: 'Algo mal', color: '#3949AB' },
      4: { emoji: '😐', label: 'Neutro', color: '#1E88E5' },
      5: { emoji: '🙂', label: 'Ok', color: '#00ACC1' },
      6: { emoji: '😊', label: 'Bien', color: '#00897B' },
      7: { emoji: '😀', label: 'Bastante bien', color: '#43A047' },
      8: { emoji: '😄', label: 'Muy bien', color: '#7CB342' },
      9: { emoji: '😁', label: 'Genial', color: '#C0CA33' },
      10: { emoji: '🤩', label: 'Increíble', color: '#FDD835' }
    }
  },

  // Obtener momentos del día
  getDayMoments() {
    return [
      { value: 'MORNING', label: 'Mañana', icon: '🌅' },
      { value: 'AFTERNOON', label: 'Tarde', icon: '☀️' },
      { value: 'EVENING', label: 'Noche', icon: '🌆' },
      { value: 'NIGHT', label: 'Madrugada', icon: '🌙' }
    ]
  },

  // Obtener niveles de riesgo
  getRiskLevels() {
    return {
      NONE: { label: 'Sin riesgo', color: 'success', icon: '✅' },
      LOW: { label: 'Riesgo bajo', color: 'info', icon: 'ℹ️' },
      MEDIUM: { label: 'Riesgo medio', color: 'warning', icon: '⚠️' },
      HIGH: { label: 'Riesgo alto', color: 'danger', icon: '🚨' },
      CRITICAL: { label: 'Riesgo crítico', color: 'danger', icon: '🆘' }
    }
  }
}
