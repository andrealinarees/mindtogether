<template>
  <div class="container mt-4">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-2 text-muted">Cargando detalle del hábito...</p>
    </div>

    <div v-else-if="habit" class="habit-detail">
      <!-- Header con título y botones -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2>
            <i :class="getLocationIcon()" class="me-2"></i>
            {{ habit.name }}
          </h2>
          <span class="badge" :class="getStatusBadgeClass()">
            {{ getStatusText() }}
          </span>
        </div>
        <div class="d-flex gap-2">
          <button 
            class="btn" 
            :class="habit.completedToday ? 'btn-success' : 'btn-outline-success'"
            @click="toggleCompletion"
            :disabled="togglingCompletion"
            :title="getCompletionButtonTooltip()"
          >
            <span v-if="togglingCompletion" class="spinner-border spinner-border-sm me-2"></span>
            <i v-else :class="habit.completedToday ? 'bi bi-check-circle-fill' : 'bi bi-check-circle'" class="me-1"></i>
            {{ getCompletionButtonText() }}
          </button>
          <button class="btn btn-outline-secondary" @click="goBack">
            <i class="bi bi-arrow-left me-1"></i>Volver
          </button>
        </div>
      </div>

      <!-- Tarjeta de información del hábito -->
      <div class="card shadow-sm">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-info-circle me-2"></i>Información del Hábito
          </h5>

          <!-- Descripción -->
          <div class="info-row mb-4">
            <div class="info-label">
              <i class="bi bi-card-text me-2 text-primary"></i>
              <strong>Descripción:</strong>
            </div>
            <div class="info-value">
              {{ habit.description || 'Sin descripción' }}
            </div>
          </div>

          <!-- Frecuencia -->
          <div class="info-row mb-4">
            <div class="info-label">
              <i class="bi bi-calendar-check me-2 text-primary"></i>
              <strong>Frecuencia:</strong>
            </div>
            <div class="info-value">
              <span class="badge bg-primary fs-6">{{ getFrequencyText(habit.frequency) }}</span>
            </div>
          </div>

          <!-- Fechas -->
          <div class="info-row mb-4">
            <div class="info-label">
              <i class="bi bi-calendar-range me-2 text-primary"></i>
              <strong>Período:</strong>
            </div>
            <div class="info-value">
              <span class="badge bg-info fs-6 me-2">Inicio: {{ formatDate(habit.startDate) }}</span>
              <span class="badge bg-info fs-6">Fin: {{ formatDate(habit.endDate) }}</span>
            </div>
          </div>

          <!-- Hora (si existe) -->
          <div class="info-row mb-4" v-if="habit.habitTime">
            <div class="info-label">
              <i class="bi bi-clock me-2 text-primary"></i>
              <strong>Hora preferida:</strong>
            </div>
            <div class="info-value">
              <span class="badge bg-secondary fs-6">{{ habit.habitTime }}</span>
            </div>
          </div>

          <!-- Ubicación -->
          <div class="info-row mb-4">
            <div class="info-label">
              <i class="bi bi-geo-alt me-2 text-primary"></i>
              <strong>Ubicación:</strong>
            </div>
            <div class="info-value">
              <span class="badge fs-6" :class="habit.location === 'EXTERIOR' ? 'bg-success' : 'bg-secondary'">
                <i :class="getLocationIcon()" class="me-1"></i>
                {{ habit.location === 'EXTERIOR' ? 'Exterior' : 'Interior' }}
              </span>
            </div>
          </div>

          <!-- Categoría -->
          <div class="info-row mb-4" v-if="habit.category">
            <div class="info-label">
              <i class="bi bi-tags me-2 text-primary"></i>
              <strong>Categoría:</strong>
            </div>
            <div class="info-value">
              <span 
                class="badge fs-6 category-badge-clickable" 
                :style="{ backgroundColor: habit.category.color, cursor: 'pointer' }"
                @click="goToCategories"
                title="Ver todas las categorías"
              >
                <i :class="habit.category.icon + ' me-1'"></i>
                {{ habit.category.name }}
              </span>
            </div>
          </div>

          <!-- Barra de Progreso -->
          <div class="info-row">
            <div class="info-label">
              <i class="bi bi-graph-up me-2 text-primary"></i>
              <strong>Progreso del Hábito:</strong>
            </div>
            <div class="info-value w-100">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Completado</span>
                <span class="fw-bold">{{ Math.round(habit.progressPercentage || 0) }}%</span>
              </div>
              <div class="progress" style="height: 25px;">
                <div 
                  class="progress-bar" 
                  :class="getProgressBarClass(habit.progressPercentage)"
                  role="progressbar" 
                  :style="{ width: (habit.progressPercentage || 0) + '%' }"
                  :aria-valuenow="habit.progressPercentage || 0" 
                  aria-valuemin="0" 
                  aria-valuemax="100">
                  {{ Math.round(habit.progressPercentage || 0) }}%
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Sección de Comentarios -->
      <div class="card shadow-sm mt-4">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-chat-left-text me-2"></i>Comentarios
            <span class="badge bg-secondary ms-2">{{ comments.length }}</span>
          </h5>

          <!-- Formulario para añadir comentario -->
          <div class="add-comment-form mb-4">
            <div class="form-floating">
              <textarea
                class="form-control"
                placeholder="Añade un comentario sobre este hábito..."
                id="commentText"
                v-model="newCommentText"
                style="height: 100px"
                :disabled="addingComment"
              ></textarea>
              <label for="commentText">Añade un comentario sobre este hábito...</label>
            </div>
            <button
              class="btn btn-primary mt-2"
              @click="addComment"
              :disabled="!newCommentText.trim() || addingComment"
            >
              <span v-if="addingComment" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-plus-circle me-2"></i>
              {{ addingComment ? 'Añadiendo...' : 'Añadir Comentario' }}
            </button>
          </div>

          <!-- Lista de comentarios -->
          <div v-if="comments.length === 0" class="text-center text-muted py-4">
            <i class="bi bi-chat-left display-4"></i>
            <p class="mt-2">Aún no hay comentarios para este hábito</p>
            <p class="small">Añade tu primera reflexión sobre este hábito</p>
          </div>

          <div v-else class="comments-list">
            <div
              v-for="comment in sortedComments"
              :key="comment.id"
              class="comment-item mb-3"
            >
              <div class="d-flex justify-content-between align-items-start mb-2">
                <div class="comment-date text-muted small">
                  <i class="bi bi-calendar3 me-1"></i>
                  {{ formatDateTime(comment.createdAt) }}
                </div>
                <div class="comment-actions">
                  <button
                    class="btn btn-sm btn-outline-primary me-1"
                    @click="startEditComment(comment)"
                    title="Editar comentario"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger"
                    @click="confirmDeleteComment(comment.id)"
                    title="Eliminar comentario"
                  >
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              </div>

              <!-- Modo vista -->
              <div v-if="editingCommentId !== comment.id" class="comment-text">
                {{ comment.text }}
              </div>

              <!-- Modo edición -->
              <div v-else class="edit-comment-form">
                <div class="form-floating mb-2">
                  <textarea
                    class="form-control"
                    v-model="editingCommentText"
                    style="height: 80px"
                    :disabled="updatingComment"
                  ></textarea>
                  <label>Editar comentario</label>
                </div>
                <div class="d-flex gap-2">
                  <button
                    class="btn btn-sm btn-success"
                    @click="updateComment(comment.id)"
                    :disabled="!editingCommentText.trim() || updatingComment"
                  >
                    <span v-if="updatingComment" class="spinner-border spinner-border-sm me-1"></span>
                    <i v-else class="bi bi-check-lg me-1"></i>
                    Guardar
                  </button>
                  <button
                    class="btn btn-sm btn-secondary"
                    @click="cancelEdit"
                    :disabled="updatingComment"
                  >
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Sección de Recomendación del Tiempo -->
      <div class="card shadow-sm mt-4" v-if="weatherRecommendation">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i :class="weatherRecommendation.icon + ' me-2'"></i>{{ weatherRecommendation.message }}
          </h5>
          <div class="weather-info">
            <p class="mb-2">
              <i class="bi bi-thermometer-half me-2"></i>
              <strong>Temperatura:</strong> {{ weatherRecommendation.temperature }}°C
            </p>
            <p class="mb-2">
              <i class="bi bi-cloud me-2"></i>
              <strong>Condición:</strong> {{ weatherRecommendation.weatherCondition }}
            </p>
            <div class="alert alert-info mb-0">
              <i class="bi bi-lightbulb me-2"></i>
              <strong>Recomendación:</strong> {{ weatherRecommendation.recommendation }}
            </div>
          </div>
        </div>
      </div>

      <!-- Sección de Metas Asociadas -->
      <div class="card shadow-sm mt-4">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-trophy me-2"></i>Metas Asociadas
            <span class="badge bg-secondary ms-2">{{ goals.length }}</span>
          </h5>

          <div v-if="goals.length === 0" class="text-center text-muted py-4">
            <i class="bi bi-trophy display-4"></i>
            <p class="mt-2">No hay metas asociadas a este hábito</p>
            <p class="small">Crea una meta para motivarte a completar este hábito</p>
          </div>

          <div v-else class="goals-list">
            <div
              v-for="goal in goals"
              :key="goal.id"
              class="goal-item mb-3"
            >
              <div class="d-flex justify-content-between align-items-start">
                <div>
                  <h6 class="mb-1">
                    <i class="bi bi-bullseye me-1"></i>{{ goal.name }}
                  </h6>
                  <p class="text-muted small mb-2" v-if="goal.description">{{ goal.description }}</p>
                  <div class="goal-info">
                    <span class="badge bg-primary me-2">
                      Progreso: {{ goal.currentProgress }}/{{ goal.targetValue }}
                    </span>
                    <span class="badge bg-info">
                      <i class="bi bi-calendar-event me-1"></i>{{ formatDate(goal.targetDate) }}
                    </span>
                    <span class="badge ms-2" :class="getGoalStatusBadge(goal.status)">
                      {{ goal.status }}
                    </span>
                  </div>
                  <p class="text-success small mt-2 mb-0" v-if="goal.personalReward">
                    <i class="bi bi-gift me-1"></i>Recompensa: {{ goal.personalReward }}
                  </p>
                </div>
              </div>
              <div class="progress mt-2" style="height: 8px">
                <div
                  class="progress-bar"
                  :class="getGoalProgressColor(goal)"
                  role="progressbar"
                  :style="{ width: getGoalProgress(goal) + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="alert alert-warning mt-4">
      <i class="bi bi-exclamation-triangle me-2"></i>
      No se pudo cargar el hábito
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import HTTP from '@/common/http.js'
import HabitRepository from '@/repositories/HabitRepository.js'
import GoalRepository from '@/repositories/GoalRepository'
import AccountRepository from '@/repositories/AccountRepository.js'
import { getCityCoordinates } from '@/common/cities.js'

