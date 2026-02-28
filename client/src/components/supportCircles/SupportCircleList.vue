<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-people-fill"></i> Círculos de Apoyo</h1>
      <router-link to="/support-circles/new" class="btn btn-primary">
        <i class="bi bi-plus-circle me-1"></i> Crear Círculo
      </router-link>
    </div>

    <!-- Tabs -->
    <ul class="nav nav-tabs mb-4">
      <li class="nav-item">
        <a class="nav-link" :class="{ active: activeTab === 'my' }" @click.prevent="activeTab = 'my'; loadMyCircles()" href="#">
          <i class="bi bi-bookmark-heart me-1"></i>Mis Círculos
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" :class="{ active: activeTab === 'all' }" @click.prevent="activeTab = 'all'; loadAllCircles()" href="#">
          <i class="bi bi-globe me-1"></i>Todos los Círculos
        </a>
      </li>
    </ul>

    <!-- Buscador -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="input-group">
          <span class="input-group-text"><i class="bi bi-search"></i></span>
          <input type="text" class="form-control" placeholder="Buscar círculos..." v-model="searchQuery">
        </div>
      </div>
      <div class="col-md-6 text-end">
        <router-link to="/support-circles/discover" class="btn btn-outline-primary">
          <i class="bi bi-compass me-1"></i> Descubrir Círculos
        </router-link>
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

    <!-- Lista de círculos -->
    <div v-else-if="filteredCircles.length > 0" class="row g-3">
      <div v-for="circle in filteredCircles" :key="circle.id" class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm circle-card" @click="viewCircle(circle)">
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
            <!-- No es miembro -->
            <div v-if="!circle.isMember" class="d-flex gap-2">
              <button class="btn btn-outline-secondary btn-sm flex-fill" @click.stop="viewCircle(circle)">
                <i class="bi bi-eye me-1"></i>Ver detalle
              </button>
              <button class="btn btn-primary btn-sm flex-fill" @click.stop="joinCircle(circle.id)">
                <i class="bi bi-box-arrow-in-right me-1"></i>Unirse
              </button>
            </div>
            <!-- Es creador -->
            <div v-else-if="circle.isCreator" class="d-flex gap-2">
              <button class="btn btn-success btn-sm" @click.stop="enterCircle(circle.id)">
                <i class="bi bi-door-open me-1"></i>Entrar
              </button>
              <button class="btn btn-warning btn-sm" @click.stop="$router.push(`/support-circles/${circle.id}/edit`)">
                <i class="bi bi-pencil me-1"></i>Editar
              </button>
              <button class="btn btn-danger btn-sm" @click.stop="confirmDelete(circle)">
                <i class="bi bi-trash me-1"></i>Eliminar
              </button>
            </div>
            <!-- Es miembro -->
            <div v-else class="d-flex gap-2">
              <button class="btn btn-success btn-sm flex-fill" @click.stop="enterCircle(circle.id)">
                <i class="bi bi-door-open me-1"></i>Entrar
              </button>
              <button class="btn btn-outline-danger btn-sm" @click.stop="confirmLeave(circle)">
                <i class="bi bi-box-arrow-right me-1"></i>Salir
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sin círculos -->
    <div v-else class="card shadow-sm">
      <div class="card-body text-center py-5">
        <i class="bi bi-people display-1 text-muted"></i>
        <h3 class="mt-4 text-muted">
          {{ activeTab === 'my' ? 'Aún no te has unido a ningún círculo' : 'No hay círculos disponibles' }}
        </h3>
        <p class="text-muted">
          {{ activeTab === 'my' ? 'Explora círculos de apoyo y únete a los que te interesen' : 'Sé el primero en crear un círculo de apoyo' }}
        </p>
        <router-link v-if="activeTab === 'my'" to="/support-circles/discover" class="btn btn-primary mt-2">
          <i class="bi bi-compass me-1"></i>Descubrir Círculos
        </router-link>
        <router-link v-else to="/support-circles/new" class="btn btn-primary mt-2">
          <i class="bi bi-plus-circle me-1"></i>Crear Círculo
        </router-link>
      </div>
    </div>

    <!-- Modal de confirmación para eliminar -->
    <div v-if="showDeleteModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title"><i class="bi bi-exclamation-triangle me-2"></i>Confirmar Eliminación</h5>
            <button type="button" class="btn-close btn-close-white" @click="showDeleteModal = false"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres eliminar el círculo <strong>{{ circleToAction?.name }}</strong>?</p>
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-circle me-2"></i>Esta acción es permanente y eliminará todos los datos asociados.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteModal = false">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteCircle" :disabled="processing">
              <span v-if="processing" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-trash me-1"></i>{{ processing ? 'Eliminando...' : 'Eliminar' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de confirmación para salir -->
    <div v-if="showLeaveModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-warning">
            <h5 class="modal-title"><i class="bi bi-box-arrow-right me-2"></i>Salir del Círculo</h5>
            <button type="button" class="btn-close" @click="showLeaveModal = false"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres salir del círculo <strong>{{ circleToAction?.name }}</strong>?</p>
            <div class="alert alert-info">
              <i class="bi bi-info-circle me-2"></i>Podrás volver a unirte en cualquier momento.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showLeaveModal = false">Cancelar</button>
            <button type="button" class="btn btn-warning" @click="leaveCircle" :disabled="processing">
              <span v-if="processing" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-box-arrow-right me-1"></i>{{ processing ? 'Saliendo...' : 'Salir' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CommunityRepository from '@/repositories/CommunityRepository'

