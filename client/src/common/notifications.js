/**
 * Sistema de notificaciones toast para reemplazar alerts nativos
 */
import { reactive, readonly } from 'vue'

const state = reactive({
  notifications: [],
  confirmations: []
})

let notificationId = 0
let confirmationId = 0

// Función para agregar notificación
function addNotification({ type = 'info', message, duration = 5000 }) {
  const id = notificationId++
  const notification = {
    id,
    type, // 'success', 'error', 'warning', 'info'
    message,
    duration
  }
  
  state.notifications.push(notification)
  
  if (duration > 0) {
    setTimeout(() => {
      removeNotification(id)
    }, duration)
  }
  
  return id
}

// Función para eliminar notificación
function removeNotification(id) {
  const index = state.notifications.findIndex(n => n.id === id)
  if (index > -1) {
    state.notifications.splice(index, 1)
  }
}

// Funciones de conveniencia
export const notify = {
  success(message, duration = 4000) {
    return addNotification({ type: 'success', message, duration })
  },
  
  error(message, duration = 6000) {
    return addNotification({ type: 'error', message, duration })
  },
  
  warning(message, duration = 5000) {
    return addNotification({ type: 'warning', message, duration })
  },
  
  info(message, duration = 5000) {
    return addNotification({ type: 'info', message, duration })
  }
}

// Sistema de confirmaciones
export function confirm(message, options = {}) {
  return new Promise((resolve) => {
    const id = confirmationId++
    const confirmation = {
      id,
      message,
      title: options.title || '¿Estás seguro?',
      confirmText: options.confirmText || 'Confirmar',
      cancelText: options.cancelText || 'Cancelar',
      danger: options.danger || false,
      onConfirm: () => {
        removeConfirmation(id)
        resolve(true)
      },
      onCancel: () => {
        removeConfirmation(id)
        resolve(false)
      }
    }
    
    state.confirmations.push(confirmation)
  })
}

function removeConfirmation(id) {
  const index = state.confirmations.findIndex(c => c.id === id)
  if (index > -1) {
    state.confirmations.splice(index, 1)
  }
}

// Hook para usar en componentes
export function useNotifications() {
  return {
    notifications: readonly(state.notifications),
    confirmations: readonly(state.confirmations),
    notify,
    confirm,
    removeNotification
  }
}
