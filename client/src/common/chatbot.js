/**
 * Servicio del Chatbot de bienestar mental.
 * Usa Ollama con llama3.2 para proporcionar apoyo emocional.
 * Incluye system prompt especializado, contexto del usuario y detección de crisis.
 */
import JournalRepository from '@/repositories/JournalRepository'

const OLLAMA_URL = 'http://localhost:11434/api/chat'
const MODEL = 'llama3.2'

// ── Palabras clave de EMERGENCIA MÉDICA (peligro de muerte inminente) ──
const MEDICAL_EMERGENCY_KEYWORDS = [
  'pastillas', 'sobredosis', 'me tomé', 'me he tomado', 'me tome',
  'paracetamol', 'ibuprofeno', 'medicamento', 'veneno', 'lejía',
  'intoxic', 'envenen', 'sangr', 'me corté', 'me he cortado',
  'me tiré', 'me he tirado', 'tirarme', 'saltar', 'me lancé',
  'me hice daño', 'me he hecho daño', 'no paro de sangrar'
]

// ── Palabras clave de crisis emocional ──
const CRISIS_KEYWORDS = [
  'suicid', 'matarme', 'quitarme la vida', 'no quiero vivir',
  'acabar con todo', 'no vale la pena', 'mejor muerto',
  'hacerme daño', 'autolesion', 'cortarme',
  'no puedo más', 'no aguanto más', 'quiero desaparecer',
  'nadie me quiere', 'estoy solo', 'no tengo a nadie',
  'quiero morir', 'morirme', 'sin salida'
]

// ── Respuesta INMEDIATA para emergencia médica ──
const MEDICAL_EMERGENCY_RESPONSE = `🚨 ESTO ES UNA EMERGENCIA MÉDICA 🚨

LLAMA AL 112 AHORA MISMO. No esperes.

Mientras llegan:
1. No te provoques el vómito a menos que te lo digan desde el 112
2. Si estás con alguien, avísale ahora
3. Ten a mano el envase de lo que hayas tomado para decírselo a los médicos
4. No te duermas, intenta mantenerte despierto/a
5. Si puedes, abre la puerta de casa para que puedan entrar

📞 Emergencias: 112
📞 Información toxicológica: 915 620 420

Tu vida importa. Los médicos pueden ayudarte. Llama YA.`

// ── Footer de emergencia emocional ──
const EMERGENCY_FOOTER = `

Si sientes que estás en peligro, por favor contacta ahora:
📞 Emergencias: 112
📞 Teléfono de la Esperanza: 717 003 717
📞 Línea Nacional de Crisis: 024
Hablar con un profesional siempre es una buena decisión.`

// ── System prompt principal ──
const SYSTEM_PROMPT = `Eres un asistente de bienestar mental llamado MindTogether. Tu rol es proporcionar apoyo emocional, consejos prácticos y técnicas de autoayuda.

REGLAS FUNDAMENTALES:
1. SIEMPRE responde en español.
2. NUNCA uses asteriscos, markdown ni formato especial. Escribe en texto plano sin ningún tipo de formato. Nada de **negrita** ni *cursiva* ni viñetas con asterisco. Usa guiones (-) para listas.
3. NUNCA diagnostiques enfermedades mentales. No eres médico ni psicólogo.
4. Sé empático, cálido y comprensivo. Valida las emociones del usuario.
5. Ofrece técnicas prácticas y concretas.
6. Responde de forma breve y clara, máximo 3-4 párrafos cortos.
7. Si alguien describe una EMERGENCIA MÉDICA (ha tomado pastillas, se ha hecho daño físico, está sangrando), lo PRIMERO que dices es "LLAMA AL 112 AHORA MISMO" en mayúsculas. No preguntes cómo se siente. No redirijas. Dile que llame al 112 YA y da instrucciones de primeros auxilios básicas.
8. Si detectas crisis emocional (ideación suicida, autolesión), responde con empatía y proporciona los números: Emergencias 112, Teléfono de la Esperanza 717 003 717, Línea 024.
9. No pongas disclaimer al final de cada mensaje. El disclaimer ya aparece al inicio de la conversación.

TÉCNICAS QUE CONOCES:
- Respiración 4-7-8: Inspira 4 segundos, mantén 7 segundos, exhala 8 segundos
- Grounding 5-4-3-2-1: 5 cosas que ves, 4 que tocas, 3 que oyes, 2 que hueles, 1 que saboreas
- Técnica de relajación muscular progresiva
- Reestructuración cognitiva básica (CBT)
- Mindfulness y atención plena
- Journaling y escritura terapéutica
- Activación conductual: paseo, ejercicio, contacto social

PARA ANSIEDAD:
- Primero valida: "Es normal sentir ansiedad, no estás solo/a"
- Ofrece técnica inmediata de respiración o grounding
- Sugiere una acción concreta: beber agua, salir a caminar, llamar a alguien
- Si es frecuente, menciona brevemente que un profesional puede ayudar

PARA TRISTEZA:
- Valida la emoción sin minimizar
- Sugiere actividades de activación conductual
- Recuerda que los días difíciles son temporales
- Fomenta la conexión social

PARA ESTRÉS:
- Ayuda a identificar qué lo causa
- Ofrece técnicas de respiración y pausas activas
- Sugiere priorizar y poner límites
- Recomienda descanso`

