# MindTogether - Plataforma de Salud Mental y Bienestar

## 🌟 Descripción

MindTogether es una plataforma integral de salud mental y bienestar que combina herramientas de autocuidado, establecimiento de metas, prácticas de bienestar y apoyo comunitario en un solo lugar.

## 🏗️ Arquitectura

### Microservicios Backend (Spring Boot)

1. **Gateway Service** (Puerto 8080)
   - API Gateway con Spring Cloud Gateway
   - Enrutamiento y balanceo de carga
   - Autenticación JWT

2. **User Service** (Puerto 9091)
   - Gestión de usuarios y autenticación
   - Perfiles de usuario
   - Roles y permisos

3. **Mental Health Goals Service** (Puerto 9095)
   - Gestión de metas de salud mental
   - Seguimiento de progreso
   - Hitos y logros
   - Sistema de gamificación con achievements

4. **Journal AI Service** (Puerto 9098) *[Pendiente implementación]*
   - Diario personal con IA
   - Análisis de sentimiento y patrones emocionales
   - Chatbot terapéutico con Ollama
   - Detección de riesgos
   - Reportes de bienestar

5. **Wellness Practices Service** (Puerto 9094)
   - Prácticas de mindfulness y meditación
   - Ejercicios de respiración
   - Contenido guiado
   - Seguimiento de sesiones

6. **Support Circles Service** (Puerto 9097)
   - Círculos de apoyo temáticos
   - Foros y discusiones
   - Eventos y actividades grupales
   - Check-ins semanales

### Frontend (Vue.js 3 + Vite)

- **Framework**: Vue.js 3 con Composition API
- **Build Tool**: Vite 7.0.6
- **Estado**: Pinia
- **Routing**: Vue Router 4
- **UI**: Bootstrap 5.3.8 + Bootstrap Icons
- **HTTP Client**: Axios

## 📦 Estructura del Proyecto

```
mindtogether/
├── gateway/                          # API Gateway
├── user/                            # Servicio de usuarios
├── mental-health-goals/             # Servicio de metas de salud mental
│   ├── model/
│   │   ├── MentalHealthGoal.java
│   │   ├── GoalMilestone.java
│   │   ├── Achievement.java
│   │   ├── ProgressSnapshot.java
│   │   ├── GoalCategory.java
│   │   └── AchievementType.java
│   ├── repository/
│   ├── service/
│   └── rest/
├── habits/                          # Servicio de prácticas de bienestar
├── community/                       # Servicio de círculos de apoyo
└── client/                          # Aplicación frontend
    ├── src/
    │   ├── components/
    │   │   ├── mentalHealthGoals/  # Componentes de metas
    │   │   ├── journal/            # Componentes de diario
    │   │   ├── wellness/           # Componentes de prácticas
    │   │   └── supportCircles/     # Componentes de círculos
    │   ├── repositories/
    │   │   ├── MentalHealthGoalRepository.js
    │   │   ├── AchievementRepository.js
    │   │   ├── JournalRepository.js
    │   │   ├── WellnessPracticeRepository.js
    │   │   └── SupportCircleRepository.js
    │   ├── views/
    │   │   └── DashboardView.vue
    │   └── router/
    │       └── index.js
    └── package.json
```

## 🎯 Funcionalidades Implementadas

### ✅ Backend

#### Mental Health Goals Service
- CRUD completo de metas de salud mental
- 14 categorías de metas (Mindfulness, Actividad Física, Conexión Social, etc.)
- Sistema de progreso con snapshots
- Hitos y celebraciones
- 26+ tipos de logros (achievements)
- Integración con prácticas de bienestar
- Analytics y estadísticas
- Compartir metas con círculos de apoyo

#### Funcionalidades por Implementar
- Journal AI Service completo
- Transformación de Habits → Wellness Practices
- Transformación de Community → Support Circles
- Integración con Ollama para IA conversacional

### ✅ Frontend

#### Estructura de Repositorios
Todos los repositorios frontend están completamente implementados con métodos para:

1. **MentalHealthGoalRepository.js**
   - CRUD de metas
   - Gestión de progreso y mood tracking
   - Hitos y analytics
   - 14 categorías con metadata

2. **AchievementRepository.js**
   - Gestión de logros
   - Estadísticas de gamificación
   - 26+ tipos de achievements
   - Sistema de featured/share

3. **JournalRepository.js**
   - Entradas de diario
   - Vista de calendario mensual
   - Búsqueda semántica
   - Análisis de patrones emocionales
   - Chatbot IA
   - Reportes de bienestar
   - Detección de riesgos

4. **WellnessPracticeRepository.js**
   - Prácticas de bienestar
   - Contenido guiado
   - 14 categorías de prácticas
   - Tracking de sesiones

5. **SupportCircleRepository.js**
   - Círculos de apoyo
   - 15 tipos temáticos
   - Posts, eventos, recursos
   - Check-ins y moderación

#### Rutas Configuradas
Todas las rutas están definidas y conectadas al router principal:

- `/mental-health-goals` - Lista y gestión de metas
- `/achievements` - Sistema de logros
- `/journal` - Diario con IA y calendario
- `/wellness` - Prácticas de bienestar
- `/support-circles` - Círculos de apoyo

