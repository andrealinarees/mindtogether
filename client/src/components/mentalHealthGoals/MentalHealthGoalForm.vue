<template>
  <div class="container py-4">
    <h1>{{ isEdit ? 'Editar' : 'Nueva' }} Meta de Salud Mental</h1>
    
    <form @submit.prevent="handleSubmit" class="mt-4">
      <div class="mb-3">
        <label class="form-label">Nombre de la Meta</label>
        <input type="text" class="form-control" v-model="form.name" required>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Categoría</label>
        <select class="form-select" v-model="form.category" required>
          <option value="">Selecciona una categoría</option>
          <option value="ANXIETY_MANAGEMENT">Manejo de Ansiedad</option>
          <option value="MOOD_IMPROVEMENT">Mejora del Estado de Ánimo</option>
          <option value="STRESS_REDUCTION">Reducción del Estrés</option>
          <option value="SLEEP_QUALITY">Calidad del Sueño</option>
          <option value="SOCIAL_CONNECTION">Conexión Social</option>
        </select>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Descripción</label>
        <textarea class="form-control" v-model="form.description" rows="3"></textarea>
      </div>
      
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Guardar</button>
        <router-link to="/mental-health-goals" class="btn btn-secondary">Cancelar</router-link>
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
  name: '',
  category: '',
  description: ''
})

const handleSubmit = () => {
  // TODO: Guardar en el API
  router.push('/mental-health-goals')
}
</script>
