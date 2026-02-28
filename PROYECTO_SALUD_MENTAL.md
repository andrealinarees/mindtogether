# MindTogether - Documentacion del Proyecto

## Que es MindTogether

MindTogether es una plataforma de salud mental y bienestar comunitario. Proporciona herramientas para el seguimiento emocional, la gestión de habitos saludables, el establecimiento de metas, la participación en círculos de apoyo y la asistencia mediante inteligencia artificial.

No es un sustituto de la atención profesional de salud mental, sino una herramienta de apoyo complementaria.

Para los detalles técnicos (arquitectura, tecnologías, despliegue, puertos y configuración), consulta el [README.md](README.md).

---

## Qué puede hacer un usuario?

### Prácticas de bienestar

El usuario puede crear prácticas orientadas a la salud mental y hacer seguimiento de su cumplimiento. Las prácticas se organizan en 10 categorías precargadas.

- Crear prácticas con frecuencia diaria o semanal.
- Marcar cada día si se ha completado la práctica.
- El sistema calcula automáticamente las rachas de realización de las prácticas (racha actual y racha más larga).
- Añadir reflexiones sobre la dificultad de cada práctica.


### Metas de salud mental

Metas específicas orientadas al bienestar emocional, con categorías como Mindfulness, Actividad Física, Conexión Social, Regulación Emocional, Calidad del Sueño, Nutrición o Expresión Creativa.

- Cada meta puede tener hitos intermedios con mensajes de celebración.
- El usuario puede escribir su motivación personal, el beneficio esperado y el nivel de dificultad de la meta.
- Puede activar recordatorios y compartir progreso con sus círculos de apoyo.
- Hay una sección de analíticas que muestra tendencias, gráficos de progreso y distribución por categorías.

### Gamificación: recompensas 

Al cumplir metas, el usuario desbloquea recompensas automáticamente.

El usuario puede crear sus propias recompensas personalizadas vinculadas a metas (una comida, una experiencia, algo material...). Las recompensas empiezan bloqueadas y se desbloquean al completar la meta asociada.

### Diario emocional

El usuario puede registrar su estado de ánimo (feliz, neutral, triste) tanto desde la pantalla principal (dashboard) como desde el propio diario. Al registrarlo, el estado de ánimo se actualiza automáticamente en ambos sitios.

Además, puede escribir entradas diarias en un diario personal. La plataforma ofrece:

- Una vista de calendario que muestra visualmente los días según el estado de ánimo.
- Análisis de las tendencias emocionales a lo largo del tiempo.
- El chatbot de IA tiene acceso al diario para personalizar sus respuestas en función de la información que escriba el usuario.

### Círculos de apoyo

Los usuarios pueden crear o unirse a círculos de apoyos para compartir su experiencia. Al unirse, pueden elegir hacerlo de forma anónima, de modo que su identidad no sea visible para el resto de miembros.

Dentro de cada círculo de apoyo es posible publicar distintos tipos de entradas: reflexiones, consejos, motivaciones, propuestas, preguntas o logros.

Cada comunidad tiene roles (miembro y administrador). El administrador de la comunidad es quien la creó y puede gestionarla.

### Noticias positivas

El dashboard muestra un feed de noticias positivas sobre bienestar y salud mental en español, obtenidas de una API externa.

### Perfil

El usuario puede ver y editar su perfil: nombre, email, teléfono y ciudad.

---

## Qué puede hacer un administrador?

Los administradores acceden a un panel con funcionalidades adicionales:

- Ver estadísticas generales de la plataforma.
- Listar todos los usuarios, activar o desactivar cuentas y eliminar usuarios.
- Visualizar los hábitos de cualquier usuario con su detalle.
- Gestionar círculos de apoyo.

---

## Inteligencia Artificial: chatbot de apoyo emocional

La plataforma incluye un chatbot de IA que funciona como asistente de apoyo emocional. Utiliza el modelo llama3.2 a través de Ollama, ejecutándose de forma local (los datos no salen del equipo del usuario).

### Qué hace el chatbot?

- Ofrece apoyo emocional básico en español.
- Aplica tácnicas de terapia cognitivo-conductual.
- Propone ejercicios de mindfulness y respiración guiada.
- Practica escucha activa y validación emocional.
- Lee las entradas del diario del usuario para personalizar las respuestas, detectando patrones emocionales, situaciones recurrentes y estrategias que ya funcionaron en el pasado.
- Mantiene el historial de la conversación para dar continuidad.

### Detección de crisis

El chatbot analiza cada mensaje en busca de señales de crisis:

- **Emergencia médica** (sobredosis, autolesión, ingesta de sustancias): responde inmediatamente con un mensaje predefinido que incluye números de emergencia (112, 024, 915 620 420), sin esperar a la IA. La prioridad es la seguridad.
- **Crisis emocional** (ideación suicida, desesperanza extrema, deseo de desaparecer): adjunta automáticamente recursos de ayuda profesional al final de la respuesta de la IA.

El chatbot muestra en todo momento un aviso de que no reemplaza la atención profesional.

---

## APIs externas utilizadas

| API | Para qué se usa |
|-----|-----------------|
| GNews | Muestra noticias positivas de bienestar y salud mental en español |
| Ollama (local) | Ejecuta el modelo de IA llama3.2 para el chatbot de apoyo emocional |

---

## Casos de uso resumidos

1. **Registro y login**: El usuario crea una cuenta e inicia sesión.
2. **Gestionar prácticas de bienestar**: Crear prácticas categorizadas, marcarlas como completados...
3. **Definir metas**: Establecer metas personales y de salud mental con hitos, seguimiento de progreso.
4. **Desbloquear logros**: Ganar logros y puntos al cumplir metas y mantener rachas. Crear recompensas personalizadas.
5. **Escribir en el diario**: Registrar el estado de ánimo diario, ver la evolución en un calendario y analizar tendencias.
6. **Hablar con el chatbot**: Conversar con un asistente de IA que ofrece apoyo emocional y detecta situaciones de crisis.
7. **Participar en círculos de apoyo**: Unirse a círculos de apoyo y compartir reflexiones, consejos y logros.
8. **Administrar la plataforma**: Gestionar usuarios, revisar estadísticas y moderar los círculos de apoyo (solo administradores).

---

Disclaimer: MindTogether es una herramienta de apoyo y no reemplaza la atención profesional de salud mental. Si estás en crisis, contacta con los servicios de emergencia de tu país.
