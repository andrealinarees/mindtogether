<template>
  <div class="container py-4">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Cargando entrada...</p>
    </div>

    <div v-else-if="entry" class="row justify-content-center">
      <div class="col-lg-8">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <button class="btn btn-outline-secondary" @click="goBack">
            <i class="bi bi-arrow-left me-1"></i>Volver
          </button>
          <div class="d-flex gap-2">
            <router-link :to="`/journal/${entry.id}/edit`" class="btn btn-warning text-white">
              <i class="bi bi-pencil me-1"></i>Editar
            </router-link>
            <button class="btn btn-danger" @click="confirmDelete" :disabled="deleting">
              <span v-if="deleting" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-trash me-1"></i>Eliminar
            </button>
          </div>
        </div>

        <!-- Entry card -->
        <div class="card shadow-sm">
          <!-- Mood header -->
          <div class="card-header text-center py-4" :style="{ backgroundColor: moodData.color + '20' }">
            <div style="font-size: 4rem;">{{ moodData.emoji }}</div>
            <h5 class="mt-2 mb-0" :style="{ color: moodData.color }">Me sentí {{ moodData.label.toLowerCase() }}</h5>
          </div>

          <div class="card-body p-4">
            <!-- Fecha -->
            <div class="text-center mb-4">
              <span class="badge bg-primary fs-6 px-3 py-2">
                <i class="bi bi-calendar-event me-1"></i>{{ formattedDate }}
              </span>
            </div>

            <!-- Título -->
            <h3 class="mb-4">{{ entry.title }}</h3>

            <!-- Contenido -->
            <div class="entry-content mb-4" v-if="entry.content">
              <p style="white-space: pre-wrap; line-height: 1.8; font-size: 1.05rem;">{{ entry.content }}</p>
            </div>
            <div v-else class="text-muted text-center py-3">
              <i class="bi bi-journal me-2"></i>Sin descripción adicional
            </div>

            <hr>

            <!-- Meta info -->
            <div class="d-flex justify-content-between text-muted small">
              <span><i class="bi bi-clock me-1"></i>Creada: {{ formatDateTime(entry.createdAt) }}</span>
              <span v-if="entry.updatedAt"><i class="bi bi-pencil me-1"></i>Editada: {{ formatDateTime(entry.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>No se encontró la entrada.
      <button class="btn btn-sm btn-outline-danger ms-3" @click="goBack">Volver</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import JournalRepository from '@/repositories/JournalRepository'
import { notify, confirm } from '@/common/notifications'

export default {
  name: 'JournalEntryDetail',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(true)
    const deleting = ref(false)
    const entry = ref(null)

    const moodData = computed(() => {
      return entry.value ? JournalRepository.getMoodByValue(entry.value.mood) : { emoji: '😐', label: 'Regular', color: '#FF9800' }
    })

    const formattedDate = computed(() => {
      if (!entry.value?.date) return ''
      const [y, m, d] = entry.value.date.split('-')
      return new Date(y, m - 1, d).toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
    })

    const loadEntry = async () => {
      loading.value = true
      try {
        entry.value = await JournalRepository.findById(route.params.id)
      } catch (error) {
        console.error('Error:', error)
      } finally {
        loading.value = false
      }
    }

    const confirmDelete = async () => {
      const confirmed = await confirm('¿Eliminar esta entrada del diario?\n\nEsta acción no se puede deshacer.', { danger: true })
      if (confirmed) {
        deleteEntry()
      }
    }

    const deleteEntry = async () => {
      deleting.value = true
      try {
        await JournalRepository.delete(entry.value.id)
        notify.success('Entrada eliminada correctamente')
        router.push('/journal')
      } catch (error) {
        console.error('Error:', error)
        notify.error('Error al eliminar la entrada')
      } finally {
        deleting.value = false
      }
    }

    const formatDateTime = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('es-ES', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })
    }

    const goBack = () => router.push('/journal')

    onMounted(() => loadEntry())

    return { loading, deleting, entry, moodData, formattedDate, confirmDelete, formatDateTime, goBack }
  }
}
</script>

<style scoped>
.entry-content {
  background-color: #fafafa;
  border-left: 4px solid #0d6efd;
  padding: 1.5rem;
  border-radius: 0 8px 8px 0;
}
</style>
