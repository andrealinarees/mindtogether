<template>
  <div class="card wellness-card shadow-sm mb-3" :class="{ 'completed': practice.completedToday }">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-start mb-2">
        <div class="flex-grow-1">
          <h5 class="card-title mb-1">
            <i :class="getLocationIcon()" class="me-2"></i>
            {{ practice.name }}
          </h5>
          <p class="card-text text-muted small mb-2" v-if="practice.description">
            {{ practice.description }}
          </p>
        </div>
      </div>

      <!-- Info badges -->
      <div class="practice-info mb-3">
        <span class="badge bg-primary me-2">
          <i class="bi bi-calendar-check me-1"></i>
          {{ practice.frequency === 'DAILY' ? 'Diario' : 'Semanal' }}
        </span>
        <span v-if="practice.habitTime" class="badge bg-info me-2">
          <i class="bi bi-clock me-1"></i>
          {{ practice.habitTime }}
        </span>
        <span class="badge" :class="practice.location === 'EXTERIOR' ? 'bg-success' : 'bg-secondary'">
          <i :class="getLocationIcon()" class="me-1"></i>
          {{ practice.location === 'EXTERIOR' ? 'Exterior' : 'Interior' }}
        </span>
      </div>

      <!-- Categoría -->
      <div class="mb-3" v-if="practice.category">
        <span class="badge category-badge" :style="{ backgroundColor: practice.category.color }">
          <i :class="practice.category.icon + ' me-1'"></i>
          {{ practice.category.name }}
        </span>
      </div>

      <!-- Barra de progreso -->
      <div class="mb-3">
        <div class="d-flex justify-content-between mb-1">
          <small class="text-muted">Progreso</small>
          <small class="fw-bold">{{ progressPercentage }}%</small>
        </div>
        <div class="progress" style="height: 10px;">
          <div
            class="progress-bar"
            :class="progressBarClass"
            role="progressbar"
            :style="{ width: progressPercentage + '%' }"
          ></div>
        </div>
      </div>

      <!-- Fechas -->
      <div class="text-muted small mb-3">
        <i class="bi bi-calendar-range me-1"></i>
        {{ formatDate(practice.startDate) }} - {{ formatDate(practice.endDate) }}
      </div>

      <!-- Botones de acción -->
      <div class="action-buttons d-flex gap-2 mt-3">
        <button
          class="btn btn-sm flex-fill"
          :class="practice.completedToday ? 'btn-success' : 'btn-outline-success'"
          @click="toggleCompletion"
          :disabled="togglingCompletion"
        >
          <span v-if="togglingCompletion" class="spinner-border spinner-border-sm me-1"></span>
          <i v-else :class="practice.completedToday ? 'bi bi-check-circle-fill' : 'bi bi-check-circle'" class="me-1"></i>
          {{ practice.completedToday ? 'Completada' : 'Completar' }}
        </button>
        <button class="btn btn-sm btn-primary" @click="viewDetail" title="Ver detalles">
          <i class="bi bi-eye-fill"></i>
        </button>
        <button class="btn btn-sm btn-warning text-white" @click="editPractice" title="Editar">
          <i class="bi bi-pencil-fill"></i>
        </button>
        <button class="btn btn-sm btn-danger" @click="confirmDelete" title="Eliminar" :disabled="deleting">
          <span v-if="deleting" class="spinner-border spinner-border-sm"></span>
          <i v-else class="bi bi-trash-fill"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import WellnessPracticeRepository from '@/repositories/WellnessPracticeRepository'
import { notify, confirm } from '@/common/notifications'

export default {
  name: 'WellnessPracticeCard',
  props: {
    practice: { type: Object, required: true }
  },
  emits: ['deleted', 'updated'],
  setup(props, { emit }) {
    const router = useRouter()
    const deleting = ref(false)
    const togglingCompletion = ref(false)

    const progressPercentage = computed(() => Math.round(props.practice.progressPercentage || 0))

    const progressBarClass = computed(() => {
      const p = progressPercentage.value
      if (p >= 75) return 'bg-success'
      if (p >= 50) return 'bg-info'
      if (p >= 25) return 'bg-warning'
      return 'bg-danger'
    })

    const toggleCompletion = async () => {
      togglingCompletion.value = true
      try {
        await WellnessPracticeRepository.toggleCompletion(props.practice.id)
        props.practice.completedToday = !props.practice.completedToday
        emit('updated')
      } catch (error) {
        console.error('Error al alternar completación:', error)
        notify.error('Error al actualizar la práctica')
      } finally {
        togglingCompletion.value = false
      }
    }

    const viewDetail = () => router.push(`/wellness/${props.practice.id}`)
    const editPractice = () => router.push(`/wellness/${props.practice.id}/edit`)

    const confirmDelete = async () => {
      const confirmed = await confirm(`¿Eliminar la práctica "${props.practice.name}"?\n\nEsta acción no se puede deshacer.`, {
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
        await WellnessPracticeRepository.delete(props.practice.id)
        emit('deleted')
        notify.success('Práctica eliminada correctamente')
      } catch (error) {
        console.error('Error eliminando práctica:', error)
        notify.error('Error al eliminar la práctica')
      } finally {
        deleting.value = false
      }
    }

    const getLocationIcon = () => {
      return props.practice.location === 'EXTERIOR'
        ? 'bi bi-tree text-success'
        : 'bi bi-house-door text-secondary'
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr + 'T00:00:00')
      return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
    }

    return {
      deleting, togglingCompletion, progressPercentage, progressBarClass,
      toggleCompletion, viewDetail, editPractice, confirmDelete,
      getLocationIcon, formatDate
    }
  }
}
</script>

<style scoped>
.wellness-card {
  transition: all 0.3s ease;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
}
.wellness-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1) !important;
}
.wellness-card.completed {
  border-color: #198754;
  background-color: #f8fff9;
}
.category-badge {
  font-size: 0.9rem;
  padding: 0.5rem 0.75rem;
}
.progress {
  border-radius: 10px;
  background-color: #e9ecef;
}
</style>
