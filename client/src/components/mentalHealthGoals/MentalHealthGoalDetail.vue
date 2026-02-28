<template>
  <div class="container py-4">
    <h1>Detalle de Meta</h1>
    <div class="alert alert-info mt-3">
      Meta ID: {{ $route.params.id }}
    </div>
    <router-link to="/mental-health-goals" class="btn btn-secondary">Volver</router-link>
  </div>
</template>
