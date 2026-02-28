<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>
        <i class="bi bi-heart me-2"></i>{{ isEdit ? 'Editar' : 'Crear' }} Círculo de Apoyo
      </h1>
    </div>

    <!-- Loading (solo en edición) -->
    <div v-if="loadingCircle" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else class="row">
      <!-- Formulario principal -->
      <div class="col-lg-8">
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <form @submit.prevent="handleSubmit">
              <div class="mb-3">
                <label for="name" class="form-label">Nombre del Círculo <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="name"
                  v-model="form.name"
                  placeholder="Ej: Manejo de la Ansiedad"
                  maxlength="100"
                  required
                >
                <small class="text-muted">{{ form.name.length }}/100 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="creationReason" class="form-label">Motivo de Creación <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="creationReason"
                  v-model="form.creationReason"
                  placeholder="Ej: Apoyarnos mutuamente para gestionar la ansiedad en el día a día"
                  maxlength="500"
                  required
                >
                <small class="text-muted">{{ form.creationReason.length }}/500 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="description" class="form-label">Descripción</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="form.description"
                  rows="4"
                  placeholder="Describe el propósito del círculo, las normas de convivencia, a quién va dirigido..."
                  maxlength="2000"
                ></textarea>
                <small class="text-muted">{{ form.description.length }}/2000 caracteres</small>
              </div>

              <div v-if="error" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
              </div>

              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary" :disabled="!isFormValid || saving">
                  <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-check-circle me-1"></i>
                  {{ saving ? 'Guardando...' : (isEdit ? 'Guardar Cambios' : 'Crear Círculo') }}
                </button>
                <router-link to="/support-circles" class="btn btn-secondary">Cancelar</router-link>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Panel lateral con categorías y ejemplos -->
      <div v-if="!isEdit" class="col-lg-4">
        <!-- Categorías -->
        <div class="card shadow-sm mb-3">
          <div class="card-header bg-primary text-white">
            <h6 class="mb-0"><i class="bi bi-tags me-2"></i>Categorías sugeridas</h6>
          </div>
          <div class="card-body p-2">
            <div class="d-flex flex-wrap gap-1">
              <span
                v-for="cat in categories"
                :key="cat.value"
                class="badge rounded-pill px-2 py-1"
                style="cursor: pointer; font-size: 0.8rem;"
                :style="{ backgroundColor: cat.color, color: '#fff' }"
                @click="applyCategoryToName(cat)"
                :title="cat.description"
              >
                {{ cat.icon }} {{ cat.label }}
              </span>
            </div>
            <small class="text-muted d-block mt-2">
              <i class="bi bi-info-circle me-1"></i>Haz clic en una categoría para usarla como nombre
            </small>
          </div>
        </div>

        <!-- Ejemplos -->
        <div class="card shadow-sm">
          <div class="card-header bg-success text-white">
            <h6 class="mb-0"><i class="bi bi-lightbulb me-2"></i>Ideas para tu círculo</h6>
          </div>
          <div class="list-group list-group-flush">
            <a
              v-for="(example, i) in examples"
              :key="i"
              href="#"
              class="list-group-item list-group-item-action py-2"
              @click.prevent="applyExample(example)"
            >
              <strong class="d-block">{{ example.name }}</strong>
              <small class="text-muted">{{ example.reason }}</small>
            </a>
          </div>
          <div class="card-footer">
            <small class="text-muted">
              <i class="bi bi-info-circle me-1"></i>Haz clic en un ejemplo para rellenar el formulario
            </small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CommunityRepository from '@/repositories/CommunityRepository'
import SupportCircleRepository from '@/repositories/SupportCircleRepository'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const loadingCircle = ref(false)
const saving = ref(false)
const error = ref('')

const categories = SupportCircleRepository.getCircleTypes()
const examples = SupportCircleRepository.getExamples()

const form = ref({
  name: '',
  creationReason: '',
  description: ''
})

const isFormValid = computed(() => {
  return form.value.name.trim().length > 0 && form.value.creationReason.trim().length > 0
})

const applyCategoryToName = (cat) => {
  form.value.name = cat.label
  if (!form.value.creationReason) {
    form.value.creationReason = cat.description
  }
}

const applyExample = (example) => {
  form.value.name = example.name
  form.value.creationReason = example.reason
}

const loadCircle = async () => {
  if (!isEdit.value) return
  loadingCircle.value = true
  try {
    const circle = await CommunityRepository.findById(route.params.id)
    form.value = {
      name: circle.name || '',
      creationReason: circle.creationReason || '',
      description: circle.description || ''
    }
  } catch (err) {
    console.error('Error loading circle:', err)
    error.value = 'Error al cargar el círculo'
  } finally {
    loadingCircle.value = false
  }
}

const handleSubmit = async () => {
  if (!isFormValid.value) return
  saving.value = true
  error.value = ''

  try {
    if (isEdit.value) {
      await CommunityRepository.update(route.params.id, form.value)
    } else {
      await CommunityRepository.create(form.value)
    }
    router.push('/support-circles')
  } catch (err) {
    console.error('Error saving circle:', err)
    if (err.response?.status === 409) {
      error.value = 'Ya existe un círculo con ese nombre'
    } else if (err.response?.status === 403) {
      error.value = 'No tienes permisos para editar este círculo'
    } else {
      error.value = 'Error al guardar el círculo'
    }
  } finally {
    saving.value = false
  }
}

onMounted(() => loadCircle())
</script>
