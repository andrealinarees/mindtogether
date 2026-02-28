# 🧠💚 MindTogether - Plataforma de Salud Mental y Bienestar Comunitario

## 📋 Descripción del Proyecto

**MindTogether** es una plataforma de código abierto diseñada para proporcionar apoyo mutuo en salud mental, permitiendo a los usuarios realizar seguimiento de su bienestar emocional, participar en comunidades de apoyo, y recibir asistencia inteligente mediante IA para la detección temprana de riesgos y apoyo personalizado.

### Misión
Destigmatizar la salud mental proporcionando herramientas gratuitas, accesibles y basadas en comunidad para el seguimiento y mejora del bienestar emocional.

### Valores Principales
- ✅ **Privacidad y Anonimato**: Protección máxima de datos sensibles
- ✅ **Accesibilidad**: Gratuito y de código abierto
- ✅ **Apoyo Comunitario**: Círculos de apoyo seguros
- ✅ **Detección Temprana**: IA para identificar señales de riesgo
- ✅ **Empoderamiento**: Herramientas de auto-gestión del bienestar

---

## 🏗️ Arquitectura de Microservicios

### Microservicios Adaptados

#### 1. **wellness-practices**
**Puerto**: 9094  
**Base de datos**: PostgreSQL `wellness_db`

**Responsabilidades**:
- Gestión de prácticas de bienestar (meditación, ejercicio, journaling, etc.)
- Categorías de prácticas (mindfulness, físicas, sociales, creativas)
- Seguimiento de completitud y streaks
- Sugerencias personalizadas según estado de ánimo -> logear como te sientes en ese dia con emoticonos y que simplemente te devuelve una respuesta breve en una frase

**Entidades principales**:
- `WellnessPractice`: Práctica de bienestar del usuario
- `PracticeCompletion`: Registro de completitud
- `PracticeCategory`: Categorías (Mindfulness, Ejercicio, Social, etc.)
- `MoodLog`: Registro de estado de ánimo antes/después de práctica

---

#### 2. **mental-health-goals** 
**Puerto**: 9095  
**Base de datos**: PostgreSQL `mental_health_goals_db`

**Responsabilidades**:
- Objetivos de bienestar emocional
- Hitos de progreso en salud mental
- Metas relacionadas con prácticas de bienestar
- Sistema de logros y badges

**Entidades principales**:
- `MentalHealthGoal`: Meta de bienestar (ej: "Meditar 5 días esta semana")
- `GoalMilestone`: Hitos intermedios
- `Achievement`: Logros desbloqueados
- `ProgressSnapshot`: Instantáneas de progreso

---

#### 3. **support-circles** 
**Puerto**: 9097  
**Base de datos**: PostgreSQL `support_circles_db`

**Responsabilidades**:
- Comunidades de apoyo por temáticas
- Publicaciones y recursos compartidos
- Moderación de contenido -> tendencias problemáticas?

**Entidades principales**:
- `SupportCircle`: Círculo de apoyo (Ansiedad, Depresión, Duelo, etc.)
- `CircleMember`: Miembros del círculo
- `CirclePost`: Publicaciones anónimas/identificadas
- `Resource`: Recursos compartidos (artículos, videos, ejercicios)
- `SafetyReport`: Reportes de contenido inapropiado

---

#### 4. **user-service** 
**Puerto**: 9091  
**Base de datos**: PostgreSQL `users_db`

**Responsabilidades**:
- Gestión de usuarios y autenticación
- Perfiles de usuario (pueden ser anónimos)
- Preferencias de privacidad
- Historial de bienestar general

**Nuevos campos en perfil**:
- `anonymousMode`: boolean - modo completamente anónimo
- `shareProgressWithCircles`: boolean
- `emergencyContact`: String (opcional)
- `therapistCode`: String - código para compartir progreso con terapeuta
- `riskLevel`: Enum (LOW, MEDIUM, HIGH) - calculado por IA

---

#### 5. **journal-ai-service** ⭐ NUEVO MICROSERVICIO
**Puerto**: 9098  
**Base de datos**: PostgreSQL `journal_db` 
**Tecnología IA**: Ollama 

