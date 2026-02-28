<template>
  <div class="container py-4">
    <h1>Entrada de Diario</h1>
    <div class="alert alert-info mt-3">
      Entrada ID: {{ $route.params.id }}
    </div>
    <router-link to="/journal" class="btn btn-secondary">Volver</router-link>
  </div>
</template>
