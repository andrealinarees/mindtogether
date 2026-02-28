<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-compass"></i> Descubrir Círculos de Apoyo</h1>
      <router-link to="/support-circles" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i>Volver
      </router-link>
    </div>

    <!-- Buscador -->
    <div class="row mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <span class="input-group-text"><i class="bi bi-search"></i></span>
          <input type="text" class="form-control" placeholder="Buscar círculos por nombre o descripción..." v-model="searchQuery" @input="handleSearch">
        </div>
      </div>
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

    <!-- Lista de círculos disponibles -->
    <div v-else-if="availableCircles.length > 0" class="row g-3">
      <div v-for="circle in availableCircles" :key="circle.id" class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm">
          <div class="card-body">
            <h5 class="card-title">
              <i class="bi bi-heart-fill me-2 text-danger"></i>{{ circle.name }}
            </h5>
            <p class="card-text text-muted small mb-2" v-if="circle.creationReason">
              <strong>Motivo:</strong> {{ circle.creationReason }}
            </p>
            <p class="card-text">{{ truncateText(circle.description, 120) }}</p>
            <div class="d-flex justify-content-between align-items-center mt-3">
              <small class="text-muted">
                <i class="bi bi-person me-1"></i>{{ circle.memberCount || 0 }} miembros
              </small>
              <small class="text-muted">
                <i class="bi bi-chat-dots me-1"></i>{{ circle.entryCount || 0 }} publicaciones
              </small>
            </div>
          </div>
          <div class="card-footer bg-transparent border-0">
            <div class="d-flex gap-2">
              <router-link :to="`/support-circles/${circle.id}`" class="btn btn-outline-secondary btn-sm flex-fill">
                <i class="bi bi-eye me-1"></i>Ver detalle
              </router-link>
              <button v-if="!circle.isMember" class="btn btn-primary btn-sm flex-fill" @click="joinCircle(circle)" :disabled="circle.joining">
                <span v-if="circle.joining" class="spinner-border spinner-border-sm me-1"></span>
                <i v-else class="bi bi-box-arrow-in-right me-1"></i>
                {{ circle.joining ? 'Uniéndose...' : 'Unirse' }}
              </button>
              <span v-else class="badge bg-success align-self-center px-3 py-2">
                <i class="bi bi-check-circle me-1"></i>Ya eres miembro
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sin resultados -->
    <div v-else class="card shadow-sm">
      <div class="card-body text-center py-5">
        <i class="bi bi-search display-1 text-muted"></i>
        <h3 class="mt-4 text-muted">No se encontraron círculos</h3>
        <p class="text-muted">Intenta buscar con otros términos o crea tu propio círculo</p>
        <router-link to="/support-circles/new" class="btn btn-primary mt-2">
          <i class="bi bi-plus-circle me-1"></i>Crear Círculo
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CommunityRepository from '@/repositories/CommunityRepository'

const searchQuery = ref('')
const loading = ref(false)
const error = ref('')
const allCircles = ref([])
const availableCircles = ref([])

let searchTimeout = null

const truncateText = (text, max) => {
  if (!text || text.length <= max) return text
  return text.substring(0, max) + '...'
}

const loadAllCircles = async () => {
  loading.value = true
  error.value = ''
  try {
    const data = await CommunityRepository.findAll()
    allCircles.value = data.map(c => ({ ...c, joining: false }))
    availableCircles.value = [...allCircles.value]
  } catch (err) {
    console.error('Error loading circles:', err)
    error.value = 'Error al cargar los círculos'
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    if (!searchQuery.value.trim()) {
      availableCircles.value = [...allCircles.value]
      return
    }
    const q = searchQuery.value.toLowerCase()
    availableCircles.value = allCircles.value.filter(c =>
      c.name.toLowerCase().includes(q) ||
      (c.creationReason && c.creationReason.toLowerCase().includes(q)) ||
      (c.description && c.description.toLowerCase().includes(q))
    )
  }, 300)
}

const joinCircle = async (circle) => {
  circle.joining = true
  try {
    await CommunityRepository.join(circle.id)
    circle.isMember = true
    circle.memberCount = (circle.memberCount || 0) + 1
  } catch (err) {
    console.error('Error joining circle:', err)
    if (err.response?.status === 409) {
      circle.isMember = true
    } else {
      alert('Error al unirse al círculo')
    }
  } finally {
    circle.joining = false
  }
}

onMounted(() => loadAllCircles())
</script>
