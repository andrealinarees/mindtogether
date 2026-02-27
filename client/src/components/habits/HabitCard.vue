<template>
  <div class="card habit-card shadow-sm mb-3">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-start mb-2">
        <div class="flex-grow-1">
          <h5 class="card-title mb-1">
            <i :class="getLocationIcon()" class="me-2"></i>
            {{ habit.name }}
          </h5>
          <p class="card-text text-muted small mb-2" v-if="habit.description">
            {{ habit.description }}
          </p>
        </div>
      </div>

      <!-- Información del hábito -->
      <div class="habit-info mb-3">
        <span class="badge bg-primary me-2">
          <i class="bi bi-calendar-check me-1"></i>
          {{ getFrequencyText(habit.frequency) }}
        </span>
        <span v-if="habit.habitTime" class="badge bg-info me-2">
          <i class="bi bi-clock me-1"></i>
          {{ habit.habitTime }}
        </span>
        <span class="badge" :class="habit.location === 'EXTERIOR' ? 'bg-success' : 'bg-secondary'">
          <i :class="getLocationIcon()" class="me-1"></i>
          {{ habit.location === 'EXTERIOR' ? 'Exterior' : 'Interior' }}
        </span>
      </div>

      <!-- Categoría -->
      <div class="mb-3" v-if="habit.category">
        <span
          class="badge category-badge clickable"
          :style="{ backgroundColor: habit.category.color, cursor: 'pointer' }"
          @click="viewCategories"
          title="Ver todas las categorías"
        >
          <i :class="habit.category.icon + ' me-1'"></i>
          {{ habit.category.name }}
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
            :aria-valuenow="progressPercentage"
            aria-valuemin="0"
            aria-valuemax="100">
          </div>
        </div>
      </div>

      <!-- Fechas -->
      <div class="text-muted small mb-3">
        <i class="bi bi-calendar-range me-1"></i>
        {{ formatDate(habit.startDate) }} - {{ formatDate(habit.endDate) }}
      </div>

      <!-- Botones de acción -->
      <div class="action-buttons d-flex gap-2 mt-3">
        <button
          class="btn btn-sm flex-fill"
          :class="habit.completedToday ? 'btn-success' : 'btn-outline-success'"
          @click="toggleCompletion"
          :disabled="togglingCompletion"
          :title="habit.completedToday ? 'Marcar como no completado' : 'Marcar como completado'"
        >
          <span v-if="togglingCompletion" class="spinner-border spinner-border-sm me-1"></span>
          <i v-else :class="habit.completedToday ? 'bi bi-check-circle-fill' : 'bi bi-check-circle'" class="me-1"></i>
          {{ habit.completedToday ? 'Completado' : 'Completar' }}
        </button>
        <button
          class="btn btn-sm btn-primary"
          @click="viewDetail"
          title="Ver detalles del hábito"
        >
          <i class="bi bi-eye-fill"></i>
        </button>
        <button
          class="btn btn-sm btn-warning text-white"
          @click="editHabit"
          title="Editar hábito"
        >
          <i class="bi bi-pencil-fill"></i>
        </button>
        <button
          class="btn btn-sm btn-danger"
          @click="confirmDelete"
          title="Eliminar hábito"
          :disabled="deleting"
        >
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
import HTTP from '@/common/http.js'
import GoalRepository from '@/repositories/GoalRepository'

