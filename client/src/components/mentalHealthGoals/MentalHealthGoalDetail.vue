<template>
  <div class="container py-4">
    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="mt-2 text-muted">Cargando meta...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
      <router-link to="/mental-health-goals" class="btn btn-sm btn-outline-secondary ms-2">Volver</router-link>
    </div>

    <template v-else-if="goal">
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-start mb-4">
        <div>
          <router-link to="/mental-health-goals" class="text-decoration-none text-muted small">
            <i class="bi bi-arrow-left"></i> Volver a mis metas
          </router-link>
          <h2 class="mt-1 mb-0">{{ getCategoryInfo(goal.category).icon }} {{ goal.name }}</h2>
          <div class="mt-1">
            <span class="badge" :class="statusBadgeClass(goal.status)">{{ statusLabel(goal.status) }}</span>
            <span class="badge bg-light text-dark ms-1">{{ getCategoryInfo(goal.category).label }}</span>
            <span class="badge bg-light text-dark ms-1">Dificultad: {{ goal.difficultyLevel || 3 }}/5</span>
          </div>
        </div>
        <div class="d-flex gap-1" v-if="goal.status === 'ACTIVE'">
          <router-link :to="`/mental-health-goals/${goal.id}/edit`" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-pencil"></i> Editar
          </router-link>
        </div>
      </div>

      <div class="row g-4">
        <!-- Left column: main info -->
        <div class="col-lg-8">
          <!-- Progress Card -->
          <div class="card mb-4">
            <div class="card-header bg-white fw-bold">
              <i class="bi bi-graph-up"></i> Progreso
            </div>
            <div class="card-body">
              <div class="d-flex justify-content-between mb-2">
                <span>{{ goal.currentProgress }} / {{ goal.targetValue }} {{ goal.unit }}</span>
                <span class="fw-bold">{{ Math.round(progressPct) }}%</span>
              </div>
              <div class="progress mb-3" style="height: 20px;">
                <div class="progress-bar progress-bar-striped" :class="progressBarClass" :style="{ width: progressPct + '%' }">
                  {{ Math.round(progressPct) }}%
                </div>
              </div>

              <!-- Increment progress -->
              <div v-if="goal.status === 'ACTIVE'" class="row align-items-end g-2">
                <div class="col-auto">
                  <label class="form-label small">Incrementar</label>
                  <input type="number" class="form-control form-control-sm" v-model.number="incrementAmount" min="1" style="width:80px">
                </div>
                <div class="col-auto">
                  <label class="form-label small">Estado de ánimo</label>
                  <select class="form-select form-select-sm" v-model.number="moodRating" style="width:100px">
                    <option :value="null">--</option>
                    <option v-for="n in 10" :key="n" :value="n">{{ n }}/10 {{ moodEmoji(n) }}</option>
                  </select>
                </div>
                <div class="col-auto">
                  <button class="btn btn-sm btn-primary" @click="doIncrement" :disabled="incrementing">
                    <span v-if="incrementing" class="spinner-border spinner-border-sm"></span>
                    <i v-else class="bi bi-plus-lg"></i> Registrar progreso
                  </button>
                </div>
              </div>

              <!-- Date info -->
              <div class="row text-center mt-3 pt-3 border-top">
                <div class="col-4">
                  <small class="text-muted d-block">Creada</small>
                  <small class="fw-bold">{{ formatDate(goal.createdAt) }}</small>
                </div>
                <div class="col-4">
                  <small class="text-muted d-block">Fecha límite</small>
                  <small class="fw-bold" :class="{ 'text-danger': goal.overdue }">{{ formatDate(goal.targetDate) }}</small>
                </div>
                <div class="col-4">
                  <small class="text-muted d-block">Días restantes</small>
                  <small class="fw-bold">{{ goal.daysRemaining >= 0 ? goal.daysRemaining : 0 }}</small>
                </div>
              </div>
            </div>
          </div>

          <!-- Description -->
          <div class="card mb-4" v-if="goal.description">
            <div class="card-header bg-white fw-bold">
              <i class="bi bi-text-paragraph"></i> Descripción
            </div>
            <div class="card-body">
              <p class="mb-0">{{ goal.description }}</p>
            </div>
          </div>

          <!-- Milestones -->
          <div class="card mb-4">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
              <span class="fw-bold"><i class="bi bi-flag"></i> Hitos</span>
              <button v-if="goal.status === 'ACTIVE'" class="btn btn-sm btn-outline-primary" @click="showMilestoneForm = !showMilestoneForm">
                <i class="bi bi-plus"></i>
              </button>
            </div>
            <div class="card-body">
              <!-- Add milestone form -->
              <div v-if="showMilestoneForm" class="row g-2 mb-3 p-2 bg-light rounded">
                <div class="col-md-5">
                  <input type="text" class="form-control form-control-sm" v-model="newMilestone.name" placeholder="Nombre del hito">
                </div>
                <div class="col-md-3">
                  <input type="number" class="form-control form-control-sm" v-model.number="newMilestone.targetValue" placeholder="Valor" min="1">
                </div>
                <div class="col-md-4">
                  <button class="btn btn-sm btn-primary" @click="addMilestone" :disabled="!newMilestone.name">
                    <i class="bi bi-check"></i> Añadir
                  </button>
                  <button class="btn btn-sm btn-secondary ms-1" @click="showMilestoneForm = false">
                    <i class="bi bi-x"></i>
                  </button>
                </div>
              </div>

              <div v-if="milestones.length === 0" class="text-muted text-center py-2">
                <small>Sin hitos definidos</small>
              </div>
              <ul v-else class="list-group list-group-flush">
                <li class="list-group-item d-flex justify-content-between align-items-center" v-for="m in milestones" :key="m.id">
                  <div>
                    <i class="bi" :class="m.isCompleted ? 'bi-check-circle-fill text-success' : 'bi-circle text-muted'"></i>
                    <span class="ms-2" :class="{ 'text-decoration-line-through': m.isCompleted }">{{ m.name }}</span>
                    <small class="text-muted ms-1" v-if="m.targetValue"> ({{ m.targetValue }} {{ goal.unit }})</small>
                  </div>
                  <span v-if="m.isCompleted" class="badge bg-success">Completado</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Right column: sidebar -->
        <div class="col-lg-4">
          <!-- Motivation Card -->
          <div class="card mb-3" v-if="goal.personalMotivation">
            <div class="card-header bg-white fw-bold"><i class="bi bi-heart"></i> Motivación</div>
            <div class="card-body"><p class="mb-0 small">{{ goal.personalMotivation }}</p></div>
          </div>

          <!-- Benefit Card -->
          <div class="card mb-3" v-if="goal.expectedBenefit">
            <div class="card-header bg-white fw-bold"><i class="bi bi-stars"></i> Beneficio Esperado</div>
            <div class="card-body"><p class="mb-0 small">{{ goal.expectedBenefit }}</p></div>
          </div>

          <!-- Reward Card -->
          <div class="card mb-3" v-if="goal.rewardDescription">
            <div class="card-header bg-white fw-bold"><i class="bi bi-gift"></i> Recompensa</div>
            <div class="card-body"><p class="mb-0 small">{{ goal.rewardDescription }}</p></div>
          </div>


        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MentalHealthGoalRepository from '@/repositories/MentalHealthGoalRepository'

