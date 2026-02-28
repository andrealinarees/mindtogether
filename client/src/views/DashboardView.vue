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
            <i class="bi bi-emoji-smile me-2"></i>Tu Estado de Ánimo
          </div>
          <div class="card-body">
            <div class="text-center mb-3">
              <span style="font-size: 4rem;">😊</span>
              <h4 class="mt-2">Estado general: <strong>Bien</strong></h4>
              <p class="text-muted">Últimas 7 días</p>
            </div>
            <div class="progress" style="height: 25px;">
              <div class="progress-bar bg-success" style="width: 70%">70%</div>
            </div>
            <p class="text-muted text-center mt-2 mb-0">
              <small>Sigue así, estás haciendo un excelente trabajo</small>
            </p>
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

export default {
  name: 'DashboardView',
  setup() {
    const store = getStore()
    
    // Datos estáticos por ahora - se conectarán a los servicios reales más adelante
    const journalEntries = ref(12)
    const activeGoals = ref(5)
    const wellnessPractices = ref(8)
    const supportCircles = ref(3)

    const userName = computed(() => {
      return store.state.user.name || store.state.user.login || 'Usuario'
    })

    onMounted(() => {
      // Aquí se cargarán las estadísticas reales de los servicios
      // TODO: Conectar con los repositorios
    })

    return {
      userName,
      journalEntries,
      activeGoals,
      wellnessPractices,
      supportCircles
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
</style>
