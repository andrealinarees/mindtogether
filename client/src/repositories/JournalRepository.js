/**
 * JournalRepository - Almacenamiento local de entradas del diario emocional.
 * Usa localStorage ya que no hay microservicio de journal.
 * Las entradas se guardan por usuario usando su login como clave.
 */
import { getStore } from '@/common/store'

const STORAGE_PREFIX = 'mindtogether_journal_'

const getStorageKey = () => {
  const store = getStore()
  const login = store.state.user.login || 'anonymous'
  return `${STORAGE_PREFIX}${login}`
}

const getEntries = () => {
  const data = localStorage.getItem(getStorageKey())
  return data ? JSON.parse(data) : []
}

const saveEntries = (entries) => {
  localStorage.setItem(getStorageKey(), JSON.stringify(entries))
}

export default {
  async findAll() {
    return getEntries().sort((a, b) => new Date(b.date) - new Date(a.date))
  },

  async findById(id) {
    const entries = getEntries()
    const entry = entries.find(e => e.id === id)
    if (!entry) throw new Error('Entrada no encontrada')
    return entry
  },

  async findByDate(date) {
    return getEntries().filter(e => e.date === date)
  },

  async findByMonth(year, month) {
    const prefix = `${year}-${String(month).padStart(2, '0')}`
    return getEntries().filter(e => e.date.startsWith(prefix))
  },

  async create(entry) {
    const entries = getEntries()
    const newEntry = {
      ...entry,
      id: Date.now().toString(),
      createdAt: new Date().toISOString(),
      updatedAt: null
    }
    entries.push(newEntry)
    saveEntries(entries)
    return newEntry
  },

  async update(id, data) {
    const entries = getEntries()
    const idx = entries.findIndex(e => e.id === id)
    if (idx === -1) throw new Error('Entrada no encontrada')
    entries[idx] = { ...entries[idx], ...data, updatedAt: new Date().toISOString() }
    saveEntries(entries)
    return entries[idx]
  },

  async delete(id) {
    const entries = getEntries().filter(e => e.id !== id)
    saveEntries(entries)
  },

  // Obtener días con entradas para un mes (para marcar en el calendario)
  async getDaysWithEntries(year, month) {
    const prefix = `${year}-${String(month).padStart(2, '0')}`
    const entries = getEntries().filter(e => e.date.startsWith(prefix))
    const daysMap = {}
    entries.forEach(e => {
      const day = parseInt(e.date.split('-')[2])
      if (!daysMap[day]) daysMap[day] = []
      daysMap[day].push(e)
    })
    return daysMap
  },

  getMoodOptions() {
    return [
      { value: 'happy', emoji: '😊', label: 'Bien', color: '#4CAF50' },
      { value: 'neutral', emoji: '😐', label: 'Regular', color: '#FF9800' },
      { value: 'sad', emoji: '😢', label: 'Mal', color: '#F44336' }
    ]
  },

  getMoodByValue(value) {
    return this.getMoodOptions().find(m => m.value === value) || { emoji: '😐', label: 'Regular', color: '#FF9800' }
  }
}
