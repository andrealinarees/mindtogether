<template>
  <div class="container mt-4">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-2 text-muted">Cargando detalle de la práctica...</p>
    </div>

    <div v-else-if="practice" class="practice-detail">
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2>
            <i :class="getLocationIcon()" class="me-2"></i>
            {{ practice.name }}
          </h2>
          <span class="badge" :class="getStatusBadgeClass()">{{ getStatusText() }}</span>
        </div>
        <div class="d-flex gap-2">
          <button
            class="btn"
            :class="practice.completedToday ? 'btn-success' : 'btn-outline-success'"
            @click="toggleCompletion"
            :disabled="togglingCompletion"
          >
            <span v-if="togglingCompletion" class="spinner-border spinner-border-sm me-2"></span>
            <i v-else :class="practice.completedToday ? 'bi bi-check-circle-fill' : 'bi bi-check-circle'" class="me-1"></i>
            {{ practice.completedToday ? 'Completada hoy ✓' : 'Marcar completada' }}
          </button>
          <button class="btn btn-warning text-white" @click="editPractice">
            <i class="bi bi-pencil me-1"></i>Editar
          </button>
          <button class="btn btn-danger" @click="confirmDelete" :disabled="deleting">
            <span v-if="deleting" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="bi bi-trash me-1"></i>Eliminar
          </button>
          <button class="btn btn-outline-secondary" @click="goBack">
            <i class="bi bi-arrow-left me-1"></i>Volver
          </button>
        </div>
      </div>

      <!-- Info card -->
      <div class="card shadow-sm">
        <div class="card-body">
          <h5 class="card-title mb-4"><i class="bi bi-info-circle me-2"></i>Información de la Práctica</h5>

          <div class="info-row mb-4">
            <div class="info-label"><i class="bi bi-card-text me-2 text-primary"></i><strong>Descripción:</strong></div>
            <div class="info-value">{{ practice.description || 'Sin descripción' }}</div>
          </div>

          <div class="info-row mb-4">
            <div class="info-label"><i class="bi bi-calendar-check me-2 text-primary"></i><strong>Frecuencia:</strong></div>
            <div class="info-value">
              <span class="badge bg-primary fs-6">{{ practice.frequency === 'DAILY' ? 'Diaria' : 'Semanal' }}</span>
            </div>
          </div>

          <div class="info-row mb-4">
            <div class="info-label"><i class="bi bi-calendar-range me-2 text-primary"></i><strong>Período:</strong></div>
            <div class="info-value">
              <span class="badge bg-info fs-6 me-2">Inicio: {{ formatDate(practice.startDate) }}</span>
              <span class="badge bg-info fs-6">Fin: {{ formatDate(practice.endDate) }}</span>
            </div>
          </div>

          <div class="info-row mb-4" v-if="practice.habitTime">
            <div class="info-label"><i class="bi bi-clock me-2 text-primary"></i><strong>Hora preferida:</strong></div>
            <div class="info-value"><span class="badge bg-secondary fs-6">{{ practice.habitTime }}</span></div>
          </div>

          <div class="info-row mb-4">
            <div class="info-label"><i class="bi bi-geo-alt me-2 text-primary"></i><strong>Ubicación:</strong></div>
            <div class="info-value">
              <span class="badge fs-6" :class="practice.location === 'EXTERIOR' ? 'bg-success' : 'bg-secondary'">
                <i :class="getLocationIcon()" class="me-1"></i>
                {{ practice.location === 'EXTERIOR' ? 'Exterior' : 'Interior' }}
              </span>
            </div>
          </div>

          <div class="info-row mb-4" v-if="practice.category">
            <div class="info-label"><i class="bi bi-tags me-2 text-primary"></i><strong>Categoría:</strong></div>
            <div class="info-value">
              <span class="badge fs-6" :style="{ backgroundColor: practice.category.color }">
                <i :class="practice.category.icon + ' me-1'"></i>{{ practice.category.name }}
              </span>
            </div>
          </div>

          <!-- Progreso -->
          <div class="info-row">
            <div class="info-label"><i class="bi bi-graph-up me-2 text-primary"></i><strong>Progreso:</strong></div>
            <div class="info-value w-100">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Completado</span>
                <span class="fw-bold">{{ Math.round(practice.progressPercentage || 0) }}%</span>
              </div>
              <div class="progress" style="height: 25px;">
                <div class="progress-bar" :class="getProgressBarClass()" role="progressbar"
                  :style="{ width: (practice.progressPercentage || 0) + '%' }">
                  {{ Math.round(practice.progressPercentage || 0) }}%
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Comentarios / Reflexiones -->
      <div class="card shadow-sm mt-4">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-chat-left-text me-2"></i>Reflexiones
            <span class="badge bg-secondary ms-2">{{ comments.length }}</span>
          </h5>

          <!-- Añadir reflexión -->
          <div class="add-comment-form mb-4">
            <div class="form-floating">
              <textarea class="form-control" placeholder="Reflexión..." id="commentText"
                v-model="newCommentText" style="height: 100px" :disabled="addingComment"></textarea>
              <label for="commentText">Añade una reflexión sobre esta práctica...</label>
            </div>
            <button class="btn btn-primary mt-2" @click="addComment" :disabled="!newCommentText.trim() || addingComment">
              <span v-if="addingComment" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-plus-circle me-2"></i>
              {{ addingComment ? 'Añadiendo...' : 'Añadir Reflexión' }}
            </button>
          </div>

          <div v-if="comments.length === 0" class="text-center text-muted py-4">
            <i class="bi bi-chat-left display-4"></i>
            <p class="mt-2">Aún no hay reflexiones</p>
            <p class="small">Comparte cómo te sientes con esta práctica</p>
          </div>

          <div v-else class="comments-list">
            <div v-for="comment in sortedComments" :key="comment.id" class="comment-item mb-3">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <div class="comment-date text-muted small">
                  <i class="bi bi-calendar3 me-1"></i>{{ formatDateTime(comment.createdAt) }}
                  <span v-if="comment.updatedAt" class="ms-2">(editado)</span>
                </div>
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-warning btn-sm" @click="startEditComment(comment)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button class="btn btn-outline-danger btn-sm" @click="confirmDeleteComment(comment)">
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              </div>

              <!-- Modo edición -->
              <div v-if="editingCommentId === comment.id">
                <textarea class="form-control mb-2" v-model="editCommentText" rows="3"></textarea>
                <div class="d-flex gap-2">
                  <button class="btn btn-sm btn-primary" @click="saveEditComment(comment.id)" :disabled="updatingComment">
                    <span v-if="updatingComment" class="spinner-border spinner-border-sm me-1"></span>
                    Guardar
                  </button>
                  <button class="btn btn-sm btn-secondary" @click="cancelEditComment">Cancelar</button>
                </div>
              </div>
              <div v-else>
                <p class="mb-0" style="white-space: pre-wrap;">{{ comment.text }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>No se encontró la práctica.
      <button class="btn btn-sm btn-outline-danger ms-3" @click="goBack">Volver</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import WellnessPracticeRepository from '@/repositories/WellnessPracticeRepository'
import { notify, confirm } from '@/common/notifications'

export default {
  name: 'WellnessPracticeDetail',
  setup() {
    const router = useRouter()
    const route = useRoute()

    const loading = ref(true)
    const practice = ref(null)
    const comments = ref([])
    const togglingCompletion = ref(false)
    const deleting = ref(false)
    const addingComment = ref(false)
    const updatingComment = ref(false)
    const newCommentText = ref('')
    const editingCommentId = ref(null)
    const editCommentText = ref('')

    const sortedComments = computed(() => {
      return [...comments.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    })

    const loadPractice = async () => {
      loading.value = true
      try {
        const detail = await WellnessPracticeRepository.getDetail(route.params.id)
        practice.value = detail.habit || detail
        comments.value = detail.comments || []
      } catch (error) {
        console.error('Error loading practice:', error)
        // Fallback: cargar sin detail
        try {
          practice.value = await WellnessPracticeRepository.findById(route.params.id)
        } catch (e) {
          console.error('Error loading practice fallback:', e)
        }
      } finally {
        loading.value = false
      }
    }

    const toggleCompletion = async () => {
      togglingCompletion.value = true
      try {
        await WellnessPracticeRepository.toggleCompletion(practice.value.id)
        practice.value.completedToday = !practice.value.completedToday
      } catch (error) {
        console.error('Error:', error)
        notify.error('Error al actualizar la práctica')
      } finally {
        togglingCompletion.value = false
      }
    }

    const confirmDelete = async () => {
      const confirmed = await confirm(`¿Eliminar la práctica "${practice.value.name}"?\n\nEsta acción no se puede deshacer.`, {
        title: '¿Eliminar práctica?',
        confirmText: 'Eliminar',
        danger: true
      })
      if (confirmed) {
        deletePractice()
      }
    }

    const deletePractice = async () => {
      deleting.value = true
      try {
        await WellnessPracticeRepository.delete(practice.value.id)
        router.push('/wellness')
      } catch (error) {
        console.error('Error:', error)
        notify.error('Error al eliminar la práctica')
      } finally {
        deleting.value = false
      }
    }

    const addComment = async () => {
      if (!newCommentText.value.trim()) return
      addingComment.value = true
      try {
        const comment = await WellnessPracticeRepository.addComment(practice.value.id, { text: newCommentText.value })
        comments.value.push(comment)
        newCommentText.value = ''
      } catch (error) {
        console.error('Error:', error)
        notify.error('Error al añadir reflexión')
      } finally {
        addingComment.value = false
      }
    }

    const startEditComment = (comment) => {
      editingCommentId.value = comment.id
      editCommentText.value = comment.text
    }

    const cancelEditComment = () => {
      editingCommentId.value = null
      editCommentText.value = ''
    }

    const saveEditComment = async (commentId) => {
      updatingComment.value = true
      try {
        const updated = await WellnessPracticeRepository.updateComment(practice.value.id, commentId, { text: editCommentText.value })
        const idx = comments.value.findIndex(c => c.id === commentId)
        if (idx !== -1) comments.value[idx] = updated
        cancelEditComment()
      } catch (error) {
        console.error('Error:', error)
        notify.error('Error al actualizar reflexión')
      } finally {
        updatingComment.value = false
      }
    }

    const confirmDeleteComment = async (comment) => {
      const confirmed = await confirm('¿Eliminar esta reflexión?', {
        title: '¿Eliminar reflexión?',
        confirmText: 'Eliminar',
        danger: true
      })
      if (!confirmed) return
      try {
        await WellnessPracticeRepository.deleteComment(practice.value.id, comment.id)
        comments.value = comments.value.filter(c => c.id !== comment.id)
        notify.success('Reflexión eliminada')
      } catch (error) {
        console.error('Error:', error)
        notify.error('Error al eliminar reflexión')
      }
    }

    const editPractice = () => router.push(`/wellness/${route.params.id}/edit`)
    const goBack = () => router.push('/wellness')

    const getLocationIcon = () => practice.value?.location === 'EXTERIOR' ? 'bi bi-tree text-success' : 'bi bi-house-door text-secondary'

    const getStatusBadgeClass = () => {
      if (practice.value?.completedToday) return 'bg-success'
      if (practice.value?.status === 'COMPLETED') return 'bg-info'
      return 'bg-primary'
    }

    const getStatusText = () => {
      if (practice.value?.completedToday) return '✓ Completada hoy'
      if (practice.value?.status === 'COMPLETED') return 'Finalizada'
      return 'Activa'
    }

    const getProgressBarClass = () => {
      const p = practice.value?.progressPercentage || 0
      if (p >= 75) return 'bg-success'
      if (p >= 50) return 'bg-info'
      if (p >= 25) return 'bg-warning'
      return 'bg-danger'
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr + 'T00:00:00').toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
    }

    const formatDateTime = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('es-ES', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
    }

    onMounted(() => loadPractice())

    return {
      loading, practice, comments, togglingCompletion, deleting,
      addingComment, updatingComment, newCommentText,
      editingCommentId, editCommentText, sortedComments,
      toggleCompletion, confirmDelete, editPractice, goBack,
      addComment, startEditComment, cancelEditComment, saveEditComment, confirmDeleteComment,
      getLocationIcon, getStatusBadgeClass, getStatusText, getProgressBarClass,
      formatDate, formatDateTime
    }
  }
}
</script>

<style scoped>
.info-row { display: flex; flex-direction: column; }
.info-label { margin-bottom: 0.5rem; }
.info-value { padding-left: 1.5rem; }
.comment-item { padding: 1rem; background-color: #f8f9fa; border-radius: 8px; border-left: 4px solid #0d6efd; }
.progress { border-radius: 10px; }
</style>
