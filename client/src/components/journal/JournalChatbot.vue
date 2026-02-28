<template>
  <div class="container py-4">
    <h1><i class="bi bi-robot"></i> Asistente IA para el Diario</h1>
    
    <div class="card mt-4">
      <div class="card-body" style="min-height: 400px;">
        <div class="mb-3">
          <p class="text-muted"><strong>IA:</strong> Hola, estoy aquí para escucharte y ayudarte a reflexionar. ¿Cómo te sientes hoy?</p>
        </div>
        
        <div class="input-group">
          <input type="text" class="form-control" v-model="message" 
                 placeholder="Escribe tu mensaje..." @keyup.enter="sendMessage">
          <button class="btn btn-primary" @click="sendMessage">
            <i class="bi bi-send"></i> Enviar
          </button>
        </div>
      </div>
    </div>
    
    <router-link to="/journal" class="btn btn-secondary mt-3">Volver</router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const message = ref('')

const sendMessage = () => {
  if (message.value.trim()) {
    // TODO: Integrar con Ollama
    message.value = ''
  }
}
</script>
