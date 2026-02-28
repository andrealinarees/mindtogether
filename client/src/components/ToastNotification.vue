<template>
  <div>
    <!-- Notificaciones Toast -->
    <Teleport to="body">
      <div class="toast-container">
        <TransitionGroup name="toast">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="toast-item"
            :class="`toast-${notification.type}`"
            @click="removeNotification(notification.id)"
          >
            <div class="toast-icon">
              <i v-if="notification.type === 'success'" class="bi bi-check-circle-fill"></i>
              <i v-else-if="notification.type === 'error'" class="bi bi-x-circle-fill"></i>
              <i v-else-if="notification.type === 'warning'" class="bi bi-exclamation-triangle-fill"></i>
              <i v-else class="bi bi-info-circle-fill"></i>
            </div>
            <div class="toast-content">
              <p>{{ notification.message }}</p>
            </div>
            <button class="toast-close" @click.stop="removeNotification(notification.id)">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </TransitionGroup>
      </div>
    </Teleport>

    <!-- Modales de Confirmación -->
    <Teleport to="body">
      <TransitionGroup name="modal">
        <div
          v-for="confirmation in confirmations"
          :key="confirmation.id"
          class="modal-overlay"
          @click="confirmation.onCancel"
        >
          <div class="modal-dialog" @click.stop>
            <div class="modal-content">
              <div class="modal-header" :class="{ 'modal-danger': confirmation.danger }">
                <h5 class="modal-title">
                  <i v-if="confirmation.danger" class="bi bi-exclamation-triangle-fill me-2"></i>
                  <i v-else class="bi bi-question-circle-fill me-2"></i>
                  {{ confirmation.title }}
                </h5>
              </div>
              <div class="modal-body">
                <p>{{ confirmation.message }}</p>
              </div>
              <div class="modal-footer">
                <button
                  class="btn btn-secondary"
                  @click="confirmation.onCancel"
                >
                  {{ confirmation.cancelText }}
                </button>
                <button
                  class="btn"
                  :class="confirmation.danger ? 'btn-danger' : 'btn-primary'"
                  @click="confirmation.onConfirm"
                >
                  {{ confirmation.confirmText }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </TransitionGroup>
    </Teleport>
  </div>
</template>

<script>
import { useNotifications } from '@/common/notifications'

export default {
  name: 'ToastNotification',
  setup() {
    const { notifications, confirmations, removeNotification } = useNotifications()
    
    return {
      notifications,
      confirmations,
      removeNotification
    }
  }
}
</script>

<style scoped>
/* Toast Container */
.toast-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 400px;
  pointer-events: none;
}

/* Toast Item */
.toast-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-left: 4px solid;
  pointer-events: auto;
  cursor: pointer;
  transition: all 0.3s ease;
  max-width: 100%;
  word-wrap: break-word;
}

.toast-item:hover {
  transform: translateX(-5px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
}

/* Toast Types */
.toast-success {
  border-left-color: #28a745;
}

.toast-success .toast-icon {
  color: #28a745;
}

.toast-error {
  border-left-color: #dc3545;
}

.toast-error .toast-icon {
  color: #dc3545;
}

.toast-warning {
  border-left-color: #ffc107;
}

.toast-warning .toast-icon {
  color: #ffc107;
}

.toast-info {
  border-left-color: #17a2b8;
}

.toast-info .toast-icon {
  color: #17a2b8;
}

/* Toast Icon */
.toast-icon {
  font-size: 24px;
  flex-shrink: 0;
}

/* Toast Content */
.toast-content {
  flex: 1;
  text-align: left;
}

.toast-content p {
  margin: 0;
  color: #333;
  font-size: 14px;
  line-height: 1.5;
}

/* Toast Close Button */
.toast-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 0;
  margin-left: auto;
  flex-shrink: 0;
  transition: color 0.2s;
}

.toast-close:hover {
  color: #333;
}

/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 20px;
}

/* Modal Dialog */
.modal-dialog {
  width: 100%;
  max-width: 500px;
}

.modal-content {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Modal Header */
.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.modal-header.modal-danger {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
}

.modal-title {
  margin: 0;
  color: white;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.modal-title i {
  font-size: 22px;
}

/* Modal Body */
.modal-body {
  padding: 24px;
}

.modal-body p {
  margin: 0;
  color: #333;
  font-size: 15px;
  line-height: 1.6;
  text-align: left;
  white-space: pre-wrap;
}

/* Modal Footer */
.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-footer .btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.2s;
}

/* Animations */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100px) scale(0.8);
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.9) translateY(-50px);
}

/* Responsive */
@media (max-width: 768px) {
  .toast-container {
    left: 20px;
    right: 20px;
    top: 70px;
  }
  
  .toast-item {
    max-width: 100%;
  }
  
  .modal-dialog {
    margin: 0 auto;
  }
}
</style>
