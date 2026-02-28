<template>
  <div class="container mt-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">
              <i :class="['bi', 'me-2', isEditMode ? 'bi-pencil-square' : 'bi-heart-pulse']"></i>
              {{ isEditMode ? 'Editar Práctica' : 'Nueva Práctica de Bienestar' }}
            </h3>
          </div>
          <div class="card-body" v-if="!loadingPractice">
            <form @submit.prevent="savePractice">
              <!-- Nombre -->
              <div class="mb-3">
                <label for="name" class="form-label fw-bold">
                  <i class="bi bi-tag me-1"></i>Nombre de la Práctica *
                </label>
                <input type="text" class="form-control" id="name" v-model="form.name" required
                  placeholder="Ej: Meditación matutina, Respiración 4-7-8...">
              </div>

              <!-- Descripción -->
              <div class="mb-3">
                <label for="description" class="form-label fw-bold">
                  <i class="bi bi-card-text me-1"></i>Descripción
                </label>
                <textarea class="form-control" id="description" v-model="form.description" rows="3"
                  placeholder="Describe la práctica, beneficios y cómo realizarla..."></textarea>
              </div>

              <!-- Frecuencia -->
              <div class="mb-3">
                <label for="frequency" class="form-label fw-bold">
                  <i class="bi bi-calendar-check me-1"></i>Frecuencia *
                </label>
                <select class="form-select" id="frequency" v-model="form.frequency" required>
                  <option value="">Selecciona una frecuencia</option>
                  <option value="DAILY">📅 Diaria</option>
                  <option value="WEEKLY">📆 Semanal</option>
                </select>
              </div>

              <!-- Categoría -->
              <div class="mb-3">
                <label for="category" class="form-label fw-bold">
                  <i class="bi bi-tags me-1"></i>Categoría *
                </label>
                <select class="form-select" id="category" v-model="form.categoryId" required>
                  <option value="">Selecciona una categoría</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                </select>
              </div>

              <!-- Fechas -->
              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="startDate" class="form-label fw-bold">
                    <i class="bi bi-calendar-event me-1"></i>Fecha de Inicio *
                  </label>
                  <input type="date" class="form-control" id="startDate" v-model="form.startDate" required :min="today">
                </div>
                <div class="col-md-6">
                  <label for="endDate" class="form-label fw-bold">
                    <i class="bi bi-calendar-x me-1"></i>Fecha de Fin *
                  </label>
                  <input type="date" class="form-control" id="endDate" v-model="form.endDate" required :min="form.startDate || today">
                </div>
              </div>

              <!-- Hora -->
              <div class="mb-3">
                <label for="habitTime" class="form-label fw-bold">
                  <i class="bi bi-clock me-1"></i>Hora preferida (opcional)
                </label>
                <input type="time" class="form-control" id="habitTime" v-model="form.habitTime">
                <div class="form-text">¿A qué hora prefieres hacer esta práctica?</div>
              </div>

              <!-- Ubicación -->
              <div class="mb-3">
                <label class="form-label fw-bold">
                  <i class="bi bi-geo-alt me-1"></i>Ubicación *
                </label>
                <div class="btn-group w-100" role="group">
                  <input type="radio" class="btn-check" name="location" id="interior" value="INTERIOR" v-model="form.location" required>
                  <label class="btn btn-outline-primary" for="interior">
                    <i class="bi bi-house-door me-1"></i>Interior
                  </label>
                  <input type="radio" class="btn-check" name="location" id="exterior" value="EXTERIOR" v-model="form.location" required>
                  <label class="btn btn-outline-success" for="exterior">
                    <i class="bi bi-tree me-1"></i>Exterior
                  </label>
                </div>
              </div>

              <!-- Botones -->
              <div class="d-flex justify-content-between mt-4">
                <button type="button" class="btn btn-secondary" @click="goBack">
                  <i class="bi bi-arrow-left me-1"></i>Cancelar
                </button>
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  <span v-if="saving"><span class="spinner-border spinner-border-sm me-1"></span>Guardando...</span>
                  <span v-else><i class="bi bi-check-circle me-1"></i>{{ isEditMode ? 'Actualizar' : 'Crear Práctica' }}</span>
                </button>
              </div>
            </form>
          </div>
          <div class="card-body text-center py-5" v-else>
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2 text-muted">Cargando...</p>
          </div>
        </div>
      </div>

      <!-- Panel lateral de sugerencias -->
      <div v-if="!isEditMode" class="col-lg-4 mt-4 mt-lg-0">
        <div class="card shadow-sm">
          <div class="card-header bg-success text-white">
            <h6 class="mb-0"><i class="bi bi-lightbulb me-2"></i>Prácticas sugeridas</h6>
          </div>
          <div class="list-group list-group-flush">
            <a v-for="(suggestion, i) in suggestions" :key="i" href="#"
              class="list-group-item list-group-item-action py-2" @click.prevent="applySuggestion(suggestion)">
              <strong class="d-block">{{ suggestion.name }}</strong>
              <small class="text-muted">{{ suggestion.description }}</small>
              <div class="mt-1">
                <span class="badge bg-primary me-1">{{ suggestion.frequency === 'DAILY' ? 'Diario' : 'Semanal' }}</span>
                <span class="badge" :class="suggestion.location === 'EXTERIOR' ? 'bg-success' : 'bg-secondary'">
                  {{ suggestion.location === 'EXTERIOR' ? '🌳 Exterior' : '🏠 Interior' }}
                </span>
              </div>
            </a>
          </div>
          <div class="card-footer">
            <small class="text-muted"><i class="bi bi-info-circle me-1"></i>Haz clic para rellenar el formulario</small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import WellnessPracticeRepository from '@/repositories/WellnessPracticeRepository'
