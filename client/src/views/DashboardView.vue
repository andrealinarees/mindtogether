<template>
  <div class="container mt-4">
    <!-- Saludo personalizado -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow-sm bg-gradient-primary text-white">
          <div class="card-body p-4">
            <h2 class="mb-3">
              <i class="bi bi-sun me-2"></i>¡Hola, {{ userName }}!
            </h2>
            <div class="weather-info">
              <div v-if="loadingWeather" class="text-white">
                <span class="spinner-border spinner-border-sm me-2"></span>
                Cargando información del clima...
              </div>
              <div v-else-if="weather">
                <h4 class="mb-2">
                  <i class="bi bi-geo-alt me-1"></i>{{ userCity }}
                </h4>
                <div class="d-flex align-items-center mb-3">
                  <div class="me-4">
                    <h1 class="display-4 mb-0">{{ Math.round(weather.temperature) }}°C</h1>
                  </div>
                  <div>
                    <h5 class="mb-1">
                      <i :class="weatherIcon" class="me-2"></i>{{ weather.conditionDescription }}
                    </h5>
                    <p class="mb-0">
                      <i class="bi bi-wind me-1"></i>Viento: {{ weather.windspeed }} km/h
                    </p>
                  </div>
                </div>
                <div class="alert alert-light mb-0">
                  <i class="bi bi-lightbulb me-2"></i>
                  <strong>{{ weather.suggestion }}</strong>
                </div>
              </div>
              <div v-else-if="weatherError" class="text-white">
                <i class="bi bi-exclamation-triangle me-2"></i>
                No se pudo cargar la información del clima
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Estadísticas Generales -->
    <div class="row mb-4">
      <div class="col-md-4 mb-3">
        <div class="card text-center shadow-sm hover-card">
          <div class="card-body">
            <i class="bi bi-calendar-check display-4 text-primary"></i>
            <h3 class="mt-3">{{ totalHabits }}</h3>
            <p class="text-muted">Hábitos Activos</p>
          </div>
        </div>
      </div>
      <div class="col-md-4 mb-3">
        <div class="card text-center shadow-sm hover-card">
          <div class="card-body">
            <i class="bi bi-fire display-4 text-danger"></i>
            <h3 class="mt-3">{{ currentStreak }}</h3>
            <p class="text-muted">Racha Actual (días)</p>
          </div>
        </div>
      </div>
      <div class="col-md-4 mb-3">
        <div class="card text-center shadow-sm hover-card">
          <div class="card-body">
            <i class="bi bi-check-circle display-4 text-success"></i>
            <h3 class="mt-3">{{ completedToday }}</h3>
            <p class="text-muted">Completados Hoy</p>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { ref, onMounted, onActivated, computed, watch } from 'vue'
import { getStore } from '@/common/store'
import WeatherRepository from '@/repositories/WeatherRepository'
import HabitRepository from '@/repositories/HabitRepository'
import AccountRepository from '@/repositories/AccountRepository'
import { getCityCoordinates } from '@/common/cities'

