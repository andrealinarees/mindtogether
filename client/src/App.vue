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

  <!-- Panel lateral del Chatbot -->
  <div
    v-if="store.state.user.logged && store.state.user.authority !== 'ADMIN'"
    class="chatbot-panel"
    :class="{ 'chatbot-panel-open': chatOpen }"
  >
    <!-- Cabecera -->
    <div class="chatbot-header">
      <div class="d-flex align-items-center gap-2">
        <i class="bi bi-robot"></i>
        <strong>Asistente IA</strong>
      </div>
      <button class="btn btn-sm btn-outline-light border-0" @click="chatOpen = false">
        <i class="bi bi-x-lg"></i>
      </button>
    </div>

    <!-- Mensajes -->
    <div class="chatbot-messages" ref="chatMessages">
      <div
        v-for="(msg, i) in chatMessages"
        :key="i"
        class="chatbot-msg"
        :class="msg.role === 'user' ? 'chatbot-msg-user' : 'chatbot-msg-bot'"
      >
        <small v-if="msg.role === 'assistant'" class="text-muted d-block mb-1">
          <i class="bi bi-robot me-1"></i>Asistente
          <span v-if="msg.isCrisis" class="badge bg-danger ms-1">⚠️ Apoyo</span>
        </small>
        <div
          class="chatbot-bubble"
          :class="[
            msg.role === 'user' ? 'bg-primary text-white' : 'bg-light',
            msg.isCrisis ? 'chatbot-crisis' : ''
          ]"
          style="white-space: pre-line;"
        >{{ msg.content }}</div>
      </div>
      <div v-if="chatLoading" class="chatbot-msg chatbot-msg-bot">
        <div class="chatbot-bubble bg-light">
          <span class="chatbot-typing"><span>.</span><span>.</span><span>.</span></span>
        </div>
      </div>
    </div>

    <!-- Input -->
    <div class="chatbot-input">
      <input
        type="text"
        class="form-control form-control-sm"
        v-model="chatInput"
        placeholder="Escribe un mensaje..."
        @keyup.enter="sendChatMessage"
        :disabled="chatLoading"
      />
      <button class="btn btn-primary btn-sm" @click="sendChatMessage" :disabled="!chatInput.trim() || chatLoading">
        <i class="bi bi-send-fill"></i>
      </button>
    </div>
  </div>

  <!-- Botón flotante del Chatbot -->
  <button
    v-if="store.state.user.logged && store.state.user.authority !== 'ADMIN'"
    class="chatbot-fab"
    :class="{ 'chatbot-fab-active': chatOpen }"
    title="Hablar con el asistente IA"
    @click="chatOpen = !chatOpen"
  >
    <i :class="chatOpen ? 'bi bi-x-lg' : 'bi bi-chat-dots-fill'"></i>
  </button>
</template>

<script>
import { getStore } from "./common/store";
import auth from "./common/auth";
import chatbot from "./common/chatbot";
import ToastNotification from "./components/ToastNotification.vue";