import { notify } from '@/common/notifications'

export default {
  name: 'WellnessPracticeForm',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const saving = ref(false)
    const loadingPractice = ref(false)
    const categories = ref([])
    const isEditMode = computed(() => !!route.params.id)
    const suggestions = WellnessPracticeRepository.getSuggestedPractices()

    const today = computed(() => new Date().toISOString().split('T')[0])

    const form = ref({
      name: '', description: '', frequency: '', categoryId: '',
      startDate: today.value, endDate: '', habitTime: '', location: ''
    })

    const applySuggestion = (suggestion) => {
      form.value.name = suggestion.name
      form.value.description = suggestion.description
      form.value.frequency = suggestion.frequency
      form.value.location = suggestion.location
    }

    const loadPractice = async () => {
      if (!isEditMode.value) return
      loadingPractice.value = true
      try {
        const practice = await WellnessPracticeRepository.findById(route.params.id)
        form.value = {
          name: practice.name, description: practice.description || '',
          frequency: practice.frequency, categoryId: practice.categoryId || '',
          startDate: practice.startDate, endDate: practice.endDate,
          habitTime: practice.habitTime || '', location: practice.location
        }
      } catch (error) {
        console.error('Error cargando práctica:', error)
        notify.error('No se pudo cargar la práctica')
        router.push('/wellness')
      } finally {
        loadingPractice.value = false
      }
    }

    const savePractice = async () => {
      saving.value = true
      try {
        const data = {
          name: form.value.name, description: form.value.description,
          frequency: form.value.frequency,
          categoryId: form.value.categoryId ? parseInt(form.value.categoryId) : null,
          startDate: form.value.startDate, endDate: form.value.endDate,
          habitTime: form.value.habitTime || null, location: form.value.location
        }

        if (isEditMode.value) {
          await WellnessPracticeRepository.update(route.params.id, data)
          notify.success('Práctica actualizada correctamente')
        } else {
          await WellnessPracticeRepository.create(data)
          notify.success('Práctica creada correctamente')
        }
        await router.push('/wellness')
        window.location.reload()
      } catch (error) {
        console.error('Error guardando:', error)
        notify.error(`Error al ${isEditMode.value ? 'actualizar' : 'crear'} la práctica`)
      } finally {
        saving.value = false
      }
    }

    const loadCategories = async () => {
      try {
        categories.value = await WellnessPracticeRepository.getCategories()
      } catch (error) {
        console.error('Error cargando categorías:', error)
      }
    }

    const goBack = () => router.push('/wellness')

    onMounted(() => {
      loadCategories()
      loadPractice()
    })

    return {
      form, today, saving, loadingPractice, categories, isEditMode, suggestions,
      savePractice, goBack, applySuggestion
    }
  }
}
</script>

<style scoped>
.card { border: none; border-radius: 15px; }
.card-header { border-radius: 15px 15px 0 0 !important; }
.btn-check:checked + .btn-outline-primary { background-color: #0d6efd; color: white; }
.btn-check:checked + .btn-outline-success { background-color: #198754; color: white; }
</style>
