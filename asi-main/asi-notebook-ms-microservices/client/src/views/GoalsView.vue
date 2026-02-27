<template>
  <div class="container mt-4">
    <div class="row mb-4">
      <div class="col-12 d-flex justify-content-between align-items-center">
        <h1>
          <i class="bi bi-trophy me-2"></i>Metas
        </h1>
        <button class="btn btn-primary" @click="createGoal">
          <i class="bi bi-plus-circle me-1"></i>Nueva Meta
        </button>
      </div>
    </div>

    <!-- Lista de Metas (placeholder) -->
    <div class="row">
      <div class="col-12">
        <div class="card shadow-sm">
          <div class="card-body text-center py-5">
            <i class="bi bi-trophy display-1 text-muted"></i>
            <h3 class="mt-4 text-muted">Aún no tienes metas</h3>
            <p class="text-muted">Crea tu primera meta para comenzar a alcanzar tus objetivos</p>
            <button class="btn btn-primary mt-3" @click="createGoal">
              <i class="bi bi-plus-circle me-1"></i>Crear Primera Meta
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'GoalsView',
  setup() {
    const router = useRouter()

    const createGoal = () => {
      router.push('/goals/new')
    }

    return {
      createGoal
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
