<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>
        <i class="bi bi-grid-3x3-gap me-2"></i>Categorías de Hábitos
      </h2>
      <button @click="goBack" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i>Volver
      </button>
    </div>

    <div class="alert alert-info mb-4">
      <i class="bi bi-info-circle me-2"></i>
      Las categorías te ayudan a organizar y clasificar tus hábitos según diferentes ámbitos de tu vida.
      Al crear un hábito, debes seleccionar una de estas categorías predeterminadas.
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-3 text-muted">Cargando categorías...</p>
    </div>

    <div v-else-if="categories.length === 0" class="alert alert-warning">
      <i class="bi bi-exclamation-triangle me-2"></i>
      No se encontraron categorías disponibles.
    </div>

    <div v-else class="row g-4">
      <div 
        v-for="category in categories" 
        :key="category.id"
        class="col-md-6 col-lg-4"
      >
        <div class="card category-card h-100 shadow-sm hover-card">
          <div 
            class="card-header text-white d-flex align-items-center"
            :style="{ backgroundColor: category.color }"
          >
            <i :class="category.icon + ' fs-3 me-3'"></i>
            <div>
              <h5 class="mb-0">{{ category.name }}</h5>
            </div>
          </div>
          <div class="card-body">
            <p class="card-text text-muted">{{ category.description }}</p>
            
            <div class="mt-3">
              <button 
                @click="viewHabitsByCategory(category.id)"
                class="btn btn-sm btn-outline-primary w-100"
              >
                <i class="bi bi-list-check me-1"></i>
                Ver hábitos de esta categoría
              </button>
            </div>
          </div>
          <div class="card-footer bg-transparent border-top-0 pt-0 pb-3">
            <small class="text-muted">
              <i class="bi bi-tag me-1"></i>
              ID: {{ category.id }}
            </small>
          </div>
        </div>
      </div>
    </div>

    <!-- Estadísticas de categorías -->
    <div class="card mt-5 shadow-sm">
      <div class="card-body">
        <h4 class="card-title mb-4">
          <i class="bi bi-bar-chart me-2"></i>
          Resumen de Categorías
        </h4>
        <div class="row text-center">
          <div class="col-md-4">
            <div class="stat-box">
              <i class="bi bi-grid-3x3-gap-fill text-primary fs-1"></i>
              <h3 class="mt-2 mb-0">{{ categories.length }}</h3>
              <p class="text-muted">Categorías Disponibles</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="stat-box">
              <i class="bi bi-palette-fill text-success fs-1"></i>
              <h3 class="mt-2 mb-0">8</h3>
              <p class="text-muted">Colores Únicos</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="stat-box">
              <i class="bi bi-tags-fill text-warning fs-1"></i>
              <h3 class="mt-2 mb-0">Predefinidas</h3>
              <p class="text-muted">Por el Sistema</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Información adicional -->
    <div class="card mt-4 shadow-sm">
      <div class="card-body">
        <h5 class="card-title">
          <i class="bi bi-question-circle me-2"></i>
          ¿Cómo usar las categorías?
        </h5>
        <ul class="mb-0">
          <li class="mb-2">
            <strong>Al crear un hábito:</strong> Selecciona la categoría que mejor describa el ámbito del hábito.
          </li>
          <li class="mb-2">
            <strong>Organización:</strong> Las categorías te ayudan a mantener tus hábitos organizados y fáciles de encontrar.
          </li>
          <li class="mb-2">
            <strong>Visualización:</strong> Cada categoría tiene un color e icono único para identificarla rápidamente.
          </li>
          <li>
            <strong>Filtrado:</strong> Puedes filtrar tus hábitos por categoría para enfocarte en un área específica de tu vida.
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import HTTP from '@/common/http.js'

export default {
  name: 'CategoriesView',
  setup() {
    const router = useRouter()
    const categories = ref([])
    const loading = ref(true)

    const loadCategories = async () => {
      try {
        loading.value = true
        const response = await HTTP.get('/habits/categories')
        categories.value = response.data
        console.log('Categories loaded:', categories.value)
      } catch (error) {
        console.error('Error loading categories:', error)
        alert('Error al cargar las categorías. Por favor, intenta de nuevo.')
      } finally {
        loading.value = false
      }
    }

    const viewHabitsByCategory = (categoryId) => {
      // Navegar a la vista de hábitos con filtro de categoría
      router.push({ 
        name: 'habits',
        query: { category: categoryId }
      })
    }

    const goBack = () => {
      router.push('/habits')
    }

    onMounted(() => {
      loadCategories()
    })

    return {
      categories,
      loading,
      viewHabitsByCategory,
      goBack
    }
  }
}
</script>

<style scoped>
.category-card {
  border: none;
  border-radius: 15px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15) !important;
}

.card-header {
  border: none;
  padding: 1.5rem;
}

.card-header h5 {
  font-weight: 600;
}

.stat-box {
  padding: 1.5rem;
  border-radius: 10px;
  background-color: #f8f9fa;
  transition: background-color 0.3s;
}

.stat-box:hover {
  background-color: #e9ecef;
}

.stat-box h3 {
  font-weight: 700;
  color: #333;
}

.alert-info {
  border-left: 4px solid #0dcaf0;
  background-color: #d1ecf1;
  border-color: #bee5eb;
}

.card {
  border: none;
}

.btn-outline-primary:hover {
  transform: scale(1.02);
}
</style>
