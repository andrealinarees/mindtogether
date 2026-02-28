<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-people me-2"></i>Miembros del Círculo</h1>
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

    <!-- Members list -->
    <div v-else-if="members.length > 0" class="row g-3">
      <div v-for="member in members" :key="member.id" class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm">
          <div class="card-body d-flex align-items-center">
            <i class="bi bi-person-circle fs-1 text-secondary me-3"></i>
            <div>
              <h5 class="mb-1">
                <template v-if="member.userId === currentUserId">
                  {{ isAnonymous ? 'Anónimo 🎭 (Tú)' : getUserDisplay(member.userId) + ' (Tú)' }}
                </template>
                <template v-else>
                  {{ isAnonymous ? 'Anónimo 🎭' : getUserDisplay(member.userId) }}
                </template>
              </h5>
              <span class="badge" :class="getRoleBadgeClass(member.role)">
                {{ getRoleLabel(member.role) }}
              </span>
              <br>
              <small class="text-muted" v-if="member.joinedAt">
                Desde {{ formatDate(member.joinedAt) }}
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sin miembros -->
    <div v-else class="alert alert-info">
      <i class="bi bi-info-circle me-2"></i>No hay miembros en este círculo aún.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CommunityRepository from '@/repositories/CommunityRepository'
import UserRepository from '@/repositories/UserRepository'

const route = useRoute()
const members = ref([])
const loading = ref(false)
const error = ref('')
const currentUserId = ref(localStorage.getItem('userId'))
const isAnonymous = ref(false)

const usersCache = ref({})

const loadMembers = async () => {
  loadingMembers.value = true
  try {
    members.value = await CommunityRepository.getMembers(circleId.value)

    // mi estado anónimo viene del servidor
    const me = members.value.find(m => String(m.userId) === String(currentUserId.value))
    if (me) {
      isAnonymous.value = !!me.anonymous
    }
  } catch (err) {
    console.error('Error loading members:', err)
  } finally {
    loadingMembers.value = false
  }
}

const getRoleBadgeClass = (role) => {
  const classes = { ADMIN: 'bg-danger', MODERATOR: 'bg-warning text-dark', MEMBER: 'bg-primary' }
  return classes[role] || 'bg-secondary'
}

const getUserDisplay = (userId) => {
  const uid = String(userId)
  const user = usersCache.value[userId] || usersCache.value[uid]
  return user ? (user.name || user.login || `Usuario ${userId}`) : `Usuario ${userId}`
}

const loadUserInfo = async (userId) => {
  if (!userId) return
  const sid = String(userId)
  if (usersCache.value[userId] || usersCache.value[sid]) return
  try {
    const user = await UserRepository.findOne(userId)
    usersCache.value[userId] = user
    usersCache.value[sid] = user
  } catch (err) {
    console.error('Error fetching user info', err)
  }
}

const getRoleLabel = (role) => {
  const labels = { ADMIN: 'Administrador', MODERATOR: 'Moderador', MEMBER: 'Miembro' }
  return labels[role] || role
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('es-ES', { year: 'numeric', month: 'short', day: 'numeric' })
}

onMounted(() => loadMembers())
</script>