const route = useRoute()

const goal = ref(null)
const milestones = ref([])
const loading = ref(true)
const error = ref(null)
const incrementAmount = ref(1)
const moodRating = ref(null)
const incrementing = ref(false)
const showMilestoneForm = ref(false)
const newMilestone = ref({ name: '', targetValue: null })

const progressPct = computed(() => {
  if (!goal.value || !goal.value.targetValue) return 0
  return Math.min(100, (goal.value.currentProgress * 100) / goal.value.targetValue)
})

const progressBarClass = computed(() => {
  const pct = progressPct.value
  if (goal.value?.status === 'COMPLETED') return 'bg-success'
  if (pct >= 75) return 'bg-success'
  if (pct >= 50) return 'bg-info'
  if (pct >= 25) return 'bg-warning'
  return 'bg-primary'
})

function getCategoryInfo(cat) {
  return MentalHealthGoalRepository.getCategoryInfo(cat)
}

function statusBadgeClass(status) {
  const map = { ACTIVE: 'bg-primary', COMPLETED: 'bg-success', FAILED: 'bg-danger', PAUSED: 'bg-secondary', CANCELLED: 'bg-secondary' }
  return map[status] || 'bg-secondary'
}

function statusLabel(status) {
  const map = { ACTIVE: 'Activa', COMPLETED: 'Completada', FAILED: 'Fallida', PAUSED: 'Pausada', CANCELLED: 'Cancelada' }
  return map[status] || status
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' })
}

function moodEmoji(n) {
  if (n <= 2) return '😢'
  if (n <= 4) return '😕'
  if (n <= 6) return '😐'
  if (n <= 8) return '😊'
  return '😄'
}

async function loadGoal() {
  loading.value = true
  error.value = null
  try {
    const [goalData, milestonesData] = await Promise.all([
      MentalHealthGoalRepository.findById(route.params.id),
      MentalHealthGoalRepository.getMilestones(route.params.id)
    ])
    goal.value = goalData
    milestones.value = milestonesData
  } catch (err) {
    console.error('Error loading goal:', err)
    error.value = 'No se pudo cargar la meta.'
  } finally {
    loading.value = false
  }
}

async function doIncrement() {
  incrementing.value = true
  try {
    const updated = await MentalHealthGoalRepository.incrementProgress(
      goal.value.id, incrementAmount.value, moodRating.value
    )
    goal.value = updated
    // Reload milestones in case any were completed by this progress
    milestones.value = await MentalHealthGoalRepository.getMilestones(goal.value.id)
  } catch (err) {
    console.error('Error incrementing progress:', err)
    alert('Error al registrar progreso')
  } finally {
    incrementing.value = false
  }
}

async function addMilestone() {
  try {
    const orderIndex = milestones.value.length
    const milestone = await MentalHealthGoalRepository.addMilestone(goal.value.id, {
      name: newMilestone.value.name,
      targetValue: newMilestone.value.targetValue || goal.value.targetValue,
      orderIndex
    })
    milestones.value.push(milestone)
    newMilestone.value = { name: '', targetValue: null }
    showMilestoneForm.value = false
  } catch (err) {
    console.error('Error adding milestone:', err)
    alert('Error al añadir hito')
  }
}

onMounted(() => loadGoal())
</script>
