<template>
  <div class="container-fluid py-4">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col">
        <h2 class="mb-1">
          <i class="bi bi-gift me-2"></i>Mis Recompensas
        </h2>
        <p class="text-muted">Crea recompensas personalizadas y asócialas a tus metas</p>
      </div>
      <div class="col-auto">
        <router-link to="/rewards/new" class="btn btn-primary">
          <i class="bi bi-plus-circle me-2"></i>Nueva Recompensa
        </router-link>
      </div>
    </div>

    <!-- Estadísticas -->
    <div v-if="stats" class="row g-3 mb-4">
      <div class="col-6 col-md-4">
        <div class="card shadow-sm text-center">
          <div class="card-body py-3">
            <div class="fs-3 fw-bold text-primary">{{ stats.total || 0 }}</div>
            <small class="text-muted">Total</small>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-4">
        <div class="card shadow-sm text-center">
          <div class="card-body py-3">
            <div class="fs-3 fw-bold text-secondary">{{ stats.locked || 0 }}</div>
            <small class="text-muted">🔒 Bloqueadas</small>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-4">
        <div class="card shadow-sm text-center">
          <div class="card-body py-3">
            <div class="fs-3 fw-bold text-success">{{ stats.unlocked || 0 }}</div>
            <small class="text-muted">🎉 Desbloqueadas</small>
          </div>
        </div>
      </div>
    </div>

    <!-- Filtros -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="btn-group w-100" role="group">
          <input type="radio" class="btn-check" name="statusFilter" id="filter-all" value="" v-model="statusFilter" @change="loadRewards" />
          <label class="btn btn-outline-primary" for="filter-all">Todas</label>

          <input type="radio" class="btn-check" name="statusFilter" id="filter-locked" value="LOCKED" v-model="statusFilter" @change="loadRewards" />
          <label class="btn btn-outline-secondary" for="filter-locked">🔒 Bloqueadas</label>

          <input type="radio" class="btn-check" name="statusFilter" id="filter-unlocked" value="UNLOCKED" v-model="statusFilter" @change="loadRewards" />
          <label class="btn btn-outline-success" for="filter-unlocked">🎉 Desbloqueadas</label>
        </div>
      </div>
      <div class="col-md-6">
        <select v-model="categoryFilter" class="form-select" @change="loadRewards">
          <option value="">Todas las categorías</option>
          <option v-for="cat in categories" :key="cat.value" :value="cat.value">
            {{ cat.icon }} {{ cat.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <!-- Lista de recompensas -->
    <div v-else-if="filteredRewards.length > 0" class="row g-4">
      <div
        v-for="reward in filteredRewards"
        :key="reward.id"
        class="col-12 col-md-6 col-lg-4"
      >
        <RewardCard
          :reward="reward"
          @edit="editReward"
          @delete="deleteReward"
        />
      </div>
    </div>

    <!-- Estado vacío -->
    <div v-else class="text-center py-5">
      <i class="bi bi-gift display-1 text-muted"></i>
      <h3 class="mt-4 text-muted">
        {{ statusFilter ? 'No hay recompensas con este filtro' : 'Aún no tienes recompensas' }}
      </h3>
      <p class="text-muted">
        Crea recompensas personalizadas y asócialas a tus metas para motivarte.
      </p>
      <router-link to="/rewards/new" class="btn btn-primary mt-3">
        <i class="bi bi-plus-circle me-1"></i>Crear primera recompensa
      </router-link>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CustomRewardRepository from '@/repositories/CustomRewardRepository'
import RewardCard from './RewardCard.vue'

export default {
  name: 'RewardList',
  components: { RewardCard },
  setup() {
    const router = useRouter()
    const rewards = ref([])
    const stats = ref(null)
    const loading = ref(true)
    const statusFilter = ref('')
    const categoryFilter = ref('')
    const categories = CustomRewardRepository.getCategories()

    const filteredRewards = computed(() => {
      let result = rewards.value
      if (categoryFilter.value && !statusFilter.value) {
        result = result.filter(r => r.category === categoryFilter.value)
      }
      return result
    })

    const loadRewards = async () => {
      try {
        loading.value = true
        if (statusFilter.value) {
          rewards.value = await CustomRewardRepository.findByStatus(statusFilter.value)
        } else if (categoryFilter.value) {
          rewards.value = await CustomRewardRepository.findByCategory(categoryFilter.value)
        } else {
          rewards.value = await CustomRewardRepository.findAll()
        }
      } catch (error) {
        console.error('Error loading rewards:', error)
      } finally {
        loading.value = false
      }
    }

    const loadStats = async () => {
      try {
        stats.value = await CustomRewardRepository.getStats()
      } catch (error) {
        console.error('Error loading stats:', error)
      }
    }

    const editReward = (id) => {
      router.push(`/rewards/${id}/edit`)
    }

    const deleteReward = async (id) => {
      if (!confirm('¿Seguro que quieres eliminar esta recompensa?')) return
      try {
        await CustomRewardRepository.delete(id)
        await loadRewards()
        await loadStats()
      } catch (error) {
        console.error('Error deleting reward:', error)
        alert('Error al eliminar la recompensa')
      }
    }

    onMounted(async () => {
      await Promise.all([loadRewards(), loadStats()])
    })

    return {
      rewards,
      stats,
      loading,
      statusFilter,
      categoryFilter,
      categories,
      filteredRewards,
      loadRewards,
      editReward,
      deleteReward
    }
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 15px;
}
</style>