export default {
  name: 'HabitCard',
  props: {
    habit: {
      type: Object,
      required: true
    }
  },
  emits: ['deleted', 'updated'],
  setup(props, { emit }) {
    const router = useRouter()
    const deleting = ref(false)
    const togglingCompletion = ref(false)

    const progressPercentage = computed(() => {
      return Math.round(props.habit.progressPercentage || 0)
    })

    const progressBarClass = computed(() => {
      const percentage = progressPercentage.value
      if (percentage >= 75) return 'bg-success'
      if (percentage >= 50) return 'bg-info'
      if (percentage >= 25) return 'bg-warning'
      return 'bg-danger'
    })

    const toggleCompletion = async () => {
      togglingCompletion.value = true
      try {
        await HTTP.post(`/habits/${props.habit.id}/toggle`)

        // Actualizar el estado local del hábito
        props.habit.completedToday = !props.habit.completedToday

        // Emitir evento para que el padre recargue la lista si es necesario
        emit('updated')

        // Mostrar feedback visual
        const action = props.habit.completedToday ? 'completado' : 'desmarcado como completado'
        console.log(`Hábito ${action} exitosamente`)
      } catch (error) {
        console.error('Error al alternar completación:', error)
        alert('Error al actualizar el hábito: ' + (error.response?.data?.message || error.message))
      } finally {
        togglingCompletion.value = false
      }
    }

    const viewDetail = () => {
      router.push(`/habits/${props.habit.id}`)
    }

    const editHabit = () => {
      router.push(`/habits/${props.habit.id}/edit`)
    }

    const viewCategories = () => {
      router.push('/categories')
    }

    const confirmDelete = () => {
      if (confirm(`¿Estás seguro de que quieres eliminar el hábito "${props.habit.name}"?\n\nEsta acción no se puede deshacer.`)) {
        deleteHabit()
      }
    }

    const deleteHabit = async () => {
      deleting.value = true
      try {
        // Primero eliminar todas las metas asociadas al hábito
        try {
          const goalsToDelete = await GoalRepository.findByHabit(props.habit.id)
          for (const goal of goalsToDelete) {
            await GoalRepository.delete(goal.id)
          }
        } catch (goalError) {
          console.error('Error al eliminar metas asociadas:', goalError)
        }

        // Ahora eliminar el hábito
        await HTTP.delete(`/habits/${props.habit.id}`)
        emit('deleted')
      } catch (error) {
        console.error('Error eliminando hábito:', error)
        alert('Error al eliminar el hábito: ' + (error.response?.data?.message || error.message))
      } finally {
        deleting.value = false
      }
    }

    const getFrequencyText = (frequency) => {
      const frequencies = {
        DAILY: 'Diario',
        WEEKLY: 'Semanal'
      }
      return frequencies[frequency] || frequency
    }

    const getLocationIcon = () => {
      return props.habit.location === 'EXTERIOR'
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

    return {
      deleting,
      togglingCompletion,
      progressPercentage,
      progressBarClass,
      toggleCompletion,
      viewDetail,
      editHabit,
      viewCategories,
      confirmDelete,
      getFrequencyText,
      getLocationIcon,
      formatDate
    }
  }
}
</script>

<style scoped>
.habit-card {
  transition: all 0.3s ease;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
}

.habit-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1) !important;
}

.category-badge {
  font-size: 0.9rem;
  padding: 0.5rem 0.75rem;
  transition: transform 0.2s, opacity 0.2s;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.category-badge:hover {
  transform: scale(1.05);
  opacity: 0.9;
  border-color: rgba(255, 255, 255, 0.6);
}

.clickable {
  cursor: pointer;
}

.habit-card.completed {
  border-color: #198754;
  background-color: #f8fff9;
}

.card-title {
  color: #2c3e50;
  font-weight: 600;
}

.habit-actions button {
  transition: all 0.2s ease;
}

.habit-actions button:hover {
  transform: scale(1.1);
}

.progress {
  border-radius: 10px;
  background-color: #e9ecef;
}

.progress-bar {
  font-weight: bold;
  font-size: 0.85rem;
  transition: width 0.6s ease;
}

.badge {
  font-size: 0.75rem;
  padding: 0.35em 0.65em;
}

.progress-section {
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-buttons .btn {
  flex: 1;
  font-weight: 500;
  padding: 10px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 0.95rem;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-buttons .btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.action-buttons .btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-buttons .btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.action-buttons .btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f8e 100%);
}

.action-buttons .btn-warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.action-buttons .btn-warning:hover:not(:disabled) {
  background: linear-gradient(135deg, #e082ea 0%, #e44658 100%);
}

.action-buttons .btn-danger {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.action-buttons .btn-danger:hover:not(:disabled) {
  background: linear-gradient(135deg, #e95f89 0%, #efd02f 100%);
}

.action-buttons .btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-buttons .btn i {
  font-size: 1rem;
}
</style>

