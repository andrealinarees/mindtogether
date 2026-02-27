<template>
  <div class="container mt-4">
    <div class="row mb-4">
      <div class="col-12">
        <h1 class="display-4">
          <i class="bi bi-shield-fill-check me-3 text-warning"></i>
          Panel de Administración
        </h1>
        <p class="lead text-muted">Bienvenido al panel de control de administrador</p>
      </div>
    </div>

    <!-- Información del administrador -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow-sm border-warning border-2">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <i class="bi bi-person-circle fs-1 text-warning me-3"></i>
              <div>
                <h5 class="mb-1">{{ userName }}</h5>
                <p class="text-muted mb-0">
                  <span class="badge bg-danger">
                    <i class="bi bi-shield-fill-check me-1"></i>Administrador
                  </span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Estadísticas Generales -->
    <div class="row mb-4">
      <div class="col-12">
        <h3 class="mb-3">
          <i class="bi bi-bar-chart-fill me-2"></i>Estadísticas Generales del Sistema
        </h3>
      </div>
    </div>

    <div v-if="loadingStats" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando estadísticas...</span>
      </div>
    </div>

    <div v-else class="row g-4 mb-4">
      <!-- Estadísticas de Usuarios -->
      <div class="col-md-6 col-xl-4">
        <div class="card shadow-sm h-100 border-primary">
          <div class="card-body">
            <h5 class="card-title text-primary">
              <i class="bi bi-people-fill me-2"></i>Usuarios
            </h5>
            <div class="mt-3">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span>Total de Usuarios:</span>
                <strong class="fs-5">{{ userStats.totalUsers || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span><i class="bi bi-check-circle text-success me-1"></i>Activos:</span>
                <strong class="text-success">{{ userStats.activeUsers || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span><i class="bi bi-x-circle text-danger me-1"></i>Inactivos:</span>
                <strong class="text-danger">{{ userStats.inactiveUsers || 0 }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Estadísticas de Hábitos -->
      <div class="col-md-6 col-xl-4">
        <div class="card shadow-sm h-100 border-success">
          <div class="card-body">
            <h5 class="card-title text-success">
              <i class="bi bi-list-check me-2"></i>Hábitos
            </h5>
            <div class="mt-3">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span>Total de Hábitos:</span>
                <strong class="fs-5">{{ habitStats.totalHabits || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span><i class="bi bi-play-circle text-success me-1"></i>Activos:</span>
                <strong class="text-success">{{ habitStats.activeHabits || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span><i class="bi bi-check-circle text-info me-1"></i>Completados:</span>
                <strong class="text-info">{{ habitStats.completedHabits || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span><i class="bi bi-calendar-check text-primary me-1"></i>Completados Hoy:</span>
                <strong class="text-primary">{{ habitStats.completionsToday || 0 }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Estadísticas de Comunidades -->
      <div class="col-md-12 col-xl-4">
        <div class="card shadow-sm h-100 border-info">
          <div class="card-body">
            <h5 class="card-title text-info">
              <i class="bi bi-people me-2"></i>Comunidades
            </h5>
            <div class="mt-3">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span>Total de Comunidades:</span>
                <strong class="fs-5">{{ communityStats.totalCommunities || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span><i class="bi bi-person-plus text-success me-1"></i>Total Miembros:</span>
                <strong class="text-success">{{ communityStats.totalMembers || 0 }}</strong>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span><i class="bi bi-chat-dots text-primary me-1"></i>Publicaciones:</span>
                <strong class="text-primary">{{ communityStats.totalEntries || 0 }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Menú de opciones de administración -->
    <div class="row mb-4">
      <div class="col-12">
        <h3 class="mb-3">
          <i class="bi bi-gear-fill me-2"></i>Opciones de Administración
        </h3>
      </div>
    </div>

    <div class="row g-4">
      <!-- Gestión de Usuarios -->
      <div class="col-md-6 col-lg-6">
        <div class="card h-100 shadow-sm hover-card" @click="goToUsers">
          <div class="card-body text-center p-4">
            <div class="mb-3">
              <i class="bi bi-people-fill display-1 text-primary"></i>
            </div>
            <h4 class="card-title mb-3">Gestión de Usuarios</h4>
            <p class="card-text text-muted">
              Ver, activar, desactivar y eliminar usuarios del sistema.
            </p>
            <button class="btn btn-primary mt-3">
              <i class="bi bi-arrow-right-circle me-2"></i>Ver Usuarios
            </button>
          </div>
        </div>
      </div>

      <!-- Gestión de Comunidades -->
      <div class="col-md-6 col-lg-6">
        <div class="card h-100 shadow-sm hover-card" @click="goToCommunities">
          <div class="card-body text-center p-4">
            <div class="mb-3">
              <i class="bi bi-people display-1 text-info"></i>
            </div>
            <h4 class="card-title mb-3">Gestión de Comunidades</h4>
            <p class="card-text text-muted">
              Supervisar, editar o eliminar comunidades y publicaciones.
            </p>
            <button class="btn btn-info mt-3">
              <i class="bi bi-arrow-right-circle me-2"></i>Ver Comunidades
            </button>
          </div>
        </div>
      </div>
    </div>


    <!-- Información adicional -->
    <div class="row mt-5">
      <div class="col-12">
        <div class="card shadow-sm">
          <div class="card-body">
            <h5 class="card-title">
              <i class="bi bi-info-circle me-2"></i>Información Importante
            </h5>
            <ul class="mb-0">
              <li class="mb-2">
                <strong>Rol Exclusivo:</strong> Como administrador, tu función es gestionar el sistema, no participar en hábitos o comunidades.
              </li>
              <li class="mb-2">
                <strong>Gestión de Usuarios:</strong> Puedes activar, desactivar o eliminar usuarios normales (no otros administradores).
              </li>
              <li class="mb-2">
                <strong>Moderación:</strong> Supervisa y modera comunidades y publicaciones inapropiadas.
              </li>
              <li class="mb-2">
                <strong>Responsabilidad:</strong> Las acciones de eliminación son permanentes. Úsalas con precaución.
              </li>
              <li>
                <strong>Acceso Limitado:</strong> No tienes acceso a crear hábitos, metas ni participar en comunidades como usuario normal.
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import HTTP from '@/common/http';

export default {
  name: 'AdminDashboardView',
  setup() {
    const router = useRouter();
    const userName = ref('');
    const loadingStats = ref(true);

    const userStats = ref({
      totalUsers: 0,
      activeUsers: 0,
      inactiveUsers: 0
    });

    const habitStats = ref({
      totalHabits: 0,
      activeHabits: 0,
      completedHabits: 0,
      completionsToday: 0
    });

    const communityStats = ref({
      totalCommunities: 0,
      totalMembers: 0,
      totalEntries: 0
    });

    const loadStats = async () => {
      try {
        loadingStats.value = true;

        // Cargar estadísticas de usuarios
        const usersResponse = await HTTP.get('/users/stats');
        userStats.value = usersResponse.data;

        // Cargar estadísticas de hábitos
        const habitsResponse = await HTTP.get('/habits/stats');
        habitStats.value = habitsResponse.data;

        // Cargar estadísticas de comunidades
        const communitiesResponse = await HTTP.get('/communities/stats');
        communityStats.value = communitiesResponse.data;

      } catch (error) {
        console.error('Error loading stats:', error);
      } finally {
        loadingStats.value = false;
      }
    };

    const goToUsers = () => {
      router.push('/admin/users');
    };

    const goToCommunities = () => {
      router.push('/admin/communities');
    };

    onMounted(() => {
      // Obtener el nombre del usuario del localStorage o store
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      userName.value = user.name || 'Administrador';

      // Cargar estadísticas
      loadStats();
    });

    return {
      userName,
      loadingStats,
      userStats,
      habitStats,
      communityStats,
      goToUsers,
      goToCommunities
    };
  }
};
</script>

<style scoped>
.hover-card {
  transition: all 0.3s;
  cursor: pointer;
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15) !important;
}

.card {
  border: none;
  border-radius: 15px;
}

.display-1 {
  font-size: 4rem;
}

@media (max-width: 768px) {
  .display-1 {
    font-size: 3rem;
  }
}
</style>

