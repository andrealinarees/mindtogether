<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
      <router-link class="navbar-brand" to="/dashboard">
        <i class="bi bi-heart-pulse"></i> MindTogether
      </router-link>
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-if="store.state.user.logged">
          <!-- Enlace Inicio para todos -->
          <li class="nav-item" v-if="store.state.user.authority !== 'ADMIN'">
            <router-link class="nav-link" to="/dashboard" active-class="active">
              <i class="bi bi-house-door"></i> Inicio
            </router-link>
          </li>
          <!-- Enlaces solo para usuarios normales -->
          <li class="nav-item" v-if="store.state.user.authority !== 'ADMIN'">
            <router-link class="nav-link" to="/journal" active-class="active">
              <i class="bi bi-journal-text"></i> Diario
            </router-link>
          </li>
          <li class="nav-item" v-if="store.state.user.authority !== 'ADMIN'">
            <router-link class="nav-link" to="/mental-health-goals" active-class="active">
              <i class="bi bi-trophy"></i> Metas
            </router-link>
          </li>
          <li class="nav-item" v-if="store.state.user.authority !== 'ADMIN'">
            <router-link class="nav-link" to="/wellness" active-class="active">
              <i class="bi bi-flower1"></i> Prácticas
            </router-link>
          </li>
          <li class="nav-item" v-if="store.state.user.authority !== 'ADMIN'">
            <router-link class="nav-link" to="/support-circles" active-class="active">
              <i class="bi bi-people"></i> Círculos
            </router-link>
          </li>
          <li class="nav-item" v-if="store.state.user.authority !== 'ADMIN'">
            <router-link class="nav-link" to="/rewards" active-class="active">
              <i class="bi bi-gift"></i> Recompensas
            </router-link>
          </li>
          <!-- Panel Admin solo para administradores -->
          <li class="nav-item" v-if="store.state.user.authority === 'ADMIN'">
            <router-link class="nav-link" to="/admin/dashboard" active-class="active">
              <i class="bi bi-shield-fill-check"></i> Panel Admin
            </router-link>
          </li>
          <li class="nav-item" v-if="store.state.user.authority === 'ADMIN'">
            <router-link class="nav-link" to="/admin/users" active-class="active">
              <i class="bi bi-people-fill"></i> Usuarios
            </router-link>
          </li>
          <li class="nav-item" v-if="store.state.user.authority === 'ADMIN'">
            <router-link class="nav-link" to="/admin/communities" active-class="active">
              <i class="bi bi-chat-dots"></i> Comunidades
            </router-link>
          </li>
        </ul>
        <ul class="navbar-nav ms-auto" v-if="store.state.user.logged">
          <li class="nav-item dropdown">
            <a
              class="nav-link dropdown-toggle user-menu"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <i class="bi bi-person-circle user-icon"></i>
              <span class="user-name">{{ store.state.user.name || store.state.user.login }}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end user-dropdown">
              <li class="dropdown-header">
                <div class="user-info">
                  <i class="bi bi-person-circle user-avatar"></i>
                  <div class="user-details">
                    <strong>{{ store.state.user.name || store.state.user.login }}</strong>
                    <small class="text-muted d-block">@{{ store.state.user.login }}</small>
                  </div>
                </div>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <router-link class="dropdown-item" to="/profile">
                  <i class="bi bi-person"></i> Ver perfil
                </router-link>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <a class="dropdown-item text-danger" href="#" @click.prevent="desautenticarme()">
                  <i class="bi bi-box-arrow-right"></i> Cerrar sesión
                </a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <router-view />
  <ToastNotification />
</template>

<script>
import { getStore } from "./common/store";
import auth from "./common/auth";
import ToastNotification from "./components/ToastNotification.vue";

export default {
  components: {
    ToastNotification
  },
  data() {
    return {
      store: getStore()
    };
  },
  methods: {
    desautenticarme() {
      auth.logout();
      this.$router.push("/");
    }
  },
  watch: {
    $route(newValue) {
      if (this.store.state.user.logged) {
        if (newValue.name === "NoteList") {
          this.$refs.dropdownNotesElement?.classList.add("active");
        } else {
          this.$refs.dropdownNotesElement?.classList.remove("active");
        }
      }
    }
  }
};
</script>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
  cursor: pointer;
}

/* Estilos para el menú de usuario */
.user-menu {
  display: flex;
  align-items: center;
  padding: 8px 12px !important;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.user-menu:hover {
  background-color: #f0f0f0;
}

.user-icon {
  font-size: 28px;
  margin-right: 8px;
  color: #667eea;
}

.user-name {
  font-weight: 600;
  color: #333;
  margin-right: 5px;
}

.user-dropdown {
  min-width: 280px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border: none;
  padding: 0;
}

.dropdown-header {
  padding: 0;
  border-bottom: 1px solid #e0e0e0;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 12px;
}

.user-avatar {
  font-size: 48px;
  color: #667eea;
}

.user-details {
  flex: 1;
  text-align: left;
}

.user-details strong {
  font-size: 16px;
  color: #333;
  display: block;
  margin-bottom: 2px;
}

.user-details small {
  font-size: 13px;
  color: #999;
}

.user-dropdown .dropdown-item {
  padding: 12px 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}

.user-dropdown .dropdown-item i {
  margin-right: 10px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.user-dropdown .dropdown-item:hover {
  background-color: #f8f9fa;
  padding-left: 24px;
}

.user-dropdown .dropdown-item.text-danger:hover {
  background-color: #fff5f5;
  color: #dc3545 !important;
}

.user-dropdown .dropdown-divider {
  margin: 0;
}
</style>
