import axios from 'axios'

/**
 * Repositorio para obtener noticias positivas de bienestar y salud mental.
 * Usa la API de GNews (free tier: 100 req/día) con fallback a tips locales.
 */

const GNEWS_API_KEY = '5aa43c3e7e879de978321d3b6dc3b1d1'
const GNEWS_BASE_URL = 'https://gnews.io/api/v4'

// Tips de bienestar locales como fallback
const localWellnessTips = [
  {
    title: 'Practicar la gratitud mejora tu bienestar emocional',
    description: 'Escribir tres cosas por las que estás agradecido cada día puede mejorar significativamente tu estado de ánimo y reducir el estrés.',
    source: 'Bienestar MindTogether',
    icon: '🙏',
    url: null
  },
  {
    title: 'La meditación reduce la ansiedad en un 60%',
    description: 'Estudios recientes demuestran que meditar solo 10 minutos al día puede reducir significativamente los niveles de ansiedad y mejorar la concentración.',
    source: 'Salud Mental',
    icon: '🧘',
    url: null
  },
  {
    title: 'Caminar 30 minutos mejora tu salud mental',
    description: 'Un paseo diario al aire libre libera endorfinas, mejora el sueño y reduce los síntomas de depresión.',
    source: 'Bienestar Físico',
    icon: '🚶',
    url: null
  },
  {
    title: 'Dormir bien es clave para la salud emocional',
    description: 'Mantener un horario regular de sueño de 7-8 horas fortalece tu capacidad para manejar el estrés y regular las emociones.',
    source: 'Hábitos Saludables',
    icon: '😴',
    url: null
  },
  {
    title: 'Las conexiones sociales protegen tu bienestar',
    description: 'Mantener relaciones significativas reduce el riesgo de depresión y mejora la resiliencia emocional.',
    source: 'Bienestar Social',
    icon: '🤝',
    url: null
  },
  {
    title: 'La respiración consciente calma tu mente',
    description: 'Técnicas de respiración como la 4-7-8 pueden reducir la ansiedad en minutos y ayudarte a recuperar la calma.',
    source: 'Mindfulness',
    icon: '🌬️',
    url: null
  },
  {
    title: 'El ejercicio es el mejor antidepresivo natural',
    description: 'La actividad física regular es tan efectiva como la medicación para tratar la depresión leve a moderada.',
    source: 'Ciencia del Bienestar',
    icon: '💪',
    url: null
  },
  {
    title: 'Escribir un diario mejora la claridad mental',
    description: 'Expresar tus pensamientos y emociones por escrito ayuda a procesar experiencias difíciles y encontrar perspectiva.',
    source: 'Psicología Positiva',
    icon: '📝',
    url: null
  },
  {
    title: 'La naturaleza sana: el poder del verde',
    description: 'Pasar tiempo en la naturaleza reduce los niveles de cortisol y mejora la creatividad y el bienestar general.',
    source: 'Ecoterapia',
    icon: '🌿',
    url: null
  },
  {
    title: 'Pequeños actos de bondad generan felicidad',
    description: 'Ayudar a los demás activa las mismas áreas del cerebro que se activan con recompensas, generando una sensación de bienestar duradera.',
    source: 'Psicología Positiva',
    icon: '💝',
    url: null
  }
]

export default {
  /**
   * Obtener noticias positivas sobre bienestar y salud mental.
   * Intenta la API de GNews y si falla, devuelve tips locales.
   * @param {number} count - Número de noticias a obtener (max 10 en free tier)
   * @returns {Array} Lista de noticias positivas
   */
  async getPositiveNews(count = 4) {
    try {
      const response = await axios.get(`${GNEWS_BASE_URL}/search`, {
        params: {
          q: 'bienestar OR salud mental OR mindfulness OR meditacion',
          lang: 'es',
          max: count,
          apikey: GNEWS_API_KEY
        },
        timeout: 5000
      })

      if (response.data && response.data.articles && response.data.articles.length > 0) {
        return response.data.articles.map(article => ({
          title: article.title,
          description: article.description || '',
          source: article.source?.name || 'Noticias',
          icon: '📰',
          url: article.url,
          image: article.image
        }))
      }

      // Si no hay resultados, usar tips locales
      return this.getLocalTips(count)
    } catch (error) {
      console.warn('No se pudieron cargar noticias externas, usando tips locales:', error.message)
      return this.getLocalTips(count)
    }
  },

  /**
   * Obtiene tips locales aleatorios como fallback.
   */
  getLocalTips(count = 4) {
    const shuffled = [...localWellnessTips].sort(() => Math.random() - 0.5)
    return shuffled.slice(0, count)
  }
}
