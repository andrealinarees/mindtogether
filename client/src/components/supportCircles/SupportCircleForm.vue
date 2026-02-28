<template>
  <div class="container py-4">
    <h1>{{ isEdit ? 'Editar' : 'Crear' }} Círculo de Apoyo</h1>
    
    <form @submit.prevent="handleSubmit" class="mt-4">
      <div class="mb-3">
        <label class="form-label">Nombre del Círculo</label>
        <input type="text" class="form-control" v-model="form.name" required>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Descripción</label>
        <textarea class="form-control" v-model="form.description" rows="3"></textarea>
      </div>
      
      <div class="mb-3">
        <label class="form-label">Tipo</label>
        <select class="form-select" v-model="form.type">
          <option value="public">Público</option>
          <option value="private">Privado</option>
        </select>
      </div>
      
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Guardar</button>
        <router-link to="/support-circles" class="btn btn-secondary">Cancelar</router-link>
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
  description: '',
  type: 'public'
})

const handleSubmit = () => {
  router.push('/support-circles')
}
</script>
