<template>
  <div class="profile-edit-container">
    <div class="profile-edit-card">
      <div class="profile-edit-header">
        <i class="bi bi-pencil-square header-icon"></i>
        <h2>Editar Perfil</h2>
        <p>Actualiza tu información personal</p>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
      </div>

      <div v-else class="profile-edit-content">
        <form @submit.prevent="guardarCambios">
          <div class="form-section">
            <h4 class="section-title">
              <i class="bi bi-person"></i> Información Personal
            </h4>

            <div class="form-group">
              <label for="name">
                <i class="bi bi-person-badge"></i> Nombre completo
              </label>
              <input
                type="text"
                id="name"
                class="form-control"
                v-model="formData.name"
                placeholder="Tu nombre completo"
                required
              />
            </div>

            <div class="form-group">
              <label for="login">
                <i class="bi bi-at"></i> Usuario
              </label>
              <input
                type="text"
                id="login"
                class="form-control"
                v-model="formData.login"
                disabled
                title="El usuario no se puede modificar"
              />
              <small class="form-text text-muted">
                El nombre de usuario no se puede cambiar
              </small>
            </div>

            <div class="form-group">
              <label for="email">
                <i class="bi bi-envelope"></i> Email
              </label>
              <input
                type="email"
                id="email"
                class="form-control"
                v-model="formData.email"
                placeholder="tu@email.com"
                required
              />
            </div>

            <div class="form-group">
              <label for="phone">
                <i class="bi bi-phone"></i> Teléfono
              </label>
              <input
                type="tel"
                id="phone"
                class="form-control"
                v-model="formData.phone"
                placeholder="+34 123 456 789"
                required
              />
            </div>

            <div class="form-group">
              <label for="city">
                <i class="bi bi-geo-alt"></i> Ciudad
              </label>
              <select
                id="city"
                class="form-select"
                v-model="formData.city"
                required
              >
                <option value="">Selecciona tu ciudad</option>
                <option v-for="city in cityList" :key="city" :value="city">
                  {{ city }}
                </option>
              </select>
              <small class="form-text text-muted">
                Se usará para mostrar información meteorológica personalizada
              </small>
            </div>
          </div>

          <div v-if="successMessage" class="alert alert-success">
            <i class="bi bi-check-circle"></i> {{ successMessage }}
          </div>

          <div v-if="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle"></i> {{ errorMessage }}
          </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="saving">
              <span v-if="saving">
                <span class="spinner-border spinner-border-sm me-2"></span>
                Guardando...
              </span>
              <span v-else>
                <i class="bi bi-check-circle"></i> Guardar cambios
              </span>
            </button>
            <router-link to="/profile" class="btn btn-outline-secondary">
              <i class="bi bi-x-circle"></i> Cancelar
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import AccountRepository from "../repositories/AccountRepository";
import { getStore } from "../common/store";
import { getCityList } from "../common/cities";

export default {
  name: "ProfileEditView",
  data() {
    return {
      cityList: getCityList(), // Lista de ciudades ordenadas
      formData: {
        name: "",
        login: "",
        email: "",
        phone: "",
        city: ""
      },
      loading: true,
      saving: false,
      successMessage: "",
      errorMessage: ""
    };
  },
  async mounted() {
    await this.loadUserProfile();
  },
  methods: {
    async loadUserProfile() {
      try {
        this.loading = true;
        const user = await AccountRepository.getAccount();
        this.formData = {
          name: user.name || "",
          login: user.login,
          email: user.email || "",
          phone: user.phone || "",
          city: user.city || ""
        };
      } catch (e) {
        console.error("Error al cargar el perfil:", e);
        this.errorMessage = "No se pudo cargar la información del perfil";
      } finally {
        this.loading = false;
      }
    },
    async guardarCambios() {
      this.saving = true;
      this.successMessage = "";
      this.errorMessage = "";

      try {
        await AccountRepository.updateAccount(this.formData);

        // Actualizar el store con el nuevo nombre
        const store = getStore();
        store.state.user.name = this.formData.name;

        this.successMessage = "Perfil actualizado correctamente";

        // Redirigir al perfil después de 1.5 segundos
        setTimeout(() => {
          this.$router.push("/profile");
        }, 1500);
      } catch (e) {
        console.error("Error al actualizar el perfil:", e);
        if (e.response?.data?.message) {
          this.errorMessage = e.response.data.message;
        } else {
          this.errorMessage = "No se pudo actualizar el perfil. Intenta de nuevo.";
        }
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.profile-edit-container {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.profile-edit-card {
  max-width: 700px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.profile-edit-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
  text-align: center;
}

.header-icon {
  font-size: 64px;
  display: block;
  margin-bottom: 15px;
}

.profile-edit-header h2 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 600;
}

.profile-edit-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.profile-edit-content {
  padding: 40px;
}

.loading {
  text-align: center;
  padding: 60px;
}

.form-section {
  margin-bottom: 30px;
}

.section-title {
  color: #333;
  font-size: 18px;
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

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-group label i {
  margin-right: 6px;
  color: #667eea;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
}

.form-control:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-text {
  display: block;
  margin-top: 6px;
  font-size: 12px;
}

.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.alert i {
  font-size: 18px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 30px;
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
  border: none;
  cursor: pointer;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}

.btn-outline-secondary {
  border: 2px solid #6c757d;
  color: #6c757d;
  text-decoration: none;
}

.btn-outline-secondary:hover {
  background: #6c757d;
  color: white;
}
</style>