export default {
  components: {
    ToastNotification
  },
  data() {
    return {
      store: getStore(),
      chatOpen: false,
      chatInput: '',
      chatLoading: false,
      chatMessages: []
    };
  },
  computed: {
    currentUserLogin() {
      return this.store.state.user.login;
    }
  },
  created() {
    this.chatMessages = chatbot.loadConversation(this.currentUserLogin);
  },
  methods: {
    desautenticarme() {
      this.saveChatMessages();
      this.chatMessages = [];
      this.chatOpen = false;
      this.chatInput = '';
      auth.logout();
      this.$router.push("/");
    },
    async sendChatMessage() {
      const text = this.chatInput.trim();
      if (!text) return;

      this.chatMessages.push({ role: 'user', content: text, isCrisis: false });
      this.chatInput = '';
      this.chatLoading = true;
      this.saveChatMessages();
      this.$nextTick(() => this.scrollChat());

      try {
        const history = this.chatMessages.filter(m => m.role !== 'system');
        const result = await chatbot.sendMessage(history, text);

        this.chatMessages.push({
          role: 'assistant',
          content: result.content,
          isCrisis: result.isCrisis
        });
      } catch (error) {
        this.chatMessages.push({
          role: 'assistant',
          content: 'No se pudo conectar con el asistente. Asegúrate de que Ollama esté ejecutándose (ollama serve).\n\nSi necesitas ayuda urgente:\n📞 Teléfono de la Esperanza: 717 003 717\n📞 Línea 024\n📞 Emergencias: 112',
          isCrisis: false
        });
      } finally {
        this.chatLoading = false;
        this.saveChatMessages();
        this.$nextTick(() => this.scrollChat());
      }
    },
    saveChatMessages() {
      if (this.currentUserLogin) {
        chatbot.saveConversation(this.currentUserLogin, this.chatMessages);
      }
    },
    scrollChat() {
      const container = this.$refs.chatMessages;
      if (container) container.scrollTop = container.scrollHeight;
    }
  },
  watch: {
    currentUserLogin(newLogin, oldLogin) {
      if (oldLogin) {
        chatbot.saveConversation(oldLogin, this.chatMessages);
      }
      this.chatMessages = chatbot.loadConversation(newLogin);
      this.chatOpen = false;
      this.chatInput = '';
    },
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

/* Botón flotante del chatbot */
.chatbot-fab {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.6rem;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: transform 0.3s, box-shadow 0.3s;
  z-index: 1060;
  border: none;
  cursor: pointer;
}

.chatbot-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
  color: #fff;
}

.chatbot-fab-active {
  background: #dc3545;
  box-shadow: 0 4px 15px rgba(220, 53, 69, 0.4);
}

.chatbot-fab-active:hover {
  box-shadow: 0 6px 20px rgba(220, 53, 69, 0.6);
}

/* Panel lateral del chatbot */
.chatbot-panel {
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 380px;
  height: 500px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
  z-index: 1055;
  overflow: hidden;
  transform: scale(0) translateY(20px);
  transform-origin: bottom right;
  opacity: 0;
  transition: transform 0.3s ease, opacity 0.3s ease;
  pointer-events: none;
}

.chatbot-panel-open {
  transform: scale(1) translateY(0);
  opacity: 1;
  pointer-events: all;
}

.chatbot-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1rem;
}

.chatbot-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chatbot-msg-user {
  align-self: flex-end;
  max-width: 80%;
  text-align: right;
}

.chatbot-msg-bot {
  align-self: flex-start;
  max-width: 80%;
  text-align: left;
}

.chatbot-bubble {
  display: inline-block;
  padding: 8px 14px;
  border-radius: 14px;
  font-size: 0.9rem;
  line-height: 1.4;
  word-break: break-word;
}

.chatbot-msg-user .chatbot-bubble {
  border-bottom-right-radius: 4px;
}

.chatbot-msg-bot .chatbot-bubble {
  border-bottom-left-radius: 4px;
}

.chatbot-input {
  padding: 10px;
  border-top: 1px solid #e9ecef;
  display: flex;
  gap: 8px;
}

.chatbot-input .form-control {
  border-radius: 20px;
}

.chatbot-input .btn {
  border-radius: 50%;
  width: 36px;
  height: 36px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Animación de typing */
.chatbot-typing span {
  animation: blink 1.4s infinite;
  font-size: 1.4rem;
  line-height: 1;
}

.chatbot-typing span:nth-child(2) {
  animation-delay: 0.2s;
}

.chatbot-typing span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes blink {
  0%, 20% { opacity: 0.2; }
  50% { opacity: 1; }
  100% { opacity: 0.2; }
}

/* Mensaje de crisis */
.chatbot-crisis {
  border-left: 3px solid #dc3545;
}
</style>
