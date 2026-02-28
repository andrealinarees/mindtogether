<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1><i class="bi bi-people"></i> Círculos de Apoyo</h1>
      <router-link to="/support-circles/new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Crear Círculo
      </router-link>
    </div>

    <div class="alert alert-info">
      <i class="bi bi-heart"></i> Conéctate con comunidades de apoyo
    </div>

    <div class="mb-3">
      <router-link to="/support-circles/discover" class="btn btn-outline-primary">
        <i class="bi bi-search"></i> Descubrir Círculos
      </router-link>
    </div>

    <div class="row g-3">
      <div class="col-12">
        <div class="alert alert-secondary text-center">
          No formas parte de ningún círculo aún. ¡Únete o crea uno!
        </div>
      </div>
    </div>
  </div>
</template>
