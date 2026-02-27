<template>
  <div class="container mt-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2>
          <i class="bi bi-list-check me-2"></i>Mis Hábitos
        </h2>
        <p class="text-muted mb-0">Gestiona y sigue tus hábitos diarios</p>
      </div>
      <button class="btn btn-primary btn-lg" @click="createHabit">
        <i class="bi bi-plus-circle me-2"></i>Crear Hábito
      </button>
    </div>

    <!-- Filtros y Búsqueda -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <!-- Búsqueda por nombre -->
          <div class="col-md-3">
            <label class="form-label">
              <i class="bi bi-search me-1"></i>Buscar por nombre
            </label>
            <input
              type="text"
              class="form-control"
              v-model="filters.searchText"
              placeholder="Buscar hábito..."
              @input="applyFilters"
            />
          </div>

          <!-- Filtro por categoría -->
          <div class="col-md-3">
            <label class="form-label">
              <i class="bi bi-tag me-1"></i>Categoría
            </label>
            <select class="form-select" v-model="filters.categoryId" @change="applyFilters">
              <option value="">Todas las categorías</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>

          <!-- Filtro por estado -->
          <div class="col-md-2">
            <label class="form-label">
              <i class="bi bi-filter me-1"></i>Estado
            </label>
            <select class="form-select" v-model="filters.status" @change="applyFilters">
              <option value="">Todos</option>
              <option value="ACTIVE">Activos</option>
              <option value="COMPLETED">Completados</option>
            </select>
          </div>

          <!-- Filtro por progreso -->
          <div class="col-md-2">
            <label class="form-label">
              <i class="bi bi-graph-up me-1"></i>Progreso
            </label>
            <select class="form-select" v-model="filters.progress" @change="applyFilters">
              <option value="">Todos</option>
              <option value="high">Alto (≥75%)</option>
              <option value="medium">Medio (50-74%)</option>
              <option value="low">Bajo (<50%)</option>
            </select>
          </div>

          <!-- Filtro por fecha desde -->
          <div class="col-md-2">
            <label class="form-label">
              <i class="bi bi-calendar-range me-1"></i>Período
            </label>
            <div class="input-group">
              <input
                type="date"
                class="form-control"
                v-model="filters.dateFrom"
                @change="applyFilters"
                title="Desde"
              />
              <input
                type="date"
                class="form-control"
                v-model="filters.dateTo"
                @change="applyFilters"
                title="Hasta"
              />
            </div>
          </div>
        </div>

        <!-- Ordenamiento -->
        <div class="row g-3 mt-2">
          <div class="col-md-3">
            <label class="form-label">
              <i class="bi bi-sort-down me-1"></i>Ordenar por
            </label>
            <select class="form-select" v-model="filters.sortBy" @change="applyFilters">
              <option value="">Predeterminado</option>
              <option value="progressDesc">Mayor progreso primero</option>
              <option value="progressAsc">Menor progreso primero</option>
              <option value="nameAsc">Nombre (A-Z)</option>
              <option value="nameDesc">Nombre (Z-A)</option>
            </select>
          </div>
        </div>

        <!-- Botón limpiar filtros -->
        <div class="mt-3 d-flex justify-content-between align-items-center">
          <div class="text-muted">
            <small>
              Mostrando {{ filteredHabits.length }} de {{ habits.length }} hábitos
            </small>
          </div>
          <button
            class="btn btn-sm btn-outline-secondary"
            @click="clearFilters"
            v-if="hasActiveFilters"
          >
            <i class="bi bi-x-circle me-1"></i>Limpiar filtros
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-2 text-muted">Cargando hábitos...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="habits.length === 0" class="text-center py-5">
      <div class="empty-state">
        <i class="bi bi-inbox display-1 text-muted"></i>
        <h3 class="mt-3">No tienes hábitos aún</h3>
        <p class="text-muted">Crea tu primer hábito para comenzar tu viaje de auto-mejora</p>
        <button class="btn btn-primary mt-3" @click="createHabit">
          <i class="bi bi-plus-circle me-2"></i>Crear Mi Primer Hábito
        </button>
      </div>
    </div>

    <!-- Habit Cards -->
    <div v-else class="row">
      <div
        v-for="habit in filteredHabits"
        :key="habit.id"
        class="col-md-6 col-lg-4 mb-4"
      >
        <HabitCard :habit="habit" @deleted="loadHabits" @updated="loadHabits" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import HTTP from '@/common/http.js'
import HabitCard from './HabitCard.vue'

