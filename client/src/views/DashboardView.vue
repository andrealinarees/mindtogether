<template>
  <div class="container mt-4">
    <!-- Saludo personalizado -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow-sm bg-gradient-primary text-white">
          <div class="card-body p-4">
            <h2 class="mb-3">
              <i class="bi bi-heart-pulse me-2"></i>¡Hola, {{ userName }}!
            </h2>
            <p class="lead mb-0">Bienvenido a tu espacio de bienestar mental</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Estadísticas Generales -->
    <div class="row mb-4">
      <!-- Spinner de carga -->
      <div v-if="loading" class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
          <span class="visually-hidden">Cargando estadísticas...</span>
        </div>
        <p class="mt-3 text-muted">Cargando tus estadísticas...</p>
      </div>

      <!-- Tarjetas de estadísticas -->
      <template v-else>
        <div class="col-md-3 col-sm-6 mb-3">
          <router-link to="/journal" class="text-decoration-none">
            <div class="card text-center shadow-sm hover-card stat-card">
              <div class="card-body py-4">
                <i class="bi bi-journal-text display-4 text-primary mb-3"></i>
                <h2 class="stat-number text-primary mb-2">{{ journalEntries }}</h2>
                <p class="text-muted mb-0 stat-label">Entradas del Diario</p>
              </div>
            </div>
          </router-link>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
          <router-link to="/mental-health-goals" class="text-decoration-none">
            <div class="card text-center shadow-sm hover-card stat-card">
              <div class="card-body py-4">
                <i class="bi bi-trophy display-4 text-warning mb-3"></i>
                <h2 class="stat-number text-warning mb-2">{{ activeGoals }}</h2>
                <p class="text-muted mb-0 stat-label">Metas Activas</p>
              </div>
            </div>
          </router-link>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
          <router-link to="/wellness" class="text-decoration-none">
            <div class="card text-center shadow-sm hover-card stat-card">
              <div class="card-body py-4">
                <i class="bi bi-flower1 display-4 text-success mb-3"></i>
                <h2 class="stat-number text-success mb-2">{{ wellnessPractices }}</h2>
                <p class="text-muted mb-0 stat-label">Prácticas de Bienestar</p>
              </div>
            </div>
          </router-link>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
          <router-link to="/support-circles" class="text-decoration-none">
            <div class="card text-center shadow-sm hover-card stat-card">
              <div class="card-body py-4">
                <i class="bi bi-people display-4 text-info mb-3"></i>
                <h2 class="stat-number text-info mb-2">{{ supportCircles }}</h2>
                <p class="text-muted mb-0 stat-label">Círculos de Apoyo</p>
              </div>
            </div>
          </router-link>
        </div>
      </template>
    </div>

    <!-- Recordatorios del día -->
    <div class="row mb-4">
      <div class="col-lg-6 mb-3">
        <div class="card shadow-sm h-100">
          <div class="card-header bg-primary text-white">
            <i class="bi bi-calendar-day me-2"></i><strong>Momentos de Hoy</strong>
          </div>
          <div class="card-body">
            <div class="list-group list-group-flush">
              <div class="list-group-item d-flex justify-content-between align-items-center border-0 px-0">
                <div>
                  <i class="bi bi-sunrise text-warning me-2 fs-5"></i>
                  <strong>Meditación matutina</strong>
                </div>
                <span class="badge bg-success rounded-pill">10 min</span>
              </div>
              <div class="list-group-item d-flex justify-content-between align-items-center border-0 px-0">
                <div>
                  <i class="bi bi-journal-text text-primary me-2 fs-5"></i>
                  <strong>Escribir en el diario</strong>
                </div>
                <span class="badge bg-info rounded-pill">Pendiente</span>
              </div>
              <div class="list-group-item d-flex justify-content-between align-items-center border-0 px-0">
                <div>
                  <i class="bi bi-moon-stars text-info me-2 fs-5"></i>
                  <strong>Reflexión nocturna</strong>
                </div>
                <span class="badge bg-secondary rounded-pill">Programado</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Estado de ánimo reciente -->
      <div class="col-lg-6 mb-3">
        <div class="card shadow-sm h-100">
          <div class="card-header bg-info text-white">
            <i class="bi bi-emoji-smile me-2"></i><strong>¿Cómo te sientes hoy?</strong>
          </div>
          <div class="card-body">
            <!-- Caritas interactivas -->
            <div v-if="!moodSelected" class="text-center">
              <p class="mb-3">Selecciona tu estado de ánimo:</p>
              <div class="mood-selector d-flex justify-content-around mb-3">
                <div
                  v-for="mood in moods"
                  :key="mood.value"
                  class="mood-option"
                  @click="selectMood(mood)"
                  :title="mood.label"
                >
                  <span class="mood-emoji">{{ mood.emoji }}</span>
                  <p class="mood-label">{{ mood.label }}</p>
                </div>
              </div>
            </div>

            <!-- Mensaje motivacional después de seleccionar -->
            <div v-else class="text-center">
              <div class="selected-mood mb-3">
                <span class="mood-emoji-large">{{ selectedMood.emoji }}</span>
                <h5 class="mt-2">{{ selectedMood.label }}</h5>
              </div>
              <div class="motivational-message alert" :class="selectedMood.alertClass">
                <p class="mb-0">{{ selectedMood.message }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recursos de ayuda -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow-sm border-danger">
          <div class="card-header bg-danger text-white">
            <i class="bi bi-heart-pulse me-2"></i>Recursos de Emergencia
          </div>
          <div class="card-body">
            <p class="mb-2">Si necesitas ayuda inmediata, estos son algunos recursos:</p>
            <ul class="list-unstyled mb-0">
              <li><i class="bi bi-telephone text-danger me-2"></i><strong>Teléfono de la Esperanza:</strong> 717 003 717</li>
              <li><i class="bi bi-telephone text-danger me-2"></i><strong>Línea Nacional (España):</strong> 024</li>
              <li><i class="bi bi-globe text-primary me-2"></i><a href="https://www.telefonodelaesperanza.org" target="_blank">www.telefonodelaesperanza.org</a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { getStore } from '@/common/store'
import JournalRepository from '@/repositories/JournalRepository'
import MentalHealthGoalRepository from '@/repositories/MentalHealthGoalRepository'
import WellnessPracticeRepository from '@/repositories/WellnessPracticeRepository'
import SupportCircleRepository from '@/repositories/SupportCircleRepository'
import { notify } from '@/common/notifications'

export default {
  name: 'DashboardView',
  setup() {
    const store = getStore()

    // Contadores inicializados en 0
    const journalEntries = ref(0)
    const activeGoals = ref(0)
    const wellnessPractices = ref(0)
    const supportCircles = ref(0)
    const loading = ref(true)

    // Estado de ánimo
    const moodSelected = ref(false)
    const selectedMood = ref(null)

    const moods = ref([
      {
        emoji: '😢',
        label: 'Triste',
        value: 'sad',
        message: 'Es normal sentirse así a veces. Recuerda que los días difíciles son temporales. Estás haciendo lo mejor que puedes, y eso es suficiente.',
        alertClass: 'alert-info'
      },
      {
        emoji: '😟',
        label: 'Regular',
        value: 'okay',
        message: 'Está bien no estar al 100% todos los días. Tómate un momento para ti, practica la respiración consciente y recuerda que mañana es un nuevo día.',
        alertClass: 'alert-warning'
      },
      {
        emoji: '😊',
        label: 'Bien',
        value: 'good',
        message: '¡Qué bien que te sientas así! Sigue cuidando tu bienestar con pequeñas acciones diarias. Estás en el camino correcto.',
        alertClass: 'alert-success'
      },
      {
        emoji: '😄',
        label: 'Excelente',
        value: 'excellent',
        message: '¡Fantástico! Tu energía positiva es inspiradora. Disfruta este momento y comparte tu alegría con quienes te rodean.',
        alertClass: 'alert-primary'
      }
    ])

    const userName = computed(() => {
      return store.state.user.name || store.state.user.login || 'Usuario'
    })

    const userLogin = computed(() => {
      return store.state.user.login || 'guest'
    })

    const getMoodStorageKey = () => {
      return `lastMoodDate_${userLogin.value}`
    }

    const selectMood = async (mood) => {
      // Verificar si ya se registró un estado de ánimo hoy
      const today = new Date().toISOString().split('T')[0]
      const lastMoodDate = localStorage.getItem(getMoodStorageKey())

      if (lastMoodDate === today) {
        // Ya se registró un estado de ánimo hoy
        notify.warning('Ya registraste tu estado de ánimo hoy. Solo puedes registrar uno por día.')
        return
      }

      try {
        // PRIMERO: Guardar en el diario
        const moodEntry = {
          date: today,
          mood: mood.value,
          moodLabel: mood.label,
          title: `Estado de ánimo: ${mood.label}`,
          content: `Hoy me siento ${mood.label.toLowerCase()}. ${mood.message}`,
          tags: ['estado-animo'],
          timestamp: new Date().toISOString()
        }

        // Guardar en el diario usando el repositorio
        await JournalRepository.create(moodEntry)

        // SEGUNDO: Actualizar el estado para mostrar la frase motivacional
        selectedMood.value = mood
        moodSelected.value = true

        // TERCERO: Guardar en localStorage para control de fecha (por usuario)
        localStorage.setItem(getMoodStorageKey(), today)

        // CUARTO: Incrementar el contador de entradas
        journalEntries.value++

        // QUINTO: Mostrar notificación de éxito
        notify.success('¡Estado de ánimo registrado en tu diario!')

      } catch (error) {
        console.error('Error al registrar estado de ánimo:', error)
        notify.error('Error al registrar tu estado de ánimo. Inténtalo de nuevo.')
      }
    }

    const loadStatistics = async () => {
      loading.value = true

      // Cargar todas las estadísticas en paralelo para mejor rendimiento
      const [journals, goals, practices, circles] = await Promise.allSettled([
        JournalRepository.findAll(),
        MentalHealthGoalRepository.findActive(),
        WellnessPracticeRepository.findAll(),
        SupportCircleRepository.findAll()
      ])

      // Procesar entradas del diario
      if (journals.status === 'fulfilled') {
        journalEntries.value = Array.isArray(journals.value) ? journals.value.length : 0
      } else {
        journalEntries.value = 0
      }

      // Procesar metas activas
      if (goals.status === 'fulfilled') {
        activeGoals.value = Array.isArray(goals.value) ? goals.value.length : 0
      } else {
        activeGoals.value = 0
      }

      // Procesar prácticas de bienestar
      if (practices.status === 'fulfilled') {
        wellnessPractices.value = Array.isArray(practices.value) ? practices.value.length : 0
      } else {
        wellnessPractices.value = 0
      }

      // Procesar círculos de apoyo
      if (circles.status === 'fulfilled') {
        supportCircles.value = Array.isArray(circles.value) ? circles.value.length : 0
      } else {
        supportCircles.value = 0
      }

      // Verificar si ya se registró un estado de ánimo hoy para este usuario
      const today = new Date().toISOString().split('T')[0]
      const lastMoodDate = localStorage.getItem(getMoodStorageKey())

      if (lastMoodDate === today) {
        // Buscar el mood registrado en el diario
        try {
          const entries = await JournalRepository.findByDate(today)
          const moodEntry = entries.find(e => e.tags && e.tags.includes('estado-animo'))
          
          if (moodEntry && moodEntry.mood) {
            const mood = moods.value.find(m => m.value === moodEntry.mood)
            if (mood) {
              selectedMood.value = mood
              moodSelected.value = true
            }
          }
        } catch (error) {
          console.log('No se pudo cargar el estado de ánimo del día')
        }
      }

      loading.value = false
    }

    onMounted(() => {
      loadStatistics()
    })

    return {
      userName,
      journalEntries,
      activeGoals,
      wellnessPractices,
      supportCircles,
      loading,
      moods,
      moodSelected,
      selectedMood,
      selectMood
    }
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 15px;
}

.card-header {
  border-radius: 15px 15px 0 0 !important;
  border: none;
  font-weight: 600;
}

.hover-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1) !important;
}