**Acceso a datos de otros microservicios**:
- ✅ Acceso a `wellness-practices` (puerto 9094): Para conocer las prácticas de bienestar del usuario y correlacionarlas con su estado emocional
- ✅ Acceso a `mental-health-goals` (puerto 9095): Para entender objetivos y medir progreso
- ✅ Acceso a `support-circles` (puerto 9097): Para contextualizar interacciones sociales del usuario

**Responsabilidades principales**:

##### A. 📓 **Diario Digital con Calendario Inteligente**

**Sistema de Calendario:**
- Vista de calendario mensual/semanal con indicadores visuales de cada día
- Cada día puede tener múltiples entradas de diario
- Estados visuales del día:
  - 🟢 Verde: Día con entrada y mood positivo (8-10/10)
  - 🟡 Amarillo: Día con entrada y mood neutral (5-7/10)
  - 🔴 Rojo: Día con entrada y mood negativo (1-4/10)
  - ⚪ Gris: Día sin entrada
  - ⭐ Estrella: Día con logro especial o hito importante
  - 🚨 Alerta: Día con detección de riesgo
- Navegación rápida entre fechas
- Resumen mensual: "Este mes escribiste 15 días, promedio de mood: 7/10"
- Timeline cronológica de todas las entradas
- Comparativa mes a mes

**Entradas de Diario:**
- Registro de entradas por fecha específica (pasada, presente o futura)
- Editor de texto enriquecido con formato markdown
- Posibilidad de múltiples entradas por día (mañana, tarde, noche)
- Campos de cada entrada:
  - Fecha y hora específica
  - Momento del día (MORNING, AFTERNOON, EVENING, NIGHT)
  - Título opcional
  - Contenido (reflexiones libres) - encriptado
  - Mood antes de escribir (1-10)
  - Mood después de escribir (1-10)
  - Tags manuales del usuario
  - Tags automáticos generados por IA
  - Imagen adjunta opcional
  - Contexto: ubicación emocional ("casa", "trabajo", "parque")
  - Personas mencionadas (anónimas: "madre", "amigo1", "terapeuta")
  - Eventos importantes del día
  - Marcador de favorito
  - Control de privacidad: ¿Es accesible para la IA?

**Análisis Automático en cada entrada:**
- Análisis de sentimiento automático (score -1.0 a 1.0)
- Detección de patrones emocionales semanales/mensuales
- Identificación de triggers recurrentes
- Correlación entre eventos y estados de ánimo
- Detección de nivel de riesgo (NONE, LOW, MEDIUM, HIGH, CRITICAL)
- Generación de tags automáticos basados en contenido
- Sugerencias de reflexión personalizadas
- Búsqueda semántica en entradas previas: "¿Cuándo me sentí así antes?"

**Contexto para la IA del Chat:**
- Todas las entradas del diario forman el "perfil psicoemocional" del usuario
- La IA tiene acceso completo (con permiso del usuario) a:
  - Historial completo de entradas del diario
  - Patrones emocionales identificados a largo plazo
  - Triggers y situaciones recurrentes
  - Progreso emocional a lo largo del tiempo
  - Temas frecuentes de preocupación
  - Estrategias que han funcionado anteriormente
  - Correlación entre prácticas de bienestar y mood
  - Información de wellness-practices activas
  - Objetivos actuales de mental-health-goals
- Este contexto permite al chatbot:
  - Personalizar respuestas según la historia única del usuario
  - Recordar conversaciones y situaciones previas mencionadas en el diario
  - Detectar cambios o regresiones en el estado emocional
  - Ofrecer recomendaciones basadas en lo que funcionó antes
  - Validar emociones con conocimiento de la situación completa
  - Hacer seguimiento de temas específicos a lo largo del tiempo
  - Celebrar progreso y recordar victorias pasadas

##### B. 📊 **Reportes de Bienestar**
- Generación de reportes semanales/mensuales de estado emocional
- Gráficos de tendencias de humor
- Identificación de triggers y patrones
- Correlación entre prácticas y mejoras en bienestar
- Exportación de reportes para profesionales de salud

