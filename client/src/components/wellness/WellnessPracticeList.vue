<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-heart-pulse-fill"></i> Prácticas de Bienestar</h1>
      <router-link to="/wellness/new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Nueva Práctica
      </router-link>
    </div>

    <div class="alert alert-info">
      <i class="bi bi-info-circle"></i> Gestiona tus prácticas de bienestar diarias
    </div>

    <div class="row g-3">
      <div class="col-md-4" v-for="practice in practices" :key="practice.id">
        <div class="card h-100">
          <div class="card-body">
            <h5 class="card-title">{{ practice.name }}</h5>
            <p class="card-text">{{ practice.description }}</p>
            <router-link :to="`/wellness/${practice.id}`" class="btn btn-sm btn-outline-primary">
              Ver detalles
            </router-link>
          </div>
        </div>
      </div>
      
      <div v-if="practices.length === 0" class="col-12">
        <div class="alert alert-secondary text-center">
          No tienes prácticas de bienestar registradas. ¡Crea tu primera práctica!
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const practices = ref([])

onMounted(async () => {
  // TODO: Cargar prácticas desde el API
  practices.value = []
})
</script>
