<template>
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>
            <i class="bi bi-gift me-2"></i>
            {{ isEditMode ? 'Editar Recompensa' : 'Nueva Recompensa' }}
          </h2>
          <button @click="goBack" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i> Volver
          </button>
        </div>

        <!-- Formulario -->
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <form @submit.prevent="handleSubmit">

              <!-- Icono (selector visual) -->
              <div class="mb-3">
                <label class="form-label">Icono</label>
                <div class="d-flex flex-wrap gap-2 mb-2">
                  <button
                    v-for="icon in suggestedIcons"
                    :key="icon"
                    type="button"
                    :class="['btn', form.icon === icon ? 'btn-primary' : 'btn-outline-secondary']"
                    @click="form.icon = icon"
                    style="font-size: 1.5rem; width: 50px; height: 50px; padding: 0;"
                  >
                    {{ icon }}
                  </button>
                </div>
                <input
                  v-model="form.icon"
                  type="text"
                  class="form-control"
                  maxlength="10"
                  placeholder="O escribe un emoji..."
                  style="width: 100px;"
                />
              </div>

              <!-- Nombre -->
              <div class="mb-3">
                <label for="name" class="form-label">
                  Nombre de la recompensa <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-control"
                  id="name"
                  maxlength="200"
                  placeholder="Ej: Comprarme unas zapatillas nuevas"
                  required
                />
                <div class="form-text">{{ form.name.length }}/200 caracteres</div>
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
                  placeholder="Describe tu recompensa con más detalle..."
                ></textarea>
                <div class="form-text">{{ form.description.length }}/1000 caracteres</div>
              </div>

              <!-- Categoría -->
              <div class="mb-3">
                <label for="category" class="form-label">
                  Categoría <span class="text-danger">*</span>
                </label>
                <select
                  v-model="form.category"
                  class="form-select"
                  id="category"
                  required
                  @change="onCategoryChange"
                >
                  <option value="">Selecciona categoría</option>
                  <option
                    v-for="cat in categories"
                    :key="cat.value"
                    :value="cat.value"
                  >
                    {{ cat.icon }} {{ cat.label }} — {{ cat.description }}
                  </option>
                </select>
              </div>

              <!-- Meta asociada -->
              <div class="mb-4">
                <label for="goalId" class="form-label">
                  Meta asociada
                </label>
                <select
                  v-model="form.mentalHealthGoalId"
                  class="form-select"
                  id="goalId"
                >
                  <option :value="null">Sin meta asociada</option>
                  <option
                    v-for="goal in goals"
                    :key="goal.id"
                    :value="goal.id"
                  >
                    {{ goal.name }}
                    <template v-if="goal.status === 'COMPLETED'"> ✅</template>
                    <template v-else-if="goal.status === 'ACTIVE'"> (activa)</template>
                  </option>
                </select>
                <div class="form-text">
                  La recompensa se desbloqueará automáticamente cuando completes esta meta.
                  Si no asocias ninguna meta, podrás hacerlo más tarde.
                </div>
              </div>

              <!-- Preview -->
              <div class="mb-4">
                <label class="form-label text-muted">Vista previa</label>
                <div class="card bg-light">
                  <div class="card-body d-flex align-items-center gap-3">
                    <span class="fs-1">{{ form.icon || '🎁' }}</span>
                    <div>
                      <h5 class="mb-1">{{ form.name || 'Tu recompensa' }}</h5>
                      <small class="text-muted">{{ form.description || 'Descripción de la recompensa' }}</small>
                      <div v-if="selectedGoalName" class="mt-1">
                        <span class="badge bg-primary">
                          <i class="bi bi-bullseye me-1"></i>{{ selectedGoalName }}
                        </span>
                      </div>
                    </div>
                  </div>
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
                  {{ isEditMode ? 'Guardar cambios' : 'Crear recompensa' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Sugerencias -->
        <div v-if="!isEditMode" class="card shadow-sm mt-4">
          <div class="card-header bg-light">
            <h5 class="mb-0"><i class="bi bi-lightbulb me-2"></i>Ideas de recompensas</h5>
          </div>
          <div class="card-body">
            <div class="row g-2">
              <div
                v-for="suggestion in suggestions"
                :key="suggestion.name"
                class="col-md-6"
              >
                <button
                  type="button"
                  class="btn btn-outline-secondary w-100 text-start"
                  @click="applySuggestion(suggestion)"
                >
                  <span class="me-2">{{ suggestion.icon }}</span>
                  {{ suggestion.name }}
                  <small class="d-block text-muted">{{ suggestion.description }}</small>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import CustomRewardRepository from '@/repositories/CustomRewardRepository'
import MentalHealthGoalRepository from '@/repositories/MentalHealthGoalRepository'

export default {
  name: 'RewardForm',
  setup() {
    const router = useRouter()
    const route = useRoute()

    const form = ref({
      name: '',
      description: '',
      icon: '🎁',
      category: '',
      mentalHealthGoalId: null
    })

    const goals = ref([])
    const submitting = ref(false)
    const categories = CustomRewardRepository.getCategories()
    const rewardId = computed(() => route.params.id)
    const isEditMode = computed(() => !!rewardId.value && route.path.includes('edit'))

    const suggestedIcons = computed(() => {
      if (form.value.category) {
        return CustomRewardRepository.getSuggestedIcons(form.value.category)
      }
      return ['🎁', '⭐', '🎯', '💎', '🏆', '🌟', '✨', '🎊']
    })

    const selectedGoalName = computed(() => {
      if (!form.value.mentalHealthGoalId) return null
      const goal = goals.value.find(g => g.id === form.value.mentalHealthGoalId)
      return goal ? goal.name : null
    })

    const suggestions = [
      { name: 'Comprarme algo especial', icon: '🛍️', description: 'Un capricho que tengas pendiente', category: 'MATERIAL' },
      { name: 'Día de spa/descanso', icon: '🧖', description: 'Un día dedicado al autocuidado', category: 'PERSONAL' },
      { name: 'Cena en restaurante favorito', icon: '🍽️', description: 'Disfrutar de tu comida preferida', category: 'FOOD' },
      { name: 'Escapada de fin de semana', icon: '✈️', description: 'Un viaje corto como premio', category: 'EXPERIENCE' },
      { name: 'Suscripción premium', icon: '📱', description: 'App, streaming o servicio digital', category: 'DIGITAL' },
      { name: 'Salida con amigos', icon: '🎉', description: 'Plan especial con personas queridas', category: 'SOCIAL' },
      { name: 'Nuevo libro o curso', icon: '📚', description: 'Invertir en tu crecimiento', category: 'MATERIAL' },
      { name: 'Tarde libre sin culpa', icon: '😴', description: 'Tiempo de relax absoluto', category: 'PERSONAL' }
    ]

    const onCategoryChange = () => {
      // Actualizar icono al cambiar categoría
      const icons = CustomRewardRepository.getSuggestedIcons(form.value.category)
      if (icons.length > 0) {
        form.value.icon = icons[0]
      }
    }

    const applySuggestion = (suggestion) => {
      form.value.name = suggestion.name
      form.value.description = suggestion.description
      form.value.icon = suggestion.icon
      form.value.category = suggestion.category
    }

    const loadGoals = async () => {
      try {
        const allGoals = await MentalHealthGoalRepository.findAll()
        goals.value = allGoals
      } catch (error) {
        console.error('Error loading mental health goals:', error)
      }
    }

    const loadReward = async () => {
      try {
        const reward = await CustomRewardRepository.findOne(rewardId.value)
        form.value = {
          name: reward.name,
          description: reward.description || '',
          icon: reward.icon || '🎁',
          category: reward.category || 'OTHER',
          mentalHealthGoalId: reward.mentalHealthGoalId || null
        }
      } catch (error) {
        console.error('Error loading reward:', error)
        alert('Error al cargar la recompensa')
        goBack()
      }
    }

    const handleSubmit = async () => {
      try {
        submitting.value = true

        const rewardData = {
          name: form.value.name,
          description: form.value.description || null,
          icon: form.value.icon || '🎁',
          category: form.value.category || 'OTHER',
          mentalHealthGoalId: form.value.mentalHealthGoalId || null
        }

        if (isEditMode.value) {
          await CustomRewardRepository.update(rewardId.value, rewardData)
          alert('Recompensa actualizada correctamente')
        } else {
          await CustomRewardRepository.create(rewardData)
          alert('¡Recompensa creada! Se desbloqueará cuando completes la meta asociada.')
        }

        router.push('/rewards')
      } catch (error) {
        console.error('Error saving reward:', error)
        alert('Error al guardar la recompensa: ' + (error.response?.data?.message || error.message))
      } finally {
        submitting.value = false
      }
    }

    const goBack = () => {
      router.push('/rewards')
    }

    onMounted(async () => {
      await loadGoals()
      if (isEditMode.value) {
        await loadReward()
      }

      // Si viene con goalId en la query (desde una meta de salud mental)
      if (route.query.goalId) {
        form.value.mentalHealthGoalId = parseInt(route.query.goalId)
      }
    })

    return {
      form,
      goals,
      submitting,
      categories,
      isEditMode,
      suggestedIcons,
      selectedGoalName,
      suggestions,
      onCategoryChange,
      applySuggestion,
      handleSubmit,
      goBack
    }
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 15px;
}
</style>
