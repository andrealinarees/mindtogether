<template>
  <div class="container mt-4">
    <div class="row mb-4">
      <div class="col-12 d-flex justify-content-between align-items-center">
        <h1>
          <i class="bi bi-people me-2"></i>Comunidades
        </h1>
        <button class="btn btn-primary" @click="showCreateModal = true">
          <i class="bi bi-plus-circle me-1"></i>Crear Comunidad
        </button>
      </div>
    </div>

    <!-- Tabs para Todas las comunidades y Mis comunidades -->
    <ul class="nav nav-tabs mb-4">
      <li class="nav-item">
        <a
          class="nav-link"
          :class="{ active: activeTab === 'all' }"
          @click="activeTab = 'all'; loadCommunities()"
          href="#"
        >
          <i class="bi bi-globe me-1"></i>Todas las comunidades
        </a>
      </li>
      <li class="nav-item">
        <a
          class="nav-link"
          :class="{ active: activeTab === 'my' }"
          @click="activeTab = 'my'; loadMyCommunities()"
          href="#"
        >
          <i class="bi bi-bookmark-heart me-1"></i>Mis comunidades
        </a>
      </li>
    </ul>

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
            @input="handleSearch"
          >
        </div>
      </div>
    </div>

    <!-- Cargando -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Lista de Comunidades -->
    <div v-else-if="displayedCommunities.length > 0" class="row">
      <div v-for="community in displayedCommunities" :key="community.id" class="col-md-6 col-lg-4 mb-4">
        <div class="card h-100 shadow-sm community-card" @click="viewCommunity(community.id)">
          <div class="card-body">
            <h5 class="card-title">
              <i class="bi bi-people-fill me-2 text-primary"></i>
              {{ community.name }}
            </h5>
            <p class="card-text text-muted small mb-2">
              <strong>Motivo:</strong> {{ community.creationReason }}
            </p>
            <p class="card-text">{{ truncateText(community.description, 100) }}</p>
            <div class="d-flex justify-content-between align-items-center mt-3">
              <small class="text-muted">
                <i class="bi bi-person me-1"></i>
                {{ community.memberCount || 0 }} miembros
              </small>
              <small class="text-muted">
                <i class="bi bi-chat-dots me-1"></i>
                {{ community.entryCount || 0 }} publicaciones
              </small>
            </div>
          </div>
          <div class="card-footer bg-transparent border-0">
            <!-- Usuario no es miembro -->
            <div v-if="!isMember(community.id)" class="d-flex gap-2">
              <button
                class="btn btn-outline-secondary btn-sm flex-fill"
                @click.stop="viewCommunity(community.id)"
              >
                <i class="bi bi-eye me-1"></i>Ver detalle
              </button>
              <button
                class="btn btn-primary btn-sm flex-fill"
                @click.stop="joinCommunity(community.id)"
              >
                <i class="bi bi-box-arrow-in-right me-1"></i>Unirse
              </button>
            </div>

            <!-- Usuario es creador -->
            <div v-else-if="isCreator(community)" class="d-flex gap-2">
              <button
                class="btn btn-outline-secondary btn-sm"
                @click.stop="viewCommunity(community.id)"
              >
                <i class="bi bi-eye me-1"></i>Ver detalle
              </button>
              <button
                class="btn btn-success btn-sm"
                @click.stop="enterCommunity(community.id)"
              >
                <i class="bi bi-door-open me-1"></i>Entrar
              </button>
              <button
                class="btn btn-warning btn-sm"
                @click.stop="openEditModal(community)"
              >
                <i class="bi bi-pencil me-1"></i>Editar
              </button>
              <button
                class="btn btn-danger btn-sm"
                @click.stop="confirmDelete(community)"
              >
                <i class="bi bi-trash me-1"></i>Eliminar
              </button>
            </div>

            <!-- Usuario es miembro pero no creador -->
            <div v-else class="d-flex gap-2">
              <button
                class="btn btn-outline-secondary btn-sm"
                @click.stop="viewCommunity(community.id)"
              >
                <i class="bi bi-eye me-1"></i>Ver detalle
              </button>
              <button
                class="btn btn-success btn-sm"
                @click.stop="enterCommunity(community.id)"
              >
                <i class="bi bi-door-open me-1"></i>Entrar
              </button>
              <button
                class="btn btn-outline-danger btn-sm"
                @click.stop="confirmLeave(community)"
              >
                <i class="bi bi-box-arrow-right me-1"></i>Salir
              </button>
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
            <h3 class="mt-4 text-muted">
              {{ activeTab === 'all' ? 'No hay comunidades disponibles' : 'Aún no te has unido a ninguna comunidad' }}
            </h3>
            <p class="text-muted">
              {{ activeTab === 'all' ? 'Sé el primero en crear una comunidad' : 'Explora comunidades y únete a las que te interesen' }}
            </p>
            <button class="btn btn-primary mt-3" @click="activeTab === 'all' ? showCreateModal = true : activeTab = 'all'">
              <i :class="activeTab === 'all' ? 'bi bi-plus-circle me-1' : 'bi bi-search me-1'"></i>
              {{ activeTab === 'all' ? 'Crear Comunidad' : 'Explorar Comunidades' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para crear comunidad -->
    <div v-if="showCreateModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-plus-circle me-2"></i>Crear Nueva Comunidad
            </h5>
            <button type="button" class="btn-close" @click="closeCreateModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="createCommunity">
              <div class="mb-3">
                <label for="communityName" class="form-label">Nombre <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="communityName"
                  v-model="newCommunity.name"
                  placeholder="Ej: Manejo de la Ansiedad"
                  maxlength="100"
                  required
                >
                <small class="text-muted">{{ newCommunity.name.length }}/100 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="creationReason" class="form-label">Motivo de Creación <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="creationReason"
                  v-model="newCommunity.creationReason"
                  placeholder="Ej: Apoyarnos mutuamente para gestionar la ansiedad en el día a día"
                  maxlength="500"
                  required
                >
                <small class="text-muted">{{ newCommunity.creationReason.length }}/500 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="description" class="form-label">Descripción</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="newCommunity.description"
                  rows="4"
                  placeholder="Describe la comunidad, objetivos, etc."
                  maxlength="2000"
                ></textarea>
                <small class="text-muted">{{ newCommunity.description.length }}/2000 caracteres</small>
              </div>

              <div v-if="error" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeCreateModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="createCommunity" :disabled="!isFormValid || creating">
              <span v-if="creating" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-check-circle me-1"></i>
              {{ creating ? 'Creando...' : 'Crear Comunidad' }}
            </button>
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
                <label for="editCommunityName" class="form-label">Nombre <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="editCommunityName"
                  v-model="editingCommunity.name"
                  placeholder="Ej: Manejo de la Ansiedad"
                  maxlength="100"
                  required
                >
                <small class="text-muted">{{ editingCommunity.name.length }}/100 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="editCreationReason" class="form-label">Motivo de Creación <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  id="editCreationReason"
                  v-model="editingCommunity.creationReason"
                  placeholder="Ej: Apoyarnos mutuamente para gestionar la ansiedad en el día a día"
                  maxlength="500"
                  required
                >
                <small class="text-muted">{{ editingCommunity.creationReason.length }}/500 caracteres</small>
              </div>

              <div class="mb-3">
                <label for="editDescription" class="form-label">Descripción</label>
                <textarea
                  class="form-control"
                  id="editDescription"
                  v-model="editingCommunity.description"
                  rows="4"
                  placeholder="Describe la comunidad, objetivos, etc."
                  maxlength="2000"
                ></textarea>
                <small class="text-muted">{{ editingCommunity.description.length }}/2000 caracteres</small>
              </div>

              <div v-if="error" class="alert alert-danger">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeEditModal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="updateCommunity" :disabled="!isEditFormValid || updating">
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
            <p class="mb-3">¿Estás seguro de que quieres eliminar la comunidad <strong>{{ communityToDelete?.name }}</strong>?</p>
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-circle me-2"></i>
              Esta acción es permanente y eliminará todos los datos asociados, incluyendo publicaciones y miembros.
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

    <!-- Modal de confirmación para salir de comunidad -->
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
            <p class="mb-3">¿Estás seguro de que quieres salir de la comunidad <strong>{{ communityToLeave?.name }}</strong>?</p>
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

    <!-- Modal de modo anónimo al unirse -->
    <div v-if="showJoinAnonymousModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">
              <i class="bi bi-incognito me-2"></i>¿Cómo quieres participar?
            </h5>
            <button type="button" class="btn-close btn-close-white" @click="closeJoinAnonymousModal"></button>
          </div>
          <div class="modal-body text-center">
            <i class="bi bi-shield-lock display-3 text-info mb-3 d-block"></i>
            <h5>Elige tu modo de participación en <strong>{{ communityToJoin?.name }}</strong></h5>
            <p class="text-muted">
              Puedes unirte de forma anónima. Los demás miembros verán tu nombre como <strong>"Anónimo"</strong> y no podrán identificarte.
            </p>
            <div class="alert alert-light border">
              <i class="bi bi-info-circle me-2"></i>Puedes cambiar esta opción en cualquier momento desde dentro de la comunidad.
            </div>
          </div>
          <div class="modal-footer justify-content-center">
            <button type="button" class="btn btn-outline-primary btn-lg" @click="confirmJoin(false)">
              <i class="bi bi-person-fill me-2"></i>Con mi nombre
            </button>
            <button type="button" class="btn btn-info btn-lg text-white" @click="confirmJoin(true)">
              <i class="bi bi-incognito me-2"></i>Anónimo
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import CommunityRepository from '@/repositories/CommunityRepository';
import { notify } from '@/common/notifications';

export default {
  name: 'CommunityView',
  setup() {
    const router = useRouter();
    const activeTab = ref('all');
    const searchQuery = ref('');
    const loading = ref(false);
    const creating = ref(false);
    const updating = ref(false);
    const deleting = ref(false);
    const leaving = ref(false);
    const showCreateModal = ref(false);
    const showEditModal = ref(false);
    const showDeleteModal = ref(false);
    const showLeaveModal = ref(false);
    const error = ref('');

    const allCommunities = ref([]);
    const myCommunities = ref([]);
    const myCommunitiesIds = ref(new Set());
    const currentUserId = ref(parseInt(localStorage.getItem('userId')));

    const newCommunity = ref({
      name: '',
      creationReason: '',
      description: ''
    });

    const editingCommunity = ref({
      id: null,
      name: '',
      creationReason: '',
      description: ''
    });

    const communityToDelete = ref(null);
    const communityToLeave = ref(null);
    const communityToJoin = ref(null);
    const showJoinAnonymousModal = ref(false);

    const displayedCommunities = computed(() => {
      const communities = activeTab.value === 'all' ? allCommunities.value : myCommunities.value;

      if (!searchQuery.value.trim()) {
        return communities;
      }

      const query = searchQuery.value.toLowerCase();
      return communities.filter(c =>
        c.name.toLowerCase().includes(query) ||
        c.creationReason.toLowerCase().includes(query) ||
        (c.description && c.description.toLowerCase().includes(query))
      );
    });

    const isFormValid = computed(() => {
      return newCommunity.value.name.trim().length > 0 &&
             newCommunity.value.creationReason.trim().length > 0;
    });

    const isEditFormValid = computed(() => {
      return editingCommunity.value.name.trim().length > 0 &&
             editingCommunity.value.creationReason.trim().length > 0;
    });

    const loadCommunities = async () => {
      loading.value = true;
      try {
        allCommunities.value = await CommunityRepository.findAll();
      } catch (err) {
        console.error('Error loading communities:', err);
        error.value = 'Error al cargar las comunidades';
      } finally {
        loading.value = false;
      }
    };

    const loadMyCommunities = async () => {
      loading.value = true;
      try {
        myCommunities.value = await CommunityRepository.findMyCommunities();
        myCommunitiesIds.value = new Set(myCommunities.value.map(c => c.id));

        // Establecer isMember=true e isCreator para todas mis comunidades
        myCommunities.value.forEach(community => {
          community.isMember = true;
          community.isCreator = parseInt(community.creatorUserId) === currentUserId.value;
        });

        console.log('🔄 Mis comunidades recargadas:', myCommunities.value.length);
        console.log('🆔 IDs en Set:', Array.from(myCommunitiesIds.value));
      } catch (err) {
        console.error('Error loading my communities:', err);
        error.value = 'Error al cargar tus comunidades';
      } finally {
        loading.value = false;
      }
    };

    const isMember = (communityId) => {
      // Buscar la comunidad en la lista actual
      const communities = activeTab.value === 'all' ? allCommunities.value : myCommunities.value;
      const community = communities.find(c => c.id === communityId);

      if (!community) {
        return false;
      }

      // Si tiene el campo isMember del backend, usarlo
      if (community.hasOwnProperty('isMember')) {
        console.log(`🔍 isMember(${communityId}) from backend:`, community.isMember);
        return community.isMember;
      }

      // Fallback: verificar si está en el Set
      const result = myCommunitiesIds.value.has(communityId);
      console.log(`🔍 isMember(${communityId}) from Set:`, result);
      return result;
    };

    const isCreator = (community) => {
      // Si tiene el campo isCreator del backend, usarlo
      if (community.hasOwnProperty('isCreator')) {
        console.log(`🔍 isCreator for community ${community.name} from backend:`, community.isCreator);
        return community.isCreator;
      }

      // Fallback: comparar con el ID del usuario actual
      const creatorId = parseInt(community.creatorUserId);
      const result = creatorId === currentUserId.value;
      console.log(`🔍 isCreator for community ${community.name}:`, result,
                  `(creatorId: ${creatorId}, currentUserId: ${currentUserId.value})`);
      return result;
    };

    const handleSearch = () => {
      // El filtrado se hace automáticamente con computed
    };

    const truncateText = (text, maxLength) => {
      if (!text || text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    };

    const viewCommunity = (id) => {
      router.push({ name: 'CommunityDetail', params: { id } });
    };

    const enterCommunity = (id) => {
      router.push({ name: 'CommunityEnter', params: { id } });
    };

    const joinCommunity = (communityId) => {
      const community = allCommunities.value.find(c => c.id === communityId);
      communityToJoin.value = community || { id: communityId, name: 'esta comunidad' };
      showJoinAnonymousModal.value = true;
    };

    const closeJoinAnonymousModal = () => {
      showJoinAnonymousModal.value = false;
      communityToJoin.value = null;
    };

    const confirmJoin = async (anonymous) => {
      if (!communityToJoin.value) return;
      const communityId = communityToJoin.value.id;
      closeJoinAnonymousModal();

      try {
        await CommunityRepository.join(communityId, anonymous);

        // PRIMERO: Añadir al Set de IDs INMEDIATAMENTE
        myCommunitiesIds.value.add(communityId);

        // SEGUNDO: Actualizar isMember en allCommunities
        const community = allCommunities.value.find(c => c.id === communityId);
        if (community) {
          community.isMember = true;
          // Añadir a myCommunities si no está
          if (!myCommunities.value.find(c => c.id === communityId)) {
            myCommunities.value.push(community);
          }
        }

        // Guardar preferencia de anonimato en localStorage para CommunityEnterView
        localStorage.setItem(`anonymous_community_${communityId}`, JSON.stringify(anonymous));

        // TERCERO: Recargar desde el servidor para sincronizar
        await loadMyCommunities();
        await loadCommunities();

        console.log('✅ Te has unido a la comunidad exitosamente (anónimo:', anonymous, ')');
        console.log('📝 IDs actualizados:', Array.from(myCommunitiesIds.value));
        console.log('📋 Mis comunidades:', myCommunities.value.length);

      } catch (err) {
        console.error('Error joining community:', err);
        if (err.response && err.response.status === 409) {
          notify.warning('Ya eres miembro de esta comunidad');
        } else {
          notify.error('Error al unirse a la comunidad. Por favor, inténtalo de nuevo.');
        }
      }
    };

    const createCommunity = async () => {
      if (!isFormValid.value) return;

      creating.value = true;
      error.value = '';

      try {
        const created = await CommunityRepository.create(newCommunity.value);

        // Inicializar contadores si no vienen del backend
        if (!created.memberCount) {
          created.memberCount = 1; // El creador es el primer miembro
        }
        if (!created.entryCount) {
          created.entryCount = 0;
        }

        // Asegurar que el creatorUserId esté en la comunidad creada
        if (!created.creatorUserId) {
          created.creatorUserId = currentUserId.value;
        }

        // Establecer isMember e isCreator
        created.isMember = true;
        created.isCreator = true;

        // PRIMERO: Añadir al Set de IDs INMEDIATAMENTE
        myCommunitiesIds.value.add(created.id);

        // SEGUNDO: Añadir a ambas listas INMEDIATAMENTE
        allCommunities.value.unshift(created);
        myCommunities.value.unshift(created);

        // Cerrar modal y resetear formulario
        closeCreateModal();

        // TERCERO: Recargar desde el servidor para sincronizar
        await loadMyCommunities();
        await loadCommunities();

        // Mostrar mensaje de éxito
        console.log('✅ Comunidad creada exitosamente:', created.name);
        console.log('📝 IDs actualizados:', Array.from(myCommunitiesIds.value));
        console.log('📋 Mis comunidades:', myCommunities.value.length);
        console.log('🔍 Es creador?', isCreator(created));

      } catch (err) {
        console.error('Error creating community:', err);
        if (err.response && err.response.status === 409) {
          error.value = 'Ya existe una comunidad con ese nombre';
        } else {
          error.value = 'Error al crear la comunidad. Por favor, inténtalo de nuevo.';
        }
      } finally {
        creating.value = false;
      }
    };

    const updateCommunity = async () => {
      if (!isEditFormValid.value) return;

      updating.value = true;
      error.value = '';

      try {
        const updated = await CommunityRepository.update(editingCommunity.value.id, {
          name: editingCommunity.value.name,
          creationReason: editingCommunity.value.creationReason,
          description: editingCommunity.value.description
        });

        // Actualizar en la lista de todas las comunidades
        const allIndex = allCommunities.value.findIndex(c => c.id === updated.id);
        if (allIndex !== -1) {
          allCommunities.value[allIndex] = { ...allCommunities.value[allIndex], ...updated };
        }

        // Actualizar en mis comunidades
        const myIndex = myCommunities.value.findIndex(c => c.id === updated.id);
        if (myIndex !== -1) {
          myCommunities.value[myIndex] = { ...myCommunities.value[myIndex], ...updated };
        }

        closeEditModal();
        console.log('✅ Comunidad actualizada exitosamente');
      } catch (err) {
        console.error('Error updating community:', err);
        if (err.response && err.response.status === 403) {
          error.value = 'No tienes permisos para editar esta comunidad';
        } else if (err.response && err.response.status === 409) {
          error.value = 'Ya existe una comunidad con ese nombre';
        } else {
          error.value = 'Error al actualizar la comunidad. Por favor, inténtalo de nuevo.';
        }
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

        // Eliminar de ambas listas
        allCommunities.value = allCommunities.value.filter(c => c.id !== communityToDelete.value.id);
        myCommunities.value = myCommunities.value.filter(c => c.id !== communityToDelete.value.id);
        myCommunitiesIds.value.delete(communityToDelete.value.id);

        closeDeleteModal();
        console.log('✅ Comunidad eliminada exitosamente');
      } catch (err) {
        console.error('Error deleting community:', err);
        if (err.response && err.response.status === 403) {
          notify.error('Solo el creador puede eliminar esta comunidad');
        } else {
          notify.error('Error al eliminar la comunidad. Por favor, inténtalo de nuevo.');
        }
      } finally {
        deleting.value = false;
      }
    };

    const confirmLeave = (community) => {
      communityToLeave.value = community;
      showLeaveModal.value = true;
    };

    const closeLeaveModal = () => {
      showLeaveModal.value = false;
      communityToLeave.value = null;
    };

    const leaveCommunity = async () => {
      if (!communityToLeave.value) return;

      leaving.value = true;

      try {
        await CommunityRepository.leave(communityToLeave.value.id);

        // PRIMERO: Eliminar del Set INMEDIATAMENTE
        myCommunitiesIds.value.delete(communityToLeave.value.id);

        // SEGUNDO: Actualizar isMember en allCommunities
        const communityInAll = allCommunities.value.find(c => c.id === communityToLeave.value.id);
        if (communityInAll) {
          communityInAll.isMember = false;
        }

        // TERCERO: Eliminar de myCommunities INMEDIATAMENTE
        myCommunities.value = myCommunities.value.filter(c => c.id !== communityToLeave.value.id);

        closeLeaveModal();

        // CUARTO: Recargar desde el servidor para sincronizar
        await loadMyCommunities();
        await loadCommunities();

        console.log('✅ Has salido de la comunidad exitosamente');
        console.log('📝 IDs actualizados:', Array.from(myCommunitiesIds.value));

        // Cambiar a la pestaña "Todas las comunidades" si estamos en "Mis comunidades"
        if (activeTab.value === 'my') {
          activeTab.value = 'all';
        }
      } catch (err) {
        console.error('Error leaving community:', err);
        if (err.response && err.response.status === 403) {
          notify.error('No puedes salir de esta comunidad. El creador no puede abandonar su propia comunidad.');
        } else {
          notify.error('Error al salir de la comunidad. Por favor, inténtalo de nuevo.');
        }
      } finally {
        leaving.value = false;
      }
    };

    const closeCreateModal = () => {
      showCreateModal.value = false;
      newCommunity.value = {
        name: '',
        creationReason: '',
        description: ''
      };
      error.value = '';
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
      editingCommunity.value = {
        id: null,
        name: '',
        creationReason: '',
        description: ''
      };
      error.value = '';
    };

    onMounted(async () => {
      // Cargar primero mis comunidades para tener los IDs antes de cargar todas
      await loadMyCommunities();
      await loadCommunities();

      console.log('🚀 Vista de comunidades iniciada');
      console.log('👤 Usuario actual:', currentUserId.value);
      console.log('📝 Mis comunidades IDs:', Array.from(myCommunitiesIds.value));
    });

    return {
      activeTab,
      searchQuery,
      loading,
      creating,
      updating,
      deleting,
      leaving,
      showCreateModal,
      showEditModal,
      showDeleteModal,
      showLeaveModal,
      error,
      displayedCommunities,
      newCommunity,
      editingCommunity,
      communityToDelete,
      communityToLeave,
      communityToJoin,
      showJoinAnonymousModal,
      isFormValid,
      isEditFormValid,
      loadCommunities,
      loadMyCommunities,
      isMember,
      isCreator,
      handleSearch,
      truncateText,
      viewCommunity,
      enterCommunity,
      joinCommunity,
      confirmJoin,
      closeJoinAnonymousModal,
      createCommunity,
      closeCreateModal,
      openEditModal,
      closeEditModal,
      updateCommunity,
      confirmDelete,
      closeDeleteModal,
      deleteCommunity,
      confirmLeave,
      closeLeaveModal,
      leaveCommunity
    };
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 15px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.community-card {
  cursor: pointer;
}

.community-card:hover {
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

.modal.show {
  display: block;
}

.card-footer {
  padding: 0.75rem 1.25rem;
}

.card-footer .btn {
  font-size: 0.875rem;
  font-weight: 500;
  padding: 0.5rem 0.75rem;
}

.card-footer .gap-2 {
  gap: 0.5rem;
}
</style>
