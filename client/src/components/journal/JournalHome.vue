<template>
  <div class="container py-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2><i class="bi bi-journal-text me-2"></i>Mi Diario Emocional</h2>
        <p class="text-muted mb-0">Registra cómo te sientes cada día</p>
      </div>
      <div class="d-flex gap-2">
        <router-link to="/journal/calendar" class="btn btn-outline-primary">
          <i class="bi bi-calendar3 me-1"></i>Calendario
        </router-link>
        <router-link to="/journal/new" class="btn btn-primary btn-lg">
          <i class="bi bi-plus-circle me-2"></i>Escribir hoy
        </router-link>
      </div>
    </div>

    <!-- Fecha de hoy -->
    <div class="card shadow-sm mb-4 border-primary">
      <div class="card-body text-center py-4">
        <h4 class="text-primary mb-1">
          <i class="bi bi-calendar-heart me-2"></i>{{ todayFormatted }}
        </h4>
        <p class="text-muted mb-3">{{ todayHasEntry ? '✅ Ya escribiste hoy' : '¿Cómo te sientes hoy?' }}</p>
        <router-link v-if="!todayHasEntry" to="/journal/new" class="btn btn-primary">
          <i class="bi bi-pencil me-1"></i>Escribir entrada de hoy
        </router-link>
        <router-link v-else :to="`/journal/${todayEntry.id}`" class="btn btn-outline-primary">
          <i class="bi bi-eye me-1"></i>Ver entrada de hoy
          <span class="ms-2">{{ getMoodEmoji(todayEntry.mood) }}</span>
        </router-link>
      </div>
    </div>

    <!-- Mini calendario del mes actual -->
    <div class="card shadow-sm mb-4">
      <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
        <button class="btn btn-sm btn-outline-light" @click="prevMonth">
          <i class="bi bi-chevron-left"></i>
        </button>
        <h5 class="mb-0">{{ monthYearLabel }}</h5>
        <button class="btn btn-sm btn-outline-light" @click="nextMonth">
          <i class="bi bi-chevron-right"></i>
        </button>
      </div>
      <div class="card-body p-2">
        <!-- Días de la semana -->
        <div class="row g-0 text-center mb-1">
          <div v-for="day in weekDays" :key="day" class="col fw-bold text-muted small py-1">{{ day }}</div>
        </div>
        <!-- Días del mes -->
        <div class="row g-0 text-center">
          <div v-for="(cell, i) in calendarCells" :key="i" class="col p-0" style="min-height: 45px;">
            <div v-if="cell.day"
              class="calendar-day mx-auto d-flex flex-column align-items-center justify-content-center rounded-circle"
              :class="{
                'bg-primary text-white': cell.isToday,
                'calendar-has-entry': cell.hasEntry && !cell.isToday,
                'text-muted': !cell.isCurrentMonth
              }"
              style="width: 38px; height: 38px; cursor: pointer; font-size: 0.85rem;"
              @click="cell.hasEntry ? viewDayEntry(cell) : null"
              :title="cell.hasEntry ? 'Ver entrada' : ''"
            >
              <span>{{ cell.day }}</span>
              <span v-if="cell.mood" style="font-size: 0.6rem; margin-top: -2px;">{{ getMoodEmoji(cell.mood) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Entradas recientes -->
    <div class="card shadow-sm">
      <div class="card-header bg-light">
        <h5 class="mb-0"><i class="bi bi-clock-history me-2"></i>Entradas Recientes</h5>
      </div>
      <div class="card-body p-0">
        <div v-if="entries.length === 0" class="text-center py-5">
          <i class="bi bi-journal display-1 text-muted"></i>
          <h4 class="mt-3 text-muted">Tu diario está vacío</h4>
          <p class="text-muted">Empieza a escribir cómo te sientes cada día</p>
          <router-link to="/journal/new" class="btn btn-primary mt-2">
            <i class="bi bi-pencil me-1"></i>Primera entrada
          </router-link>
        </div>
        <div v-else class="list-group list-group-flush">
          <router-link
            v-for="entry in entries.slice(0, 10)"
            :key="entry.id"
            :to="`/journal/${entry.id}`"
            class="list-group-item list-group-item-action py-3"
          >
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <span class="me-2" style="font-size: 1.5rem;">{{ getMoodEmoji(entry.mood) }}</span>
                <strong>{{ entry.title }}</strong>
                <br>
                <small class="text-muted">{{ formatDate(entry.date) }}</small>
              </div>
              <div>
                <span class="badge rounded-pill" :style="{ backgroundColor: getMoodColor(entry.mood), color: '#fff' }">
                  {{ getMoodLabel(entry.mood) }}
                </span>
              </div>
            </div>
            <p class="text-muted small mb-0 mt-1" v-if="entry.content">
              {{ entry.content.substring(0, 100) }}{{ entry.content.length > 100 ? '...' : '' }}
            </p>
          </router-link>
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
  name: 'JournalHome',
  setup() {
    const router = useRouter()
    const entries = ref([])
    const currentYear = ref(new Date().getFullYear())
    const currentMonth = ref(new Date().getMonth() + 1)
    const daysWithEntries = ref({})
    const weekDays = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']

    const todayStr = computed(() => {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    })

    const todayFormatted = computed(() => {
      return new Date().toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
    })

    const todayEntry = computed(() => entries.value.find(e => e.date === todayStr.value))
    const todayHasEntry = computed(() => !!todayEntry.value)

    const monthYearLabel = computed(() => {
      const d = new Date(currentYear.value, currentMonth.value - 1)
      return d.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
    })

    const calendarCells = computed(() => {
      const cells = []
      const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1)
      const lastDay = new Date(currentYear.value, currentMonth.value, 0)
      // Lunes = 0, Domingo = 6
      let startWeekday = firstDay.getDay() - 1
      if (startWeekday < 0) startWeekday = 6

      // Celdas vacías antes del primer día
      for (let i = 0; i < startWeekday; i++) {
        cells.push({ day: null })
      }

      const today = new Date()
      for (let d = 1; d <= lastDay.getDate(); d++) {
        const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(d).padStart(2, '0')}`
        const isToday = today.getFullYear() === currentYear.value && today.getMonth() + 1 === currentMonth.value && today.getDate() === d
        const dayEntries = daysWithEntries.value[d]
        cells.push({
          day: d,
          dateStr,
          isToday,
          isCurrentMonth: true,
          hasEntry: !!dayEntries,
          mood: dayEntries ? dayEntries[0].mood : null,
          entryId: dayEntries ? dayEntries[0].id : null
        })
      }

      // Rellenar hasta completar filas de 7
      while (cells.length % 7 !== 0) {
        cells.push({ day: null })
      }

      return cells
    })

    const loadEntries = async () => {
      entries.value = await JournalRepository.findAll()
    }

    const loadCalendar = async () => {
      daysWithEntries.value = await JournalRepository.getDaysWithEntries(currentYear.value, currentMonth.value)
    }

    const prevMonth = () => {
      if (currentMonth.value === 1) {
        currentMonth.value = 12
        currentYear.value--
      } else {
        currentMonth.value--
      }
      loadCalendar()
    }

    const nextMonth = () => {
      if (currentMonth.value === 12) {
        currentMonth.value = 1
        currentYear.value++
      } else {
        currentMonth.value++
      }
      loadCalendar()
    }

    const viewDayEntry = (cell) => {
      if (cell.entryId) {
        router.push(`/journal/${cell.entryId}`)
      }
    }

    const getMoodEmoji = (mood) => JournalRepository.getMoodByValue(mood).emoji
    const getMoodLabel = (mood) => JournalRepository.getMoodByValue(mood).label
    const getMoodColor = (mood) => JournalRepository.getMoodByValue(mood).color

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const [y, m, d] = dateStr.split('-')
      return new Date(y, m - 1, d).toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric', month: 'long', year: 'numeric' })
    }

    onMounted(() => {
      loadEntries()
      loadCalendar()
    })

    return {
      entries, weekDays, currentYear, currentMonth,
      todayStr, todayFormatted, todayEntry, todayHasEntry,
      monthYearLabel, calendarCells,
      prevMonth, nextMonth, viewDayEntry,
      getMoodEmoji, getMoodLabel, getMoodColor, formatDate
    }
  }
}
</script>

<style scoped>
.calendar-has-entry {
  background-color: #e8f5e9;
  border: 2px solid #4CAF50;
  font-weight: bold;
}
.calendar-day:hover {
  opacity: 0.8;
}
</style>
