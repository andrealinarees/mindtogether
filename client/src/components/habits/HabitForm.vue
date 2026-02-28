<template>
  <div class="container mt-4">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">
              <i :class="['bi', 'me-2', isEditMode ? 'bi-pencil-square' : 'bi-plus-circle']"></i>
              {{ isEditMode ? 'Editar Hábito' : 'Crear Nuevo Hábito' }}
            </h3>
          </div>
          <div class="card-body" v-if="!loading">
            <form @submit.prevent="saveHabit">
              <!-- Nombre -->
              <div class="mb-3">
                <label for="name" class="form-label fw-bold">
                  <i class="bi bi-tag me-1"></i>Nombre del Hábito *
                </label>
                <input
                  type="text"
                  class="form-control"
                  id="name"
                  v-model="form.name"
                  required
                  placeholder="Ej: Meditación matutina, Respiración 4-7-8, Caminata consciente..."
                />
              </div>

              <!-- Descripción -->
              <div class="mb-3">
                <label for="description" class="form-label fw-bold">
                  <i class="bi bi-card-text me-1"></i>Descripción
                </label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="form.description"
                  rows="3"
                  placeholder="Describe tu práctica de bienestar, beneficios y cómo realizarla..."
                ></textarea>
              </div>

              <!-- Frecuencia -->
              <div class="mb-3">
                <label for="frequency" class="form-label fw-bold">
                  <i class="bi bi-calendar-check me-1"></i>Frecuencia *
                </label>
                <select
                  class="form-select"
                  id="frequency"
                  v-model="form.frequency"
                  required
                >
                  <option value="">Selecciona una frecuencia</option>
                  <option value="DAILY">📅 Diaria</option>
                  <option value="WEEKLY">📆 Semanal</option>
                </select>
                <div class="form-text">
                  ¿Con qué frecuencia quieres realizar este hábito?
                </div>
              </div>

              <!-- Categoría -->
              <div class="mb-3">
                <label for="category" class="form-label fw-bold">
                  <i class="bi bi-tags me-1"></i>Categoría *
                </label>
                <select
                  class="form-select"
                  id="category"
                  v-model="form.categoryId"
                  required
                >
                  <option value="">Selecciona una categoría</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                    {{ cat.name }}
                  </option>
                </select>
                <div class="form-text">
                  Ayuda a organizar tus hábitos por temática
                </div>
              </div>

              <!-- Fechas -->
              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="startDate" class="form-label fw-bold">
                    <i class="bi bi-calendar-event me-1"></i>Fecha de Inicio *
                  </label>
                  <input
                    type="date"
                    class="form-control"
                    id="startDate"
                    v-model="form.startDate"
                    required
                    :min="today"
                  />
                </div>
                <div class="col-md-6">
                  <label for="endDate" class="form-label fw-bold">
                    <i class="bi bi-calendar-x me-1"></i>Fecha de Fin *
                  </label>
                  <input
                    type="date"
                    class="form-control"
                    id="endDate"
                    v-model="form.endDate"
                    required
                    :min="form.startDate || today"
                  />
                </div>
              </div>

              <!-- Hora (opcional) -->
              <div class="mb-3">
                <label for="habitTime" class="form-label fw-bold">
                  <i class="bi bi-clock me-1"></i>Hora (opcional)
                </label>
                <input
                  type="time"
                  class="form-control"
                  id="habitTime"
                  v-model="form.habitTime"
                />
                <div class="form-text">
                  ¿A qué hora prefieres hacer este hábito?
                </div>
              </div>

              <!-- Ubicación -->
              <div class="mb-3">
                <label class="form-label fw-bold">
                  <i class="bi bi-geo-alt me-1"></i>Ubicación *
                </label>
                <div class="btn-group w-100" role="group">
                  <input
                    type="radio"
                    class="btn-check"
                    name="location"
                    id="interior"
                    value="INTERIOR"
                    v-model="form.location"
                    required
                  />
                  <label class="btn btn-outline-primary" for="interior">
                    <i class="bi bi-house-door me-1"></i>Interior
                  </label>

                  <input
                    type="radio"
                    class="btn-check"
                    name="location"
                    id="exterior"
                    value="EXTERIOR"
                    v-model="form.location"
                    required
                  />
                  <label class="btn btn-outline-success" for="exterior">
                    <i class="bi bi-tree me-1"></i>Exterior
                  </label>
                </div>
                <div class="form-text">
                  ¿Dónde realizarás este hábito?
                </div>
              </div>

              <!-- Botones -->
              <div class="d-flex justify-content-between mt-4">
                <button
                  type="button"
                  class="btn btn-secondary"
                  @click="goBack"
                >
                  <i class="bi bi-arrow-left me-1"></i>Cancelar
                </button>
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  <span v-if="saving">
                    <span class="spinner-border spinner-border-sm me-1"></span>
                    Guardando...
                  </span>
                  <span v-else>
                    <i class="bi bi-check-circle me-1"></i>
                    {{ isEditMode ? 'Actualizar Hábito' : 'Crear Hábito' }}
                  </span>
                </button>
              </div>
            </form>
          </div>
          <div class="card-body text-center py-5" v-else>
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Cargando...</span>
            </div>
            <p class="mt-2 text-muted">Cargando hábito...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import HTTP from '@/common/http.js'

