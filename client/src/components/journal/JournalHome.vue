<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-journal-text"></i> Mi Diario Emocional</h1>
      <router-link to="/journal/new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Nueva Entrada
      </router-link>
    </div>

    <div class="row mb-4">
      <div class="col-md-4">
        <div class="card bg-light">
          <div class="card-body text-center">
            <h5>💬 Chatbot IA</h5>
            <router-link to="/journal/chatbot" class="btn btn-sm btn-outline-primary">
              Conversar
            </router-link>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card bg-light">
          <div class="card-body text-center">
            <h5>📊 Análisis</h5>
            <router-link to="/journal/analysis" class="btn btn-sm btn-outline-primary">
              Ver análisis
            </router-link>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card bg-light">
          <div class="card-body text-center">
            <h5>📅 Calendario</h5>
            <router-link to="/journal/calendar" class="btn btn-sm btn-outline-primary">
              Ver calendario
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <h3>Entradas Recientes</h3>
    <div class="list-group mt-3">
      <div v-if="entries.length === 0" class="alert alert-secondary">
        No tienes entradas en tu diario. ¡Escribe tu primera entrada!
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const entries = ref([])
</script>
