<template>
  <div class="container py-4">
    <h1>{{ isEdit ? 'Editar' : 'Nueva' }} Entrada de Diario</h1>
    
    <form @submit.prevent="handleSubmit" class="mt-4">
      <div class="mb-3">
        <label class="form-label">¿Cómo te sientes hoy?</label>
        <select class="form-select" v-model="form.mood">
          <option value="">Selecciona tu estado de ánimo</option>
          <option value="excellent">Excelente</option>
          <option value="good">Bien</option>
          <option value="okay">Regular</option>
          <option value="sad">Triste</option>
          <option value="anxious">Ansioso/a</option>
          <option value="stressed">Estresado/a</option>
          <option value="angry">Enojado/a</option>
          <option value="calm">Tranquilo/a</option>
          <option value="energetic">Con energía</option>
          <option value="tired">Cansado/a</option>
        </select>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Escribe tus pensamientos</label>
        <textarea class="form-control" v-model="form.content" rows="8" 
                  placeholder="Comparte lo que sientes..."></textarea>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Etiquetas</label>
        <input type="text" class="form-control" v-model="form.tags" 
               placeholder="Ej: ansiedad, trabajo, familia">
      </div>
      
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Guardar</button>
        <router-link to="/journal" class="btn btn-secondary">Cancelar</router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  mood: '',
  content: '',
  tags: ''
})

const handleSubmit = () => {
  router.push('/journal')
}
</script>