const router = useRouter()

const activeTab = ref('my')
const searchQuery = ref('')
const loading = ref(false)
const error = ref('')
const processing = ref(false)
const showDeleteModal = ref(false)
const showLeaveModal = ref(false)
const circleToAction = ref(null)

const allCircles = ref([])
const myCircles = ref([])

const currentList = computed(() => activeTab.value === 'my' ? myCircles.value : allCircles.value)

const filteredCircles = computed(() => {
  if (!searchQuery.value.trim()) return currentList.value
  const q = searchQuery.value.toLowerCase()
  return currentList.value.filter(c =>
    c.name.toLowerCase().includes(q) ||
    (c.creationReason && c.creationReason.toLowerCase().includes(q)) ||
    (c.description && c.description.toLowerCase().includes(q))
  )
})

const truncateText = (text, max) => {
  if (!text || text.length <= max) return text
  return text.substring(0, max) + '...'
}

const loadMyCircles = async () => {
  loading.value = true
  error.value = ''
  try {
    const data = await CommunityRepository.findMyCommunities()
    const userId = parseInt(localStorage.getItem('userId'))
    myCircles.value = data.map(c => ({
      ...c,
      isMember: true,
      isCreator: c.isCreator || parseInt(c.creatorUserId) === userId
    }))
  } catch (err) {
    console.error('Error loading my circles:', err)
    error.value = 'Error al cargar tus círculos'
  } finally {
    loading.value = false
  }
}

const loadAllCircles = async () => {
  loading.value = true
  error.value = ''
  try {
    allCircles.value = await CommunityRepository.findAll()
  } catch (err) {
    console.error('Error loading circles:', err)
    error.value = 'Error al cargar los círculos'
  } finally {
    loading.value = false
  }
}

const viewCircle = (circle) => {
  router.push({ name: 'SupportCircleDetail', params: { id: circle.id } })
}

const enterCircle = (id) => {
  router.push({ name: 'SupportCircleDetail', params: { id } })
}

const joinCircle = async (circleId) => {
  try {
    await CommunityRepository.join(circleId)
    await loadMyCircles()
    if (activeTab.value === 'all') await loadAllCircles()
  } catch (err) {
    console.error('Error joining circle:', err)
    if (err.response?.status === 409) {
      alert('Ya eres miembro de este círculo')
    } else {
      alert('Error al unirse al círculo')
    }
  }
}

const confirmDelete = (circle) => {
  circleToAction.value = circle
  showDeleteModal.value = true
}

const deleteCircle = async () => {
  if (!circleToAction.value) return
  processing.value = true
  try {
    await CommunityRepository.delete(circleToAction.value.id)
    myCircles.value = myCircles.value.filter(c => c.id !== circleToAction.value.id)
    allCircles.value = allCircles.value.filter(c => c.id !== circleToAction.value.id)
    showDeleteModal.value = false
  } catch (err) {
    console.error('Error deleting circle:', err)
    alert(err.response?.status === 403 ? 'Solo el creador puede eliminar este círculo' : 'Error al eliminar el círculo')
  } finally {
    processing.value = false
  }
}

const confirmLeave = (circle) => {
  circleToAction.value = circle
  showLeaveModal.value = true
}

const leaveCircle = async () => {
  if (!circleToAction.value) return
  processing.value = true
  try {
    await CommunityRepository.leave(circleToAction.value.id)
    myCircles.value = myCircles.value.filter(c => c.id !== circleToAction.value.id)
    if (activeTab.value === 'all') await loadAllCircles()
    showLeaveModal.value = false
  } catch (err) {
    console.error('Error leaving circle:', err)
    alert(err.response?.status === 403 ? 'El creador no puede abandonar su propio círculo' : 'Error al salir del círculo')
  } finally {
    processing.value = false
  }
}

onMounted(async () => {
  await loadMyCircles()
})
</script>

<style scoped>
.circle-card {
  cursor: pointer;
  border: none;
  border-radius: 15px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.circle-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15) !important;
}
.nav-link {
  cursor: pointer;
  color: #6c757d;
}
.nav-link.active {
  color: #0d6efd;
  font-weight: 500;
}
.card-footer .btn {
  font-size: 0.875rem;
  font-weight: 500;
}
</style>
