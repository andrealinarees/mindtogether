<template>
  <div class="container mt-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2><i class="bi bi-heart-pulse-fill me-2"></i>Prácticas de Bienestar</h2>
        <p class="text-muted mb-0">Cuida tu salud mental con prácticas diarias</p>
      </div>
      <router-link to="/wellness/new" class="btn btn-primary btn-lg">
        <i class="bi bi-plus-circle me-2"></i>Nueva Práctica
      </router-link>
    </div>

    <!-- Filtros -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <label class="form-label"><i class="bi bi-search me-1"></i>Buscar</label>
            <input type="text" class="form-control" v-model="filters.searchText" placeholder="Buscar práctica..." @input="applyFilters">
          </div>
          <div class="col-md-3">
            <label class="form-label"><i class="bi bi-tag me-1"></i>Categoría</label>
            <select class="form-select" v-model="filters.categoryId" @change="applyFilters">
              <option value="">Todas</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label"><i class="bi bi-filter me-1"></i>Estado</label>
            <select class="form-select" v-model="filters.status" @change="applyFilters">
              <option value="">Todos</option>
              <option value="ACTIVE">Activos</option>
              <option value="COMPLETED">Completados</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label"><i class="bi bi-sort-down me-1"></i>Ordenar</label>
            <select class="form-select" v-model="filters.sortBy" @change="applyFilters">
              <option value="">Predeterminado</option>
              <option value="progressDesc">Mayor progreso</option>
              <option value="progressAsc">Menor progreso</option>
              <option value="nameAsc">Nombre (A-Z)</option>
            </select>
          </div>
        </div>
        <div class="mt-3 d-flex justify-content-between align-items-center">
          <small class="text-muted">Mostrando {{ filteredPractices.length }} de {{ practices.length }} prácticas</small>
          <button v-if="hasActiveFilters" class="btn btn-sm btn-outline-secondary" @click="clearFilters">
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
      <p class="mt-2 text-muted">Cargando prácticas...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="practices.length === 0" class="text-center py-5">
      <i class="bi bi-heart-pulse display-1 text-muted"></i>
      <h3 class="mt-3">No tienes prácticas de bienestar aún</h3>
      <p class="text-muted">Crea tu primera práctica para comenzar a cuidar tu salud mental</p>
      <router-link to="/wellness/new" class="btn btn-primary mt-3">
        <i class="bi bi-plus-circle me-2"></i>Crear Mi Primera Práctica
      </router-link>
    </div>

    <!-- Practice Cards -->
    <div v-else class="row">
      <div v-for="practice in filteredPractices" :key="practice.id" class="col-md-6 col-lg-4 mb-4">
        <WellnessPracticeCard :practice="practice" @deleted="loadPractices" @updated="loadPractices" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRoute } from 'vue-router'
import WellnessPracticeRepository from '@/repositories/WellnessPracticeRepository'
import WellnessPracticeCard from './WellnessPracticeCard.vue'
import { notify } from '@/common/notifications'

export default {
  name: 'WellnessPracticeList',
  components: { WellnessPracticeCard },
  setup() {
    const route = useRoute()
    const practices = ref([])
    const categories = ref([])
    const loading = ref(false)

    const filters = ref({
      searchText: '',
      categoryId: '',
      status: '',
      sortBy: ''
    })

    const filteredPractices = computed(() => {
      let result = practices.value

      if (filters.value.searchText) {
        const search = filters.value.searchText.toLowerCase()
        result = result.filter(p =>
          p.name.toLowerCase().includes(search) ||
          (p.description && p.description.toLowerCase().includes(search))
        )
      }

      if (filters.value.categoryId) {
        result = result.filter(p => String(p.categoryId) === String(filters.value.categoryId))
      }

      if (filters.value.status) {
        result = result.filter(p => p.status === filters.value.status)
      }

      if (filters.value.sortBy) {
        result = [...result]
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
        }
      }

      return result
    })

    const hasActiveFilters = computed(() => {
      return filters.value.searchText || filters.value.categoryId || filters.value.status || filters.value.sortBy
    })

    const loadPractices = async () => {
      loading.value = true
      try {
        practices.value = await WellnessPracticeRepository.findAll()
      } catch (error) {
        console.error('Error loading practices:', error)
        notify.error('Error al cargar las prácticas')
      } finally {
        loading.value = false
      }
    }

    const loadCategories = async () => {
      try {
        categories.value = await WellnessPracticeRepository.getCategories()
      } catch (error) {
        console.error('Error loading categories:', error)
      }
    }

    const applyFilters = () => {}

    const clearFilters = () => {
      filters.value = { searchText: '', categoryId: '', status: '', sortBy: '' }
    }

    onMounted(() => {
      loadPractices()
      loadCategories()
      if (route.query.category) {
        filters.value.categoryId = route.query.category
      }
    })

    onActivated(() => { loadPractices() })

    return {
      practices, categories, loading, filters,
      filteredPractices, hasActiveFilters,
      loadPractices, applyFilters, clearFilters
    }
  }
}
</script>
