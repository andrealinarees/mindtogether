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
        <label class="form-label fw-bold">Recompensa Personal</label>
        <input type="text" class="form-control" v-model="form.rewardDescription"
               placeholder="¿Cómo te premiarás al completarla?" maxlength="500">
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

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const categories = MentalHealthGoalRepository.getCategories()
const today = new Date().toISOString().split('T')[0]

const loadingGoal = ref(false)
const submitting = ref(false)
const errorMsg = ref(null)

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

  try {
    if (isEdit.value) {
      await MentalHealthGoalRepository.update(route.params.id, form.value)
    } else {
      await MentalHealthGoalRepository.create(form.value)
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
