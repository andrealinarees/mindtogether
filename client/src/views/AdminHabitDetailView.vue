<template>
  <div class="container mt-4">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-2 text-muted">Cargando detalle del hábito...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger" role="alert">
      <i class="bi bi-exclamation-triangle me-2"></i>
      {{ error }}
    </div>

    <div v-else-if="habit" class="habit-detail">
      <!-- Header con título y botón volver -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2>
            <i :class="getLocationIcon()" class="me-2"></i>
            {{ habit.name }}
          </h2>
          <span class="badge" :class="getStatusBadgeClass()">
            {{ getStatusText() }}
          </span>
        </div>
        <button class="btn btn-outline-secondary" @click="goBack">
          <i class="bi bi-arrow-left me-1"></i>Volver
        </button>
      </div>

      <!-- Tarjeta de información del hábito -->
      <div class="card shadow-sm mb-4">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-info-circle me-2"></i>Información del Hábito
          </h5>

          <!-- Descripción -->
          <div class="info-row mb-3">
            <div class="info-label">
              <i class="bi bi-card-text me-2 text-primary"></i>
              <strong>Descripción:</strong>
            </div>
            <div class="info-value">{{ habit.description || 'Sin descripción' }}</div>
          </div>

          <!-- Usuario -->
          <div class="info-row mb-3">
            <div class="info-label">
              <i class="bi bi-person me-2 text-primary"></i>
              <strong>Usuario:</strong>
            </div>
            <div class="info-value">ID: {{ habit.userId }}</div>
          </div>

          <!-- Frecuencia -->
          <div class="info-row mb-3">
            <div class="info-label">
              <i class="bi bi-arrow-repeat me-2 text-primary"></i>
              <strong>Frecuencia:</strong>
            </div>
            <div class="info-value">
              <span class="badge bg-info">{{ getFrequencyText() }}</span>
            </div>
          </div>

          <!-- Fechas -->
          <div class="info-row mb-3">
            <div class="info-label">
              <i class="bi bi-calendar-range me-2 text-primary"></i>
              <strong>Período:</strong>
            </div>
            <div class="info-value">
              Desde <strong>{{ formatDate(habit.startDate) }}</strong>
              hasta <strong>{{ formatDate(habit.endDate) }}</strong>
            </div>
          </div>

          <!-- Hora -->
          <div class="info-row mb-3" v-if="habit.habitTime">
            <div class="info-label">
              <i class="bi bi-clock me-2 text-primary"></i>
              <strong>Hora:</strong>
            </div>
            <div class="info-value">{{ habit.habitTime }}</div>
          </div>

          <!-- Ubicación -->
          <div class="info-row mb-3">
            <div class="info-label">
              <i :class="getLocationIcon()" class="me-2 text-primary"></i>
              <strong>Ubicación:</strong>
            </div>
            <div class="info-value">
              <span class="badge" :class="habit.location === 'INTERIOR' ? 'bg-secondary' : 'bg-success'">
                {{ habit.location === 'INTERIOR' ? 'Interior' : 'Exterior' }}
              </span>
            </div>
          </div>

          <!-- Categoría -->
          <div class="info-row mb-3" v-if="habit.category">
            <div class="info-label">
              <i class="bi bi-tag me-2 text-primary"></i>
              <strong>Categoría:</strong>
            </div>
            <div class="info-value">
              <span class="badge" :style="{ backgroundColor: habit.category.color }">
                <i :class="habit.category.icon" class="me-1"></i>
                {{ habit.category.name }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Estadísticas -->
      <div class="row mb-4">
        <div class="col-md-4">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <i class="bi bi-graph-up-arrow display-4 text-primary mb-3"></i>
              <h5 class="card-title">Progreso</h5>
              <h2 class="text-primary mb-0">{{ habit.progressPercentage?.toFixed(1) || 0 }}%</h2>
              <small class="text-muted">completado</small>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <i class="bi bi-fire display-4 text-warning mb-3"></i>
              <h5 class="card-title">Racha Actual</h5>
              <h2 class="text-warning mb-0">{{ habit.currentStreak || 0 }}</h2>
              <small class="text-muted">{{ habit.frequency === 'DAILY' ? 'días' : 'semanas' }}</small>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <i class="bi bi-trophy display-4 text-success mb-3"></i>
              <h5 class="card-title">Mejor Racha</h5>
              <h2 class="text-success mb-0">{{ habit.longestStreak || 0 }}</h2>
              <small class="text-muted">{{ habit.frequency === 'DAILY' ? 'días' : 'semanas' }}</small>
            </div>
          </div>
        </div>
      </div>

      <!-- Comentarios (solo lectura) -->
      <div class="card shadow-sm mb-4" v-if="habit.comments && habit.comments.length > 0">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-chat-dots me-2"></i>Comentarios del Usuario
          </h5>
          <div class="comments-list">
            <div
              v-for="comment in habit.comments"
              :key="comment.id"
              class="comment-item mb-3 p-3 border rounded"
            >
              <div class="d-flex justify-content-between align-items-start mb-2">
                <small class="text-muted">
                  <i class="bi bi-calendar me-1"></i>
                  {{ formatDateTime(comment.createdAt) }}
                </small>
              </div>
              <p class="mb-0">{{ comment.text }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Metas (solo lectura) -->
      <div class="card shadow-sm" v-if="goals && goals.length > 0">
        <div class="card-body">
          <h5 class="card-title mb-4">
            <i class="bi bi-bullseye me-2"></i>Metas Asociadas
          </h5>
          <div class="goals-list">
            <div
              v-for="goal in goals"
              :key="goal.id"
              class="goal-item mb-3 p-3 border rounded"
            >
              <div class="d-flex justify-content-between align-items-start">
                <div>
                  <h6 class="mb-2">
                    <i class="bi bi-flag me-2"></i>{{ goal.name }}
                  </h6>
                  <p class="mb-2 text-muted small">{{ goal.description }}</p>
                  <div class="d-flex gap-3 flex-wrap">
                    <small>
                      <i class="bi bi-calendar-check me-1"></i>
                      Fecha límite: <strong>{{ formatDate(goal.targetDate) }}</strong>
                    </small>
                    <small v-if="goal.targetValue">
                      <i class="bi bi-bullseye me-1"></i>
                      Objetivo: <strong>{{ goal.targetValue }} {{ goal.unit }}</strong>
                    </small>
                  </div>
                </div>
                <span class="badge" :class="getGoalStatusBadgeClass(goal.status)">
                  {{ getGoalStatusText(goal.status) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import HabitRepository from '@/repositories/HabitRepository';

export default {
  name: 'AdminHabitDetailView',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const habitId = route.params.id;

    const habit = ref(null);
    const goals = ref([]);
    const loading = ref(true);
    const error = ref('');

    const loadHabit = async () => {
      try {
        loading.value = true;
        error.value = '';

        // Obtener coordenadas del usuario para recomendación del tiempo (opcional)
        let latitude = null;
        let longitude = null;

        const userData = JSON.parse(localStorage.getItem('user') || '{}');
        if (userData.city) {
          const cityCoords = getCityCoordinates(userData.city);
          latitude = cityCoords.latitude;
          longitude = cityCoords.longitude;
        }

        const response = await HabitRepository.getHabitDetail(habitId, latitude, longitude);

        if (response && response.habit) {
          habit.value = response.habit;
          goals.value = response.goals || [];
        }
      } catch (err) {
        console.error('Error cargando hábito:', err);
        error.value = 'Error al cargar el detalle del hábito';
      } finally {
        loading.value = false;
      }
    };

    const getCityCoordinates = (city) => {
      const coordinates = {
        'A Coruña': { latitude: 43.3623, longitude: -8.4115 },
        'Vigo': { latitude: 42.2328, longitude: -8.7226 },
        'Santiago de Compostela': { latitude: 42.8782, longitude: -8.5448 },
        'Lugo': { latitude: 43.0096, longitude: -7.5567 },
        'Ourense': { latitude: 42.3406, longitude: -7.8636 },
        'Pontevedra': { latitude: 42.4319, longitude: -8.6444 },
        'Ferrol': { latitude: 43.4833, longitude: -8.2333 }
      };
      return coordinates[city] || { latitude: 43.3623, longitude: -8.4115 };
    };

    const goBack = () => {
      router.back();
    };

    const getLocationIcon = () => {
      return habit.value?.location === 'INTERIOR'
        ? 'bi bi-house-door'
        : 'bi bi-tree';
    };

    const getStatusBadgeClass = () => {
      const status = habit.value?.status;
      return {
        'bg-success': status === 'ACTIVE',
        'bg-secondary': status === 'COMPLETED',
        'bg-danger': status === 'PAUSED'
      };
    };

    const getStatusText = () => {
      const status = habit.value?.status;
      const statusMap = {
        'ACTIVE': 'Activo',
        'COMPLETED': 'Completado',
        'PAUSED': 'Pausado'
      };
      return statusMap[status] || status;
    };

    const getFrequencyText = () => {
      const frequency = habit.value?.frequency;
      const frequencyMap = {
        'DAILY': 'Diaria',
        'WEEKLY': 'Semanal'
      };
      return frequencyMap[frequency] || frequency;
    };

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return '';
      const date = new Date(dateTimeString);
      return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    };

    const getGoalStatusBadgeClass = (status) => {
      return {
        'bg-warning': status === 'EN_PROGRESO',
        'bg-success': status === 'COMPLETADA',
        'bg-danger': status === 'FUERA_DE_PLAZO'
      };
    };

    const getGoalStatusText = (status) => {
      const statusMap = {
        'EN_PROGRESO': 'En Progreso',
        'COMPLETADA': 'Completada',
        'FUERA_DE_PLAZO': 'Fuera de Plazo'
      };
      return statusMap[status] || status;
    };

    onMounted(() => {
      loadHabit();
    });

    return {
      habit,
      goals,
      loading,
      error,
      goBack,
      getLocationIcon,
      getStatusBadgeClass,
      getStatusText,
      getFrequencyText,
      formatDate,
      formatDateTime,
      getGoalStatusBadgeClass,
      getGoalStatusText
    };
  }
};
</script>

<style scoped>
.habit-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.info-row {
  padding: 0.75rem 0;
  border-bottom: 1px solid #e9ecef;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  color: #495057;
}

.info-value {
  padding-left: 1.75rem;
  color: #212529;
}

.comment-item {
  background-color: #f8f9fa;
  transition: all 0.2s;
}

.comment-item:hover {
  background-color: #e9ecef;
}

.goal-item {
  background-color: #f8f9fa;
  transition: all 0.2s;
}

.goal-item:hover {
  background-color: #e9ecef;
}

.card {
  border: none;
  border-radius: 12px;
}

.card-body {
  padding: 1.5rem;
}

.badge {
  font-size: 0.875rem;
  padding: 0.5rem 1rem;
}

@media (max-width: 768px) {
  .d-flex.gap-2 {
    flex-direction: column;
    width: 100%;
  }

  .d-flex.gap-2 button {
    width: 100%;
  }
}
</style>

