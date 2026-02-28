<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>
        <i class="bi bi-people-fill me-2"></i>Gestión de Usuarios
      </h2>
      <button @click="goBack" class="btn btn-outline-warning">
        <i class="bi bi-house-door me-1"></i>Panel de Administración
      </button>
    </div>

    <div class="alert alert-info mb-4">
      <i class="bi bi-info-circle me-2"></i>
      Como administrador, puedes ver, activar, desactivar y eliminar usuarios del sistema.
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
            placeholder="Buscar usuarios por nombre o email..."
            v-model="searchQuery"
          >
        </div>
      </div>
      <div class="col-md-6 text-end">
        <div class="btn-group" role="group">
          <button
            type="button"
            class="btn"
            :class="filterStatus === 'all' ? 'btn-primary' : 'btn-outline-primary'"
            @click="filterStatus = 'all'"
          >
            Todos ({{ users.length }})
          </button>
          <button
            type="button"
            class="btn"
            :class="filterStatus === 'active' ? 'btn-success' : 'btn-outline-success'"
            @click="filterStatus = 'active'"
          >
            Activos ({{ activeUsers.length }})
          </button>
          <button
            type="button"
            class="btn"
            :class="filterStatus === 'inactive' ? 'btn-danger' : 'btn-outline-danger'"
            @click="filterStatus = 'inactive'"
          >
            Inactivos ({{ inactiveUsers.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- Cargando -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-3 text-muted">Cargando usuarios...</p>
    </div>

    <!-- Tabla de usuarios -->
    <div v-else-if="filteredUsers.length > 0" class="card shadow-sm">
      <div class="table-responsive">
        <table class="table table-hover mb-0">
          <thead class="table-light">
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td>{{ user.id }}</td>
              <td>
                <i class="bi bi-person-circle me-2 text-muted"></i>
                {{ user.name }}
              </td>
              <td>{{ user.email }}</td>
              <td>
                <span :class="['badge', user.authority === 'ADMIN' ? 'bg-danger' : 'bg-primary']">
                  <i :class="user.authority === 'ADMIN' ? 'bi bi-shield-fill-check' : 'bi bi-person-fill'" class="me-1"></i>
                  {{ user.authority === 'ADMIN' ? 'Administrador' : 'Usuario' }}
                </span>
              </td>
              <td>
                <span :class="['badge', user.active ? 'bg-success' : 'bg-secondary']">
                  <i :class="user.active ? 'bi bi-check-circle-fill' : 'bi bi-x-circle-fill'" class="me-1"></i>
                  {{ user.active ? 'Activo' : 'Inactivo' }}
                </span>
              </td>
              <td>
                <div class="d-flex gap-2">
                  <!-- Botón Ver Hábitos -->
                  <button
                    class="btn btn-sm btn-outline-info"
                    @click="viewUserHabits(user)"
                    title="Ver hábitos del usuario"
                  >
                    <i class="bi bi-list-check me-1"></i>Ver Hábitos
                  </button>

                  <!-- Botones de gestión -->
                  <div class="btn-group btn-group-sm" role="group">
                    <button
                      v-if="!user.active"
                      class="btn btn-outline-success"
                      @click="activateUser(user)"
                      :disabled="user.authority === 'ADMIN'"
                      title="Activar usuario"
                    >
                      <i class="bi bi-check-circle me-1"></i>Activar
                    </button>
                    <button
                      v-if="user.active"
                      class="btn btn-outline-warning"
                      @click="deactivateUser(user)"
                      :disabled="user.authority === 'ADMIN' || user.id === currentUserId"
                      title="Desactivar usuario"
                    >
                      <i class="bi bi-x-circle me-1"></i>Desactivar
                    </button>
                    <button
                      class="btn btn-outline-danger"
                      @click="confirmDelete(user)"
                      :disabled="user.authority === 'ADMIN' || user.id === currentUserId"
                      title="Eliminar usuario"
                    >
                      <i class="bi bi-trash me-1"></i>Eliminar
                    </button>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Sin usuarios -->
    <div v-else class="alert alert-warning">
      <i class="bi bi-exclamation-triangle me-2"></i>
      No se encontraron usuarios con los filtros aplicados.
    </div>

    <!-- Estadísticas -->
    <div class="row mt-4">
      <div class="col-md-3">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <i class="bi bi-people-fill text-primary fs-1"></i>
            <h3 class="mt-2 mb-0">{{ users.length }}</h3>
            <p class="text-muted mb-0">Total Usuarios</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <i class="bi bi-check-circle-fill text-success fs-1"></i>
            <h3 class="mt-2 mb-0">{{ activeUsers.length }}</h3>
            <p class="text-muted mb-0">Activos</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <i class="bi bi-x-circle-fill text-danger fs-1"></i>
            <h3 class="mt-2 mb-0">{{ inactiveUsers.length }}</h3>
            <p class="text-muted mb-0">Inactivos</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <i class="bi bi-shield-fill-check text-warning fs-1"></i>
            <h3 class="mt-2 mb-0">{{ adminUsers.length }}</h3>
            <p class="text-muted mb-0">Administradores</p>
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
            <p>¿Estás seguro de que quieres eliminar al usuario <strong>{{ userToDelete?.name }}</strong>?</p>
            <div class="alert alert-danger">
              <i class="bi bi-exclamation-circle me-2"></i>
              <strong>¡Atención!</strong> Esta acción no se puede deshacer. Se eliminarán todos los datos asociados al usuario.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeDeleteModal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="deleteUser" :disabled="deleting">
              <span v-if="deleting" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-trash me-1"></i>
              {{ deleting ? 'Eliminando...' : 'Eliminar Usuario' }}
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
import HTTP from '@/common/http';
import { notify } from '@/common/notifications';

export default {
  name: 'AdminUsersView',
  setup() {
    const router = useRouter();
    const users = ref([]);
    const loading = ref(true);
    const searchQuery = ref('');
    const filterStatus = ref('all');
    const showDeleteModal = ref(false);
    const userToDelete = ref(null);
    const deleting = ref(false);
    const currentUserId = ref(localStorage.getItem('userId'));

    const activeUsers = computed(() => users.value.filter(u => u.active));
    const inactiveUsers = computed(() => users.value.filter(u => !u.active));
    const adminUsers = computed(() => users.value.filter(u => u.authority === 'ADMIN'));

    const filteredUsers = computed(() => {
      let filtered = users.value;

      // Filtrar por estado
      if (filterStatus.value === 'active') {
        filtered = filtered.filter(u => u.active);
      } else if (filterStatus.value === 'inactive') {
        filtered = filtered.filter(u => !u.active);
      }

      // Filtrar por búsqueda
      if (searchQuery.value.trim()) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(u =>
          u.name.toLowerCase().includes(query) ||
          u.email.toLowerCase().includes(query)
        );
      }

      return filtered;
    });

    const loadUsers = async () => {
      loading.value = true;
      try {
        const response = await HTTP.get('/users');
        users.value = response.data;
        console.log('✅ Usuarios cargados:', users.value.length);
      } catch (error) {
        console.error('Error loading users:', error);
        notify.error('Error al cargar los usuarios. Por favor, intenta de nuevo.');
      } finally {
        loading.value = false;
      }
    };

    const activateUser = async (user) => {
      try {
        await HTTP.put(`/users/${user.id}/active`);
        user.active = true;
        notify.success(`Usuario ${user.name} activado correctamente`);
      } catch (error) {
        console.error('Error activating user:', error);
        notify.error('Error al activar el usuario. Por favor, intenta de nuevo.');
      }
    };

    const deactivateUser = async (user) => {
      try {
        await HTTP.delete(`/users/${user.id}/active`);
        user.active = false;
        notify.success(`Usuario ${user.name} desactivado correctamente`);
      } catch (error) {
        console.error('Error deactivating user:', error);
        notify.error('Error al desactivar el usuario. Por favor, intenta de nuevo.');
      }
    };

    const confirmDelete = (user) => {
      userToDelete.value = user;
      showDeleteModal.value = true;
    };

    const closeDeleteModal = () => {
      showDeleteModal.value = false;
      userToDelete.value = null;
    };

    const deleteUser = async () => {
      if (!userToDelete.value) return;

      deleting.value = true;
      try {
        await HTTP.delete(`/users/${userToDelete.value.id}`);
        users.value = users.value.filter(u => u.id !== userToDelete.value.id);
        notify.success(`Usuario ${userToDelete.value.name} eliminado correctamente`);
        closeDeleteModal();
      } catch (error) {
        console.error('Error deleting user:', error);
        notify.error('Error al eliminar el usuario. Por favor, intenta de nuevo.');
      } finally {
        deleting.value = false;
      }
    };

    const goBack = () => {
      router.push('/admin/dashboard');
    };

    const viewUserHabits = (user) => {
      router.push({
        name: 'AdminViewUserHabits',
        params: { userId: user.id },
        query: {
          name: user.name,
          email: user.email
        }
      });
    };

    onMounted(() => {
      loadUsers();
    });

    return {
      users,
      loading,
      searchQuery,
      filterStatus,
      showDeleteModal,
      userToDelete,
      deleting,
      currentUserId,
      activeUsers,
      inactiveUsers,
      adminUsers,
      filteredUsers,
      activateUser,
      deactivateUser,
      confirmDelete,
      closeDeleteModal,
      deleteUser,
      goBack,
      viewUserHabits
    };
  }
};
</script>

<style scoped>
.table-hover tbody tr:hover {
  background-color: #f8f9fa;
}

.btn-group-sm .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.card {
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-2px);
}

.modal.show {
  display: block;
}
</style>

