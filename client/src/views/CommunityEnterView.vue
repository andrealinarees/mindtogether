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

    <!-- Vista simplificada de la comunidad -->
    <div v-else-if="community" class="row">
      <div class="col-12">
        <!-- Header con botones -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <button @click="$router.push('/community')" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i>Volver
          </button>

          <div class="d-flex gap-2">
            <button @click="viewDetails" class="btn btn-outline-info">
              <i class="bi bi-info-circle me-1"></i>Ver Detalle Completo
            </button>
            <button v-if="!isCreator" @click="confirmLeave" class="btn btn-outline-danger">
              <i class="bi bi-box-arrow-right me-1"></i>Salir
            </button>
          </div>
        </div>

        <!-- Card con información básica de la comunidad -->
        <div class="card shadow-lg mb-4">
          <div class="card-header bg-primary text-white py-4">
            <h1 class="mb-0">
              <i class="bi bi-people-fill me-3"></i>{{ community.name }}
            </h1>
          </div>
          <div class="card-body p-4">
            <!-- Motivo de creación -->
            <div class="mb-3">
              <h5 class="text-primary">
                <i class="bi bi-lightbulb me-2"></i>Motivo de creación
              </h5>
              <p class="fs-5 text-muted">{{ community.creationReason }}</p>
            </div>

            <!-- Descripción -->
            <div v-if="community.description">
              <h5 class="text-primary">
                <i class="bi bi-card-text me-2"></i>Descripción
              </h5>
              <p class="fs-6" style="white-space: pre-wrap;">{{ community.description }}</p>
            </div>
          </div>
        </div>

        <!-- Sección de Publicaciones -->
        <div class="card shadow-lg">
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
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import CommunityRepository from '@/repositories/CommunityRepository';

export default {
  name: 'CommunityEnterView',
  setup() {
    const router = useRouter();
    const route = useRoute();

    const loading = ref(true);
    const loadingEntries = ref(false);
    const error = ref('');
    const community = ref(null);
    const entries = ref([]);
    const currentUserId = ref(localStorage.getItem('userId'));

    const showEditEntryModal = ref(false);
    const showDeleteEntryModal = ref(false);
    const showLeaveModal = ref(false);

    const creatingEntry = ref(false);
    const updatingEntry = ref(false);
    const deletingEntry = ref(false);
    const leaving = ref(false);

    const entryError = ref('');

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

    const loadCommunity = async () => {
      loading.value = true;
      error.value = '';

      try {
        const id = route.params.id;
        community.value = await CommunityRepository.findById(id);
        await loadEntries();
      } catch (err) {
        console.error('Error loading community:', err);
        error.value = 'No se pudo cargar la información de la comunidad';
      } finally {
        loading.value = false;
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

    const viewDetails = () => {
      router.push(`/community/${community.value.id}`);
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

    onMounted(() => {
      loadCommunity();
    });

    return {
      loading,
      loadingEntries,
      error,
      community,
      entries,
      currentUserId,
      isCreator,
      showEditEntryModal,
      showDeleteEntryModal,
      showLeaveModal,
      creatingEntry,
      updatingEntry,
      deletingEntry,
      leaving,
      entryError,
      newEntry,
      editEntryForm,
      viewDetails,
      formatDateTime,
      getEntryTypeLabel,
      getEntryTypeBadge,
      createEntry,
      openEditEntryModal,
      closeEditEntryModal,
      updateEntry,
      confirmDeleteEntry,
      closeDeleteEntryModal,
      deleteEntry,
      confirmLeave,
      closeLeaveModal,
      leaveCommunity
    };
  }
};
</script>

<style scoped>
.entry-card {
  transition: all 0.2s;
  background-color: white;
}

.entry-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.own-entry {
  background-color: #f8f9fa;
  border-left: 4px solid #0d6efd;
}

.entry-content {
  line-height: 1.6;
}

.bg-light {
  background-color: #f8f9fa !important;
}
</style>