#### Componentes por Crear
Los siguientes componentes están planificados pero pendientes de implementación:

**Mental Health Goals**
- MentalHealthGoalList.vue
- MentalHealthGoalForm.vue
- MentalHealthGoalDetail.vue
- MentalHealthGoalAnalytics.vue
- AchievementList.vue

**Journal**
- JournalHome.vue
- JournalCalendar.vue
- JournalEntryForm.vue
- JournalEntryDetail.vue
- JournalAnalysis.vue
- JournalChatbot.vue
- JournalReports.vue

**Wellness Practices**
- WellnessPracticeList.vue
- WellnessPracticeForm.vue
- WellnessPracticeDetail.vue
- GuidedContentLibrary.vue
- GuidedContentDetail.vue
- WellnessRecommendations.vue

**Support Circles**
- SupportCircleList.vue
- SupportCircleDiscover.vue
- SupportCircleForm.vue
- SupportCircleDetail.vue
- SupportCircleMembers.vue
- SupportCircleEvents.vue
- SupportCircleResources.vue

## 🚀 Cómo Ejecutar

### Requisitos Previos

- Java 17 o superior
- Node.js 18 o superior
- Docker y Docker Compose
- Maven 3.8+

### Backend

```bash
# Iniciar bases de datos y servicios con Docker Compose
docker-compose up -d

# Compilar todos los servicios
mvn clean install

# O iniciar cada servicio individualmente
cd gateway && mvn spring-boot:run
cd user && mvn spring-boot:run
cd mental-health-goals && mvn spring-boot:run
cd habits && mvn spring-boot:run
cd community && mvn spring-boot:run
```

### Frontend

```bash
cd client
npm install
npm run dev
```

La aplicación estará disponible en `http://localhost:1234`

## 🗄️ Bases de Datos

Cada microservicio tiene su propia base de datos PostgreSQL:

- **users_db** (Puerto 5432) - Base de datos de usuarios
- **habits_db** (Puerto 5456) - Base de datos de prácticas de bienestar
- **mental_health_goals_db** (Puerto 5458) - Base de datos de metas
- **community_db** (Puerto 5457) - Base de datos de círculos de apoyo

## 🎨 Categorías de Metas de Salud Mental

1. 🧘 **Mindfulness** - Atención plena
2. 🏃 **Actividad Física** - Ejercicio y movimiento
3. 💬 **Conexión Social** - Relaciones interpersonales
4. 🎭 **Regulación Emocional** - Gestión de emociones
5. 😴 **Calidad del Sueño** - Higiene del sueño
6. 🥗 **Nutrición** - Alimentación saludable
7. 🎨 **Expresión Creativa** - Arte y creatividad
8. 📚 **Crecimiento Personal** - Desarrollo personal
9. 🧘‍♀️ **Gestión del Estrés** - Reducción del estrés
10. 🙏 **Gratitud** - Práctica de agradecimiento
11. 💆 **Autocuidado** - Cuidado personal
12. 🩺 **Apoyo Terapéutico** - Terapia y apoyo profesional
13. ✍️ **Escritura Terapéutica** - Journaling
14. ⭐ **Otro** - Metas personalizadas

## 🏆 Sistema de Logros (Achievements)

El sistema de gamificación incluye más de 26 tipos de logros:

- **Primeros pasos**: Primera meta creada, primera entrada de diario
- **Hitos de metas**: 5, 10, 25, 50, 100 metas completadas
- **Rachas**: 7, 30, 90, 365 días consecutivos
- **Maestrías**: Experto en categorías específicas
- **Sociales**: Compartir logros, ayudar a otros
- **Especiales**: Logros únicos y eventos

## 🔐 Autenticación

El sistema usa JWT (JSON Web Tokens) para autenticación:

1. Login en `/api/users/login`
2. Token JWT devuelto en la respuesta
3. Token incluido en header `Authorization: Bearer <token>`
4. Refrescado automático del token

## 📊 Estado del Proyecto

### Completado ✅
- Arquitectura de microservicios configurada
- Mental Health Goals Service (backend completo)
- Todos los repositorios frontend
- Sistema de rutas completo
- Navegación actualizada
- Dashboard de salud mental

### En Progreso 🔄
- Creación de componentes Vue.js
- Vistas de cada módulo
- Integración completa frontend-backend

### Pendiente 📋
- Journal AI Service (backend)
- Transformación Habits → Wellness Practices
- Transformación Community → Support Circles
- Integración con Ollama
- Tests unitarios y de integración
- Documentación de API (Swagger)
- Despliegue en producción

## 🤝 Contribuir

Este proyecto está en desarrollo activo. Para contribuir:

1. Fork el repositorio
2. Crea una rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT.

## 🆘 Recursos de Ayuda

En caso de crisis, contacta con:

- **Teléfono de la Esperanza**: 717 003 717
- **Línea Nacional (España)**: 024
- **Web**: https://www.telefonodelaesperanza.org

## 👥 Equipo

Desarrollado con ❤️ por el equipo de MindTogether

---

**Nota**: Este es un proyecto en desarrollo. Muchas funcionalidades están planificadas pero aún no implementadas. Consulta la sección "Estado del Proyecto" para conocer el progreso actual.
