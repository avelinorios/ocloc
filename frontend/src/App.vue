<template>
  <div class="container">
    <h1>⏰ Mi Horario 1.0</h1>
    <p class="subtitle">Gestiona tu tiempo de trabajo</p>

    <div v-if="error" class="error">{{ error }}</div>

    <div class="status-card" :class="{ inactive: !currentEntry }">
      <div class="status-text">{{ statusText }}</div>
      <div class="hours-counter">{{ hoursCounter }}</div>
      <div class="hours-label">Horas restantes</div>
    </div>

    <button 
      class="start-button" 
      :class="{ stop: currentEntry }"
      @click="toggleDay"
      :disabled="loading"
    >
      {{ buttonText }}
    </button>

    <div class="info-section">
      <div class="info-row">
        <span>Horas trabajadas hoy:</span>
        <strong>{{ workedHours }}h</strong>
      </div>
      <div class="info-row">
        <span>Inicio:</span>
        <strong>{{ startTime }}</strong>
      </div>
      <div class="info-row">
        <span>Estado:</span>
        <strong>{{ currentStatus }}</strong>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const WORK_DAY_HOURS = 8
const API_BASE_URL = import.meta.env.VITE_API_URL || (import.meta.env.DEV ? 'http://localhost:8080' : '')
const currentEntry = ref(null)
const workedHours = ref('0.00')
const loading = ref(false)
const error = ref('')
const currentTime = ref(Date.now())
let updateInterval = null

const statusText = computed(() => 
  currentEntry.value ? 'Día en curso' : 'Día no iniciado'
)

const hoursCounter = computed(() => {
  if (!currentEntry.value) {
    return formatDuration(WORK_DAY_HOURS)
  }
  const clockIn = currentEntry.value.clock_in
  const start = new Date(clockIn)
  const elapsed = (currentTime.value - start.getTime()) / (1000 * 60 * 60)
  const remaining = Math.max(0, WORK_DAY_HOURS - elapsed)
  return formatDuration(remaining)
})

const buttonText = computed(() => 
  currentEntry.value ? 'Finalizar Día' : 'Iniciar Día'
)

const currentStatus = computed(() => 
  currentEntry.value ? 'Activo' : 'Inactivo'
)

const startTime = computed(() => {
  if (!currentEntry.value) return '--:--'
  const clockIn = currentEntry.value.clock_in
  return formatTime(new Date(clockIn))
})

async function checkStatus() {
  try {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const tomorrow = new Date(today)
    tomorrow.setDate(tomorrow.getDate() + 1)

    const response = await fetch(
      `${API_BASE_URL}/api/entries?from=${today.toISOString()}&to=${tomorrow.toISOString()}`
    )
    
    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`Error ${response.status}: ${errorText || 'Error desconocido'}`)
    }
    
    const entries = await response.json()
    currentEntry.value = entries.find(e => !e.clock_out) || null
    
    if (currentEntry.value) {
      startTimer()
      await updateWorkedHours()
    }
  } catch (err) {
    error.value = 'Error al verificar estado: ' + err.message
    console.error('Error en checkStatus:', err)
  }
}

async function toggleDay() {
  if (!currentEntry.value) {
    await startDay()
  } else {
    await endDay()
  }
}

async function startDay() {
  try {
    loading.value = true
    error.value = ''
    const response = await fetch(`${API_BASE_URL}/api/clock-in`, {
      method: 'POST'
    })

    if (!response.ok) {
      let errorMessage = 'Error al iniciar el día'
      try {
        const data = await response.json()
        errorMessage = data.error || errorMessage
      } catch (e) {
        const text = await response.text()
        errorMessage = text || errorMessage
      }
      throw new Error(errorMessage)
    }

    currentEntry.value = await response.json()
    startTimer()
    await updateWorkedHours()
  } catch (err) {
    error.value = 'Error: ' + err.message
    console.error('Error en startDay:', err)
  } finally {
    loading.value = false
  }
}

async function endDay() {
  try {
    loading.value = true
    error.value = ''
    const response = await fetch(`${API_BASE_URL}/api/clock-out`, {
      method: 'POST'
    })

    if (!response.ok) {
      let errorMessage = 'Error al finalizar el día'
      try {
        const data = await response.json()
        errorMessage = data.error || errorMessage
      } catch (e) {
        const text = await response.text()
        errorMessage = text || errorMessage
      }
      throw new Error(errorMessage)
    }

    currentEntry.value = null
    stopTimer()
    await updateWorkedHours()
  } catch (err) {
    error.value = 'Error: ' + err.message
    console.error('Error en endDay:', err)
  } finally {
    loading.value = false
  }
}

async function updateWorkedHours() {
  try {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const tomorrow = new Date(today)
    tomorrow.setDate(tomorrow.getDate() + 1)

    const response = await fetch(
      `${API_BASE_URL}/api/summary?from=${today.toISOString()}&to=${tomorrow.toISOString()}`
    )
    
    if (!response.ok) {
      console.error('Error al obtener resumen:', response.status)
      return
    }
    
    const summary = await response.json()
    const todaySummary = summary.find(s => s.date === formatDate(today))

    if (todaySummary) {
      workedHours.value = parseFloat(todaySummary.hours).toFixed(2)
    } else {
      workedHours.value = '0.00'
    }
  } catch (err) {
    console.error('Error updating worked hours:', err)
  }
}

function startTimer() {
  stopTimer()
  currentTime.value = Date.now()
  updateWorkedHours()
  updateInterval = setInterval(() => {
    currentTime.value = Date.now()
    updateWorkedHours()
  }, 1000)
}

function stopTimer() {
  if (updateInterval) {
    clearInterval(updateInterval)
    updateInterval = null
  }
}

function formatDuration(hours) {
  const totalSeconds = Math.max(0, Math.floor(hours * 3600))
  const h = Math.floor(totalSeconds / 3600)
  const m = Math.floor((totalSeconds % 3600) / 60)
  const s = totalSeconds % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

function formatTime(date) {
  return date.toLocaleTimeString('es-ES', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

function formatDate(date) {
  return date.toISOString().split('T')[0]
}

onMounted(() => {
  checkStatus()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
  max-width: 500px;
  width: 100%;
  text-align: center;
}

h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 2rem;
}

.subtitle {
  color: #666;
  margin-bottom: 40px;
  font-size: 0.9rem;
}

.status-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 30px;
}

.status-card.inactive {
  background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
}

.status-text {
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 15px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.hours-counter {
  font-size: 3.5rem;
  font-weight: bold;
  margin: 20px 0;
  font-variant-numeric: tabular-nums;
}

.hours-label {
  font-size: 1rem;
  opacity: 0.9;
}

.start-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 18px 40px;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.start-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

.start-button:active:not(:disabled) {
  transform: translateY(0);
}

.start-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.start-button.stop {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.start-button.stop:hover:not(:disabled) {
  box-shadow: 0 10px 25px rgba(245, 87, 108, 0.4);
}

.info-section {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #e0e0e0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  color: #666;
}

.info-row strong {
  color: #333;
}

.error {
  background: #fee;
  color: #c33;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 0.9rem;
}
</style>
