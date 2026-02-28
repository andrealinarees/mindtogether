<template>
  <div class="container py-4" style="max-width: 650px;">
    <h2 class="text-center mb-4">
      <i class="bi bi-pencil-square me-2"></i>{{ isEdit ? 'Editar entrada' : 'Escribir en el diario' }}
    </h2>

    <!-- Selector de fecha con flechas -->
    <div class="card shadow-sm mb-4">
      <div class="card-body d-flex justify-content-between align-items-center py-3">
        <button class="btn btn-outline-primary btn-sm" @click="changeDay(-1)">
          <i class="bi bi-chevron-left"></i>
        </button>
        <div class="text-center">
          <h5 class="mb-0 text-capitalize">{{ formattedDate }}</h5>
          <router-link to="/journal/calendar" class="text-muted small">
            <i class="bi bi-calendar3 me-1"></i>Ir al calendario
          </router-link>
        </div>
        <button class="btn btn-outline-primary btn-sm" @click="changeDay(1)">
          <i class="bi bi-chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- Estado de ánimo -->
    <div class="card shadow-sm mb-4">
      <div class="card-body text-center">
        <label class="form-label fw-bold mb-3">¿Cómo te sientes?</label>
        <div class="d-flex justify-content-center gap-3">
          <div
            v-for="mood in moodOptions"
            :key="mood.value"
            class="mood-option rounded-circle d-flex align-items-center justify-content-center"
            :class="{ 'mood-selected': form.mood === mood.value }"
            :style="form.mood === mood.value ? { borderColor: mood.color } : {}"
            @click="form.mood = mood.value"
            :title="mood.label"
          >
            <span style="font-size: 2.2rem;">{{ mood.emoji }}</span>
          </div>
        </div>
        <small v-if="form.mood" class="text-muted mt-2 d-block">
          {{ moodOptions.find(m => m.value === form.mood)?.label }}
        </small>
      </div>
    </div>

    <!-- Título y descripción -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label fw-bold">Título</label>
          <input
            type="text"
            class="form-control"
            v-model="form.title"
            placeholder="Ej: Hoy me siento fatal, Buen día en el trabajo..."
          />
        </div>
        <div>
          <label class="form-label fw-bold">¿Qué quieres contar?</label>
          <textarea
            class="form-control"
            v-model="form.content"
            rows="5"
            placeholder="Escribe lo que sientes... Ej: Me encuentro mal, no sé si dormir todo el día..."
          ></textarea>
        </div>
      </div>
    </div>

    <!-- Botones -->
    <div class="d-flex gap-2 justify-content-center">
      <button
        class="btn btn-primary btn-lg px-4"
        :disabled="!isFormValid || saving"
        @click="saveEntry"
      >
        <i class="bi bi-check-circle me-1"></i>
        {{ saving ? 'Guardando...' : 'Guardar' }}
      </button>
      <button class="btn btn-outline-secondary btn-lg" @click="goBack">
        Cancelar
      </button>
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
    const isEdit = computed(() => !!route.params.id)
    const moodOptions = JournalRepository.getMoodOptions()

    const toDateStr = (d) =>
      `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

    const form = ref({
      date: toDateStr(new Date()),
      mood: route.query.mood || '',
      title: '',
      content: ''
    })

    const isFormValid = computed(() => form.value.mood && form.value.title.trim())

    const formattedDate = computed(() => {
      if (!form.value.date) return ''
      const [y, m, d] = form.value.date.split('-')
      return new Date(y, m - 1, d).toLocaleDateString('es-ES', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      })
    })

    const changeDay = (offset) => {
      const [y, m, d] = form.value.date.split('-').map(Number)
      const current = new Date(y, m - 1, d)
      current.setDate(current.getDate() + offset)
      form.value.date = toDateStr(current)
    }

    const loadEntry = async () => {
      if (!isEdit.value) return
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
        router.push('/journal')
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

    return { form, saving, isEdit, moodOptions, isFormValid, formattedDate, changeDay, saveEntry, goBack }
  }
}
</script>

<style scoped>
.mood-option {
  width: 60px;
  height: 60px;
  border: 3px solid #dee2e6;
  cursor: pointer;
  transition: all 0.2s;
}
.mood-option:hover {
  transform: scale(1.1);
  border-color: #adb5bd;
}
.mood-selected {
  transform: scale(1.15);
  border-width: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
