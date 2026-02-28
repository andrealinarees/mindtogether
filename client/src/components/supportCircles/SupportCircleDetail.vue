<template>
  <div class="container py-4">
    <h1>Detalle del Círculo</h1>
    <div class="alert alert-info mt-3">
      Círculo ID: {{ $route.params.id }}
    </div>
    <router-link to="/support-circles" class="btn btn-secondary">Volver</router-link>
  </div>
</template>
