<template>
  <div class="container py-4">
    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
      <router-link to="/support-circles" class="btn btn-sm btn-outline-danger ms-3">Volver</router-link>
    </div>

    <!-- Detail -->
    <div v-else-if="circle">
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <router-link to="/support-circles" class="btn btn-outline-secondary">
          <i class="bi bi-arrow-left me-2"></i>Volver
        </router-link>
        <div class="d-flex gap-2">
          <router-link v-if="isCreator" :to="`/support-circles/${circle.id}/edit`" class="btn btn-warning">
            <i class="bi bi-pencil me-1"></i>Editar
          </router-link>
          <button v-if="isMember && !isCreator" @click="confirmLeave" class="btn btn-outline-danger">
            <i class="bi bi-box-arrow-right me-1"></i>Salir
          </button>
          <button v-if="!isMember" @click="joinCircle" class="btn btn-success">
            <i class="bi bi-box-arrow-in-right me-1"></i>Unirse
          </button>
        </div>
      </div>

      <!-- Info card -->
      <div class="card shadow-lg mb-4">
        <div class="card-header bg-primary text-white py-4">
          <h1 class="mb-0">
            <i class="bi bi-heart-fill me-3"></i>{{ circle.name }}
          </h1>
        </div>
        <div class="card-body p-4">
          <div class="mb-4" v-if="circle.creationReason">
            <h5 class="text-primary"><i class="bi bi-lightbulb me-2"></i>Motivo de creación</h5>
            <p class="fs-5 text-muted">{{ circle.creationReason }}</p>
          </div>
          <hr v-if="circle.creationReason">
          <div class="mb-4">
            <h5 class="text-primary"><i class="bi bi-card-text me-2"></i>Descripción</h5>
            <p class="fs-6" style="white-space: pre-wrap;">{{ circle.description || 'Sin descripción disponible' }}</p>
          </div>
          <hr>
          <!-- Stats -->
          <div class="row text-center">
            <div class="col-md-4 mb-3">
              <div class="p-3 bg-light rounded">
                <i class="bi bi-person-fill text-primary fs-1"></i>
                <h3 class="mt-2 mb-0">{{ circle.memberCount || 0 }}</h3>
                <p class="text-muted mb-0">Miembros</p>
              </div>
            </div>
            <div class="col-md-4 mb-3">
              <div class="p-3 bg-light rounded">
                <i class="bi bi-chat-dots-fill text-success fs-1"></i>
                <h3 class="mt-2 mb-0">{{ circle.entryCount || 0 }}</h3>
                <p class="text-muted mb-0">Publicaciones</p>
              </div>
            </div>
            <div class="col-md-4 mb-3">
              <div class="p-3 bg-light rounded">
                <i class="bi bi-calendar-event text-info fs-1"></i>
                <h3 class="mt-2 mb-0">{{ formatDate(circle.createdAt) }}</h3>
                <p class="text-muted mb-0">Fecha de creación</p>
              </div>
            </div>
          </div>
          <hr>
          <!-- Members -->
          <div class="mb-4">
            <h5 class="text-primary mb-3"><i class="bi bi-people me-2"></i>Miembros</h5>
            <div v-if="loadingMembers" class="text-center py-3">
              <div class="spinner-border spinner-border-sm text-primary" role="status"></div>
            </div>
            <div v-else-if="members.length > 0" class="row">
              <div v-for="member in members" :key="member.id" class="col-md-6 col-lg-4 mb-3">
                <div class="card h-100">
                  <div class="card-body d-flex align-items-center">
                    <i class="bi bi-person-circle fs-2 text-secondary me-3"></i>
                    <div>
                     <h6 class="mb-1">
                        {{ member.anonymous ? 'Anónimo 🎭' : (member.username || ('Usuario ' + member.userId)) }}
                        <span v-if="String(member.userId) === String(currentUserId)">(Tú)</span>
                      </h6>
                      <span class="badge" :class="getRoleBadgeClass(member.role)">{{ getRoleLabel(member.role) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="alert alert-info">
              <i class="bi bi-info-circle me-2"></i>No hay miembros aún.
            </div>
          </div>
        </div>
      </div>

      <!-- Entries section (members only) -->
      <div v-if="isMember || isCreator" class="card shadow-lg">
        <div class="card-header bg-success text-white py-3 d-flex justify-content-between align-items-center">
          <h4 class="mb-0"><i class="bi bi-chat-dots-fill me-2"></i>Publicaciones del Círculo</h4>
          <!-- Toggle de modo anónimo -->
          <div class="form-check form-switch mb-0">
            <input class="form-check-input" type="checkbox" id="anonToggleCircle" v-model="isAnonymous" @change="toggleAnonymous">
            <label class="form-check-label text-white" for="anonToggleCircle">
              <i :class="isAnonymous ? 'bi bi-incognito' : 'bi bi-person-fill'"></i>
              {{ isAnonymous ? 'Anónimo' : 'Visible' }}
            </label>
          </div>
        </div>
        <div class="card-body p-4">
          <!-- New entry form -->
          <div class="mb-4 p-3 bg-light rounded">
            <h5 class="mb-3"><i class="bi bi-pencil-square me-2"></i>Nueva Publicación</h5>
            <form @submit.prevent="createEntry">
              <div class="mb-3">
                <label for="entryType" class="form-label">Tipo de publicación</label>
                <select class="form-select" id="entryType" v-model="newEntry.type" required>
                  <option value="REFLECTION">💭 Reflexión</option>
                  <option value="TIP">💡 Consejo</option>
                  <option value="MOTIVATION">🎯 Motivación</option>
                  <option value="PROPOSAL">📝 Propuesta</option>
                  <option value="QUESTION">❓ Pregunta</option>
                  <option value="ACHIEVEMENT">🏆 Logro</option>
                </select>
              </div>
              <div class="mb-3">
                <label for="entryContent" class="form-label">Contenido</label>
                <textarea class="form-control" id="entryContent" v-model="newEntry.content" rows="3" maxlength="5000" placeholder="Escribe tu publicación aquí..." required></textarea>
                <small class="text-muted">{{ newEntry.content.length }}/5000 caracteres</small>
              </div>
              <button type="submit" class="btn btn-success" :disabled="creatingEntry || !newEntry.content.trim()">
                <span v-if="creatingEntry" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-send me-1"></i>
                {{ creatingEntry ? 'Publicando...' : 'Publicar' }}
              </button>
            </form>
          </div>

          <hr class="my-4">

          <!-- Entries feed -->
          <h5 class="mb-3"><i class="bi bi-collection me-2"></i>Publicaciones Recientes</h5>
          <div v-if="loadingEntries" class="text-center py-4">
            <div class="spinner-border text-success" role="status"></div>
          </div>
          <div v-else-if="entries.length > 0" class="entries-feed">
            <div v-for="entry in entries" :key="entry.id" class="entry-card mb-3 p-3 border rounded" :class="{ 'own-entry': entry.authorUserId === currentUserId }">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <div class="d-flex align-items-center">
                  <i class="bi bi-person-circle fs-4 text-secondary me-2"></i>
                  <div>
                    <strong>{{ getEntryAuthorDisplay(entry) }}</strong>
                    <span class="badge ms-2" :class="getEntryTypeBadge(entry.type)">{{ getEntryTypeLabel(entry.type) }}</span>
                    <br>
                    <small class="text-muted">
                      {{ formatDateTime(entry.createdAt) }}
                      <span v-if="entry.updatedAt"> (editado)</span>
                    </small>
                  </div>
                </div>
                <div v-if="entry.authorUserId === currentUserId" class="btn-group btn-group-sm">
                  <button class="btn btn-outline-warning" @click="openEditEntryModal(entry)"><i class="bi bi-pencil me-1"></i>Editar</button>
                  <button class="btn btn-outline-danger" @click="confirmDeleteEntry(entry)"><i class="bi bi-trash me-1"></i>Eliminar</button>
                </div>
              </div>
              <div class="entry-content mt-3">
                <p class="mb-0" style="white-space: pre-wrap;">{{ entry.content }}</p>
              </div>
            </div>
          </div>
          <div v-else class="alert alert-info">
            <i class="bi bi-info-circle me-2"></i>Aún no hay publicaciones. ¡Sé el primero en publicar!
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de modo anónimo -->
    <div v-if="showAnonymousModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">
              <i class="bi bi-incognito me-2"></i>Modo de participación
            </h5>
          </div>
          <div class="modal-body text-center">
            <i class="bi bi-shield-lock display-3 text-info mb-3 d-block"></i>
            <h5>¿Cómo quieres participar en este círculo?</h5>
            <p class="text-muted">
              Puedes elegir participar de forma anónima. Tus publicaciones se mostrarán como <strong>"Anónimo 🎭"</strong> y los demás miembros no verán tu identidad.
            </p>
            <div class="alert alert-light border">
              <i class="bi bi-info-circle me-2"></i>Puedes cambiar esta opción en cualquier momento con el interruptor en la sección de publicaciones.
            </div>
          </div>
          <div class="modal-footer justify-content-center">
            <button type="button" class="btn btn-outline-primary btn-lg" @click="setAnonymousMode(false)">
              <i class="bi bi-person-fill me-2"></i>Participar con mi nombre
            </button>
            <button type="button" class="btn btn-info btn-lg text-white" @click="setAnonymousMode(true)">
              <i class="bi bi-incognito me-2"></i>Participar anónimamente
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal editar entry -->
    <div v-if="showEditEntryModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title"><i class="bi bi-pencil me-2"></i>Editar Publicación</h5>
            <button type="button" class="btn-close" @click="showEditEntryModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="updateEntry">
              <div class="mb-3">
                <label class="form-label">Tipo</label>
                <select class="form-select" v-model="editEntryForm.type" required>
                  <option value="REFLECTION">💭 Reflexión</option>
                  <option value="TIP">💡 Consejo</option>
                  <option value="MOTIVATION">🎯 Motivación</option>
                  <option value="PROPOSAL">📝 Propuesta</option>
                  <option value="QUESTION">❓ Pregunta</option>
                  <option value="ACHIEVEMENT">🏆 Logro</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Contenido</label>
                <textarea class="form-control" v-model="editEntryForm.content" rows="4" maxlength="5000" required></textarea>
                <small class="text-muted">{{ editEntryForm.content.length }}/5000</small>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showEditEntryModal = false">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="updateEntry" :disabled="updatingEntry">
              <span v-if="updatingEntry" class="spinner-border spinner-border-sm me-2"></span>
              {{ updatingEntry ? 'Guardando...' : 'Guardar' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal eliminar entry -->
    <div v-if="showDeleteEntryModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title"><i class="bi bi-exclamation-triangle me-2"></i>Eliminar Publicación</h5>
            <button type="button" class="btn-close btn-close-white" @click="showDeleteEntryModal = false"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres eliminar esta publicación?</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDeleteEntryModal = false">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteEntry" :disabled="deletingEntry">
              <span v-if="deletingEntry" class="spinner-border spinner-border-sm me-2"></span>
              {{ deletingEntry ? 'Eliminando...' : 'Eliminar' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal salir -->
    <div v-if="showLeaveModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-warning">
            <h5 class="modal-title"><i class="bi bi-box-arrow-right me-2"></i>Salir del Círculo</h5>
            <button type="button" class="btn-close" @click="showLeaveModal = false"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres salir del círculo <strong>{{ circle?.name }}</strong>?</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showLeaveModal = false">Cancelar</button>
            <button type="button" class="btn btn-warning" @click="leaveCircle" :disabled="leaving">
              <span v-if="leaving" class="spinner-border spinner-border-sm me-2"></span>
              {{ leaving ? 'Saliendo...' : 'Salir' }}
            </button>
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
import UserRepository from '@/repositories/UserRepository'
import { getStore } from '@/common/store'

const route = useRoute()
const router = useRouter()
const store = getStore()

const circleId = computed(() => route.params.id)
const currentUserId = ref(localStorage.getItem('userId'))

const circle = ref(null)
const members = ref([])
const entries = ref([])

// cache de usuarios para mostrar nombre/login en lugar de "Usuario X"
const usersCache = ref({})

const loading = ref(false)
const loadingMembers = ref(false)
const loadingEntries = ref(false)
const error = ref('')
const creatingEntry = ref(false)
const updatingEntry = ref(false)
const deletingEntry = ref(false)
const leaving = ref(false)

const isMember = ref(false)
const isCreator = ref(false)

const showEditEntryModal = ref(false)
const showDeleteEntryModal = ref(false)
const showLeaveModal = ref(false)
const showAnonymousModal = ref(false)
const entryToDelete = ref(null)

const isAnonymous = ref(false)

const newEntry = ref({ type: 'REFLECTION', content: '' })
const editEntryForm = ref({ id: null, type: 'REFLECTION', content: '' })

const getAnonymousKey = () => {
  return `anonymous_circle_${circleId.value}`
}

const setAnonymousMode = async (anonymous) => {
  isAnonymous.value = anonymous
  localStorage.setItem(getAnonymousKey(), JSON.stringify(anonymous))
  showAnonymousModal.value = false
  await toggleAnonymous() // guarda en servidor
}

const toggleAnonymous = async () => {
  try {
    await CommunityRepository.updateMyAnonymous(circleId.value, { anonymous: isAnonymous.value })
    await loadMembers() // refresca estado real desde servidor
  } catch (err) {
    console.error('Error updating anonymous:', err)
    alert('No se pudo actualizar el modo anónimo')
    // rollback visual si falla
    isAnonymous.value = !isAnonymous.value
  }
}

// Función para mostrar el nombre del autor de una entrada
const getEntryAuthorDisplay = (entry) => {
  const myUserId = currentUserId.value
  const entryUserId = String(entry.authorUserId) // Asegurar que sea string
  
  console.log('🔍 Entry display:', { 
    isAnonymous: entry.isAnonymous, 
    entryUserId: entryUserId,
    myUserId: myUserId,
    authorUserName: entry.authorUserName,
    storeUserName: store.state.user.name,
    storeUserLogin: store.state.user.login
  })
  
  // Si la entrada es anónima
  if (entry.isAnonymous === true) {
    // Si soy yo, indicarlo
    if (entryUserId === myUserId) {
      return 'Anónimo 🎭 (Tú)'
    }
    return 'Anónimo 🎭'
  }
  
  // Si no es anónimo
  // Si soy yo, mostrar mi nombre del store
  if (entryUserId === myUserId) {
    return store.state.user.name || store.state.user.login || 'Usuario'
  }
  
  // Si es otro usuario, mostrar el nombre que viene del backend o del caché
  if (entry.authorUserName && entry.authorUserName !== 'null' && entry.authorUserName !== 'undefined') {
    return entry.authorUserName
  }
  
  // Intentar obtener del caché de usuarios
  const userInfo = usersCache.value[entryUserId] || usersCache.value[String(entryUserId)]
  if (userInfo) {
    return userInfo.name || userInfo.login || `Usuario ${entry.authorUserId}`
  }
  
  return `Usuario ${entry.authorUserId}`
}

// Función para mostrar el nombre de un miembro
const getMemberDisplay = (userId) => {
  // Si soy yo, mostrar mi nombre del store
  if (userId === currentUserId.value) {
    return store.state.user.name || store.state.user.login || 'Usuario'
  }
  // Si es otro usuario, mostrar un nombre genérico
  return `Usuario ${userId}`
}

const loadCircle = async () => {
  loading.value = true
  error.value = ''
  try {
    circle.value = await CommunityRepository.findById(circleId.value)
    const userId = parseInt(currentUserId.value)
    isCreator.value = parseInt(circle.value.creatorUserId) === userId

    // Check membership
    try {
      const myComms = await CommunityRepository.findMyCommunities()
      isMember.value = myComms.some(c => c.id === circle.value.id)
    } catch {
      isMember.value = false
    }

    await loadMembers()
    if (isMember.value || isCreator.value) {
      await loadEntries()
    }

    // Verificar preferencia de anonimato
    if (isMember.value || isCreator.value) {
      // Obtener el estado actual del servidor
      const me = members.value.find(m => String(m.userId) === String(currentUserId.value))
      if (me) {
        isAnonymous.value = me.anonymous || false
        // Guardar en localStorage para próxima vez
        localStorage.setItem(getAnonymousKey(), JSON.stringify(isAnonymous.value))
      }
      
      // Si no hay preferencia guardada aún, mostrar el modal
      const savedPref = localStorage.getItem(getAnonymousKey())
      if (savedPref === null) {
        showAnonymousModal.value = true
      }
    }
  } catch (err) {
    console.error('Error loading circle:', err)
    error.value = 'Error al cargar el círculo'
  } finally {
    loading.value = false
  }
}

const loadMembers = async () => {
  loadingMembers.value = true
  try {
    members.value = await CommunityRepository.getMembers(circleId.value)
    
    // Cargar nombres de usuario para miembros que no son anónimos y no tienen username
    const membersToLoad = members.value.filter(m => !m.anonymous && !m.username && m.userId)
    if (membersToLoad.length > 0) {
      await Promise.all(membersToLoad.map(m => loadUserInfo(m.userId)))
      
      // Actualizar los miembros con los nombres cargados
      members.value = members.value.map(m => {
        if (!m.anonymous && !m.username && m.userId) {
          const userInfo = usersCache.value[m.userId] || usersCache.value[String(m.userId)]
          if (userInfo) {
            return { ...m, username: userInfo.name || userInfo.login }
          }
        }
        return m
      })
    }
  } catch (err) {
    console.error('Error loading members:', err)
  } finally {
    loadingMembers.value = false
  }
}

const loadEntries = async () => {
  loadingEntries.value = true
  try {
    entries.value = await CommunityRepository.getEntries(circleId.value)

    // cargar datos de autores de cada entrada (para poder mostrar nombre si no es anónimo)
    const authorIds = [...new Set(entries.value.map(e => e.authorUserId))]
    await Promise.all(authorIds.map(id => loadUserInfo(id)))
  } catch (err) {
    console.error('Error loading entries:', err)
  } finally {
    loadingEntries.value = false
  }
}

const loadUserInfo = async (userId) => {
  if (!userId) return
  const sid = String(userId)
  if (usersCache.value[userId] || usersCache.value[sid]) {
    return // ya en caché
  }
  try {
    const user = await UserRepository.findOne(userId)
    usersCache.value[userId] = user
    usersCache.value[sid] = user
  } catch (err) {
    console.error('Error fetching user info', err)
  }
}

const joinCircle = async () => {
  try {
    await CommunityRepository.join(circleId.value)
    isMember.value = true
    // Limpiar preferencia anterior para que muestre el modal
    localStorage.removeItem(getAnonymousKey())
    await loadCircle()
  } catch (err) {
    console.error('Error joining circle:', err)
    alert(err.response?.status === 409 ? 'Ya eres miembro' : 'Error al unirse')
  }
}

const confirmLeave = () => { showLeaveModal.value = true }

const leaveCircle = async () => {
  leaving.value = true
  try {
    await CommunityRepository.leave(circleId.value)
    showLeaveModal.value = false
    router.push('/support-circles')
  } catch (err) {
    console.error('Error leaving circle:', err)
    alert('Error al salir del círculo')
  } finally {
    leaving.value = false
  }
}

const createEntry = async () => {
  if (!newEntry.value.content.trim()) return
  creatingEntry.value = true
  try {
    // Añadir el estado anónimo al crear la entrada
    const entryToCreate = {
      ...newEntry.value,
      isAnonymous: isAnonymous.value
    }
    await CommunityRepository.createEntry(circleId.value, entryToCreate)
    newEntry.value = { type: 'REFLECTION', content: '' }
    await loadEntries()
  } catch (err) {
    console.error('Error creating entry:', err)
    alert('Error al publicar')
  } finally {
    creatingEntry.value = false
  }
}

const openEditEntryModal = (entry) => {
  editEntryForm.value = { id: entry.id, type: entry.type, content: entry.content }
  showEditEntryModal.value = true
}

const updateEntry = async () => {
  updatingEntry.value = true
  try {
    await CommunityRepository.updateEntry(circleId.value, editEntryForm.value.id, {
      type: editEntryForm.value.type,
      content: editEntryForm.value.content
    })
    showEditEntryModal.value = false
    await loadEntries()
  } catch (err) {
    console.error('Error updating entry:', err)
    alert('Error al actualizar')
  } finally {
    updatingEntry.value = false
  }
}

const confirmDeleteEntry = (entry) => {
  entryToDelete.value = entry
  showDeleteEntryModal.value = true
}

const deleteEntry = async () => {
  if (!entryToDelete.value) return
  deletingEntry.value = true
  try {
    await CommunityRepository.deleteEntry(circleId.value, entryToDelete.value.id)
    showDeleteEntryModal.value = false
    await loadEntries()
  } catch (err) {
    console.error('Error deleting entry:', err)
    alert('Error al eliminar')
  } finally {
    deletingEntry.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('es-ES', { year: 'numeric', month: 'short', day: 'numeric' })
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('es-ES', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const getRoleBadgeClass = (role) => {
  const classes = { ADMIN: 'bg-danger', MODERATOR: 'bg-warning text-dark', MEMBER: 'bg-primary' }
  return classes[role] || 'bg-secondary'
}

const getRoleLabel = (role) => {
  const labels = { ADMIN: 'Administrador', MODERATOR: 'Moderador', MEMBER: 'Miembro' }
  return labels[role] || role
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

onMounted(() => loadCircle())
</script>

<style scoped>
.entry-card {
  transition: background-color 0.2s;
}
.entry-card:hover {
  background-color: #f8f9fa;
}
.own-entry {
  border-left: 4px solid #198754 !important;
}
</style>