// ── Palabras clave para detectar crisis EN EL DIARIO ──
const DIARY_CRISIS_KEYWORDS = [
  ...CRISIS_KEYWORDS,
  'suicid', 'matarme', 'morirme', 'morir', 'muerte',
  'no quiero seguir', 'no merece la pena', 'odio mi vida',
  'no tengo futuro', 'todo es inútil', 'me rindo',
  'nadie me echará de menos', 'estarían mejor sin mí',
  'autolesion', 'cortarme', 'hacerme daño', 'drogas', 'alcohol',
  'pastillas', 'sobredosis', 'no duermo', 'pesadillas',
  'ataques de pánico', 'no como', 'no salgo de casa'
]

/**
 * Construye el contexto del usuario a partir de sus datos del diario.
 * Además analiza si hay señales de crisis en las entradas recientes.
 * Devuelve { contextText, diaryHasCrisis, crisisDetails }
 */
async function buildUserContext() {
  const parts = []
  let diaryHasCrisis = false
  const crisisDetails = []

  try {
    const entries = await JournalRepository.findAll()
    const recent = entries.slice(0, 10) // Últimas 10 entradas para más contexto

    if (recent.length > 0) {
      parts.push('CONTEXTO DEL USUARIO - Entradas recientes de su diario emocional:')
      parts.push('(Esta información es CONFIDENCIAL del usuario, úsala para personalizar tu apoyo)')
      parts.push('')

      recent.forEach(e => {
        const moodInfo = JournalRepository.getMoodByValue(e.mood)
        const title = e.title || 'Sin título'
        const content = e.content || ''
        parts.push(`Fecha: ${e.date} | Ánimo: ${moodInfo.emoji} ${moodInfo.label}`)
        parts.push(`Título: ${title}`)
        if (content) {
          parts.push(`Escribió: "${content.substring(0, 300)}"`)
        }
        parts.push('')

        // Analizar si esta entrada tiene señales de crisis
        const textToAnalyze = `${title} ${content}`.toLowerCase()
        const foundKeywords = DIARY_CRISIS_KEYWORDS.filter(kw => textToAnalyze.includes(kw))
        if (foundKeywords.length > 0) {
          diaryHasCrisis = true
          crisisDetails.push({
            date: e.date,
            title: title,
            keywords: foundKeywords,
            mood: moodInfo.label
          })
        }
      })

      // Resumen de estado de ánimo general
      const moodCounts = { happy: 0, neutral: 0, sad: 0 }
      entries.forEach(e => { if (moodCounts[e.mood] !== undefined) moodCounts[e.mood]++ })
      const totalEntries = entries.length
      const sadPercentage = totalEntries > 0 ? Math.round((moodCounts.sad / totalEntries) * 100) : 0

      parts.push(`--- Resumen emocional ---`)
      parts.push(`Total de entradas: ${totalEntries}`)
      parts.push(`Días sintiéndose bien: ${moodCounts.happy} | Regular: ${moodCounts.neutral} | Mal: ${moodCounts.sad}`)

      if (sadPercentage > 60) {
        parts.push(`ATENCIÓN: El ${sadPercentage}% de sus entradas reflejan malestar. El usuario está pasándolo mal con frecuencia.`)
        diaryHasCrisis = true
      }

      // Si hay crisis detectada en el diario, añadir contexto especial
      if (diaryHasCrisis) {
        parts.push('')
        parts.push('⚠️ ALERTA IMPORTANTE: Se han detectado señales preocupantes en el diario del usuario:')
        crisisDetails.forEach(d => {
          parts.push(`- El ${d.date} escribió "${d.title}" (ánimo: ${d.mood}) - Señales: ${d.keywords.join(', ')}`)
        })
        parts.push('')
        parts.push('INSTRUCCIONES ESPECIALES por contenido del diario:')
        parts.push('- El usuario puede estar pasando por una situación muy difícil aunque su mensaje actual parezca normal.')
        parts.push('- Sé especialmente empático, cálido y esperanzador.')
        parts.push('- Si pregunta qué hacer o cómo estar mejor, dale motivos para seguir adelante, recuérdale que no está solo/a.')
        parts.push('- Ofrece SIEMPRE los teléfonos de ayuda: 112 (emergencias), 024 (crisis), 717 003 717 (Teléfono de la Esperanza).')
        parts.push('- Anímale a hablar con alguien de confianza o un profesional.')
        parts.push('- Valida su dolor pero dale esperanza: los momentos difíciles pasan, hay ayuda disponible, su vida importa.')
        parts.push('- NO minimices lo que siente. NO digas "no es para tanto". Escucha y apoya.')
      }
    }
  } catch (e) {
    // Sin contexto disponible
  }

  return {
    contextText: parts.length > 0 ? parts.join('\n') : '',
    diaryHasCrisis,
    crisisDetails
  }
}