export default {
  name: 'HabitDetail',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(true)
    const habit = ref(null)
    const togglingCompletion = ref(false)
    const userCity = ref('A Coruña')

    // Nuevos estados para el detalle completo
    const goals = ref([])
    const weatherRecommendation = ref(null)

    // Estados para comentarios
    const comments = ref([])
    const newCommentText = ref('')
    const addingComment = ref(false)
    const editingCommentId = ref(null)
    const editingCommentText = ref('')
    const updatingComment = ref(false)

    // Comentarios ordenados por fecha (más recientes primero)
    const sortedComments = computed(() => {
      return [...comments.value].sort((a, b) => {
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    })

    const loadUserCity = async () => {
      try {
        const profile = await AccountRepository.getAccount()
        if (profile && profile.city) {
          userCity.value = profile.city
        }
      } catch (error) {
        console.error('Error loading user city:', error)
      }
    }

    const loadHabit = async () => {
      loading.value = true
      try {
        // Cargar ciudad del usuario primero
        await loadUserCity()
        
        // Obtener coordenadas de la ciudad
        const coordinates = getCityCoordinates(userCity.value)
        
        // Usar el nuevo endpoint de detalle completo con coordenadas
        const response = await HTTP.get(`/habits/${route.params.id}/detail`, {
          params: { 
            latitude: coordinates.latitude,
            longitude: coordinates.longitude
          }
        })
        
        const detail = response.data
        habit.value = detail.habit
        comments.value = detail.comments || []
        weatherRecommendation.value = detail.weatherRecommendation

        // Cargar metas directamente desde el servicio de Goals
        try {
          const goalsData = await GoalRepository.findByHabit(route.params.id)
          goals.value = goalsData || []
        } catch (goalError) {
          console.error('Error cargando metas:', goalError)
          goals.value = []
        }
      } catch (error) {
        console.error('Error cargando hábito:', error)
        alert('Error al cargar el hábito')
        router.push('/habits')
      } finally {
        loading.value = false
      }
    }

    const addComment = async () => {
      if (!newCommentText.value.trim()) return

      try {
        addingComment.value = true
        const commentData = {
          text: newCommentText.value.trim()
        }
        const newComment = await HabitRepository.addComment(route.params.id, commentData)
        comments.value.push(newComment)
        newCommentText.value = ''
      } catch (error) {
        console.error('Error añadiendo comentario:', error)
        alert('Error al añadir el comentario. Por favor, intenta de nuevo.')
      } finally {
        addingComment.value = false
      }
    }

    const startEditComment = (comment) => {
      editingCommentId.value = comment.id
      editingCommentText.value = comment.text
    }

    const cancelEdit = () => {
      editingCommentId.value = null
      editingCommentText.value = ''
    }

    const updateComment = async (commentId) => {
      if (!editingCommentText.value.trim()) return

      try {
        updatingComment.value = true
        const commentData = {
          text: editingCommentText.value.trim()
        }
        const updated = await HabitRepository.updateComment(route.params.id, commentId, commentData)

        // Actualizar en la lista local
        const index = comments.value.findIndex(c => c.id === commentId)
        if (index !== -1) {
          comments.value[index] = updated
        }

        cancelEdit()
        console.log('Comentario actualizado:', updated)
      } catch (error) {
        console.error('Error actualizando comentario:', error)
        alert('Error al actualizar el comentario. Por favor, intenta de nuevo.')
      } finally {
        updatingComment.value = false
      }
    }

    const confirmDeleteComment = (commentId) => {
      if (confirm('¿Estás seguro de que quieres eliminar este comentario?\n\nEsta acción no se puede deshacer.')) {
        deleteComment(commentId)
      }
    }

    const deleteComment = async (commentId) => {
      try {
        await HabitRepository.deleteComment(route.params.id, commentId)

        // Eliminar de la lista local
        comments.value = comments.value.filter(c => c.id !== commentId)

        console.log('Comentario eliminado')
      } catch (error) {
        console.error('Error eliminando comentario:', error)
        alert('Error al eliminar el comentario. Por favor, intenta de nuevo.')
      }
    }

    const toggleCompletion = async () => {
      if (togglingCompletion.value) return

      try {
        togglingCompletion.value = true
        console.log('Toggling completion for habit:', habit.value.id)
        const updated = await HabitRepository.toggleCompletion(habit.value.id)
        console.log('Response from API:', updated)
        // Actualizar el hábito con los nuevos datos
        habit.value.completedToday = updated.completedToday
        habit.value.currentStreak = updated.currentStreak
        habit.value.longestStreak = updated.longestStreak
        habit.value.progressPercentage = updated.progressPercentage
        console.log('Hábito actualizado:', habit.value)
      } catch (error) {
        console.error('Error al cambiar estado del hábito:', error)
        console.error('Error details:', error.response || error.message)
        alert('Error al actualizar el hábito. Por favor, intenta de nuevo.')
      } finally {
        togglingCompletion.value = false
      }
    }

    const goBack = () => {
      router.push('/habits')
    }

    const goToCategories = () => {
      router.push('/categories')
    }

    const getCompletionButtonText = () => {
      if (!habit.value) return 'Marcar Completado'
      
      if (habit.value.frequency === 'DAILY') {
        return habit.value.completedToday ? 'Completado Hoy' : 'Marcar Completado Hoy'
      } else if (habit.value.frequency === 'WEEKLY') {
        return habit.value.completedToday ? 'Completado Esta Semana' : 'Marcar Completado Esta Semana'
      }
      
      return habit.value.completedToday ? 'Completado' : 'Marcar Completado'
    }

    const getCompletionButtonTooltip = () => {
      if (!habit.value) return 'Marcar como completado'
      
      if (habit.value.frequency === 'DAILY') {
        return habit.value.completedToday 
          ? 'Ya has marcado este hábito como completado hoy. Haz clic para desmarcar.' 
          : 'Marca este hábito como completado para hoy'
      } else if (habit.value.frequency === 'WEEKLY') {
        return habit.value.completedToday 
          ? 'Ya has marcado este hábito como completado esta semana. Haz clic para desmarcar.' 
          : 'Marca este hábito como completado para esta semana'
      }
      
      return 'Marcar como completado'
    }

    const getFrequencyText = (frequency) => {
      const frequencies = {
        DAILY: 'Diario',
        WEEKLY: 'Semanal'
      }
      return frequencies[frequency] || frequency
    }

    const getStatusText = () => {
      if (!habit.value) return ''
      const statuses = {
        ACTIVE: 'Activo',
        COMPLETED: 'Completado'
      }
      return statuses[habit.value.status] || habit.value.status
    }

    const getStatusBadgeClass = () => {
      if (!habit.value) return 'bg-secondary'
      const classes = {
        ACTIVE: 'bg-success',
        COMPLETED: 'bg-primary'
      }
      return classes[habit.value.status] || 'bg-secondary'
    }

    const getLocationIcon = () => {
      if (!habit.value) return ''
      return habit.value.location === 'EXTERIOR'
        ? 'bi bi-tree text-success'
        : 'bi bi-house-door text-secondary'
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr + 'T00:00:00')
      return date.toLocaleDateString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    }

    const formatDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return ''
      const date = new Date(dateTimeStr)
      return date.toLocaleString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    onMounted(() => {
      loadHabit()
    })

    // Recargar cuando el componente se reactive (ej: volver desde otra vista)
    onActivated(() => {
      loadHabit()
    })

    const getGoalProgress = (goal) => {
      if (!goal || goal.targetValue === 0) return 0
      return Math.min((goal.currentProgress / goal.targetValue) * 100, 100)
    }

    const getGoalProgressColor = (goal) => {
      const progress = getGoalProgress(goal)
      if (progress >= 100) return 'bg-success'
      if (progress >= 70) return 'bg-info'
      if (progress >= 40) return 'bg-warning'
      return 'bg-danger'
    }

    const getGoalStatusBadge = (status) => {
      const badges = {
        'ACTIVE': 'bg-success',
        'COMPLETED': 'bg-primary',
        'FAILED': 'bg-danger',
        'CANCELLED': 'bg-secondary'
      }
      return badges[status] || 'bg-secondary'
    }

    const getProgressBarClass = (percentage) => {
      if (percentage >= 75) return 'bg-success'
      if (percentage >= 50) return 'bg-info'
      if (percentage >= 25) return 'bg-warning'
      return 'bg-danger'
    }

    return {
      loading,
      habit,
      togglingCompletion,
      toggleCompletion,
      comments,
      sortedComments,
      newCommentText,
      addingComment,
      editingCommentId,
      editingCommentText,
      updatingComment,
      addComment,
      startEditComment,
      cancelEdit,
      updateComment,
      confirmDeleteComment,
      goBack,
      goToCategories,
      getCompletionButtonText,
      getCompletionButtonTooltip,
      getFrequencyText,
      getStatusText,
      getStatusBadgeClass,
      getLocationIcon,
      formatDate,
      formatDateTime,
      // Nuevas funciones
      goals,
      weatherRecommendation,
      getGoalProgress,
      getGoalProgressColor,
      getGoalStatusBadge,
      getProgressBarClass
    }
  }
}
</script>

