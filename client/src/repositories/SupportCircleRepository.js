import http from '@/common/http'

const BASE_URL = 'support-circles'

export default {
  // ==================== Support Circles ====================

  // Obtener todos los círculos del usuario
  async findAll() {
    return (await http.get(`${BASE_URL}/circles`)).data
  },

  // Obtener círculos disponibles para unirse
  async findAvailable() {
    return (await http.get(`${BASE_URL}/circles/available`)).data
  },

  // Obtener círculos recomendados
  async findRecommended() {
    return (await http.get(`${BASE_URL}/circles/recommended`)).data
  },

  // Obtener círculo por ID
  async findById(id) {
    return (await http.get(`${BASE_URL}/circles/${id}`)).data
  },

  // Crear nuevo círculo
  async create(circle) {
    return (await http.post(`${BASE_URL}/circles`, circle)).data
  },

  // Actualizar círculo
  async update(id, circle) {
    return (await http.put(`${BASE_URL}/circles/${id}`, circle)).data
  },

  // Eliminar círculo
  async delete(id) {
    return await http.delete(`${BASE_URL}/circles/${id}`)
  },

  // ==================== Member Management ====================

  // Obtener miembros de un círculo
  async getMembers(circleId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/members`)).data
  },

  // Invitar miembro
  async inviteMember(circleId, userId) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/invite`, { userId })).data
  },

  // Unirse a un círculo
  async joinCircle(circleId) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/join`)).data
  },

  // Abandonar círculo
  async leaveCircle(circleId) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/leave`)).data
  },

  // Remover miembro (solo moderador/admin)
  async removeMember(circleId, userId) {
    return await http.delete(`${BASE_URL}/circles/${circleId}/members/${userId}`)
  },

  // Cambiar rol de miembro
  async updateMemberRole(circleId, userId, role) {
    return (await http.put(`${BASE_URL}/circles/${circleId}/members/${userId}/role`, { role })).data
  },

  // ==================== Posts & Content ====================

  // Obtener posts de un círculo
  async getPosts(circleId, limit = 20, offset = 0) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/posts?limit=${limit}&offset=${offset}`)).data
  },

  // Crear nuevo post
  async createPost(circleId, post) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/posts`, post)).data
  },

  // Obtener post por ID
  async getPost(circleId, postId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/posts/${postId}`)).data
  },

  // Actualizar post
  async updatePost(circleId, postId, post) {
    return (await http.put(`${BASE_URL}/circles/${circleId}/posts/${postId}`, post)).data
  },

  // Eliminar post
  async deletePost(circleId, postId) {
    return await http.delete(`${BASE_URL}/circles/${circleId}/posts/${postId}`)
  },

  // Dar like a un post
  async likePost(circleId, postId) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/posts/${postId}/like`)).data
  },

  // ==================== Comments ====================

  // Obtener comentarios de un post
  async getComments(circleId, postId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/posts/${postId}/comments`)).data
  },

  // Crear comentario
  async createComment(circleId, postId, comment) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/posts/${postId}/comments`, comment)).data
  },

  // Eliminar comentario
  async deleteComment(circleId, postId, commentId) {
    return await http.delete(`${BASE_URL}/circles/${circleId}/posts/${postId}/comments/${commentId}`)
  },

  // ==================== Events & Activities ====================

  // Obtener eventos de un círculo
  async getEvents(circleId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/events`)).data
  },

  // Crear evento
  async createEvent(circleId, event) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/events`, event)).data
  },

  // RSVP a un evento
  async rsvpEvent(circleId, eventId, response) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/events/${eventId}/rsvp`, { response })).data
  },

  // ==================== Check-ins ====================

  // Obtener check-ins de la semana
  async getCheckIns(circleId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/check-ins`)).data
  },

  // Crear check-in
  async createCheckIn(circleId, checkIn) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/check-ins`, checkIn)).data
  },

  // Reaccionar a un check-in
  async reactToCheckIn(circleId, checkInId, reaction) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/check-ins/${checkInId}/react`, { reaction })).data
  },

  // ==================== Resources ====================

  // Obtener recursos compartidos
  async getResources(circleId, type = null) {
    let url = `${BASE_URL}/circles/${circleId}/resources`
    if (type) url += `?type=${type}`
    return (await http.get(url)).data
  },

  // Compartir recurso
  async shareResource(circleId, resource) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/resources`, resource)).data
  },

  // Marcar recurso como útil
  async markResourceHelpful(circleId, resourceId) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/resources/${resourceId}/helpful`)).data
  },

  // ==================== Moderation ====================

  // Reportar contenido
  async reportContent(circleId, contentType, contentId, reason) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/report`, {
      contentType,
      contentId,
      reason
    })).data
  },

  // Obtener reportes (solo moderador)
  async getReports(circleId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/reports`)).data
  },

  // Resolver reporte (solo moderador)
  async resolveReport(circleId, reportId, action) {
    return (await http.post(`${BASE_URL}/circles/${circleId}/reports/${reportId}/resolve`, { action })).data
  },

  // ==================== Statistics ====================

  // Obtener estadísticas del círculo
  async getStatistics(circleId) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/statistics`)).data
  },

  // Obtener actividad reciente
  async getRecentActivity(circleId, days = 7) {
    return (await http.get(`${BASE_URL}/circles/${circleId}/activity?days=${days}`)).data
  },

  // ==================== Helper Methods ====================

  // Obtener tipos de círculo
  getCircleTypes() {
    return [
      {
        value: 'GENERAL_SUPPORT',
        label: 'Apoyo General',
        icon: '🤝',
        description: 'Comunidad general de apoyo mutuo',
        color: '#2196F3'
      },
      {
        value: 'ANXIETY',
        label: 'Ansiedad',
        icon: '😰',
        description: 'Apoyo para manejo de ansiedad',
        color: '#FF9800'
      },
      {
        value: 'DEPRESSION',
        label: 'Depresión',
        icon: '💙',
        description: 'Apoyo para afrontar la depresión',
        color: '#3F51B5'
      },
      {
        value: 'STRESS',
        label: 'Estrés',
        icon: '😓',
        description: 'Manejo del estrés diario',
        color: '#F44336'
      },
      {
        value: 'GRIEF',
        label: 'Duelo',
        icon: '🕊️',
        description: 'Acompañamiento en el duelo',
        color: '#607D8B'
      },
      {
        value: 'ADDICTION',
        label: 'Adicción',
        icon: '🚫',
        description: 'Recuperación de adicciones',
        color: '#9C27B0'
      },
      {
        value: 'EATING_DISORDERS',
        label: 'Trastornos Alimentarios',
        icon: '🍽️',
        description: 'Apoyo en trastornos alimentarios',
        color: '#E91E63'
      },
      {
        value: 'SELF_ESTEEM',
        label: 'Autoestima',
        icon: '💪',
        description: 'Fortalecimiento de la autoestima',
        color: '#4CAF50'
      },
      {
        value: 'RELATIONSHIPS',
        label: 'Relaciones',
        icon: '💑',
        description: 'Apoyo en relaciones interpersonales',
        color: '#E91E63'
      },
      {
        value: 'PARENTING',
        label: 'Paternidad/Maternidad',
        icon: '👨‍👩‍👧‍👦',
        description: 'Apoyo para padres y madres',
        color: '#00BCD4'
      },
      {
        value: 'LGBTQ_PLUS',
        label: 'LGBTQ+',
        icon: '🏳️‍🌈',
        description: 'Comunidad LGBTQ+ de apoyo',
        color: '#FF5722'
      },
      {
        value: 'STUDENTS',
        label: 'Estudiantes',
        icon: '🎓',
        description: 'Apoyo para estudiantes',
        color: '#009688'
      },
      {
        value: 'PROFESSIONALS',
        label: 'Profesionales',
        icon: '💼',
        description: 'Salud mental en el trabajo',
        color: '#795548'
      },
      {
        value: 'VETERANS',
        label: 'Veteranos',
        icon: '🎖️',
        description: 'Apoyo para veteranos',
        color: '#607D8B'
      },
      {
        value: 'OTHER',
        label: 'Otro',
        icon: '⭐',
        description: 'Otros temas de apoyo',
        color: '#9E9E9E'
      }
    ]
  },

  // Obtener niveles de privacidad
  getPrivacyLevels() {
    return [
      {
        value: 'PUBLIC',
        label: 'Público',
        icon: '🌍',
        description: 'Visible para todos'
      },
      {
        value: 'PRIVATE',
        label: 'Privado',
        icon: '🔒',
        description: 'Solo miembros pueden ver'
      },
      {
        value: 'INVITE_ONLY',
        label: 'Solo invitación',
        icon: '✉️',
        description: 'Requiere invitación para unirse'
      }
    ]
  },

  // Obtener roles de miembro
  getMemberRoles() {
    return [
      { value: 'ADMIN', label: 'Administrador', icon: '👑', color: 'danger' },
      { value: 'MODERATOR', label: 'Moderador', icon: '🛡️', color: 'warning' },
      { value: 'MEMBER', label: 'Miembro', icon: '👤', color: 'primary' }
    ]
  },

  // Obtener reacciones de check-in
  getCheckInReactions() {
    return [
      { value: 'HEART', emoji: '❤️', label: 'Apoyo' },
      { value: 'HUG', emoji: '🤗', label: 'Abrazo' },
      { value: 'STRENGTH', emoji: '💪', label: 'Fuerza' },
      { value: 'CELEBRATE', emoji: '🎉', label: 'Celebrar' },
      { value: 'THINKING', emoji: '💭', label: 'Pensando en ti' },
      { value: 'PRAY', emoji: '🙏', label: 'Ánimo' }
    ]
  }
}
