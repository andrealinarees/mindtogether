<template>
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>
            <i class="bi bi-bullseye me-2"></i>
            {{ isEditMode ? 'Editar Meta' : 'Nueva Meta' }}
          </h2>
          <button @click="goBack" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i> Volver
          </button>
        </div>

        <!-- Formulario -->
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <form @submit.prevent="handleSubmit">
              
              <!-- Nombre -->
              <div class="mb-3">
                <label for="name" class="form-label">
                  Nombre de la meta <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-control"
                  id="name"
                  maxlength="200"
                  placeholder="Ej: Correr 100km este mes"
                  required
                />
                <div class="form-text">{{ form.name.length }}/200 caracteres</div>
              </div>

              <!-- Hábito relacionado -->
              <div class="mb-3">
                <label for="habitId" class="form-label">
                  Hábito relacionado <span class="text-danger">*</span>
                </label>
                <select
                  v-model="form.habitId"
                  class="form-select"
                  id="habitId"
                  required
                >
                  <option value="">Selecciona un hábito</option>
                  <option
                    v-for="habit in habits"
                    :key="habit.id"
                    :value="habit.id"
                  >
                    {{ habit.name }}
                  </option>
                </select>
                <div class="form-text">
                  Esta meta estará vinculada a un hábito específico
                </div>
              </div>

              <!-- Descripción -->
              <div class="mb-3">
                <label for="description" class="form-label">Descripción</label>
                <textarea
                  v-model="form.description"
                  class="form-control"
                  id="description"
                  rows="3"
                  maxlength="1000"
                  placeholder="Describe tu meta con más detalle..."
                ></textarea>
                <div class="form-text">{{ form.description.length }}/1000 caracteres</div>
              </div>

              <!-- Objetivo numérico -->
              <div class="mb-3">
                <label for="targetValue" class="form-label">
                  Valor de la meta <span class="text-danger">*</span>
                </label>
                <input
                  v-model.number="form.targetValue"
                  type="number"
                  class="form-control"
                  id="targetValue"
                  min="1"
                  placeholder="Ej: 100"
                  required
                />
                <div class="form-text">
                  Cantidad que quieres alcanzar (días, kilómetros, repeticiones, etc.)
                </div>
              </div>

              <!-- Fecha límite -->
              <div class="mb-3">
                <label for="targetDate" class="form-label">
                  Fecha límite <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.targetDate"
                  type="date"
                  class="form-control"
                  id="targetDate"
                  :min="minDate"
                  :max="maxDate"
                  required
                />
                <div class="form-text" v-if="selectedHabitEndDate">
                  La fecha límite no puede ser posterior al {{ formatDate(selectedHabitEndDate) }} (fecha fin del hábito)
                </div>
                <div class="form-text" v-else>
                  Fecha en la que quieres haber alcanzado tu meta
                </div>
              </div>

              <!-- Recompensa personal -->
              <div class="mb-3">
                <label for="personalReward" class="form-label">
                  Recompensa personal
                </label>
                <input
                  v-model="form.personalReward"
                  type="text"
                  class="form-control"
                  id="personalReward"
                  maxlength="500"
                  placeholder="Ej: Me compraré unas zapatillas nuevas"
                />
                <div class="form-text">
                  ¿Qué te darás cuando alcances esta meta?
                </div>
              </div>

              <!-- Progreso inicial (solo en edición) -->
              <div v-if="isEditMode" class="mb-3">
                <label for="currentProgress" class="form-label">
                  Progreso actual
                </label>
                <input
                  v-model.number="form.currentProgress"
                  type="number"
                  class="form-control"
                  id="currentProgress"
                  min="0"
                  :max="form.targetValue"
                />
                <div class="form-text">
                  Tu progreso actual hacia la meta
                </div>
              </div>

              <!-- Botones -->
              <div class="d-flex gap-2 justify-content-end mt-4">
                <button
                  type="button"
                  @click="goBack"
                  class="btn btn-secondary"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  class="btn btn-primary"
                  :disabled="submitting"
                >
                  <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                  {{ isEditMode ? 'Guardar cambios' : 'Crear meta' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import GoalRepository from '@/repositories/GoalRepository'
import HabitRepository from '@/repositories/HabitRepository'

export default {
  name: 'GoalForm',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const form = ref({
      name: '',
      habitId: '',
      description: '',
      targetValue: null,
      targetDate: '',
      personalReward: '',
      currentProgress: 0
    })

    const habits = ref([])
    const submitting = ref(false)
    const goalId = computed(() => route.params.id)
    const isEditMode = computed(() => !!goalId.value && route.path.includes('edit'))

    const minDate = computed(() => {
      const tomorrow = new Date()
      tomorrow.setDate(tomorrow.getDate() + 1)
      return tomorrow.toISOString().split('T')[0]
    })

    const maxDate = computed(() => {
      if (!form.value.habitId) {
        return null
      }
      const selectedHabit = habits.value.find(h => h.id === parseInt(form.value.habitId))
      if (selectedHabit && selectedHabit.endDate) {
        return selectedHabit.endDate
      }
      return null
    })

    const selectedHabitEndDate = computed(() => {
      if (!form.value.habitId) return null
      const selectedHabit = habits.value.find(h => h.id === parseInt(form.value.habitId))
      return selectedHabit ? selectedHabit.endDate : null
    })

    const loadHabits = async () => {
      try {
        const allHabits = await HabitRepository.findAll()
        habits.value = allHabits.filter(h => h.status === 'ACTIVE')
      } catch (error) {
        console.error('Error loading habits:', error)
      }
    }

    const loadGoal = async () => {
      try {
        const goal = await GoalRepository.findOne(goalId.value)
        form.value = {
          name: goal.name,
          habitId: goal.habitId,
          description: goal.description || '',
          targetValue: goal.targetValue,
          targetDate: goal.targetDate,
          personalReward: goal.personalReward || '',
          currentProgress: goal.currentProgress
        }
      } catch (error) {
        console.error('Error loading goal:', error)
        alert('Error al cargar el objetivo')
        goBack()
      }
    }

    const handleSubmit = async () => {
      try {
        submitting.value = true

        // Validar que la fecha límite no sea posterior a la fecha fin del hábito
        if (maxDate.value && form.value.targetDate > maxDate.value) {
          alert(`La fecha límite no puede ser posterior a la fecha fin del hábito (${formatDate(maxDate.value)})`)
          return
        }

        const goalData = {
          name: form.value.name,
          habitId: parseInt(form.value.habitId),
          description: form.value.description || null,
          targetValue: parseInt(form.value.targetValue),
          targetDate: form.value.targetDate,
          personalReward: form.value.personalReward || null,
          currentProgress: parseInt(form.value.currentProgress) || 0
        }

        if (isEditMode.value) {
          await GoalRepository.update(goalId.value, goalData)
          alert('Meta actualizada correctamente')
        } else {
          await GoalRepository.create(goalData)
          alert('Meta creada correctamente')
        }

        router.push('/goals')
      } catch (error) {
        console.error('Error saving goal:', error)
        alert('Error al guardar la meta: ' + (error.response?.data?.message || error.message))
      } finally {
        submitting.value = false
      }
    }

    const goBack = () => {
      router.push('/goals')
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

    onMounted(async () => {
      await loadHabits()
      if (isEditMode.value) {
        await loadGoal()
      }
    })

    return {
      form,
      habits,
      submitting,
      isEditMode,
      minDate,
      maxDate,
      selectedHabitEndDate,
      handleSubmit,
      goBack,
      formatDate
    }
  }
}
</script>
