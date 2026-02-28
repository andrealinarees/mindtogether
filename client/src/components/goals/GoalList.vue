<template>
  <div class="container-fluid py-4">
    <div class="row mb-4">
      <div class="col">
        <h2 class="mb-3">
          <i class="bi bi-bullseye me-2"></i>
          Mis Metas
        </h2>
      </div>
      <div class="col-auto">
        <router-link to="/goals/new" class="btn btn-primary">
          <i class="bi bi-plus-circle me-2"></i>
          Nueva Meta
        </router-link>
      </div>
    </div>

    <!-- Filtros y búsqueda -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="input-group">
          <span class="input-group-text">
            <i class="bi bi-search"></i>
          </span>
          <input
            v-model="searchQuery"
            type="text"
            class="form-control"
            placeholder="Buscar metas..."
            @input="handleSearch"
          />
        </div>
      </div>
      <div class="col-md-6">
        <div class="btn-group w-100" role="group">
          <input
            type="radio"
            class="btn-check"
            name="statusFilter"
            id="filter-all"
            value=""
            v-model="statusFilter"
            @change="loadGoals"
          />
          <label class="btn btn-outline-primary" for="filter-all">Todas</label>

          <input
            type="radio"
            class="btn-check"
            name="statusFilter"
            id="filter-active"
            value="ACTIVE"
            v-model="statusFilter"
            @change="loadGoals"
          />
          <label class="btn btn-outline-primary" for="filter-active">Activas</label>

          <input
            type="radio"
            class="btn-check"
            name="statusFilter"
            id="filter-completed"
            value="COMPLETED"
            v-model="statusFilter"
            @change="loadGoals"
          />
          <label class="btn btn-outline-success" for="filter-completed">Completadas</label>

          <input
            type="radio"
            class="btn-check"
            name="statusFilter"
            id="filter-failed"
            value="FAILED"
            v-model="statusFilter"
            @change="loadGoals"
          />
          <label class="btn btn-outline-danger" for="filter-failed">Fallidas</label>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Lista de objetivos -->
    <div v-else-if="filteredGoals.length > 0" class="row g-4">
      <div
        v-for="goal in filteredGoals"
        :key="goal.id"
        class="col-12 col-md-6 col-lg-4"
      >
        <GoalCard
          :goal="goal"
          @edit="editGoal"
          @complete="completeGoal"
          @delete="deleteGoal"
        />
      </div>
    </div>

    <!-- Sin resultados -->
    <div v-else class="text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <p class="lead text-muted mt-3">
        {{ searchQuery ? 'No se encontraron metas' : 'No tienes metas creadas' }}
      </p>
      <router-link v-if="!searchQuery" to="/goals/new" class="btn btn-primary">
        Crear mi primera meta
      </router-link>
    </div>

    <!-- Estadísticas rápidas -->
    <div v-if="!loading && goals.length > 0" class="row mt-5">
      <div class="col-md-3">
        <div class="card text-center">
          <div class="card-body">
            <h3 class="text-primary">{{ stats.total }}</h3>
            <p class="text-muted mb-0">Total</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center">
          <div class="card-body">
            <h3 class="text-info">{{ stats.active }}</h3>
            <p class="text-muted mb-0">Activos</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center">
          <div class="card-body">
            <h3 class="text-success">{{ stats.completed }}</h3>
            <p class="text-muted mb-0">Completados</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center">
          <div class="card-body">
            <h3 class="text-warning">{{ stats.completionRate }}%</h3>
            <p class="text-muted mb-0">Tasa de éxito</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import GoalRepository from '@/repositories/GoalRepository'
import GoalCard from './GoalCard.vue'
import { notify, confirm } from '@/common/notifications'

export default {
  name: 'GoalList',
  components: {
    GoalCard
  },
  setup() {
    const router = useRouter()
    const goals = ref([])
    const loading = ref(true)
    const searchQuery = ref('')
    const statusFilter = ref('')
    let searchTimeout = null

    const loadGoals = async () => {
      try {
        loading.value = true
        if (statusFilter.value) {
          goals.value = await GoalRepository.findByStatus(statusFilter.value)
        } else {
          goals.value = await GoalRepository.findAll()
        }
      } catch (error) {
        console.error('Error loading goals:', error)
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      if (searchTimeout) clearTimeout(searchTimeout)
      
      searchTimeout = setTimeout(async () => {
        if (searchQuery.value.trim()) {
          try {
            loading.value = true
            goals.value = await GoalRepository.search(searchQuery.value)
          } catch (error) {
            console.error('Error searching goals:', error)
          } finally {
            loading.value = false
          }
        } else {
          loadGoals()
        }
      }, 300)
    }

    const filteredGoals = computed(() => {
      return goals.value
    })

    const stats = computed(() => {
      const total = goals.value.length
      const active = goals.value.filter(g => g.status === 'ACTIVE').length
      const completed = goals.value.filter(g => g.status === 'COMPLETED').length
      const failed = goals.value.filter(g => g.status === 'FAILED').length
      
      const completionRate = (completed + failed) > 0
        ? Math.round((completed / (completed + failed)) * 100)
        : 0

      return { total, active, completed, failed, completionRate }
    })

    const editGoal = (id) => {
      router.push(`/goals/${id}/edit`)
    }

    const completeGoal = async (id) => {
      const confirmed = await confirm('¿Estás seguro de que quieres marcar esta meta como completada?')
      if (!confirmed) return
      
      try {
        await GoalRepository.complete(id)
        notify.success('Meta completada exitosamente')
        await loadGoals()
      } catch (error) {
        console.error('Error completing goal:', error)
        notify.error('Error al marcar la meta como completada')
      }
    }

    const deleteGoal = async (id) => {
      const confirmed = await confirm('¿Estás seguro de que quieres eliminar esta meta?\n\nEsta acción no se puede deshacer.', { danger: true })
      if (!confirmed) return
      
      try {
        await GoalRepository.delete(id)
        notify.success('Meta eliminada correctamente')
        await loadGoals()
      } catch (error) {
        console.error('Error deleting goal:', error)
        notify.error('Error al eliminar la meta')
      }
    }

    onMounted(() => {
      loadGoals()
    })

    // Recargar cuando se vuelve a esta vista
    onActivated(() => {
      loadGoals()
    })

    return {
      goals,
      loading,
      searchQuery,
      statusFilter,
      filteredGoals,
      stats,
      loadGoals,
      handleSearch,
      editGoal,
      completeGoal,
      deleteGoal
    }
  }
}
</script>

<style scoped>
.btn-check:checked + .btn {
  font-weight: 600;
}
</style>
