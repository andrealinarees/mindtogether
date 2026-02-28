<template>
  <div class="container py-4">
    <h1>Detalle de Práctica</h1>
    <div class="alert alert-info mt-3">
      Práctica ID: {{ $route.params.id }}
    </div>
    <router-link to="/wellness" class="btn btn-secondary">Volver</router-link>
  </div>
</template>
