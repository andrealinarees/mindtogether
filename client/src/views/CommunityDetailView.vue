<template>
  <div class="container mt-4">
    <!-- Cargando -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
      <button @click="$router.push('/community')" class="btn btn-sm btn-outline-danger ms-3">
        Volver a comunidades
      </button>
    </div>

    <!-- Detalle de la comunidad -->
    <div v-else-if="community" class="row">
      <div class="col-12">
        <!-- Header con botón volver -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <button @click="$router.push('/community')" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i>Volver
          </button>

          <!-- Botones de acción según permisos -->
          <div class="d-flex gap-2" v-if="isCreator">
            <button @click="openEditModal" class="btn btn-warning">
              <i class="bi bi-pencil me-1"></i>Editar
            </button>
            <button @click="confirmDelete" class="btn btn-danger">
              <i class="bi bi-trash me-1"></i>Eliminar
            </button>
          </div>
          <div v-else-if="isMember">
            <button @click="confirmLeave" class="btn btn-outline-danger">
              <i class="bi bi-box-arrow-right me-1"></i>Salir de la comunidad
            </button>
          </div>
          <div v-else>
            <button @click="joinCommunity" class="btn btn-success">
              <i class="bi bi-box-arrow-in-right me-1"></i>Unirse
            </button>
          </div>
        </div>

        <!-- Card principal con información de la comunidad -->
        <div class="card shadow-lg mb-4">
          <div class="card-header bg-primary text-white py-4">
            <h1 class="mb-0">
              <i class="bi bi-people-fill me-3"></i>{{ community.name }}
            </h1>
          </div>
          <div class="card-body p-4">
            <!-- Motivo de creación -->
            <div class="mb-4">
              <h5 class="text-primary">
                <i class="bi bi-lightbulb me-2"></i>Motivo de creación
              </h5>
              <p class="fs-5 text-muted">{{ community.creationReason }}</p>
            </div>

            <hr class="my-4">

            <!-- Descripción -->
            <div class="mb-4">
              <h5 class="text-primary">
                <i class="bi bi-card-text me-2"></i>Descripción
              </h5>
              <p class="fs-6" style="white-space: pre-wrap;">
                {{ community.description || 'Sin descripción disponible' }}
              </p>
            </div>

            <hr class="my-4">

            <!-- Estadísticas -->
            <div class="row text-center">
              <div class="col-md-4 mb-3">
                <div class="p-3 bg-light rounded">
                  <i class="bi bi-person-fill text-primary fs-1"></i>
                  <h3 class="mt-2 mb-0">{{ community.memberCount || 0 }}</h3>
                  <p class="text-muted mb-0">Miembros</p>
                </div>
              </div>
              <div class="col-md-4 mb-3">
                <div class="p-3 bg-light rounded">
                  <i class="bi bi-chat-dots-fill text-success fs-1"></i>
                  <h3 class="mt-2 mb-0">{{ community.entryCount || 0 }}</h3>
                  <p class="text-muted mb-0">Publicaciones</p>
                </div>
              </div>
              <div class="col-md-4 mb-3">
                <div class="p-3 bg-light rounded">
                  <i class="bi bi-calendar-event text-info fs-1"></i>
                  <h3 class="mt-2 mb-0">{{ formatDate(community.createdAt) }}</h3>
                  <p class="text-muted mb-0">Fecha de creación</p>
                </div>
              </div>
            </div>

            <hr class="my-4">

            <!-- Lista de miembros -->
            <div class="mb-4">
              <h5 class="text-primary mb-3">
                <i class="bi bi-people me-2"></i>Miembros de la comunidad
              </h5>

              <div v-if="loadingMembers" class="text-center py-3">
                <div class="spinner-border spinner-border-sm text-primary" role="status">
                  <span class="visually-hidden">Cargando miembros...</span>
                </div>
              </div>

              <div v-else-if="members.length > 0" class="row">
                <div v-for="member in members" :key="member.id" class="col-md-6 col-lg-4 mb-3">
                  <div class="card h-100">
                    <div class="card-body d-flex align-items-center">
                      <div class="me-3">
                        <i class="bi bi-person-circle fs-2 text-secondary"></i>
                      </div>
                      <div class="flex-grow-1">
                        <h6 class="mb-1">Usuario {{ member.userId }}</h6>
                        <span class="badge" :class="getRoleBadgeClass(member.role)">
                          {{ getRoleLabel(member.role) }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="alert alert-info">
                <i class="bi bi-info-circle me-2"></i>No hay miembros en esta comunidad todavía.
              </div>
            </div>

            <!-- Información del creador -->
            <div class="alert alert-info">
              <i class="bi bi-person-badge me-2"></i>
              <strong>Creador:</strong> Usuario {{ community.creatorUserId }}
            </div>
          </div>
        </div>

        <!-- Sección de Publicaciones (solo para miembros) -->
        <div v-if="isMember || isCreator" class="card shadow-lg">
          <div class="card-header bg-success text-white py-3">
            <h4 class="mb-0">
              <i class="bi bi-chat-dots-fill me-2"></i>Publicaciones de la Comunidad
            </h4>
          </div>
          <div class="card-body p-4">

            <!-- Formulario para nueva publicación -->
            <div class="mb-4 p-3 bg-light rounded">
              <h5 class="mb-3">
                <i class="bi bi-pencil-square me-2"></i>Nueva Publicación
              </h5>
              <form @submit.prevent="createEntry">
                <div class="mb-3">
                  <label for="entryType" class="form-label">Tipo de publicación</label>
                  <select
                    class="form-select"
                    id="entryType"
                    v-model="newEntry.type"
                    required
                  >
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
                  <textarea
                    class="form-control"
                    id="entryContent"
                    v-model="newEntry.content"
                    rows="3"
                    maxlength="5000"
                    placeholder="Escribe tu publicación aquí..."
                    required
                  ></textarea>
                  <small class="text-muted">{{ newEntry.content.length }}/5000 caracteres</small>
                </div>

                <button
                  type="submit"
                  class="btn btn-success"
                  :disabled="creatingEntry || !newEntry.content.trim()"
                >
                  <span v-if="creatingEntry" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-send me-1"></i>
                  {{ creatingEntry ? 'Publicando...' : 'Publicar' }}
                </button>
              </form>
            </div>

            <hr class="my-4">

            <!-- Feed de publicaciones -->
            <div>
              <h5 class="mb-3">
                <i class="bi bi-collection me-2"></i>Publicaciones Recientes
              </h5>

              <div v-if="loadingEntries" class="text-center py-4">
                <div class="spinner-border text-success" role="status">
                  <span class="visually-hidden">Cargando publicaciones...</span>
                </div>
              </div>

              <div v-else-if="entries.length > 0" class="entries-feed">
                <div
                  v-for="entry in entries"
                  :key="entry.id"
                  class="entry-card mb-3 p-3 border rounded"
                  :class="{ 'own-entry': entry.authorUserId === currentUserId }"
                >
                  <div class="d-flex justify-content-between align-items-start mb-2">
                    <div class="d-flex align-items-center">
                      <i class="bi bi-person-circle fs-4 text-secondary me-2"></i>
                      <div>
                        <strong>Usuario {{ entry.authorUserId }}</strong>
                        <span class="badge ms-2" :class="getEntryTypeBadge(entry.type)">
                          {{ getEntryTypeLabel(entry.type) }}
                        </span>
                        <br>
                        <small class="text-muted">
                          {{ formatDateTime(entry.createdAt) }}
                          <span v-if="entry.updatedAt"> (editado)</span>
                        </small>
                      </div>
                    </div>

                    <!-- Botones de acción (solo para el autor) -->
                    <div v-if="entry.authorUserId === currentUserId" class="btn-group btn-group-sm">
                      <button
                        class="btn btn-outline-warning"
                        @click="openEditEntryModal(entry)"
                        title="Editar"
                      >
                        <i class="bi bi-pencil me-1"></i>Editar
                      </button>
                      <button
                        class="btn btn-outline-danger"
                        @click="confirmDeleteEntry(entry)"
                        title="Eliminar"
                      >
                        <i class="bi bi-trash me-1"></i>Eliminar
                      </button>
                    </div>
                  </div>

                  <div class="entry-content mt-3">
                    <p class="mb-0" style="white-space: pre-wrap;">{{ entry.content }}</p>
                  </div>
                </div>
              </div>

              <div v-else class="alert alert-info">
                <i class="bi bi-info-circle me-2"></i>
                Aún no hay publicaciones en esta comunidad. ¡Sé el primero en publicar!
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para editar comunidad -->
    <div v-if="showEditModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-pencil me-2"></i>Editar Comunidad
            </h5>
            <button type="button" class="btn-close" @click="closeEditModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="updateCommunity">
              <div class="mb-3">
                <label for="editName" class="form-label">Nombre <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="editName"
                  v-model="editForm.name"
                  maxlength="100"
                  required
                >
                <small class="text-muted">{{ editForm.name.length }}/100 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="editReason" class="form-label">Motivo de Creación <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="editReason"
                  v-model="editForm.creationReason"
                  maxlength="500"
                  required
                >
                <small class="text-muted">{{ editForm.creationReason.length }}/500 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="editDescription" class="form-label">Descripción</label>
                <textarea
                  class="form-control"
                  id="editDescription"
                  v-model="editForm.description"
                  rows="4"
                  maxlength="2000"
                ></textarea>
                <small class="text-muted">{{ editForm.description.length }}/2000 caracteres</small>
              </div>

              <div v-if="editError" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ editError }}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="updateCommunity" :disabled="updating">
              <span v-if="updating" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-check-circle me-1"></i>
              {{ updating ? 'Guardando...' : 'Guardar Cambios' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de confirmación para eliminar -->
    <div v-if="showDeleteModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">
              <i class="bi bi-exclamation-triangle me-2"></i>Confirmar Eliminación
            </h5>
            <button type="button" class="btn-close btn-close-white" @click="closeDeleteModal"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres eliminar la comunidad <strong>{{ community.name }}</strong>?</p>
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-circle me-2"></i>
              Esta acción es permanente y eliminará todos los datos asociados.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeDeleteModal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteCommunity" :disabled="deleting">
              <span v-if="deleting" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-trash me-1"></i>
              {{ deleting ? 'Eliminando...' : 'Eliminar Definitivamente' }}
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
            <h5 class="modal-title">
              <i class="bi bi-box-arrow-right me-2"></i>Salir de Comunidad
            </h5>
            <button type="button" class="btn-close" @click="closeLeaveModal"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres salir de la comunidad <strong>{{ community.name }}</strong>?</p>
            <div class="alert alert-info">
              <i class="bi bi-info-circle me-2"></i>
              Podrás volver a unirte en cualquier momento si lo deseas.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeLeaveModal">Cancelar</button>
            <button type="button" class="btn btn-warning" @click="leaveCommunity" :disabled="leaving">
              <span v-if="leaving" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-box-arrow-right me-1"></i>
              {{ leaving ? 'Saliendo...' : 'Salir de Comunidad' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para editar publicación -->
    <div v-if="showEditEntryModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-pencil me-2"></i>Editar Publicación
            </h5>
            <button type="button" class="btn-close" @click="closeEditEntryModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="updateEntry">
              <div class="mb-3">
                <label for="editEntryType" class="form-label">Tipo de publicación</label>
                <select
                  class="form-select"
                  id="editEntryType"
                  v-model="editEntryForm.type"
                  required
                >
                  <option value="REFLECTION">💭 Reflexión</option>
                  <option value="TIP">💡 Consejo</option>
                  <option value="MOTIVATION">🎯 Motivación</option>
                  <option value="PROPOSAL">📝 Propuesta</option>
                  <option value="QUESTION">❓ Pregunta</option>
                  <option value="ACHIEVEMENT">🏆 Logro</option>
                </select>
              </div>

              <div class="mb-3">
                <label for="editEntryContent" class="form-label">Contenido</label>
                <textarea
                  class="form-control"
                  id="editEntryContent"
                  v-model="editEntryForm.content"
                  rows="4"
                  maxlength="5000"
                  required
                ></textarea>
                <small class="text-muted">{{ editEntryForm.content.length }}/5000 caracteres</small>
              </div>

              <div v-if="entryError" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ entryError }}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditEntryModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="updateEntry" :disabled="updatingEntry">
              <span v-if="updatingEntry" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-check-circle me-1"></i>
              {{ updatingEntry ? 'Guardando...' : 'Guardar Cambios' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de confirmación para eliminar publicación -->
    <div v-if="showDeleteEntryModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">
              <i class="bi bi-exclamation-triangle me-2"></i>Confirmar Eliminación
            </h5>
            <button type="button" class="btn-close btn-close-white" @click="closeDeleteEntryModal"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres eliminar esta publicación?</p>
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-circle me-2"></i>
              Esta acción no se puede deshacer.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeDeleteEntryModal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteEntry" :disabled="deletingEntry">
              <span v-if="deletingEntry" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-trash me-1"></i>
              {{ deletingEntry ? 'Eliminando...' : 'Eliminar' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import CommunityRepository from '@/repositories/CommunityRepository';

export default {
  name: 'CommunityDetailView',
  setup() {
    const router = useRouter();
    const route = useRoute();

    const loading = ref(true);
    const loadingMembers = ref(false);
    const loadingEntries = ref(false);
    const error = ref('');
    const community = ref(null);
    const members = ref([]);
    const entries = ref([]);
    const currentUserId = ref(localStorage.getItem('userId'));

    const showEditModal = ref(false);
    const showDeleteModal = ref(false);
    const showLeaveModal = ref(false);
    const showEditEntryModal = ref(false);
    const showDeleteEntryModal = ref(false);

    const updating = ref(false);
    const deleting = ref(false);
    const leaving = ref(false);
    const creatingEntry = ref(false);
    const updatingEntry = ref(false);
    const deletingEntry = ref(false);

    const editError = ref('');
    const entryError = ref('');

    const editForm = ref({
      name: '',
      creationReason: '',
      description: ''
    });

    const newEntry = ref({
      type: 'REFLECTION',
      content: ''
    });

    const editEntryForm = ref({
      id: null,
      type: 'REFLECTION',
      content: ''
    });

    const entryToDelete = ref(null);

    const isCreator = computed(() => {
      return community.value && community.value.creatorUserId === currentUserId.value;
    });

    const isMember = computed(() => {
      return members.value.some(m => m.userId === currentUserId.value);
    });

    const loadCommunity = async () => {
      loading.value = true;
      error.value = '';

      try {
        const id = route.params.id;
        community.value = await CommunityRepository.findById(id);
        await loadMembers();
        await loadEntries();
      } catch (err) {
        console.error('Error loading community:', err);
        error.value = 'No se pudo cargar la información de la comunidad';
      } finally {
        loading.value = false;
      }
    };

    const loadMembers = async () => {
      if (!community.value) return;

      loadingMembers.value = true;
      try {
        members.value = await CommunityRepository.getMembers(community.value.id);
      } catch (err) {
        console.error('Error loading members:', err);
      } finally {
        loadingMembers.value = false;
      }
    };

    const loadEntries = async () => {
      if (!community.value) return;

      loadingEntries.value = true;
      try {
        entries.value = await CommunityRepository.getEntries(community.value.id);
      } catch (err) {
        console.error('Error loading entries:', err);
      } finally {
        loadingEntries.value = false;
      }
    };

    const joinCommunity = async () => {
      try {
        await CommunityRepository.join(community.value.id);
        community.value.memberCount = (community.value.memberCount || 0) + 1;
        await loadMembers();
        console.log('✅ Te has unido a la comunidad exitosamente');
      } catch (err) {
        console.error('Error joining community:', err);
        alert('Error al unirse a la comunidad. Por favor, inténtalo de nuevo.');
      }
    };

    const openEditModal = () => {
      editForm.value = {
        name: community.value.name,
        creationReason: community.value.creationReason,
        description: community.value.description || ''
      };
      showEditModal.value = true;
      editError.value = '';
    };

    const closeEditModal = () => {
      showEditModal.value = false;
      editError.value = '';
    };

    const updateCommunity = async () => {
      updating.value = true;
      editError.value = '';

      try {
        const updated = await CommunityRepository.update(community.value.id, editForm.value);
        community.value = { ...community.value, ...updated };
        closeEditModal();
        console.log('✅ Comunidad actualizada exitosamente');
      } catch (err) {
        console.error('Error updating community:', err);
        if (err.response && err.response.status === 403) {
          editError.value = 'No tienes permisos para editar esta comunidad';
        } else {
          editError.value = 'Error al actualizar la comunidad. Por favor, inténtalo de nuevo.';
        }
      } finally {
        updating.value = false;
      }
    };

    const confirmDelete = () => {
      showDeleteModal.value = true;
    };

    const closeDeleteModal = () => {
      showDeleteModal.value = false;
    };

    const deleteCommunity = async () => {
      deleting.value = true;

      try {
        await CommunityRepository.delete(community.value.id);
        console.log('✅ Comunidad eliminada exitosamente');
        router.push('/community');
      } catch (err) {
        console.error('Error deleting community:', err);
        alert('Error al eliminar la comunidad. Por favor, inténtalo de nuevo.');
      } finally {
        deleting.value = false;
        closeDeleteModal();
      }
    };

    const confirmLeave = () => {
      showLeaveModal.value = true;
    };

    const closeLeaveModal = () => {
      showLeaveModal.value = false;
    };

    const leaveCommunity = async () => {
      leaving.value = true;

      try {
        await CommunityRepository.leave(community.value.id);
        console.log('✅ Has salido de la comunidad exitosamente');
        router.push('/community');
      } catch (err) {
        console.error('Error leaving community:', err);
        if (err.response && err.response.status === 403) {
          alert('No puedes salir de esta comunidad. El creador no puede abandonar su propia comunidad.');
        } else {
          alert('Error al salir de la comunidad. Por favor, inténtalo de nuevo.');
        }
      } finally {
        leaving.value = false;
        closeLeaveModal();
      }
    };

    const formatDate = (date) => {
      if (!date) return 'N/A';
      const d = new Date(date);
      return d.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    const formatDateTime = (date) => {
      if (!date) return 'N/A';
      const d = new Date(date);
      return d.toLocaleString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    };

    const getRoleLabel = (role) => {
      const labels = {
        'ADMIN': 'Administrador',
        'MODERATOR': 'Moderador',
        'MEMBER': 'Miembro'
      };
      return labels[role] || role;
    };

    const getRoleBadgeClass = (role) => {
      const classes = {
        'ADMIN': 'bg-danger',
        'MODERATOR': 'bg-warning',
        'MEMBER': 'bg-primary'
      };
      return classes[role] || 'bg-secondary';
    };

    const getEntryTypeLabel = (type) => {
      const labels = {
        'REFLECTION': '💭 Reflexión',
        'TIP': '💡 Consejo',
        'MOTIVATION': '🎯 Motivación',
        'PROPOSAL': '📝 Propuesta',
        'QUESTION': '❓ Pregunta',
        'ACHIEVEMENT': '🏆 Logro'
      };
      return labels[type] || type;
    };

    const getEntryTypeBadge = (type) => {
      const badges = {
        'REFLECTION': 'bg-info',
        'TIP': 'bg-warning',
        'MOTIVATION': 'bg-success',
        'PROPOSAL': 'bg-primary',
        'QUESTION': 'bg-secondary',
        'ACHIEVEMENT': 'bg-danger'
      };
      return badges[type] || 'bg-secondary';
    };

    const createEntry = async () => {
      if (!newEntry.value.content.trim()) {
        return;
      }

      creatingEntry.value = true;
      entryError.value = '';

      try {
        await CommunityRepository.createEntry(community.value.id, newEntry.value);
        newEntry.value = {
          type: 'REFLECTION',
          content: ''
        };
        await loadEntries();
        community.value.entryCount = (community.value.entryCount || 0) + 1;
        console.log('✅ Publicación creada exitosamente');
      } catch (err) {
        console.error('Error creating entry:', err);
        entryError.value = 'Error al crear la publicación. Por favor, inténtalo de nuevo.';
      } finally {
        creatingEntry.value = false;
      }
    };

    const openEditEntryModal = (entry) => {
      editEntryForm.value = {
        id: entry.id,
        type: entry.type,
        content: entry.content
      };
      showEditEntryModal.value = true;
      entryError.value = '';
    };

    const closeEditEntryModal = () => {
      showEditEntryModal.value = false;
      entryError.value = '';
    };

    const updateEntry = async () => {
      updatingEntry.value = true;
      entryError.value = '';

      try {
        await CommunityRepository.updateEntry(
          community.value.id,
          editEntryForm.value.id,
          {
            type: editEntryForm.value.type,
            content: editEntryForm.value.content
          }
        );
        await loadEntries();
        closeEditEntryModal();
        console.log('✅ Publicación actualizada exitosamente');
      } catch (err) {
        console.error('Error updating entry:', err);
        entryError.value = 'Error al actualizar la publicación. Por favor, inténtalo de nuevo.';
      } finally {
        updatingEntry.value = false;
      }
    };

    const confirmDeleteEntry = (entry) => {
      entryToDelete.value = entry;
      showDeleteEntryModal.value = true;
    };

    const closeDeleteEntryModal = () => {
      showDeleteEntryModal.value = false;
      entryToDelete.value = null;
    };

    const deleteEntry = async () => {
      if (!entryToDelete.value) return;

      deletingEntry.value = true;

      try {
        await CommunityRepository.deleteEntry(community.value.id, entryToDelete.value.id);
        await loadEntries();
        community.value.entryCount = Math.max(0, (community.value.entryCount || 0) - 1);
        closeDeleteEntryModal();
        console.log('✅ Publicación eliminada exitosamente');
      } catch (err) {
        console.error('Error deleting entry:', err);
        alert('Error al eliminar la publicación. Por favor, inténtalo de nuevo.');
      } finally {
        deletingEntry.value = false;
      }
    };

    onMounted(() => {
      loadCommunity();
    });

    return {
      loading,
      loadingMembers,
      loadingEntries,
      error,
      community,
      members,
      entries,
      currentUserId,
      isCreator,
      isMember,
      showEditModal,
      showDeleteModal,
      showLeaveModal,
      showEditEntryModal,
      showDeleteEntryModal,
      updating,
      deleting,
      leaving,
      creatingEntry,
      updatingEntry,
      deletingEntry,
      editError,
      entryError,
      editForm,
      newEntry,
      editEntryForm,
      joinCommunity,
      openEditModal,
      closeEditModal,
      updateCommunity,
      confirmDelete,
      closeDeleteModal,
      deleteCommunity,
      confirmLeave,
      closeLeaveModal,
      leaveCommunity,
      formatDate,
      formatDateTime,
      getRoleLabel,
      getRoleBadgeClass,
      getEntryTypeLabel,
      getEntryTypeBadge,
      createEntry,
      openEditEntryModal,
      closeEditEntryModal,
      updateEntry,
      confirmDeleteEntry,
      closeDeleteEntryModal,
      deleteEntry
    };
  }
};
</script>

<style scoped>
.card {
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-2px);
}

.bg-light {
  background-color: #f8f9fa !important;
}

.fs-6 {
  font-size: 1rem;
}
</style>