/**
 * Detecta si es emergencia médica.
 */
function detectMedicalEmergency(text) {
  const lower = text.toLowerCase()
  return MEDICAL_EMERGENCY_KEYWORDS.some(kw => lower.includes(kw))
}

/**
 * Detecta si el mensaje contiene señales de crisis emocional.
 */
function detectCrisis(text) {
  const lower = text.toLowerCase()
  return CRISIS_KEYWORDS.some(kw => lower.includes(kw))
}

/**
 * Limpia asteriscos y formato markdown de la respuesta.
 */
function cleanMarkdown(text) {
  return text
    .replace(/\*\*(.+?)\*\*/g, '$1')
    .replace(/\*(.+?)\*/g, '$1')
    .replace(/__(.+?)__/g, '$1')
    .replace(/_(.+?)_/g, '$1')
    .replace(/^#{1,6}\s+/gm, '')
    .replace(/^\* /gm, '- ')
}

/**
 * Envía un mensaje al chatbot y recibe respuesta.
 */
async function sendMessage(conversationHistory, userMessage) {
  const isMedicalEmergency = detectMedicalEmergency(userMessage)
  const isCrisisMessage = detectCrisis(userMessage)

  // Si es emergencia médica, responder INMEDIATAMENTE sin esperar a Ollama
  if (isMedicalEmergency) {
    return { content: MEDICAL_EMERGENCY_RESPONSE, isCrisis: true }
  }

  // Construir contexto del usuario (incluye análisis del diario)
  const { contextText, diaryHasCrisis } = await buildUserContext()

  // La crisis puede venir del mensaje actual O del diario
  const isCrisis = isCrisisMessage || diaryHasCrisis

  // Construir system prompt con contexto
  let fullSystemPrompt = SYSTEM_PROMPT
  if (contextText) {
    fullSystemPrompt += `\n\n${contextText}`
  }
  if (isCrisisMessage) {
    fullSystemPrompt += `\n\nALERTA DIRECTA: El mensaje actual del usuario contiene señales de crisis. Responde con máxima empatía, valida sus sentimientos, dale motivos para seguir adelante, y di los números de ayuda: Emergencias 112, Teléfono de la Esperanza 717 003 717, Línea 024. No minimices lo que siente. Su seguridad es lo primero.`
  }

  // Construir mensajes para la API
  const messages = [
    { role: 'system', content: fullSystemPrompt },
    ...conversationHistory.filter(m => m.role !== 'system').map(m => ({
      role: m.role,
      content: m.content
    })),
    { role: 'user', content: userMessage }
  ]

  const response = await fetch(OLLAMA_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      model: MODEL,
      messages,
      stream: false,
      options: {
        temperature: 0.7,
        top_p: 0.9,
        num_predict: 500
      }
    })
  })

  if (!response.ok) {
    throw new Error(`Error de Ollama: ${response.status}`)
  }

  const data = await response.json()
  let content = cleanMarkdown(data.message.content)

  // Añadir footer de emergencia si es crisis emocional
  if (isCrisis) {
    content += EMERGENCY_FOOTER
  }

  return { content, isCrisis }
}

/**
 * Mensaje de bienvenida del chatbot.
 */
function getWelcomeMessage() {
  return `Hola 👋 Soy tu asistente de bienestar de MindTogether. Estoy aquí para escucharte y ayudarte con técnicas de relajación, consejos para gestionar emociones o simplemente para charlar.

¿Cómo te sientes hoy?

Recuerda: no soy un profesional de salud mental. Si necesitas ayuda urgente, llama al 024 o al 717 003 717.`
}

// ── Persistencia de conversaciones por usuario ──
const CHAT_STORAGE_PREFIX = 'mindtogether_chat_'

function saveConversation(userLogin, messages) {
  if (!userLogin) return
  try {
    localStorage.setItem(`${CHAT_STORAGE_PREFIX}${userLogin}`, JSON.stringify(messages))
  } catch (e) { /* localStorage lleno */ }
}

function loadConversation(userLogin) {
  if (!userLogin) return [{ role: 'assistant', content: getWelcomeMessage(), isCrisis: false }]
  try {
    const saved = localStorage.getItem(`${CHAT_STORAGE_PREFIX}${userLogin}`)
    if (saved) {
      const messages = JSON.parse(saved)
      if (Array.isArray(messages) && messages.length > 0) return messages
    }
  } catch (e) { /* error al leer */ }
  return [{ role: 'assistant', content: getWelcomeMessage(), isCrisis: false }]
}

function clearConversation(userLogin) {
  if (!userLogin) return
  localStorage.removeItem(`${CHAT_STORAGE_PREFIX}${userLogin}`)
}

export default {
  sendMessage,
  detectCrisis,
  detectMedicalEmergency,
  getWelcomeMessage,
  saveConversation,
  loadConversation,
  clearConversation,
  EMERGENCY_FOOTER
}
