/**
 * SupportCircleRepository - Usa el microservicio de Community como backend.
 * Este repositorio redirige todas las operaciones al CommunityRepository.
 */
import CommunityRepository from './CommunityRepository'

export default {
  // ==================== Circles (Communities) ====================

  async findAll() {
    // Devolver solo las comunidades del usuario (sus círculos de apoyo)
    return CommunityRepository.findMyCommunities()
  },

  async findAvailable() {
    // Todas las comunidades disponibles
    return CommunityRepository.findAll()
  },

  async findRecommended() {
    // Todas las comunidades recomendadas
    return CommunityRepository.findAll()
  },

  async findById(id) {
    return CommunityRepository.findById(id)
  },

  async create(circle) {
    return CommunityRepository.create(circle)
  },

  async update(id, circle) {
    return CommunityRepository.update(id, circle)
  },

  async delete(id) {
    return CommunityRepository.delete(id)
  },

  // ==================== Member Management ====================

  async getMembers(circleId) {
    return CommunityRepository.getMembers(circleId)
  },

  async joinCircle(circleId) {
    return CommunityRepository.join(circleId)
  },

  async leaveCircle(circleId) {
    return CommunityRepository.leave(circleId)
  },

  // ==================== Entries (Posts) ====================

  async getPosts(circleId) {
    return CommunityRepository.getEntries(circleId)
  },

  async createPost(circleId, post) {
    return CommunityRepository.createEntry(circleId, post)
  },

  async updatePost(circleId, postId, post) {
    return CommunityRepository.updateEntry(circleId, postId, post)
  },

  async deletePost(circleId, postId) {
    return CommunityRepository.deleteEntry(circleId, postId)
  },

  // ==================== Helper Methods ====================

  getCircleTypes() {
    return [
      { value: 'ANXIETY', label: 'Ansiedad', icon: '😰', description: 'Técnicas y apoyo para manejar la ansiedad', color: '#FF9800' },
      { value: 'DEPRESSION', label: 'Depresión', icon: '💙', description: 'Acompañamiento para afrontar la depresión', color: '#3F51B5' },
      { value: 'STRESS', label: 'Estrés', icon: '😓', description: 'Estrategias para reducir el estrés diario', color: '#F44336' },
      { value: 'BURNOUT', label: 'Burnout Laboral', icon: '🔥', description: 'Prevención y recuperación del agotamiento laboral', color: '#E65100' },
      { value: 'SELF_ESTEEM', label: 'Autoestima', icon: '💪', description: 'Fortalecer la confianza y el amor propio', color: '#4CAF50' },
      { value: 'GRIEF', label: 'Duelo y Pérdida', icon: '🕊️', description: 'Acompañamiento en procesos de duelo', color: '#607D8B' },
      { value: 'MINDFULNESS', label: 'Mindfulness', icon: '🧘', description: 'Meditación y prácticas de atención plena', color: '#009688' },
      { value: 'ADDICTION', label: 'Adicciones', icon: '🚫', description: 'Apoyo en procesos de recuperación', color: '#9C27B0' },
      { value: 'EATING_DISORDERS', label: 'Alimentación', icon: '🍽️', description: 'Apoyo en trastornos alimentarios', color: '#E91E63' },
      { value: 'SLEEP', label: 'Sueño e Insomnio', icon: '🌙', description: 'Mejorar la calidad del descanso', color: '#1A237E' },
      { value: 'RELATIONSHIPS', label: 'Relaciones', icon: '💑', description: 'Relaciones interpersonales y comunicación', color: '#C2185B' },
      { value: 'PARENTING', label: 'Maternidad / Paternidad', icon: '👨‍👩‍👧', description: 'Apoyo emocional para padres y madres', color: '#00BCD4' },
      { value: 'STUDENTS', label: 'Salud Mental Estudiantil', icon: '🎓', description: 'Apoyo para estudiantes bajo presión académica', color: '#FF5722' },
      { value: 'LGBTQ_PLUS', label: 'LGBTQ+', icon: '🏳️‍🌈', description: 'Espacio seguro para la comunidad LGBTQ+', color: '#AB47BC' },
      { value: 'GENERAL_SUPPORT', label: 'Apoyo General', icon: '🤝', description: 'Círculo general de apoyo mutuo', color: '#2196F3' },
      { value: 'OTHER', label: 'Otro', icon: '⭐', description: 'Otros temas de bienestar emocional', color: '#9E9E9E' }
    ]
  },

  getMemberRoles() {
    return [
      { value: 'ADMIN', label: 'Administrador', icon: '👑', color: 'danger' },
      { value: 'MODERATOR', label: 'Moderador', icon: '🛡️', color: 'warning' },
      { value: 'MEMBER', label: 'Miembro', icon: '👤', color: 'primary' }
    ]
  },

  // Ejemplos sugeridos para crear un círculo
  getExamples() {
    return [
      { name: 'Manejo de la Ansiedad', reason: 'Apoyarnos mutuamente para gestionar la ansiedad en el día a día' },
      { name: 'Superando la Depresión', reason: 'Acompañarnos en el camino hacia la recuperación emocional' },
      { name: 'Mindfulness y Meditación', reason: 'Cultivar la calma interior con prácticas de atención plena' },
      { name: 'Estrés Laboral y Burnout', reason: 'Compartir estrategias para equilibrar vida y trabajo' },
      { name: 'Autoestima y Amor Propio', reason: 'Fortalecer la confianza en nosotros mismos' }
    ]
  }
}
