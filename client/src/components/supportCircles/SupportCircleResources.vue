<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-book me-2"></i>Recursos del Círculo</h1>
      <router-link :to="`/support-circles/${$route.params.id}`" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i>Volver al Círculo
      </router-link>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <!-- Entries como recursos (filtrados por tipo TIP) -->
    <div v-else-if="resources.length > 0">
      <div v-for="entry in resources" :key="entry.id" class="card shadow-sm mb-3">
        <div class="card-body">
          <div class="d-flex align-items-center mb-2">
            <span class="badge bg-warning text-dark me-2">💡 Consejo / Recurso</span>
            <small class="text-muted">
              <i class="bi bi-person me-1"></i>Anónimo 🎭 ·
              <i class="bi bi-clock me-1"></i>{{ formatDateTime(entry.createdAt) }}
            </small>
          </div>
          <p class="mb-0" style="white-space: pre-wrap;">{{ entry.content }}</p>
        </div>
      </div>
    </div>

    <!-- Sin recursos -->
    <div v-else class="card shadow-sm">
      <div class="card-body text-center py-5">
        <i class="bi bi-journal-x display-1 text-muted"></i>
        <h3 class="mt-4 text-muted">No hay recursos compartidos</h3>
        <p class="text-muted">Los consejos y recursos compartidos en el círculo aparecerán aquí.</p>
        <router-link :to="`/support-circles/${$route.params.id}`" class="btn btn-primary mt-2">
          <i class="bi bi-chat-dots me-1"></i>Ir al círculo
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CommunityRepository from '@/repositories/CommunityRepository'

const route = useRoute()
const resources = ref([])
const loading = ref(false)
const error = ref('')

const loadResources = async () => {
  loading.value = true
  error.value = ''
  try {
    const entries = await CommunityRepository.getEntries(route.params.id)
    // Filtrar solo tips y propuestas como "recursos"
    resources.value = entries.filter(e => e.type === 'TIP' || e.type === 'PROPOSAL')
  } catch (err) {
    console.error('Error loading resources:', err)
    error.value = 'Error al cargar los recursos'
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('es-ES', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

onMounted(() => loadResources())
</script>
