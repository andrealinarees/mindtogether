<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <i class="bi bi-person-circle profile-avatar"></i>
        <h2>Mi Perfil</h2>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else class="profile-content">
        <div class="profile-section">
          <h4 class="section-title">
            <i class="bi bi-info-circle"></i> Información Personal
          </h4>
          <div class="info-grid">
            <div class="info-item">
              <label><i class="bi bi-person-badge"></i> Nombre completo</label>
              <p>{{ user.name || 'No especificado' }}</p>
            </div>
            <div class="info-item">
              <label><i class="bi bi-person"></i> Usuario</label>
              <p>{{ user.login }}</p>
            </div>
            <div class="info-item">
              <label><i class="bi bi-envelope"></i> Email</label>
              <p>{{ user.email || 'No especificado' }}</p>
            </div>
            <div class="info-item">
              <label><i class="bi bi-phone"></i> Teléfono</label>
              <p>{{ user.phone || 'No especificado' }}</p>
            </div>
            <div class="info-item">
              <label><i class="bi bi-shield-check"></i> Rol</label>
              <p>
                <span :class="['badge', user.authority === 'ADMIN' ? 'bg-danger' : 'bg-primary']">
                  {{ user.authority === 'ADMIN' ? 'Administrador' : 'Usuario' }}
                </span>
              </p>
            </div>
          </div>
        </div>

        <div class="profile-actions">
          <router-link to="/profile/edit" class="btn btn-primary">
            <i class="bi bi-pencil"></i> Editar perfil
          </router-link>
          <router-link to="/dashboard" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Volver al menú
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AccountRepository from "../repositories/AccountRepository";

export default {
  name: "ProfileView",
  data() {
    return {
      user: {},
      loading: true,
      error: null
    };
  },
  async mounted() {
    await this.loadUserProfile();
  },
  methods: {
    async loadUserProfile() {
      try {
        this.loading = true;
        this.error = null;
        this.user = await AccountRepository.getAccount();
      } catch (e) {
        console.error("Error al cargar el perfil:", e);
        this.error = "No se pudo cargar la información del perfil";
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.profile-card {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
  text-align: center;
}

.profile-avatar {
  font-size: 80px;
  display: block;
  margin-bottom: 15px;
}

.profile-header h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 600;
}

.profile-content {
  padding: 40px;
}

.loading {
  text-align: center;
  padding: 60px;
}

.profile-section {
  margin-bottom: 30px;
}

.section-title {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #667eea;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #667eea;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.info-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.info-item label {
  display: block;
  font-weight: 600;
  color: #666;
  font-size: 13px;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-item label i {
  margin-right: 6px;
  color: #667eea;
}

.info-item p {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.info-item:last-child {
  grid-column: 1 / -1;
}

.badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
}

.profile-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #e0e0e0;
}

.btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}

.btn-outline-secondary {
  border: 2px solid #6c757d;
  color: #6c757d;
}

.btn-outline-secondary:hover {
  background: #6c757d;
  color: white;
}
</style>

