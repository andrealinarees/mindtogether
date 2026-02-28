<template>
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">
              <i class="bi bi-journal-text me-2"></i>
              {{ isEdit ? 'Editar Entrada' : 'Nueva Entrada del Diario' }}
            </h3>
          </div>
          <div class="card-body" v-if="!loadingEntry">
            <form @submit.prevent="saveEntry">
              <!-- Fecha -->
              <div class="mb-4">
                <label for="date" class="form-label fw-bold">
                  <i class="bi bi-calendar-event me-1"></i>Fecha *
                </label>
                <input type="date" class="form-control" id="date" v-model="form.date" required :max="today">
                <div class="form-text">{{ formattedDate }}</div>
              </div>

              <!-- Estado de ánimo -->
              <div class="mb-4">
                <label class="form-label fw-bold">
                  <i class="bi bi-emoji-smile me-1"></i>¿Cómo te sientes? *
                </label>
                <div class="d-flex justify-content-center gap-4 mt-2">
                  <div v-for="mood in moodOptions" :key="mood.value"
                    class="mood-option text-center p-3 rounded-3"
                    :class="{ 'mood-selected': form.mood === mood.value }"
                    :style="form.mood === mood.value ? { borderColor: mood.color, backgroundColor: mood.color + '20' } : {}"
                    @click="form.mood = mood.value"
                    role="button"
                  >
                    <div style="font-size: 3rem;">{{ mood.emoji }}</div>
                    <div class="fw-bold mt-1" :style="{ color: mood.color }">{{ mood.label }}</div>
                  </div>
                </div>
              </div>

              <!-- Título -->
              <div class="mb-3">
                <label for="title" class="form-label fw-bold">
                  <i class="bi bi-type-h1 me-1"></i>Título *
                </label>
                <input type="text" class="form-control" id="title" v-model="form.title" required
                  placeholder="Ej: Un día complicado, Hoy me sentí bien..." maxlength="200">
                <small class="text-muted">{{ form.title.length }}/200 caracteres</small>
              </div>

              <!-- Contenido -->
              <div class="mb-3">
                <label for="content" class="form-label fw-bold">
                  <i class="bi bi-pencil me-1"></i>¿Qué pasó hoy?
                </label>
                <textarea class="form-control" id="content" v-model="form.content" rows="6"
                  placeholder="Escribe lo que sientes, lo que pasó, tus pensamientos..." maxlength="5000"></textarea>
                <small class="text-muted">{{ form.content.length }}/5000 caracteres</small>
              </div>

              <!-- Botones -->
              <div class="d-flex justify-content-between mt-4">
                <button type="button" class="btn btn-secondary" @click="goBack">
                  <i class="bi bi-arrow-left me-1"></i>Cancelar
                </button>
                <button type="submit" class="btn btn-primary" :disabled="saving || !isFormValid">
                  <span v-if="saving"><span class="spinner-border spinner-border-sm me-1"></span>Guardando...</span>
                  <span v-else><i class="bi bi-check-circle me-1"></i>{{ isEdit ? 'Actualizar' : 'Guardar Entrada' }}</span>
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
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import JournalRepository from '@/repositories/JournalRepository'

export default {
  name: 'JournalEntryForm',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const saving = ref(false)
    const loadingEntry = ref(false)
    const isEdit = computed(() => !!route.params.id)
    const moodOptions = JournalRepository.getMoodOptions()

    const today = computed(() => new Date().toISOString().split('T')[0])

    const form = ref({
      date: today.value,
      mood: '',
      title: '',
      content: ''
    })

    const isFormValid = computed(() => form.value.date && form.value.mood && form.value.title.trim())

    const formattedDate = computed(() => {
      if (!form.value.date) return ''
      const [y, m, d] = form.value.date.split('-')
      return new Date(y, m - 1, d).toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
    })

    const loadEntry = async () => {
      if (!isEdit.value) return
      loadingEntry.value = true
      try {
        const entry = await JournalRepository.findById(route.params.id)
        form.value = {
          date: entry.date,
          mood: entry.mood,
          title: entry.title,
          content: entry.content || ''
        }
      } catch (error) {
        console.error('Error cargando entrada:', error)
        alert('Error al cargar la entrada')
        router.push('/journal')
      } finally {
        loadingEntry.value = false
      }
    }

    const saveEntry = async () => {
      if (!isFormValid.value) return
      saving.value = true
      try {
        if (isEdit.value) {
          await JournalRepository.update(route.params.id, form.value)
        } else {
          await JournalRepository.create(form.value)
        }
        router.push('/journal')
      } catch (error) {
        console.error('Error guardando:', error)
        alert('Error al guardar la entrada')
      } finally {
        saving.value = false
      }
    }

    const goBack = () => router.push('/journal')

    onMounted(() => loadEntry())

    return { form, today, saving, loadingEntry, isEdit, moodOptions, isFormValid, formattedDate, saveEntry, goBack }
  }
}
</script>

<style scoped>
.mood-option {
  border: 3px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 100px;
}
.mood-option:hover {
  transform: scale(1.05);
  border-color: #dee2e6;
}
.mood-selected {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
