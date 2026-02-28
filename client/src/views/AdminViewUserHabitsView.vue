<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>
        <i class="bi bi-list-check me-2"></i>Hábitos del Usuario
      </h2>
      <div>
        <button @click="goToAdminUsers" class="btn btn-outline-secondary me-2">
          <i class="bi bi-arrow-left me-1"></i>Usuarios
        </button>
        <button @click="goToAdminDashboard" class="btn btn-outline-warning">
          <i class="bi bi-house-door me-1"></i>Panel
        </button>
      </div>
    </div>

    <!-- Información del usuario -->
    <div class="card shadow-sm mb-4 border-info">
      <div class="card-body">
        <div class="d-flex align-items-center">
          <i class="bi bi-person-circle fs-1 text-info me-3"></i>
          <div>
            <h5 class="mb-1">{{ userName }}</h5>
            <p class="text-muted mb-0">
              <i class="bi bi-envelope me-1"></i>{{ userEmail }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="alert alert-info mb-4">
      <i class="bi bi-info-circle me-2"></i>
      Vista de solo lectura. Puedes ver los hábitos del usuario pero no modificarlos.
    </div>

    <!-- Cargando -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-3 text-muted">Cargando hábitos...</p>
    </div>

    <!-- Lista de hábitos -->
    <div v-else-if="habits.length > 0">
      <h4 class="mb-3">
        <i class="bi bi-clipboard-check me-2"></i>
        Hábitos Creados ({{ habits.length }})
      </h4>

      <div class="row g-3">
        <div v-for="habit in habits" :key="habit.id" class="col-md-6 col-lg-4">
          <div class="card h-100 shadow-sm habit-card">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title mb-0">{{ habit.name }}</h5>
                <span :class="['badge', habit.completed ? 'bg-success' : 'bg-warning']">
                  {{ habit.completed ? 'Completado' : 'En progreso' }}
                </span>
              </div>

              <p class="card-text text-muted small" v-if="habit.description">
                {{ truncateText(habit.description, 80) }}
              </p>

              <div class="mt-3">
                <p class="mb-1 small">
                  <i class="bi bi-calendar-event me-1 text-muted"></i>
                  <strong>Frecuencia:</strong> {{ translateFrequency(habit.frequency) }}
                </p>
                <p class="mb-1 small">
                  <i class="bi bi-geo-alt me-1 text-muted"></i>
                  <strong>Ubicación:</strong> {{ translateLocation(habit.location) }}
                </p>
                <p class="mb-1 small">
                  <i class="bi bi-calendar-range me-1 text-muted"></i>
                  {{ formatDate(habit.startDate) }} - {{ formatDate(habit.endDate) }}
                </p>
              </div>

              <div class="mt-3">
                <button
                  class="btn btn-sm btn-outline-info w-100"
                  @click="viewDetail(habit.id)"
                >
                  <i class="bi bi-eye me-1"></i>Ver Detalle
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sin hábitos -->
    <div v-else class="text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-4 text-muted">Este usuario no tiene hábitos creados</h3>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import HTTP from '@/common/http';
import { notify } from '@/common/notifications';

export default {
  name: 'AdminViewUserHabitsView',
  setup() {
    const router = useRouter();
    const route = useRoute();

    const habits = ref([]);
    const loading = ref(true);
    const userId = ref(route.params.userId);
    const userName = ref(route.query.name || 'Usuario');
    const userEmail = ref(route.query.email || '');

    const loadUserHabits = async () => {
      loading.value = true;
      try {
        // Usar query parameter para obtener hábitos del usuario
        const response = await HTTP.get(`/habits?userId=${userId.value}`);
        habits.value = response.data;
        console.log('✅ Hábitos cargados:', habits.value.length);
      } catch (error) {
        console.error('Error loading habits:', error);
        notify.error('Error al cargar los hábitos del usuario.');
      } finally {
        loading.value = false;
      }
    };

    const translateFrequency = (frequency) => {
      const map = {
        'DAILY': 'Diaria',
        'WEEKLY': 'Semanal',
        'MONTHLY': 'Mensual'
      };
      return map[frequency] || frequency;
    };

    const translateLocation = (location) => {
      const map = {
        'INDOOR': 'Interior',
        'OUTDOOR': 'Exterior'
      };
      return map[location] || location;
    };

    const formatDate = (date) => {
      if (!date) return 'N/A';
      return new Date(date).toLocaleDateString('es-ES');
    };

    const truncateText = (text, maxLength) => {
      if (!text || text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    };

    const viewDetail = (habitId) => {
      // Navegar a la vista de detalle del hábito (solo lectura para administradores)
      router.push({ name: 'AdminHabitDetail', params: { id: habitId } });
    };

    const goToAdminUsers = () => {
      router.push('/admin/users');
    };

    const goToAdminDashboard = () => {
      router.push('/admin/dashboard');
    };

    onMounted(() => {
      loadUserHabits();
    });

    return {
      habits,
      loading,
      userId,
      userName,
      userEmail,
      translateFrequency,
      translateLocation,
      formatDate,
      truncateText,
      viewDetail,
      goToAdminUsers,
      goToAdminDashboard
    };
  }
};
</script>

<style scoped>
.habit-card {
  transition: all 0.3s;
  border: none;
  border-radius: 10px;
}

.habit-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15) !important;
}

.card {
  border-radius: 10px;
}
</style>

