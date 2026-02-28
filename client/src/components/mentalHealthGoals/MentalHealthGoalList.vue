<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-trophy"></i> Mis Metas de Salud Mental</h1>
      <router-link to="/mental-health-goals/new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Nueva Meta
      </router-link>
    </div>

    <div class="alert alert-info">
      <i class="bi bi-lightbulb"></i> Establece objetivos para tu bienestar emocional
    </div>

    <div class="row g-3">
      <div class="col-md-6" v-for="goal in goals" :key="goal.id">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">{{ goal.name }}</h5>
            <div class="progress mb-2">
              <div class="progress-bar" :style="`width: ${goal.progress}%`">
                {{ goal.progress }}%
              </div>
            </div>
            <router-link :to="`/mental-health-goals/${goal.id}`" class="btn btn-sm btn-outline-primary">
              Ver progreso
            </router-link>
          </div>
        </div>
      </div>
      
      <div v-if="goals.length === 0" class="col-12">
        <div class="alert alert-secondary text-center">
          No tienes metas registradas. ¡Crea tu primera meta de salud mental!
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const goals = ref([])

onMounted(async () => {
  // TODO: Cargar metas desde el API
  goals.value = []
})
</script>