export default {
  name: 'DashboardView',
  setup() {
    const store = getStore()
    const userProfile = ref(null)
    const weather = ref(null)
    const loadingWeather = ref(true)
    const weatherError = ref(false)
    const totalHabits = ref(0)
    const currentStreak = ref(0)
    const completedToday = ref(0)
    const todayHabits = ref([])
    const loadingHabits = ref(true)

    const userName = computed(() => {
      return userProfile.value?.name || store.state.user.name || store.state.user.login || 'Usuario'
    })

    const userCity = computed(() => {
      return userProfile.value?.city || store.state.user.city || 'A Coruña'
    })

    const weatherIcon = computed(() => {
      if (!weather.value) return 'bi bi-cloud'
      const condition = weather.value.condition
      const icons = {
        'CLEAR': 'bi bi-sun',
        'PARTLY_CLOUDY': 'bi bi-cloud-sun',
        'CLOUDY': 'bi bi-cloud',
        'RAINY': 'bi bi-cloud-rain',
        'STORMY': 'bi bi-cloud-lightning',
        'SNOWY': 'bi bi-cloud-snow',
        'FOGGY': 'bi bi-cloud-fog'
      }
      return icons[condition] || 'bi bi-cloud'
    })


    const loadUserProfile = async () => {
      try {
        userProfile.value = await AccountRepository.getAccount()
      } catch (error) {
        console.error('Error loading user profile:', error)
      }
    }

    const loadWeatherForCity = async (cityName) => {
      try {
        loadingWeather.value = true
        weatherError.value = false

        const coords = getCityCoordinates(cityName)

        const weatherData = await WeatherRepository.getCurrentWeather(
          coords.latitude,
          coords.longitude,
          'Europe/Madrid'
        )

        // Procesar la respuesta de Open-Meteo
        if (weatherData && weatherData.current_weather) {
          const current = weatherData.current_weather

          // Mapear weathercode a condición legible
          const getCondition = (code) => {
            if (code === 0) return 'CLEAR'
            if (code >= 1 && code <= 3) return 'PARTLY_CLOUDY'
            if (code >= 45 && code <= 48) return 'FOGGY'
            if (code >= 51 && code <= 67) return 'RAINY'
            if (code >= 71 && code <= 77) return 'SNOWY'
            if (code >= 80) return 'STORMY'
            return 'CLOUDY'
          }

          // Mapear condición a descripción en español
          const getConditionDescription = (code) => {
            if (code === 0) return 'Despejado'
            if (code >= 1 && code <= 3) return 'Parcialmente nublado'
            if (code >= 45 && code <= 48) return 'Niebla'
            if (code >= 51 && code <= 67) return 'Lluvia'
            if (code >= 71 && code <= 77) return 'Nieve'
            if (code >= 80) return 'Tormenta'
            return 'Nublado'
          }

          weather.value = {
            temperature: current.temperature,
            windspeed: current.windspeed,
            condition: getCondition(current.weathercode),
            conditionDescription: getConditionDescription(current.weathercode),
            suggestion: '¡Buen día para mantener tus hábitos!'
          }
        } else {
          throw new Error('No se recibieron datos del clima')
        }
      } catch (error) {
        console.error('Error loading weather:', error)
        weatherError.value = true
      } finally {
        loadingWeather.value = false
      }
    }

    const loadWeather = async () => {
      // Primero cargar el perfil para obtener la ciudad
      await loadUserProfile()
      // Luego cargar el clima con esa ciudad
      await loadWeatherForCity(userCity.value)
    }

    const loadHabits = async () => {
      try {
        loadingHabits.value = true
        const habits = await HabitRepository.findAll()
        console.log('Hábitos cargados en dashboard:', habits)

        // Filtrar hábitos activos
        const activeHabits = habits.filter(h => h.status === 'ACTIVE')
        totalHabits.value = activeHabits.length

        // Calcular racha actual (tomar la mayor racha de todos los hábitos activos)
        // Esto da una aproximación de la racha global del usuario
        const streaks = activeHabits.map(h => h.currentStreak || 0).filter(s => s > 0)
        console.log('Rachas encontradas:', streaks)
        currentStreak.value = streaks.length > 0 ? Math.max(...streaks) : 0

        // Hábitos de hoy (todos los activos por ahora)
        todayHabits.value = activeHabits.slice(0, 5) // Mostrar máximo 5

        // Calcular completados hoy (hábitos que tienen completedToday = true)
        completedToday.value = activeHabits.filter(h => h.completedToday === true).length
        console.log('Completados hoy:', completedToday.value)
      } catch (error) {
        console.error('Error loading habits:', error)
      } finally {
        loadingHabits.value = false
      }
    }

    onMounted(() => {
      loadWeather()
      loadHabits()
    })

    onActivated(() => {
      // Recargar hábitos cuando se vuelve a activar la vista (para actualizar las rachas)
      loadHabits()
    })

    // Watch para recargar clima cuando cambie la ciudad en el perfil
    watch(userCity, (newCity, oldCity) => {
      if (newCity && newCity !== oldCity) {
        loadWeatherForCity(newCity)
      }
    })

    return {
      userName,
      userCity,
      weather,
      loadingWeather,
      weatherError,
      weatherIcon,
      totalHabits,
      currentStreak,
      completedToday,
      todayHabits,
      loadingHabits
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
  transition: transform 0.2s;
}

.hover-card:hover {
  transform: translateY(-5px);
}

.display-4 {
  font-size: 3rem;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.weather-info {
  color: white;
}

.weather-info .alert-light {
  background-color: rgba(255, 255, 255, 0.95);
  border: none;
  color: #333;
}

.list-group-item {
  border-left: 4px solid #667eea;
  transition: all 0.2s;
}

.list-group-item:hover {
  background-color: #f8f9fa;
  transform: translateX(5px);
}
</style>