export default {
  name: 'HabitForm',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const saving = ref(false)
    const loading = ref(false)
    const categories = ref([])
    const isEditMode = computed(() => !!route.params.id)

    const today = computed(() => {
      return new Date().toISOString().split('T')[0]
    })

    const form = ref({
      name: '',
      description: '',
      frequency: '',
      categoryId: '',
      startDate: today.value,
      endDate: '',
      habitTime: '',
      location: ''
    })

    const loadHabit = async () => {
      if (!isEditMode.value) return

      loading.value = true
      try {
        const response = await HTTP.get(`/habits/${route.params.id}`)
        const habit = response.data

        form.value = {
          name: habit.name,
          description: habit.description || '',
          frequency: habit.frequency,
          categoryId: habit.categoryId || '',
          startDate: habit.startDate,
          endDate: habit.endDate,
          habitTime: habit.habitTime || '',
          location: habit.location
        }
      } catch (error) {
        console.error('Error cargando hábito:', error)
        alert('Error al cargar el hábito')
        router.push('/habits')
      } finally {
        loading.value = false
      }
    }

    const saveHabit = async () => {
      saving.value = true
      try {
        const habitData = {
          name: form.value.name,
          description: form.value.description,
          frequency: form.value.frequency,
          categoryId: form.value.categoryId ? parseInt(form.value.categoryId) : null,
          startDate: form.value.startDate,
          endDate: form.value.endDate,
          habitTime: form.value.habitTime || null,
          location: form.value.location
        }

        console.log('Enviando hábito:', habitData)
        console.log('Headers que se enviarán:')
        console.log('- Authorization:', localStorage.getItem('token') ? 'presente' : 'ausente')
        console.log('- X-User-Id:', localStorage.getItem('userId'))

        if (isEditMode.value) {
          // Actualizar hábito existente
          await HTTP.put(`/habits/${route.params.id}`, habitData)
          console.log('Hábito actualizado exitosamente')
          alert('Hábito actualizado correctamente')
        } else {
          // Crear nuevo hábito
          const response = await HTTP.post('/habits', habitData)
          console.log('Hábito creado exitosamente:', response.data)
          alert('Hábito creado correctamente')
        }

        // Redirigir a la lista de hábitos forzando recarga
        await router.push('/habits')
        // Forzar recarga de la página para actualizar la lista
        window.location.reload()
      } catch (error) {
        console.error('Error completo:', error)
        console.error('Respuesta del servidor:', error.response?.data)
        console.error('Estado:', error.response?.status)
        console.error('Headers de la respuesta:', error.response?.headers)

        let errorMsg = 'Error desconocido'
        if (error.response?.data?.message) {
          errorMsg = error.response.data.message
        } else if (error.response?.data) {
          errorMsg = JSON.stringify(error.response.data, null, 2)
        } else if (error.message) {
          errorMsg = error.message
        }

        alert(`Error al ${isEditMode.value ? 'actualizar' : 'crear'} el hábito:\n\n${errorMsg}\n\nRevisa la consola para más detalles.`)
      } finally {
        saving.value = false
      }
    }

    const goBack = () => {
      router.push('/habits')
    }
const loadCategories = async () => {
      try {
        const response = await HTTP.get('/habits/categories')
        categories.value = response.data
      } catch (error) {
        console.error('Error cargando categorías:', error)
      }
    }

    onMounted(() => {
      loadCategories()
      loadHabit()
    })

    return {
      form,
      today,
      saving,
      loading,
      categories,
      isEditMode,
      saveHabit,
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

.card-header {
  border-radius: 15px 15px 0 0 !important;
}

.btn-check:checked + .btn-outline-primary {
  background-color: #0d6efd;
  color: white;
}

.btn-check:checked + .btn-outline-success {
  background-color: #198754;
  color: white;
}

.form-label {
  color: #495057;
}

.fw-bold {
  font-weight: 600;
}
</style>

