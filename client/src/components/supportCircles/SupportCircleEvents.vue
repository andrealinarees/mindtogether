<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-calendar-event me-2"></i>Actividades del Círculo</h1>
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

    <!-- Entries como actividades -->
    <div v-else-if="entries.length > 0">
      <div v-for="entry in entries" :key="entry.id" class="card shadow-sm mb-3">
        <div class="card-body">
          <div class="d-flex align-items-center mb-2">
            <span class="badge me-2" :class="getEntryTypeBadge(entry.type)">{{ getEntryTypeLabel(entry.type) }}</span>
            <small class="text-muted">
              <i class="bi bi-person me-1"></i>Anónimo 🎭 ·
              <i class="bi bi-clock me-1"></i>{{ formatDateTime(entry.createdAt) }}
            </small>
          </div>
          <p class="mb-0" style="white-space: pre-wrap;">{{ entry.content }}</p>
        </div>
      </div>
    </div>

    <!-- Sin actividades -->
    <div v-else class="card shadow-sm">
      <div class="card-body text-center py-5">
        <i class="bi bi-calendar-x display-1 text-muted"></i>
        <h3 class="mt-4 text-muted">No hay actividades recientes</h3>
        <p class="text-muted">Las publicaciones del círculo aparecerán aquí como actividades.</p>
        <router-link :to="`/support-circles/${$route.params.id}`" class="btn btn-primary mt-2">
          <i class="bi bi-chat-dots me-1"></i>Ir a publicar
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
const entries = ref([])
const loading = ref(false)
const error = ref('')

const loadEntries = async () => {
  loading.value = true
  error.value = ''
  try {
    entries.value = await CommunityRepository.getEntries(route.params.id)
  } catch (err) {
    console.error('Error loading entries:', err)
    error.value = 'Error al cargar las actividades'
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('es-ES', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const getEntryTypeBadge = (type) => {
  const badges = {
    REFLECTION: 'bg-info', TIP: 'bg-warning text-dark', MOTIVATION: 'bg-success',
    PROPOSAL: 'bg-primary', QUESTION: 'bg-secondary', ACHIEVEMENT: 'bg-danger'
  }
  return badges[type] || 'bg-secondary'
}

const getEntryTypeLabel = (type) => {
  const labels = {
    REFLECTION: '💭 Reflexión', TIP: '💡 Consejo', MOTIVATION: '🎯 Motivación',
    PROPOSAL: '📝 Propuesta', QUESTION: '❓ Pregunta', ACHIEVEMENT: '🏆 Logro'
  }
  return labels[type] || type
}

onMounted(() => loadEntries())
</script>
