<template>
  <div class="container mt-4">
    <div class="row mb-4">
      <div class="col-12">
        <h1>
          <i class="bi bi-shield-check me-2"></i>Gestión de Comunidades (Administrador)
        </h1>
        <p class="text-muted">Supervisar, editar o eliminar comunidades y publicaciones inapropiadas</p>
      </div>
    </div>

    <!-- Buscador -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="input-group">
          <span class="input-group-text">
            <i class="bi bi-search"></i>
          </span>
          <input
            type="text"
            class="form-control"
            placeholder="Buscar comunidades..."
            v-model="searchQuery"
          >
        </div>
      </div>
      <div class="col-md-6 text-end">
        <span class="badge bg-primary fs-6">
          <i class="bi bi-people me-1"></i>
          {{ filteredCommunities.length }} comunidades
        </span>
      </div>
    </div>

    <!-- Cargando -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Lista de Comunidades -->
    <div v-else-if="filteredCommunities.length > 0" class="row">
      <div v-for="community in filteredCommunities" :key="community.id" class="col-12 mb-3">
        <div class="card shadow-sm">
          <div class="card-body">
            <div class="row">
              <div class="col-md-8">
                <h5 class="card-title">
                  <i class="bi bi-people-fill me-2 text-primary"></i>
                  {{ community.name }}
                </h5>
                <p class="card-text text-muted small mb-2">
                  <strong>Motivo:</strong> {{ community.creationReason }}
                </p>
                <p class="card-text">{{ truncateText(community.description, 150) }}</p>
                <div class="d-flex gap-3 mt-2">
                  <small class="text-muted">
                    <i class="bi bi-person me-1"></i>
                    {{ community.memberCount || 0 }} miembros
                  </small>
                  <small class="text-muted">
                    <i class="bi bi-chat-dots me-1"></i>
                    {{ community.entryCount || 0 }} publicaciones
                  </small>
                  <small class="text-muted">
                    <i class="bi bi-calendar me-1"></i>
                    {{ formatDate(community.createdAt) }}
                  </small>
                </div>
              </div>
              <div class="col-md-4 text-end d-flex flex-column justify-content-center gap-2">
                <button
                  class="btn btn-outline-primary btn-sm"
                  @click="viewCommunityEntries(community)"
                >
                  <i class="bi bi-list me-1"></i>Ver Publicaciones
                </button>
                <button
                  class="btn btn-warning btn-sm"
                  @click="openEditModal(community)"
                >
                  <i class="bi bi-pencil me-1"></i>Editar
                </button>
                <button
                  class="btn btn-danger btn-sm"
                  @click="confirmDelete(community)"
                >
                  <i class="bi bi-trash me-1"></i>Eliminar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sin comunidades -->
    <div v-else class="row">
      <div class="col-12">
        <div class="card shadow-sm">
          <div class="card-body text-center py-5">
            <i class="bi bi-people display-1 text-muted"></i>
            <h3 class="mt-4 text-muted">No hay comunidades</h3>
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
              <i class="bi bi-pencil me-2"></i>Editar Comunidad (Admin)
            </h5>
            <button type="button" class="btn-close" @click="closeEditModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="updateCommunity">
              <div class="mb-3">
                <label class="form-label">Nombre <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  v-model="editingCommunity.name"
                  maxlength="100"
                  required
                >
              </div>
              <div class="mb-3">
                <label class="form-label">Motivo de Creación <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  v-model="editingCommunity.creationReason"
                  maxlength="500"
                  required
                >
              </div>
              <div class="mb-3">
                <label class="form-label">Descripción</label>
                <textarea
                  class="form-control"
                  v-model="editingCommunity.description"
                  rows="4"
                  maxlength="2000"
                ></textarea>
              </div>
              <div v-if="error" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="updateCommunity" :disabled="updating">
              <span v-if="updating" class="spinner-border spinner-border-sm me-2"></span>
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
            <p>¿Estás seguro de que quieres eliminar la comunidad <strong>{{ communityToDelete?.name }}</strong>?</p>
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-circle me-2"></i>
              Esta acción eliminará todos los datos asociados, incluyendo publicaciones y miembros.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeDeleteModal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteCommunity" :disabled="deleting">
              <span v-if="deleting" class="spinner-border spinner-border-sm me-2"></span>
              {{ deleting ? 'Eliminando...' : 'Eliminar Definitivamente' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para ver/gestionar publicaciones -->
    <div v-if="showEntriesModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-chat-dots me-2"></i>Publicaciones de: {{ selectedCommunity?.name }}
            </h5>
            <button type="button" class="btn-close" @click="closeEntriesModal"></button>
          </div>
          <div class="modal-body" style="max-height: 500px; overflow-y: auto;">
            <div v-if="loadingEntries" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
            </div>
            <div v-else-if="entries.length > 0">
              <div v-for="entry in entries" :key="entry.id" class="card mb-3">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-2">
                    <div>
                      <h6 class="mb-1">{{ entry.title }}</h6>
                      <small class="text-muted">
                        <i class="bi bi-calendar me-1"></i>{{ formatDate(entry.createdAt) }}
                      </small>
                    </div>
                    <div class="d-flex gap-1">
                      <button
                        class="btn btn-sm btn-warning"
                        @click="openEditEntryModal(entry)"
                        title="Editar"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button
                        class="btn btn-sm btn-danger"
                        @click="confirmDeleteEntry(entry)"
                        title="Eliminar"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                  <p class="card-text mb-0">{{ entry.content }}</p>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-4 text-muted">
              <i class="bi bi-chat display-4"></i>
              <p class="mt-2">No hay publicaciones en esta comunidad</p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEntriesModal">Cerrar</button>
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
            <div class="mb-3">
              <label class="form-label">Título</label>
              <input
                type="text"
                class="form-control"
                v-model="editingEntry.title"
                maxlength="200"
              >
            </div>
            <div class="mb-3">
              <label class="form-label">Contenido</label>
              <textarea
                class="form-control"
                v-model="editingEntry.content"
                rows="4"
                maxlength="2000"
              ></textarea>
            </div>
            <div v-if="errorEntry" class="alert alert-danger">
              <i class="bi bi-exclamation-triangle me-2"></i>{{ errorEntry }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditEntryModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="updateEntry" :disabled="updatingEntry">
              <span v-if="updatingEntry" class="spinner-border spinner-border-sm me-2"></span>
              {{ updatingEntry ? 'Guardando...' : 'Guardar' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para confirmar eliminar publicación -->
    <div v-if="showDeleteEntryModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">
              <i class="bi bi-exclamation-triangle me-2"></i>Eliminar Publicación
            </h5>
            <button type="button" class="btn-close btn-close-white" @click="closeDeleteEntryModal"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que quieres eliminar esta publicación?</p>
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-circle me-2"></i>
              Esta acción es permanente y no se puede deshacer.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeDeleteEntryModal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteEntry" :disabled="deletingEntry">
              <span v-if="deletingEntry" class="spinner-border spinner-border-sm me-2"></span>
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
import CommunityRepository from '@/repositories/CommunityRepository';

export default {
  name: 'AdminCommunitiesView',
  setup() {
    const loading = ref(false);
    const updating = ref(false);
    const deleting = ref(false);
    const loadingEntries = ref(false);
    const updatingEntry = ref(false);
    const deletingEntry = ref(false);

    const communities = ref([]);
    const searchQuery = ref('');
    const error = ref('');
    const errorEntry = ref('');

    const showEditModal = ref(false);
    const showDeleteModal = ref(false);
    const showEntriesModal = ref(false);
    const showEditEntryModal = ref(false);
    const showDeleteEntryModal = ref(false);

    const editingCommunity = ref({ id: null, name: '', creationReason: '', description: '' });
    const communityToDelete = ref(null);
    const selectedCommunity = ref(null);
    const entries = ref([]);
    const editingEntry = ref({ id: null, title: '', content: '' });
    const entryToDelete = ref(null);

    const filteredCommunities = computed(() => {
      if (!searchQuery.value.trim()) {
        return communities.value;
      }
      const query = searchQuery.value.toLowerCase();
      return communities.value.filter(c =>
        c.name.toLowerCase().includes(query) ||
        c.creationReason.toLowerCase().includes(query) ||
        (c.description && c.description.toLowerCase().includes(query))
      );
    });

    const loadCommunities = async () => {
      loading.value = true;
      try {
        communities.value = await CommunityRepository.findAll();
        console.log('✅ Comunidades cargadas:', communities.value.length);
      } catch (err) {
        console.error('Error loading communities:', err);
        error.value = 'Error al cargar las comunidades';
      } finally {
        loading.value = false;
      }
    };

    const truncateText = (text, maxLength) => {
      if (!text || text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    };

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', { year: 'numeric', month: 'short', day: 'numeric' });
    };

    const openEditModal = (community) => {
      editingCommunity.value = {
        id: community.id,
        name: community.name,
        creationReason: community.creationReason,
        description: community.description || ''
      };
      showEditModal.value = true;
      error.value = '';
    };

    const closeEditModal = () => {
      showEditModal.value = false;
      editingCommunity.value = { id: null, name: '', creationReason: '', description: '' };
      error.value = '';
    };

    const updateCommunity = async () => {
      updating.value = true;
      error.value = '';
      try {
        await CommunityRepository.update(editingCommunity.value.id, {
          name: editingCommunity.value.name,
          creationReason: editingCommunity.value.creationReason,
          description: editingCommunity.value.description
        });
        closeEditModal();
        await loadCommunities();
        console.log('✅ Comunidad actualizada');
      } catch (err) {
        console.error('Error updating community:', err);
        error.value = 'Error al actualizar la comunidad';
      } finally {
        updating.value = false;
      }
    };

    const confirmDelete = (community) => {
      communityToDelete.value = community;
      showDeleteModal.value = true;
    };

    const closeDeleteModal = () => {
      showDeleteModal.value = false;
      communityToDelete.value = null;
    };

    const deleteCommunity = async () => {
      if (!communityToDelete.value) return;
      deleting.value = true;
      try {
        await CommunityRepository.delete(communityToDelete.value.id);
        closeDeleteModal();
        await loadCommunities();
        console.log('✅ Comunidad eliminada');
      } catch (err) {
        console.error('Error deleting community:', err);
        alert('Error al eliminar la comunidad');
      } finally {
        deleting.value = false;
      }
    };

    const viewCommunityEntries = async (community) => {
      selectedCommunity.value = community;
      showEntriesModal.value = true;
      loadingEntries.value = true;
      try {
        entries.value = await CommunityRepository.getEntries(community.id);
        console.log('✅ Publicaciones cargadas:', entries.value.length);
      } catch (err) {
        console.error('Error loading entries:', err);
        entries.value = [];
      } finally {
        loadingEntries.value = false;
      }
    };

    const closeEntriesModal = () => {
      showEntriesModal.value = false;
      selectedCommunity.value = null;
      entries.value = [];
    };

    const openEditEntryModal = (entry) => {
      editingEntry.value = {
        id: entry.id,
        title: entry.title,
        content: entry.content
      };
      showEditEntryModal.value = true;
      errorEntry.value = '';
    };

    const closeEditEntryModal = () => {
      showEditEntryModal.value = false;
      editingEntry.value = { id: null, title: '', content: '' };
      errorEntry.value = '';
    };

    const updateEntry = async () => {
      updatingEntry.value = true;
      errorEntry.value = '';
      try {
        await CommunityRepository.updateEntry(
          selectedCommunity.value.id,
          editingEntry.value.id,
          {
            title: editingEntry.value.title,
            content: editingEntry.value.content
          }
        );
        closeEditEntryModal();
        await viewCommunityEntries(selectedCommunity.value);
        console.log('✅ Publicación actualizada');
      } catch (err) {
        console.error('Error updating entry:', err);
        errorEntry.value = 'Error al actualizar la publicación';
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
        await CommunityRepository.deleteEntry(
          selectedCommunity.value.id,
          entryToDelete.value.id
        );
        closeDeleteEntryModal();
        await viewCommunityEntries(selectedCommunity.value);
        console.log('✅ Publicación eliminada');
      } catch (err) {
        console.error('Error deleting entry:', err);
        alert('Error al eliminar la publicación');
      } finally {
        deletingEntry.value = false;
      }
    };

    onMounted(() => {
      loadCommunities();
    });

    return {
      loading,
      updating,
      deleting,
      loadingEntries,
      updatingEntry,
      deletingEntry,
      communities,
      searchQuery,
      error,
      errorEntry,
      filteredCommunities,
      showEditModal,
      showDeleteModal,
      showEntriesModal,
      showEditEntryModal,
      showDeleteEntryModal,
      editingCommunity,
      communityToDelete,
      selectedCommunity,
      entries,
      editingEntry,
      entryToDelete,
      truncateText,
      formatDate,
      openEditModal,
      closeEditModal,
      updateCommunity,
      confirmDelete,
      closeDeleteModal,
      deleteCommunity,
      viewCommunityEntries,
      closeEntriesModal,
      openEditEntryModal,
      closeEditEntryModal,
      updateEntry,
      confirmDeleteEntry,
      closeDeleteEntryModal,
      deleteEntry
    };
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 10px;
  transition: box-shadow 0.2s;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.modal.show {
  display: block;
}
</style>

