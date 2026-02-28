<template>
  <div class="container py-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2><i class="bi bi-calendar3 me-2"></i>Calendario del Diario</h2>
        <p class="text-muted mb-0">Visualiza tus entradas por mes</p>
      </div>
      <div class="d-flex gap-2">
        <router-link to="/journal" class="btn btn-outline-secondary">
          <i class="bi bi-arrow-left me-1"></i>Volver
        </router-link>
        <router-link to="/journal/new" class="btn btn-primary">
          <i class="bi bi-plus-circle me-1"></i>Nueva Entrada
        </router-link>
      </div>
    </div>

    <!-- Navegación de mes -->
    <div class="card shadow-sm mb-4">
      <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center py-3">
        <button class="btn btn-outline-light" @click="prevMonth">
          <i class="bi bi-chevron-left me-1"></i>Anterior
        </button>
        <h4 class="mb-0 text-capitalize">{{ monthYearLabel }}</h4>
        <button class="btn btn-outline-light" @click="nextMonth">
          Siguiente<i class="bi bi-chevron-right ms-1"></i>
        </button>
      </div>
      <div class="card-body p-3">
        <!-- Días de la semana -->
        <div class="row g-1 text-center mb-2">
          <div v-for="day in weekDays" :key="day" class="col fw-bold text-muted py-2">{{ day }}</div>
        </div>
        <!-- Celdas del calendario -->
        <div class="row g-1 text-center">
          <div v-for="(cell, i) in calendarCells" :key="i" class="col p-0">
            <div
              v-if="cell.day"
              class="calendar-cell border rounded p-2 position-relative"
              :class="{
                'bg-primary text-white': cell.isToday,
                'border-success': cell.hasEntry && !cell.isToday,
                'bg-light': !cell.hasEntry && !cell.isToday
              }"
              style="min-height: 70px; cursor: pointer;"
              @click="handleCellClick(cell)"
            >
              <div class="fw-bold">{{ cell.day }}</div>
              <div v-if="cell.mood" class="mt-1" style="font-size: 1.3rem;">{{ getMoodEmoji(cell.mood) }}</div>
              <div v-if="cell.hasEntry" class="mt-1">
                <small :class="cell.isToday ? 'text-white-50' : 'text-muted'" style="font-size: 0.65rem;">
                  {{ cell.entryTitle ? cell.entryTitle.substring(0, 12) : '' }}
                </small>
              </div>
            </div>
            <div v-else style="min-height: 70px;"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Resumen del mes -->
    <div class="row g-3">
      <div class="col-md-4">
        <div class="card shadow-sm text-center">
          <div class="card-body">
            <h3 class="text-primary">{{ monthStats.total }}</h3>
            <p class="text-muted mb-0">Entradas este mes</p>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card shadow-sm text-center">
          <div class="card-body">
            <h3>
              <span v-if="monthStats.mostCommonMood">{{ getMoodEmoji(monthStats.mostCommonMood) }}</span>
              <span v-else class="text-muted">—</span>
            </h3>
            <p class="text-muted mb-0">Ánimo más frecuente</p>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card shadow-sm text-center">
          <div class="card-body">
            <h3 class="text-success">{{ monthStats.streak }}</h3>
            <p class="text-muted mb-0">Días seguidos escribiendo</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import JournalRepository from '@/repositories/JournalRepository'

export default {
  name: 'JournalCalendar',
  setup() {
    const router = useRouter()
    const currentYear = ref(new Date().getFullYear())
    const currentMonth = ref(new Date().getMonth() + 1)
    const daysWithEntries = ref({})
    const monthEntries = ref([])
    const weekDays = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']

    const monthYearLabel = computed(() => {
      const d = new Date(currentYear.value, currentMonth.value - 1)
      return d.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
    })

    const calendarCells = computed(() => {
      const cells = []
      const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1)
      const lastDay = new Date(currentYear.value, currentMonth.value, 0)
      let startWeekday = firstDay.getDay() - 1
      if (startWeekday < 0) startWeekday = 6

      for (let i = 0; i < startWeekday; i++) cells.push({ day: null })

      const today = new Date()
      for (let d = 1; d <= lastDay.getDate(); d++) {
        const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(d).padStart(2, '0')}`
        const isToday = today.getFullYear() === currentYear.value && today.getMonth() + 1 === currentMonth.value && today.getDate() === d
        const dayEntries = daysWithEntries.value[d]
        cells.push({
          day: d, dateStr, isToday, isCurrentMonth: true,
          hasEntry: !!dayEntries,
          mood: dayEntries ? dayEntries[0].mood : null,
          entryId: dayEntries ? dayEntries[0].id : null,
          entryTitle: dayEntries ? dayEntries[0].title : null
        })
      }

      while (cells.length % 7 !== 0) cells.push({ day: null })
      return cells
    })

    const monthStats = computed(() => {
      const entries = monthEntries.value
      const total = entries.length
      if (total === 0) return { total: 0, mostCommonMood: null, streak: 0 }

      // Mood más frecuente
      const moodCount = {}
      entries.forEach(e => { moodCount[e.mood] = (moodCount[e.mood] || 0) + 1 })
      const mostCommonMood = Object.keys(moodCount).sort((a, b) => moodCount[b] - moodCount[a])[0]

      // Racha: días consecutivos con entrada (desde hoy hacia atrás)
      let streak = 0
      const today = new Date()
      const entryDates = new Set(entries.map(e => e.date))
      for (let i = 0; i < 31; i++) {
        const d = new Date(today)
        d.setDate(d.getDate() - i)
        const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
        if (entryDates.has(dateStr)) {
          streak++
        } else if (i > 0) {
          break
        }
      }

      return { total, mostCommonMood, streak }
    })

    const loadCalendar = async () => {
      daysWithEntries.value = await JournalRepository.getDaysWithEntries(currentYear.value, currentMonth.value)
      monthEntries.value = await JournalRepository.findByMonth(currentYear.value, currentMonth.value)
    }

    const prevMonth = () => {
      if (currentMonth.value === 1) { currentMonth.value = 12; currentYear.value-- }
      else { currentMonth.value-- }
      loadCalendar()
    }

    const nextMonth = () => {
      if (currentMonth.value === 12) { currentMonth.value = 1; currentYear.value++ }
      else { currentMonth.value++ }
      loadCalendar()
    }

    const handleCellClick = (cell) => {
      if (cell.hasEntry) {
        router.push(`/journal/${cell.entryId}`)
      } else {
        // Navegar a crear nueva entrada con esa fecha
        router.push('/journal/new')
      }
    }

    const getMoodEmoji = (mood) => JournalRepository.getMoodByValue(mood).emoji

    onMounted(() => loadCalendar())

    return {
      currentYear, currentMonth, weekDays,
      monthYearLabel, calendarCells, monthStats,
      prevMonth, nextMonth, handleCellClick, getMoodEmoji
    }
  }
}
</script>

<style scoped>
.calendar-cell { transition: all 0.2s; }
.calendar-cell:hover { transform: scale(1.02); box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.border-success { border-width: 2px !important; }
</style>
