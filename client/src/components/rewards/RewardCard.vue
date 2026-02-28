<template>
  <div :class="['reward-card card h-100 shadow-sm', statusClass]">
    <div class="card-body">
      <!-- Header con estado -->
      <div class="d-flex justify-content-between align-items-start mb-2">
        <div class="d-flex align-items-center gap-2">
          <span class="reward-icon fs-2">{{ reward.icon || '🎁' }}</span>
          <div>
            <h5 class="card-title mb-0">{{ reward.name }}</h5>
            <small class="text-muted">{{ categoryLabel }}</small>
          </div>
        </div>
        <span :class="['badge', statusBadgeClass]">
          {{ statusText }}
        </span>
      </div>

      <!-- Descripción -->
      <p v-if="reward.description" class="card-text text-muted small mb-3">
        {{ reward.description }}
      </p>

      <!-- Meta asociada -->
      <div v-if="reward.goalName || reward.goalId" class="mb-3">
        <div class="d-flex align-items-center gap-1">
          <i class="bi bi-bullseye text-primary"></i>
          <small class="text-muted">Meta asociada:</small>
        </div>
        <span class="badge bg-primary mt-1">
          {{ reward.goalName || 'Meta #' + reward.goalId }}
        </span>
      </div>
      <div v-else class="mb-3">
        <small class="text-muted fst-italic">
          <i class="bi bi-link-45deg me-1"></i>Sin meta asociada
        </small>
      </div>

      <!-- Fecha de desbloqueo -->
      <div v-if="reward.unlockedAt" class="mb-2">
        <small class="text-success">
          <i class="bi bi-unlock me-1"></i>Desbloqueada el {{ formatDate(reward.unlockedAt) }}
        </small>
      </div>

      <!-- Fecha de reclamación -->
      <div v-if="reward.claimedAt" class="mb-2">
        <small class="text-info">
          <i class="bi bi-check-circle me-1"></i>Reclamada el {{ formatDate(reward.claimedAt) }}
        </small>
      </div>

      <!-- Overlay para recompensas bloqueadas -->
      <div v-if="reward.status === 'LOCKED'" class="locked-overlay text-center py-2">
        <i class="bi bi-lock text-muted"></i>
        <small class="text-muted d-block">Completa la meta para desbloquear</small>
      </div>

      <!-- Acciones -->
      <div class="d-flex gap-2 mt-3">
        <button
          v-if="reward.status === 'UNLOCKED'"
          @click="$emit('claim', reward.id)"
          class="btn btn-sm btn-success flex-fill"
          title="¡Reclamar recompensa!">
          <i class="bi bi-gift me-1"></i> ¡Reclamar!
        </button>
        <button
          v-if="reward.status !== 'CLAIMED'"
          @click="$emit('edit', reward.id)"
          class="btn btn-sm btn-outline-primary"
          title="Editar">
          <i class="bi bi-pencil"></i>
        </button>
        <button
          @click="$emit('delete', reward.id)"
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
import CustomRewardRepository from '@/repositories/CustomRewardRepository'

export default {
  name: 'RewardCard',
  props: {
    reward: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'claim', 'delete'],
  setup(props) {
    const statusBadgeClass = computed(() => {
      switch (props.reward.status) {
        case 'LOCKED': return 'bg-secondary'
        case 'UNLOCKED': return 'bg-warning text-dark'
        case 'CLAIMED': return 'bg-success'
        default: return 'bg-secondary'
      }
    })

    const statusText = computed(() => {
      switch (props.reward.status) {
        case 'LOCKED': return '🔒 Bloqueada'
        case 'UNLOCKED': return '🔓 ¡Desbloqueada!'
        case 'CLAIMED': return '✅ Reclamada'
        default: return props.reward.status
      }
    })

    const statusClass = computed(() => {
      switch (props.reward.status) {
        case 'LOCKED': return 'reward-locked'
        case 'UNLOCKED': return 'reward-unlocked'
        case 'CLAIMED': return 'reward-claimed'
        default: return ''
      }
    })

    const categoryLabel = computed(() => {
      const categories = CustomRewardRepository.getCategories()
      const cat = categories.find(c => c.value === props.reward.category)
      return cat ? `${cat.icon} ${cat.label}` : props.reward.category
    })

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('es-ES', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      })
    }

    return {
      statusBadgeClass,
      statusText,
      statusClass,
      categoryLabel,
      formatDate
    }
  }
}
</script>

<style scoped>
.reward-card {
  border: none;
  border-radius: 15px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.reward-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}
.reward-locked {
  opacity: 0.75;
  border-left: 4px solid #6c757d;
}
.reward-unlocked {
  border-left: 4px solid #ffc107;
  animation: pulse-glow 2s ease-in-out infinite;
}
.reward-claimed {
  border-left: 4px solid #198754;
}
.reward-icon {
  line-height: 1;
}
.locked-overlay {
  background: rgba(108, 117, 125, 0.05);
  border-radius: 8px;
}
@keyframes pulse-glow {
  0%, 100% { box-shadow: 0 0.125rem 0.25rem rgba(255, 193, 7, 0.2); }
  50% { box-shadow: 0 0.25rem 0.75rem rgba(255, 193, 7, 0.4); }
}
</style>
