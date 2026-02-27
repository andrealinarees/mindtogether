<template>
  <div class="goal-card card h-100 shadow-sm">
    <div class="card-body">
      <!-- Header con estado -->
      <div class="d-flex justify-content-between align-items-start mb-2">
        <h5 class="card-title mb-0">{{ goal.name }}</h5>
        <span :class="['badge', statusBadgeClass]">
          {{ statusText }}
        </span>
      </div>

      <!-- Descripción -->
      <p v-if="goal.description" class="card-text text-muted small mb-3">
        {{ truncatedDescription }}
      </p>

      <!-- Hábito asociado -->
      <div class="mb-3">
        <div class="d-flex align-items-center">
          <i class="bi bi-bookmark-check me-2 text-primary"></i>
          <small class="text-muted">Hábito asociado:</small>
        </div>
        <div class="mt-1">
          <span class="badge bg-primary">{{ goal.habitName || 'Hábito #' + goal.habitId }}</span>
        </div>
      </div>

      <!-- Fecha límite y días restantes -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <i class="bi bi-calendar-event me-1"></i>
          <small class="text-muted">{{ formattedDate }}</small>
        </div>
        <div v-if="goal.status === 'ACTIVE'">
          <span v-if="daysRemaining > 0" class="badge bg-info">
            {{ daysRemaining }} días restantes
          </span>
          <span v-else-if="daysRemaining === 0" class="badge bg-warning">
            ¡Hoy es el día!
          </span>
          <span v-else class="badge bg-danger">
            Vencido
          </span>
        </div>
      </div>

      <!-- Recompensa personal -->
      <div v-if="goal.personalReward" class="alert alert-light py-2 mb-3">
        <i class="bi bi-gift me-1"></i>
        <small>{{ goal.personalReward }}</small>
      </div>

      <!-- Acciones -->
      <div class="d-flex gap-2">
        <button 
          v-if="goal.status === 'ACTIVE'" 
          @click="$emit('complete', goal.id)"
          class="btn btn-sm btn-success flex-fill"
          title="Marcar como completada">
          <i class="bi bi-check-circle me-1"></i> Completar
        </button>
        <button 
          @click="$emit('edit', goal.id)" 
          class="btn btn-sm btn-outline-primary"
          title="Editar">
          <i class="bi bi-pencil"></i>
        </button>
        <button 
          @click="$emit('delete', goal.id)" 
          class="btn btn-sm btn-outline-danger"
          title="Eliminar">
          <i class="bi bi-trash"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'GoalCard',
  props: {
    goal: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'complete', 'delete'],
  setup(props) {
    const progressPercentage = computed(() => {
      if (props.goal.targetValue === 0) return 0
      return Math.min(100, (props.goal.currentProgress * 100) / props.goal.targetValue)
    })

    const progressBarClass = computed(() => {
      const percentage = progressPercentage.value
      if (percentage >= 100) return 'bg-success'
      if (percentage >= 75) return 'bg-info'
      if (percentage >= 50) return 'bg-primary'
      if (percentage >= 25) return 'bg-warning'
      return 'bg-danger'
    })

    const statusBadgeClass = computed(() => {
      const statusClasses = {
        'ACTIVE': 'bg-primary',
        'COMPLETED': 'bg-success',
        'FAILED': 'bg-danger',
        'CANCELLED': 'bg-secondary'
      }
      return statusClasses[props.goal.status] || 'bg-secondary'
    })

    const statusText = computed(() => {
      const statusTexts = {
        'ACTIVE': 'Activo',
        'COMPLETED': 'Completado',
        'FAILED': 'Fallido',
        'CANCELLED': 'Cancelado'
      }
      return statusTexts[props.goal.status] || props.goal.status
    })

    const formattedDate = computed(() => {
      return new Date(props.goal.targetDate).toLocaleDateString('es-ES', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      })
    })

    const daysRemaining = computed(() => {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const target = new Date(props.goal.targetDate)
      target.setHours(0, 0, 0, 0)
      const diffTime = target - today
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    })

    const truncatedDescription = computed(() => {
      if (!props.goal.description) return ''
      return props.goal.description.length > 100
        ? props.goal.description.substring(0, 100) + '...'
        : props.goal.description
    })

    return {
      progressPercentage,
      progressBarClass,
      statusBadgeClass,
      statusText,
      formattedDate,
      daysRemaining,
      truncatedDescription
    }
  }
}
</script>

<style scoped>
.goal-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.goal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
}

.progress {
  border-radius: 10px;
  overflow: hidden;
}

.progress-bar {
  transition: width 0.3s ease;
}
</style>