export default {
  name: 'HabitList',
  components: {
    HabitCard
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const habits = ref([])
    const categories = ref([])
    const loading = ref(false)

    const filters = ref({
      searchText: '',
      categoryId: '',
      status: '',
      progress: '',
      dateFrom: '',
      dateTo: '',
      sortBy: ''
    })

    // Computed para hábitos filtrados
    const filteredHabits = computed(() => {
      let result = habits.value

      // Filtrar por texto de búsqueda
      if (filters.value.searchText) {
        const search = filters.value.searchText.toLowerCase()
        result = result.filter(h =>
          h.name.toLowerCase().includes(search) ||
          (h.description && h.description.toLowerCase().includes(search))
        )
      }

      // Filtrar por categoría
      if (filters.value.categoryId) {
        result = result.filter(h => h.categoryId == filters.value.categoryId)
      }

      // Filtrar por estado
      if (filters.value.status) {
        result = result.filter(h => h.status === filters.value.status)
      }

      // Filtrar por progreso
      if (filters.value.progress) {
        result = result.filter(h => {
          const progress = h.progressPercentage || 0
          if (filters.value.progress === 'high') {
            return progress >= 75
          } else if (filters.value.progress === 'medium') {
            return progress >= 50 && progress < 75
          } else if (filters.value.progress === 'low') {
            return progress < 50
          }
          return true
        })
      }

      // Filtrar por fecha desde
      if (filters.value.dateFrom) {
        result = result.filter(h => {
          const habitDate = new Date(h.startDate)
          const filterDate = new Date(filters.value.dateFrom)
          return habitDate >= filterDate
        })
      }

      // Filtrar por fecha hasta
      if (filters.value.dateTo) {
        result = result.filter(h => {
          const habitDate = new Date(h.endDate || h.startDate)
          const filterDate = new Date(filters.value.dateTo)
          return habitDate <= filterDate
        })
      }

      // Ordenamiento
      if (filters.value.sortBy) {
        result = [...result] // Crear copia para no mutar el array original
        switch (filters.value.sortBy) {
          case 'progressDesc':
            result.sort((a, b) => (b.progressPercentage || 0) - (a.progressPercentage || 0))
            break
          case 'progressAsc':
            result.sort((a, b) => (a.progressPercentage || 0) - (b.progressPercentage || 0))
            break
          case 'nameAsc':
            result.sort((a, b) => a.name.localeCompare(b.name))
            break
          case 'nameDesc':
            result.sort((a, b) => b.name.localeCompare(a.name))
            break
        }
      }

      return result
    })

    const hasActiveFilters = computed(() => {
      return filters.value.searchText ||
             filters.value.categoryId ||
             filters.value.status ||
             filters.value.progress ||
             filters.value.dateFrom ||
             filters.value.dateTo ||
             filters.value.sortBy
    })

    const loadHabits = async () => {
      loading.value = true
      try {
        const response = await HTTP.get('/habits')
        habits.value = response.data
        console.log('Hábitos cargados:', habits.value)
      } catch (error) {
        console.error('Error loading habits:', error)
        alert('Error al cargar los hábitos')
      } finally {
        loading.value = false
      }
    }

    const loadCategories = async () => {
      try {
        const response = await HTTP.get('/habits/categories')
        categories.value = response.data
      } catch (error) {
        console.error('Error loading categories:', error)
      }
    }

    const applyFilters = () => {
      // Los filtros se aplican automáticamente por el computed
      console.log('Filtros aplicados:', filters.value)
    }

    const clearFilters = () => {
      filters.value = {
        searchText: '',
        categoryId: '',
        status: '',
        progress: '',
        dateFrom: '',
        dateTo: '',
        sortBy: ''
      }
    }

    const createHabit = () => {
      router.push('/habits/new')
    }

    // Cargar hábitos al montar el componente
    onMounted(() => {
      loadHabits()
      loadCategories()

      // Si viene un filtro de categoría por query param
      if (route.query.category) {
        filters.value.categoryId = route.query.category
      }
    })

    // También recargar cuando el componente se reactive
    onActivated(() => {
      loadHabits()
    })

    return {
      habits,
      categories,
      loading,
      filters,
      filteredHabits,
      hasActiveFilters,
      loadHabits,
      applyFilters,
      clearFilters,
      createHabit
    }
  }
}
</script>

<style scoped>
.empty-state {
  padding: 3rem 1rem;
}

.btn-lg {
  padding: 0.75rem 1.5rem;
  font-size: 1.1rem;
}
</style>

