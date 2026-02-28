<template>
  <div class="container py-4">
    <h2 class="mb-4">
      <i class="bi" :class="isEdit ? 'bi-pencil-square' : 'bi-plus-circle'"></i>
      {{ isEdit ? 'Editar' : 'Nueva' }} Meta de Salud Mental
    </h2>

    <div v-if="loadingGoal" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
    </div>

    <form v-else @submit.prevent="handleSubmit" class="row g-3">
      <!-- Nombre -->
      <div class="col-12">
        <label class="form-label fw-bold">Nombre de la Meta *</label>
        <input type="text" class="form-control" v-model="form.name" required
               placeholder="Ej: Meditar 10 minutos al día" maxlength="200">
      </div>

      <!-- Categoría -->
      <div class="col-md-6">
        <label class="form-label fw-bold">Categoría *</label>
        <select class="form-select" v-model="form.category" required>
          <option value="">Selecciona una categoría</option>
          <option v-for="cat in categories" :key="cat.value" :value="cat.value">
            {{ cat.icon }} {{ cat.label }}
          </option>
        </select>
        <div class="form-text" v-if="selectedCategory">{{ selectedCategory.description }}</div>
      </div>

      <!-- Dificultad -->
      <div class="col-md-6">
        <label class="form-label fw-bold">Dificultad</label>
        <select class="form-select" v-model.number="form.difficultyLevel">
          <option :value="1">1 - Muy fácil</option>
          <option :value="2">2 - Fácil</option>
          <option :value="3">3 - Normal</option>
          <option :value="4">4 - Difícil</option>
          <option :value="5">5 - Muy difícil</option>
        </select>
      </div>

      <!-- Descripción -->
      <div class="col-12">
        <label class="form-label fw-bold">Descripción</label>
        <textarea class="form-control" v-model="form.description" rows="3"
                  placeholder="Describe tu meta y por qué es importante para ti" maxlength="2000"></textarea>
      </div>

      <!-- Valor objetivo + Unidad -->
      <div class="col-md-4">
        <label class="form-label fw-bold">Valor Objetivo *</label>
        <input type="number" class="form-control" v-model.number="form.targetValue" required min="1"
               placeholder="Ej: 21">
      </div>
      <div class="col-md-4">
        <label class="form-label fw-bold">Unidad</label>
        <select class="form-select" v-model="form.unit">
          <option value="veces">Veces</option>
          <option value="días">Días</option>
          <option value="minutos">Minutos</option>
          <option value="horas">Horas</option>
          <option value="sesiones">Sesiones</option>
        </select>
      </div>
      <div class="col-md-4">
        <label class="form-label fw-bold">Fecha Límite *</label>
        <input type="date" class="form-control" v-model="form.targetDate" required :min="today">
      </div>

      <!-- Motivación y beneficios -->
      <div class="col-md-6">
        <label class="form-label fw-bold">Motivación Personal</label>
        <textarea class="form-control" v-model="form.personalMotivation" rows="2"
                  placeholder="¿Por qué quieres lograr esta meta?" maxlength="1000"></textarea>
      </div>
      <div class="col-md-6">
        <label class="form-label fw-bold">Beneficio Esperado</label>
        <textarea class="form-control" v-model="form.expectedBenefit" rows="2"
                  placeholder="¿Cómo te beneficiará completar esta meta?" maxlength="1000"></textarea>
      </div>

      <!-- Recompensa -->
      <div class="col-12">
        <div class="form-check form-switch mb-2">
          <input v-model="addReward" class="form-check-input" type="checkbox" id="addRewardSwitch" />
          <label class="form-check-label fw-bold" for="addRewardSwitch">
            <i class="bi bi-gift me-1"></i> Añadir recompensa a esta meta
          </label>
        </div>
        <div class="form-text mb-2" v-if="!addReward">
          Activa para ponerte un premio cuando completes esta meta
        </div>
      </div>

      <!-- Sección de recompensa (colapsable) -->
      <div v-if="addReward" class="col-12">
        <div class="card bg-light">
          <div class="card-body">
            <h6 class="card-title mb-3"><i class="bi bi-gift me-1"></i> Tu recompensa</h6>

            <!-- Icono -->
            <div class="mb-3">
              <label class="form-label">Icono</label>
              <div class="d-flex flex-wrap gap-1 mb-1">
                <button
                  v-for="icon in suggestedIcons"
                  :key="icon"
                  type="button"
                  :class="['btn btn-sm', reward.icon === icon ? 'btn-primary' : 'btn-outline-secondary']"
                  @click="reward.icon = icon"
                  style="font-size: 1.2rem; width: 40px; height: 40px; padding: 0;"
                >
                  {{ icon }}
                </button>
              </div>
            </div>

            <!-- Nombre de la recompensa -->
            <div class="mb-3">
              <label for="rewardName" class="form-label">Nombre <span class="text-danger">*</span></label>
              <input v-model="reward.name" type="text" class="form-control" id="rewardName"
                     maxlength="200" placeholder="Ej: Comprarme unas zapatillas nuevas" :required="addReward" />
            </div>

            <!-- Descripción de la recompensa -->
            <div class="mb-3">
              <label for="rewardDesc" class="form-label">Descripción</label>
              <input v-model="reward.description" type="text" class="form-control" id="rewardDesc"
                     maxlength="500" placeholder="Ej: Las zapatillas azules de la tienda del centro" />
            </div>

            <!-- Categoría de la recompensa -->
            <div class="mb-2">
              <label for="rewardCategory" class="form-label">Categoría</label>
              <select v-model="reward.category" class="form-select" id="rewardCategory"
                      @change="onRewardCategoryChange">
                <option v-for="cat in rewardCategories" :key="cat.value" :value="cat.value">
                  {{ cat.icon }} {{ cat.label }} — {{ cat.description }}
                </option>
              </select>
            </div>

            <!-- Ideas rápidas -->
            <div class="mt-3">
              <small class="text-muted d-block mb-1">Ideas rápidas:</small>
              <div class="d-flex flex-wrap gap-1">
                <button v-for="idea in rewardIdeas" :key="idea.name" type="button"
                        class="btn btn-sm btn-outline-secondary" @click="applyRewardIdea(idea)">
                  {{ idea.icon }} {{ idea.name }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Botones -->
      <div class="col-12">
        <hr>
        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            <span v-if="submitting" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="bi bi-check-lg"></i>
            {{ isEdit ? 'Guardar Cambios' : 'Crear Meta' }}
          </button>
          <router-link to="/mental-health-goals" class="btn btn-secondary">
            <i class="bi bi-x-lg"></i> Cancelar
          </router-link>
        </div>
      </div>

      <!-- Error -->
      <div class="col-12" v-if="errorMsg">
        <div class="alert alert-danger mt-2">
          <i class="bi bi-exclamation-triangle"></i> {{ errorMsg }}
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MentalHealthGoalRepository from '@/repositories/MentalHealthGoalRepository'
import CustomRewardRepository from '@/repositories/CustomRewardRepository'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const categories = MentalHealthGoalRepository.getCategories()
const today = new Date().toISOString().split('T')[0]

const loadingGoal = ref(false)
const submitting = ref(false)
const errorMsg = ref(null)

const addReward = ref(false)
const reward = ref({
  name: '',
  description: '',
  icon: '🎁',
  category: 'OTHER'
})

const rewardCategories = CustomRewardRepository.getCategories()

const suggestedIcons = computed(() => {
  return CustomRewardRepository.getSuggestedIcons(reward.value.category)
})

const rewardIdeas = [
  { name: 'Algo especial', icon: '🛍️', category: 'MATERIAL' },
  { name: 'Día de spa', icon: '🧖', category: 'PERSONAL' },
  { name: 'Cena favorita', icon: '🍽️', category: 'FOOD' },
  { name: 'Escapada', icon: '✈️', category: 'EXPERIENCE' },
  { name: 'Suscripción', icon: '📱', category: 'DIGITAL' },
  { name: 'Plan con amigos', icon: '🎉', category: 'SOCIAL' }
]

const onRewardCategoryChange = () => {
  const icons = CustomRewardRepository.getSuggestedIcons(reward.value.category)
  if (icons.length > 0) {
    reward.value.icon = icons[0]
  }
}

const applyRewardIdea = (idea) => {
  reward.value.name = idea.name
  reward.value.icon = idea.icon
  reward.value.category = idea.category
}

const form = ref({
  name: '',
  category: '',
  description: '',
  targetValue: 7,
  unit: 'veces',
  targetDate: '',
  difficultyLevel: 3,
  personalMotivation: '',
  expectedBenefit: '',
  rewardDescription: ''
})

const selectedCategory = computed(() => {
  if (!form.value.category) return null
  return categories.find(c => c.value === form.value.category)
})

// Set default target date to 30 days from now
const defaultDate = new Date()
defaultDate.setDate(defaultDate.getDate() + 30)
form.value.targetDate = defaultDate.toISOString().split('T')[0]

onMounted(async () => {
  if (isEdit.value) {
    loadingGoal.value = true
    try {
      const goal = await MentalHealthGoalRepository.findById(route.params.id)
      form.value = {
        name: goal.name || '',
        category: goal.category || '',
        description: goal.description || '',
        targetValue: goal.targetValue || 7,
        unit: goal.unit || 'veces',
        targetDate: goal.targetDate || '',
        difficultyLevel: goal.difficultyLevel || 3,
        personalMotivation: goal.personalMotivation || '',
        expectedBenefit: goal.expectedBenefit || '',
        rewardDescription: goal.rewardDescription || ''
      }
      // Cargar recompensas existentes vinculadas
      try {
        const existingRewards = await CustomRewardRepository.findByMentalHealthGoal(route.params.id)
        if (existingRewards && existingRewards.length > 0) {
          const existing = existingRewards[0]
          addReward.value = true
          reward.value = {
            name: existing.name || '',
            description: existing.description || '',
            icon: existing.icon || '🎁',
            category: existing.category || 'OTHER'
          }
        }
      } catch (rewardErr) {
        // No pasa nada si no se pueden cargar
      }
    } catch (err) {
      console.error('Error loading goal:', err)
      errorMsg.value = 'No se pudo cargar la meta.'
    } finally {
      loadingGoal.value = false
    }
  }
})

async function handleSubmit() {
  submitting.value = true
  errorMsg.value = null

  // Validar recompensa si está activa
  if (addReward.value && !reward.value.name.trim()) {
    errorMsg.value = 'Si añades una recompensa, debes ponerle un nombre'
    submitting.value = false
    return
  }

  try {
    // Si hay recompensa, guardar su nombre como rewardDescription
    const goalData = { ...form.value }
    if (addReward.value && reward.value.name.trim()) {
      goalData.rewardDescription = reward.value.name
    }

    let savedGoal
    if (isEdit.value) {
      savedGoal = await MentalHealthGoalRepository.update(route.params.id, goalData)
    } else {
      savedGoal = await MentalHealthGoalRepository.create(goalData)
    }

    // Crear recompensa vinculada a la meta
    if (addReward.value && reward.value.name.trim() && !isEdit.value) {
      try {
        const rewardData = {
          name: reward.value.name,
          description: reward.value.description || null,
          icon: reward.value.icon,
          category: reward.value.category,
          mentalHealthGoalId: savedGoal?.id || null
        }
        await CustomRewardRepository.create(rewardData)
      } catch (rewardErr) {
        console.error('Error creating reward:', rewardErr)
        // La meta se creó bien, solo falló la recompensa
      }
    }

    router.push('/mental-health-goals')
  } catch (err) {
    console.error('Error saving goal:', err)
    errorMsg.value = 'Error al guardar la meta. Revisa los campos e inténtalo de nuevo.'
  } finally {
    submitting.value = false
  }
}
</script>
