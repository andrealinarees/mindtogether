<template>
  <div class="container py-4">
    <h1>{{ isEdit ? 'Editar' : 'Nueva' }} Entrada de Diario</h1>
    
    <form @submit.prevent="handleSubmit" class="mt-4">
      <div class="mb-3">
        <label class="form-label">¿Cómo te sientes hoy?</label>
        <select class="form-select" v-model="form.mood">
          <option value="">Selecciona tu estado de ánimo</option>
          <option value="very_happy">😄 Muy feliz</option>
          <option value="happy">🙂 Feliz</option>
          <option value="neutral">😐 Neutral</option>
          <option value="sad">😔 Triste</option>
          <option value="very_sad">😢 Muy triste</option>
        </select>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Escribe tus pensamientos</label>
        <textarea class="form-control" v-model="form.content" rows="8" 
                  placeholder="Comparte lo que sientes..."></textarea>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Etiquetas</label>
        <input type="text" class="form-control" v-model="form.tags" 
               placeholder="Ej: ansiedad, trabajo, familia">
      </div>
      
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Guardar</button>
        <router-link to="/journal" class="btn btn-secondary">Cancelar</router-link>
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
