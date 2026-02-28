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
      <div class="col-md-3 mb-3">
        <router-link to="/journal" class="text-decoration-none">
          <div class="card text-center shadow-sm hover-card">
            <div class="card-body">
              <i class="bi bi-journal-text display-4 text-primary"></i>
              <h3 class="mt-3">{{ journalEntries }}</h3>
              <p class="text-muted">Entradas del Diario</p>
            </div>
          </div>
        </router-link>
      </div>
      <div class="col-md-3 mb-3">
        <router-link to="/mental-health-goals" class="text-decoration-none">
          <div class="card text-center shadow-sm hover-card">
            <div class="card-body">
              <i class="bi bi-trophy display-4 text-warning"></i>
              <h3 class="mt-3">{{ activeGoals }}</h3>
              <p class="text-muted">Metas Activas</p>
            </div>
          </div>
        </router-link>
      </div>
      <div class="col-md-3 mb-3">
        <router-link to="/wellness" class="text-decoration-none">
          <div class="card text-center shadow-sm hover-card">
            <div class="card-body">
              <i class="bi bi-flower1 display-4 text-success"></i>
              <h3 class="mt-3">{{ wellnessPractices }}</h3>
              <p class="text-muted">Prácticas de Bienestar</p>
            </div>
          </div>
        </router-link>
      </div>
      <div class="col-md-3 mb-3">
        <router-link to="/support-circles" class="text-decoration-none">
          <div class="card text-center shadow-sm hover-card">
            <div class="card-body">
              <i class="bi bi-people display-4 text-info"></i>
              <h3 class="mt-3">{{ supportCircles }}</h3>
              <p class="text-muted">Círculos de Apoyo</p>
            </div>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Recordatorios del día -->
    <div class="row mb-4">
      <div class="col-lg-6 mb-3">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            <i class="bi bi-calendar-day me-2"></i>Momentos de Hoy
          </div>
          <div class="card-body">
            <div class="list-group list-group-flush">
              <div class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <i class="bi bi-sunrise text-warning me-2"></i>
                  <strong>Meditación matutina</strong>
                </div>
                <span class="badge bg-success">10 min</span>
              </div>
              <div class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <i class="bi bi-journal-text text-primary me-2"></i>
                  <strong>Escribir en el diario</strong>
                </div>
                <span class="badge bg-info">Pendiente</span>
              </div>
              <div class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <i class="bi bi-moon-stars text-info me-2"></i>
                  <strong>Reflexión nocturna</strong>
                </div>
                <span class="badge bg-secondary">Programado</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Estado de ánimo reciente -->
      <div class="col-lg-6 mb-3">
        <div class="card shadow-sm">
          <div class="card-header bg-info text-white">
            <i class="bi bi-emoji-smile me-2"></i>¿Cómo te sientes hoy?
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
              <button class="btn btn-sm btn-outline-secondary mt-2" @click="resetMood">
                <i class="bi bi-arrow-clockwise me-1"></i>Cambiar estado
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Acciones rápidas -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow-sm">
          <div class="card-header">
            <i class="bi bi-lightning me-2"></i>Acciones Rápidas
          </div>
          <div class="card-body">
            <div class="row">
              <div class="col-md-4 mb-2">
                <router-link to="/journal/new" class="btn btn-outline-primary w-100">
                  <i class="bi bi-journal-plus me-2"></i>Nueva Entrada
                </router-link>
              </div>
              <div class="col-md-4 mb-2">
                <router-link to="/mental-health-goals/new" class="btn btn-outline-warning w-100">
                  <i class="bi bi-trophy me-2"></i>Crear Meta
                </router-link>
              </div>
              <div class="col-md-4 mb-2">
                <router-link to="/journal/chatbot" class="btn btn-outline-info w-100">
                  <i class="bi bi-chat-dots me-2"></i>Hablar con IA
                </router-link>
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

    const selectMood = async (mood) => {
      // Verificar si ya se registró un estado de ánimo hoy
      const today = new Date().toISOString().split('T')[0]
      const lastMoodDate = localStorage.getItem('lastMoodDate')

      if (lastMoodDate === today) {
        // Ya se registró un estado de ánimo hoy
        notify.warning('Ya registraste tu estado de ánimo hoy. Solo puedes registrar uno por día.')
        return
      }

      // PRIMERO: Mostrar el estado de ánimo seleccionado
      selectedMood.value = mood
      moodSelected.value = true

      // SEGUNDO: Guardar en localStorage inmediatamente
      localStorage.setItem('lastMoodDate', today)

      const moodEntry = {
        date: today,
        mood: mood.value,
        moodLabel: mood.label,
        title: `Estado de ánimo del día`,
        content: `Hoy me siento ${mood.label.toLowerCase()}`,
        tags: ['estado-animo'],
        timestamp: new Date().toISOString()
      }

      // Guardar en localStorage como backup
      const savedMoods = JSON.parse(localStorage.getItem('moodHistory') || '[]')
      savedMoods.push(moodEntry)
      localStorage.setItem('moodHistory', JSON.stringify(savedMoods))

      // TERCERO: Incrementar el contador de entradas
      journalEntries.value++

      // CUARTO: Intentar guardar en el backend (en segundo plano)
      try {
        await JournalRepository.create(moodEntry)
      } catch (backendError) {
        // Backend no disponible, dato ya guardado localmente
      }
    }

    const resetMood = () => {
      const today = new Date().toISOString().split('T')[0]
      const lastMoodDate = localStorage.getItem('lastMoodDate')

      if (lastMoodDate === today) {
        // Limpiar el estado pero mantener la fecha para que pueda cambiar el de hoy
        localStorage.removeItem('lastMoodDate')
      }

      moodSelected.value = false
      selectedMood.value = null
    }

    const loadStatistics = async () => {
      loading.value = true

      // Cargar entradas del diario
      try {
        const journals = await JournalRepository.findAll()
        journalEntries.value = Array.isArray(journals) ? journals.length : 0
      } catch (error) {
        journalEntries.value = 0
      }

      // Cargar metas activas
      try {
        const goals = await MentalHealthGoalRepository.findActive()
        activeGoals.value = Array.isArray(goals) ? goals.length : 0
      } catch (error) {
        activeGoals.value = 0
      }

      // Cargar prácticas de bienestar
      try {
        const practices = await WellnessPracticeRepository.findAll()
        wellnessPractices.value = Array.isArray(practices) ? practices.length : 0
      } catch (error) {
        wellnessPractices.value = 0
      }

      // Cargar círculos de apoyo (comunidades del usuario)
      try {
        const circles = await SupportCircleRepository.findAll()
        supportCircles.value = Array.isArray(circles) ? circles.length : 0
      } catch (error) {
        supportCircles.value = 0
      }

      // Verificar si ya se registró un estado de ánimo hoy
      const today = new Date().toISOString().split('T')[0]
      const lastMoodDate = localStorage.getItem('lastMoodDate')

      if (lastMoodDate === today) {
        // Buscar el mood registrado en el backend
        try {
          const entries = await JournalRepository.findByDate(today)
          if (entries && entries.length > 0 && entries[0].mood) {
            const todayEntry = entries[0]
            const mood = moods.value.find(m => m.value === todayEntry.mood)
            if (mood) {
              selectedMood.value = mood
              moodSelected.value = true
            }
          }
        } catch (error) {
          // Buscar en localStorage
          const savedMoods = JSON.parse(localStorage.getItem('moodHistory') || '[]')
          const todayMood = savedMoods.find(m => m.date === today)

          if (todayMood) {
            const mood = moods.value.find(m => m.value === todayMood.mood)
            if (mood) {
              selectedMood.value = mood
              moodSelected.value = true
            }
          }
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
      selectMood,
      resetMood
    }
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 15px;
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

.bg-gradient-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.list-group-item {
  border-left: 4px solid #667eea;
  transition: all 0.2s;
}

.list-group-item:hover {
  background-color: #f8f9fa;
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