.display-4 {
  font-size: 3rem;
}

/* Estilos para tarjetas de estadísticas */
.stat-card {
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card .card-body {
  width: 100%;
}

.stat-number {
  font-size: 3rem;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  font-size: 0.95rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Animación de entrada para números */
@keyframes countUp {
  from {
    opacity: 0;
    transform: scale(0.5);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.stat-number {
  animation: countUp 0.6s ease-out;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.list-group-item {
  transition: all 0.2s;
  border: none !important;
  padding: 0.75rem 0;
}

.list-group-item:hover {
  background-color: transparent !important;
  transform: translateX(5px);
}

.progress-bar {
  transition: width 0.6s ease;
}

.btn {
  transition: all 0.2s;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

/* Estilos para el selector de estado de ánimo */
.mood-selector {
  gap: 15px;
}

.mood-option {
  cursor: pointer;
  padding: 15px;
  border-radius: 10px;
  transition: all 0.3s ease;
  text-align: center;
  flex: 1;
  background-color: #f8f9fa;
}

.mood-option:hover {
  transform: scale(1.1) translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
  background-color: #e9ecef;
}

.mood-emoji {
  font-size: 3rem;
  display: block;
  margin-bottom: 8px;
}

.mood-label {
  font-size: 0.9rem;
  font-weight: 500;
  margin: 0;
  color: #495057;
}

.mood-emoji-large {
  font-size: 5rem;
  display: block;
  animation: bounceIn 0.6s ease;
}

.selected-mood h5 {
  color: #495057;
  font-weight: 600;
  margin-top: 10px;
}

.motivational-message {
  border-radius: 10px;
  padding: 15px;
  margin: 15px 0;
  animation: fadeIn 0.5s ease;
}

.motivational-message p {
  font-size: 1rem;
  line-height: 1.6;
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
