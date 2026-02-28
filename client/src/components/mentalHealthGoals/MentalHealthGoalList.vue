<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="mb-0"><i class="bi bi-trophy"></i> Mis Metas de Salud Mental</h2>
      <router-link to="/mental-health-goals/new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Nueva Meta
      </router-link>
    </div>

    <!-- Stats -->
    <div class="row g-3 mb-4" v-if="stats">
      <div class="col-6 col-md-3">
        <div class="card text-center h-100 border-primary">
          <div class="card-body py-2">
            <h4 class="text-primary mb-0">{{ stats.activeGoals || 0 }}</h4>
            <small class="text-muted">Activas</small>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3">
        <div class="card text-center h-100 border-success">
          <div class="card-body py-2">
            <h4 class="text-success mb-0">{{ stats.completedGoals || 0 }}</h4>
            <small class="text-muted">Completadas</small>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3">
        <div class="card text-center h-100 border-info">
          <div class="card-body py-2">
            <h4 class="text-info mb-0">{{ Math.round(stats.averageProgress || 0) }}%</h4>
            <small class="text-muted">Progreso medio</small>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3">
        <div class="card text-center h-100 border-warning">
          <div class="card-body py-2">
            <h4 class="text-warning mb-0">{{ stats.failedGoals || 0 }}</h4>
            <small class="text-muted">Fallidas</small>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="card mb-4">
      <div class="card-body py-2">
        <div class="row align-items-center g-2">
          <div class="col-auto">
            <small class="fw-bold text-muted">Filtrar:</small>
          </div>
          <div class="col-auto">
            <div class="btn-group btn-group-sm">
              <button class="btn" :class="filterStatus === null ? 'btn-primary' : 'btn-outline-primary'" @click="filterStatus = null">Todas</button>
              <button class="btn" :class="filterStatus === 'ACTIVE' ? 'btn-primary' : 'btn-outline-primary'" @click="filterStatus = 'ACTIVE'">Activas</button>
              <button class="btn" :class="filterStatus === 'COMPLETED' ? 'btn-success' : 'btn-outline-success'" @click="filterStatus = 'COMPLETED'">Completadas</button>
              <button class="btn" :class="filterStatus === 'FAILED' ? 'btn-danger' : 'btn-outline-danger'" @click="filterStatus = 'FAILED'">Fallidas</button>
            </div>
          </div>
          <div class="col-auto ms-auto">
            <select class="form-select form-select-sm" v-model="filterCategory" style="min-width:160px">
              <option :value="null">Todas las categorías</option>
              <option v-for="cat in categories" :key="cat.value" :value="cat.value">{{ cat.icon }} {{ cat.label }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Cargando metas...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
      <button class="btn btn-sm btn-outline-danger ms-2" @click="loadGoals">Reintentar</button>
    </div>

    <!-- Goals List -->
    <div v-else>
      <div v-if="filteredGoals.length === 0" class="text-center py-5">
        <div class="display-1 mb-3">🎯</div>
        <h4>No tienes metas registradas</h4>
        <p class="text-muted">¡Crea tu primera meta de salud mental para empezar tu camino de bienestar!</p>
        <router-link to="/mental-health-goals/new" class="btn btn-primary">
          <i class="bi bi-plus-circle"></i> Crear primera meta
        </router-link>
      </div>

      <div class="row g-3" v-else>
        <div class="col-md-6 col-lg-4" v-for="goal in filteredGoals" :key="goal.id">
          <div class="card h-100" :class="statusBorderClass(goal.status)">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <span class="badge" :class="statusBadgeClass(goal.status)">{{ statusLabel(goal.status) }}</span>
                <span class="badge bg-light text-dark">{{ getCategoryInfo(goal.category).icon }} {{ getCategoryInfo(goal.category).label }}</span>
              </div>
              <h5 class="card-title">{{ goal.name }}</h5>
              <p class="card-text text-muted small mb-2" v-if="goal.description">
                {{ goal.description.length > 100 ? goal.description.substring(0, 100) + '...' : goal.description }}
              </p>

              <!-- Progress -->
              <div class="mb-2">
                <div class="d-flex justify-content-between mb-1">
                  <small>Progreso</small>
                  <small class="fw-bold">{{ goal.currentProgress }}/{{ goal.targetValue }} {{ goal.unit }}</small>
                </div>
                <div class="progress" style="height: 8px;">
                  <div class="progress-bar" :class="progressBarClass(goal)" :style="{ width: progressPct(goal) + '%' }"></div>
                </div>
              </div>

              <!-- Quick increment -->
              <div v-if="goal.status === 'ACTIVE'" class="mb-2">
                <button class="btn btn-sm btn-success w-100" @click.stop="quickIncrement(goal)" :disabled="goal._incrementing">
                  <span v-if="goal._incrementing" class="spinner-border spinner-border-sm"></span>
                  <template v-else><i class="bi bi-plus-circle"></i> +1 {{ goal.unit }}</template>
                </button>
              </div>

              <!-- Date info -->
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">
                  <i class="bi bi-calendar"></i> {{ formatDate(goal.targetDate) }}
                </small>
                <div>
                  <router-link :to="`/mental-health-goals/${goal.id}`" class="btn btn-sm btn-outline-primary me-1">
                    <i class="bi bi-eye"></i>
                  </router-link>
                  <router-link :to="`/mental-health-goals/${goal.id}/edit`" class="btn btn-sm btn-outline-secondary me-1" v-if="goal.status === 'ACTIVE'">
                    <i class="bi bi-pencil"></i>
                  </router-link>
                  <button class="btn btn-sm btn-outline-danger" @click="deleteGoal(goal)" v-if="goal.status !== 'COMPLETED'">
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import MentalHealthGoalRepository from '@/repositories/MentalHealthGoalRepository'
import { notify, confirm } from '@/common/notifications'

const goals = ref([])
const stats = ref(null)
const loading = ref(true)
const error = ref(null)
const filterStatus = ref(null)
const filterCategory = ref(null)

const categories = MentalHealthGoalRepository.getCategories()

const filteredGoals = computed(() => {
  let list = goals.value
  if (filterCategory.value) {
    list = list.filter(g => g.category === filterCategory.value)
  }
  return list
})

function getCategoryInfo(cat) {
  return MentalHealthGoalRepository.getCategoryInfo(cat)
}

function progressPct(goal) {
  if (!goal.targetValue) return 0
  return Math.min(100, (goal.currentProgress * 100) / goal.targetValue)
}

function progressBarClass(goal) {
  const pct = progressPct(goal)
  if (goal.status === 'COMPLETED') return 'bg-success'
  if (goal.status === 'FAILED') return 'bg-danger'
  if (pct >= 75) return 'bg-success'
  if (pct >= 50) return 'bg-info'
  if (pct >= 25) return 'bg-warning'
  return 'bg-primary'
}

function statusBadgeClass(status) {
  const map = { ACTIVE: 'bg-primary', COMPLETED: 'bg-success', FAILED: 'bg-danger', PAUSED: 'bg-secondary', CANCELLED: 'bg-secondary' }
  return map[status] || 'bg-secondary'
}

function statusBorderClass(status) {
  const map = { ACTIVE: 'border-primary', COMPLETED: 'border-success', FAILED: 'border-danger', PAUSED: 'border-secondary' }
  return map[status] || ''
}

function statusLabel(status) {
  const map = { ACTIVE: 'Activa', COMPLETED: 'Completada', FAILED: 'Fallida', PAUSED: 'Pausada', CANCELLED: 'Cancelada' }
  return map[status] || status
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' })
}

async function loadGoals() {
  loading.value = true
  error.value = null
  try {
    const [goalsData, statsData] = await Promise.all([
      MentalHealthGoalRepository.findAll(filterStatus.value),
      MentalHealthGoalRepository.getStatistics()
    ])
    goals.value = goalsData
    stats.value = statsData
  } catch (err) {
    console.error('Error loading goals:', err)
    error.value = 'No se pudieron cargar las metas. Verifica tu conexión.'
  } finally {
    loading.value = false
  }
}

async function deleteGoal(goal) {
  const confirmed = await confirm(`¿Eliminar la meta "${goal.name}"?`, { danger: true })
  if (!confirmed) return
  
  try {
    await MentalHealthGoalRepository.delete(goal.id)
    goals.value = goals.value.filter(g => g.id !== goal.id)
    notify.success('Meta eliminada correctamente')
  } catch (err) {
    console.error('Error deleting goal:', err)
    notify.error('Error al eliminar la meta')
  }
}

async function quickIncrement(goal) {
  goal._incrementing = true
  try {
    const updated = await MentalHealthGoalRepository.incrementProgress(goal.id, 1)
    // Update the goal in the list with the server response
    const idx = goals.value.findIndex(g => g.id === goal.id)
    if (idx !== -1) {
      goals.value[idx] = { ...updated, _incrementing: false }
    }
    // Refresh stats
    stats.value = await MentalHealthGoalRepository.getStatistics()
    notify.success('Progreso registrado exitosamente')
  } catch (err) {
    console.error('Error incrementing progress:', err)
    goal._incrementing = false
    notify.error('Error al registrar progreso')
  }
}

watch(filterStatus, () => loadGoals())

onMounted(() => loadGoals())
</script>