<style scoped>
.habit-detail {
  max-width: 900px;
  margin: 0 auto;
}

.card {
  border: none;
  border-radius: 12px;
}

.card-title {
  color: #2c3e50;
  font-weight: 600;
  font-size: 1.25rem;
}

.info-row {
  padding: 1rem;
  border-radius: 8px;
  background-color: #f8f9fa;
  transition: all 0.3s ease;
}

.info-row:hover {
  background-color: #e9ecef;
  transform: translateX(5px);
}

.info-label {
  font-size: 1rem;
  color: #495057;
  margin-bottom: 0.5rem;
}

.info-value {
  font-size: 1.1rem;
  color: #212529;
  padding-left: 2rem;
}

.badge {
  padding: 0.5rem 1rem;
}

strong {
  color: #2c3e50;
}

h2 {
  color: #2c3e50;
  font-weight: 700;
}

/* Estilos para comentarios */
.add-comment-form {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
  border: 2px dashed #dee2e6;
}

.comments-list {
  max-height: 600px;
  overflow-y: auto;
}

.comment-item {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background-color: #e9ecef;
  transform: translateX(5px);
}

.comment-date {
  font-size: 0.875rem;
  color: #6c757d;
}

.comment-text {
  color: #212529;
  font-size: 1rem;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-actions button {
  padding: 0.25rem 0.5rem;
}

.edit-comment-form {
  background-color: white;
  padding: 1rem;
  border-radius: 8px;
}

/* Estilos para metas */
.goals-list {
  max-height: 500px;
  overflow-y: auto;
}

.goal-item {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border-left: 4px solid #ffc107;
  transition: all 0.3s ease;
}

.goal-item:hover {
  background-color: #e9ecef;
  transform: translateX(5px);
}

.goal-info {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

/* Estilos para recomendación del tiempo */
.weather-info {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
}

.category-badge-clickable {
  transition: transform 0.2s, opacity 0.2s;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.category-badge-clickable:hover {
  transform: scale(1.05);
  opacity: 0.9;
  border-color: rgba(255, 255, 255, 0.6);
}

.weather-info .alert {
  margin-top: 1rem;
}
</style>

