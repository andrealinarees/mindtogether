<template>
  <div class="login-container">
    <div class="login-card">
      <div class="logo-section">
        <i class="bi bi-calendar-check-fill logo-icon"></i>
        <h1 class="app-title">MindTogether</h1>
        <p class="app-subtitle">Tu compañero para el bienestar mental</p>
      </div>

      <!-- Formulario de Login -->
      <div v-if="!showRegister" class="form-section">
        <h2 class="form-title">Iniciar Sesión</h2>
        <form @submit.prevent="autenticarme">
          <div class="form-group">
            <label for="login">
              <i class="bi bi-person"></i> Usuario
            </label>
            <input
              type="text"
              id="login"
              class="form-control"
              v-model="auxLogin"
              placeholder="Ingresa tu usuario"
              required
            />
          </div>
          <div class="form-group">
            <label for="pass">
              <i class="bi bi-lock"></i> Contraseña
            </label>
            <input
              type="password"
              id="pass"
              class="form-control"
              v-model="auxPass"
              placeholder="Ingresa tu contraseña"
              required
            />
          </div>
          <button type="submit" class="btn btn-primary btn-block">
            <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
          </button>
        </form>
        <div class="register-link">
          <p>¿No tienes cuenta?
            <a href="#" @click.prevent="showRegister = true">Regístrate aquí</a>
          </p>
        </div>
      </div>

      <!-- Formulario de Registro -->
      <div v-else class="form-section">
        <h2 class="form-title">Crear Cuenta</h2>
        <form @submit.prevent="registrarse">
          <div class="form-group">
            <label for="regName">
              <i class="bi bi-person-badge"></i> Nombre completo
            </label>
            <input
              type="text"
              id="regName"
              class="form-control"
              v-model="registerData.name"
              placeholder="Tu nombre completo"
              required
            />
          </div>
          <div class="form-group">
            <label for="regLogin">
              <i class="bi bi-person"></i> Usuario
            </label>
            <input
              type="text"
              id="regLogin"
              class="form-control"
              v-model="registerData.login"
              placeholder="Elige un nombre de usuario"
              required
            />
          </div>
          <div class="form-group">
            <label for="regEmail">
              <i class="bi bi-envelope"></i> Email
            </label>
            <input
              type="email"
              id="regEmail"
              class="form-control"
              v-model="registerData.email"
              placeholder="tu@email.com"
              required
            />
          </div>
          <div class="form-group">
            <label for="regPhone">
              <i class="bi bi-phone"></i> Teléfono móvil
            </label>
            <input
              type="tel"
              id="regPhone"
              class="form-control"
              v-model="registerData.phone"
              placeholder="+34 123 456 789"
              required
            />
          </div>
          <div class="form-group">
            <label for="regPassword">
              <i class="bi bi-lock"></i> Contraseña
            </label>
            <input
              type="password"
              id="regPassword"
              class="form-control"
              v-model="registerData.password"
              placeholder="Mínimo 6 caracteres"
              required
              minlength="6"
            />
          </div>
          <div class="form-group">
            <label for="regPasswordConfirm">
              <i class="bi bi-lock-fill"></i> Repetir contraseña
            </label>
            <input
              type="password"
              id="regPasswordConfirm"
              class="form-control"
              v-model="registerData.passwordConfirm"
              placeholder="Confirma tu contraseña"
              required
            />
          </div>
          <button type="submit" class="btn btn-success btn-block">
            <i class="bi bi-person-plus"></i> Registrarse
          </button>
        </form>
        <div class="register-link">
          <p>¿Ya tienes cuenta?
            <a href="#" @click.prevent="showRegister = false">Inicia sesión aquí</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import auth from "../common/auth.js";
import AccountRepository from "../repositories/AccountRepository.js";

export default {
  data() {
    return {
      auxLogin: null,
      auxPass: null,
      showRegister: false,
      registerData: {
        name: "",
        login: "",
        email: "",
        phone: "",
        password: "",
        passwordConfirm: ""
      }
    };
  },
  methods: {
    async autenticarme() {
      try {
        await auth.login({
          login: this.auxLogin,
          password: this.auxPass
        });

        // Redirigir según el rol del usuario
        if (auth.isAdmin()) {
          this.$router.push("/admin/dashboard");
        } else {
          this.$router.push("/dashboard");
        }
      } catch (e) {
        console.error(e);
        if (e.response?.data?.message) {
          alert("Error: " + e.response.data.message);
        } else {
          alert("Error al iniciar sesión: " + e.message);
        }
      }
    },
    async registrarse() {
      // Validar que las contraseñas coincidan
      if (this.registerData.password !== this.registerData.passwordConfirm) {
        alert("Las contraseñas no coinciden");
        return;
      }

      // Validar longitud de contraseña
      if (this.registerData.password.length < 6) {
        alert("La contraseña debe tener al menos 6 caracteres");
        return;
      }

      try {
        // Registrar usuario
        await AccountRepository.registerAccount({
          name: this.registerData.name,
          login: this.registerData.login,
          email: this.registerData.email,
          phone: this.registerData.phone,
          password: this.registerData.password
        });

        // Iniciar sesión automáticamente después del registro
        await auth.login({
          login: this.registerData.login,
          password: this.registerData.password
        });

        // Redirigir al dashboard (los nuevos usuarios siempre son USER)
        this.$router.push("/dashboard");
      } catch (e) {
        console.error(e);
        if (e.response?.data?.message) {
          alert("Error al registrarse: " + e.response.data.message);
        } else {
          alert("Error al registrarse: " + e.message);
        }
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 480px;
  width: 100%;
  overflow: hidden;
}

.logo-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 20px;
  text-align: center;
}

.logo-icon {
  font-size: 64px;
  margin-bottom: 15px;
  display: block;
}

.app-title {
  font-size: 36px;
  font-weight: bold;
  margin: 0 0 10px 0;
}

.app-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.form-section {
  padding: 40px;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 500;
  color: #555;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-group label i {
  margin-right: 5px;
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

.btn-block {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 10px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
}

.btn-success {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.btn-success:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(17, 153, 142, 0.4);
}

.register-link {
  text-align: center;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.register-link p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>