##### C. 📅 **Planning Personalizado**
- Generación de planes semanales de bienestar
- Sugerencias de prácticas según historial
- Adaptación dinámica según progreso
- Recordatorios inteligentes
- Integración con calendario

##### D. 🚨 **Detección de Riesgos (Crisis Detection)**
- Análisis de entradas de diario en busca de señales de alarma
- Detección de lenguaje indicativo de:
  - Ideación suicida
  - Autolesión
  - Crisis de ansiedad severa
  - Abuso de sustancias
  - Comportamientos de riesgo
- Alertas graduales según nivel de riesgo
- Sugerencias de recursos de ayuda inmediata
- (Opcional) Notificación a contacto de emergencia

##### E. 💬 **Asistente Conversacional**
- Chatbot de apoyo emocional básico
- No reemplaza terapia profesional (disclaimer)
- Técnicas de CBT (Cognitive Behavioral Therapy)
- Ejercicios de mindfulness guiados
- Validación emocional y escucha activa

**Entidades principales**:
```java
- JournalEntry: Entrada de diario
  - id: Long
  - userId: String
  - content: String (encriptado)
  - mood: Enum (VERY_SAD, SAD, NEUTRAL, GOOD, VERY_GOOD)
  - sentimentScore: Float (-1.0 a 1.0)
  - riskLevel: Enum (NONE, LOW, MEDIUM, HIGH, CRITICAL)
  - detectedTriggers: List<String>
  - createdAt: Timestamp
  - isPrivate: Boolean

- WellnessReport: Reporte generado
  - id: Long
  - userId: String
  - reportType: Enum (WEEKLY, MONTHLY, CUSTOM)
  - generatedContent: String (markdown)
  - moodTrend: String
  - recommendations: List<String>
  - periodStart: Date
  - periodEnd: Date
  - generatedAt: Timestamp

- WellnessPlan: Plan personalizado
  - id: Long
  - userId: String
  - weekNumber: Integer
  - practices: List<PlannedPractice>
  - goals: List<String>
  - motivationalMessage: String
  - createdAt: Timestamp

- RiskAlert: Alerta de riesgo
  - id: Long
  - userId: String
  - journalEntryId: Long
  - riskType: Enum (SUICIDAL_IDEATION, SELF_HARM, SEVERE_ANXIETY, SUBSTANCE_ABUSE)
  - severity: Enum (LOW, MEDIUM, HIGH, CRITICAL)
  - detectedPhrases: List<String>
  - actionTaken: String
  - resolvedAt: Timestamp (nullable)
  - createdAt: Timestamp

- ChatConversation: Conversación con IA
  - id: Long
  - userId: String
  - messages: List<ChatMessage>
  - startedAt: Timestamp
  - endedAt: Timestamp (nullable)
```

---

## 🔌 APIs Externas a Integrar

### 1. **Mental Health America API**
**Alternativa**: Base de datos propia con:
- Teléfonos de emergencia por país (112, 911, etc.)
- Líneas de atención en crisis (Teléfono de la Esperanza, etc.)
- Chat de crisis 24/7
- Servicios de terapia online gratuitos/accesibles

### 2. **News API** (filtrado positivo) -> alternativa positiva
**Propósito**: Noticias constructivas para mejorar estado de ánimo
- **Endpoint**: `https://newsapi.org/v2/everything`
- **Filtros**: 
  - Keywords: "good news", "positive", "heartwarming", "inspiring"
  - Excluir: violence, disaster, pandemic, war
- **Uso**: Feed diario de noticias positivas en dashboard

### 3. **Quotes API**
**Propósito**: Frases motivacionales y de apoyo
- **Endpoint**: `https://api.quotable.io/random`
- **Categorías**: motivation, wellness, mindfulness, happiness
- **Uso**: Quotes diarios en dashboard y notificaciones


**⚠️ DISCLAIMER IMPORTANTE**:
Este software es una herramienta de apoyo y NO reemplaza la atención profesional de salud mental. Si estás en crisis, por favor contacta servicios de emergencia o líneas de atención en crisis de tu país. Este proyecto está desarrollado con las mejores intenciones, pero no nos hacemos responsables de decisiones tomadas basándose únicamente en la información proporcionada por la IA.
