# MindTogether - Documentacion del Proyecto

## Que es MindTogether

MindTogether es una plataforma de salud mental y bienestar comunitario. Proporciona herramientas para el seguimiento emocional, la gestion de habitos saludables, el establecimiento de metas, la participacion en comunidades de apoyo y la asistencia mediante inteligencia artificial.

No es un sustituto de la atencion profesional de salud mental, sino una herramienta de apoyo complementaria.

Para los detalles tecnicos (arquitectura, tecnologias, despliegue, puertos y configuracion), consulta el [README.md](README.md).

---

## Que puede hacer un usuario

### Prácticas de bienestar

El usuario puede crear prácticas orientadas a la salud mental y hacer seguimiento de su cumplimiento. Las prácticas se organizan en 10 categorias precargadas.

- Crear prácticas con frecuencia diaria o semanal.
- Marcar cada dia si se ha completado la práctica.
- El sistema calcula automaticamente las rachas (racha actual y racha mas larga).
- Añadir reflexiones sobre la dificultad de cada prácticas.


### Metas de salud mental

Metas especificas orientadas al bienestar emocional, con categorias como Mindfulness, Actividad Fisica, Conexion Social, Regulacion Emocional, Calidad del Sueño, Nutricion o Expresion Creativa.

- Cada meta puede tener hitos intermedios con mensajes de celebracion.
- El usuario escribe su motivacion personal, el beneficio esperado y el nivel de dificultad.
- Puede activar recordatorios y compartir progreso con circulos de apoyo.
- Una seccion de analiticas muestra tendencias, graficos de progreso y distribucion por categorias.

### Gamificacion: recompensas 

Al cumplir metas, el usuario desbloquea recompensas automaticamente.

El usuario puede crear sus propias recompensas personalizadas vinculadas a metas (una comida, una experiencia, algo material...). Las recompensas empiezan bloqueadas y se desbloquean al completar la meta asociada.

### Diario emocional

El usuario puede registrar su estado de animo (feliz, neutral, triste) tanto desde la pantalla principal (dashboard) como desde el propio diario. Al registrarlo, el estado de animo se actualiza automaticamente en ambos sitios.

Ademas, puede escribir entradas diarias en un diario personal. La plataforma ofrece:

- Una vista de calendario que muestra visualmente los dias segun el estado de animo.
- Analisis de tendencias emocionales a lo largo del tiempo.
- El chatbot de IA tiene acceso al diario para personalizar sus respuestas.

### Comunidades de apoyo

Los usuarios pueden crear o unirse a comunidades para compartir su experiencia. Al unirse, pueden elegir hacerlo de forma anonima, de modo que su identidad no sea visible para el resto de miembros.

Dentro de cada comunidad es posible publicar distintos tipos de entradas: reflexiones, consejos, motivaciones, propuestas, preguntas o logros. Los usuarios pueden editar sus propias publicaciones y dar me gusta a las de otros miembros.

Cada comunidad tiene roles (miembro y administrador). El administrador de la comunidad es quien la creo y puede gestionarla.

### Noticias positivas

El dashboard muestra un feed de noticias positivas sobre bienestar y salud mental en español, obtenidas de una API externa. Si la API no esta disponible, se muestran consejos de bienestar predefinidos.

### Perfil

El usuario puede ver y editar su perfil: nombre, email, telefono y ciudad.

---

## Que puede hacer un administrador

Los administradores acceden a un panel con funcionalidades adicionales:

- Ver estadisticas generales de la plataforma.
- Listar todos los usuarios, activar o desactivar cuentas y eliminar usuarios.
- Visualizar los habitos de cualquier usuario con su detalle.
- Gestionar comunidades.

---

## Inteligencia Artificial: chatbot de apoyo emocional

La plataforma incluye un chatbot de IA que funciona como asistente de apoyo emocional. Utiliza el modelo llama3.2 a traves de Ollama, ejecutandose de forma local (los datos no salen del equipo del usuario).

### Que hace el chatbot

- Ofrece apoyo emocional basico en español.
- Aplica tecnicas de terapia cognitivo-conductual.
- Propone ejercicios de mindfulness y respiracion guiada.
- Practica escucha activa y validacion emocional.
- Lee las entradas del diario del usuario para personalizar las respuestas, detectando patrones emocionales, situaciones recurrentes y estrategias que funcionaron en el pasado.
- Mantiene el historial de la conversacion para dar continuidad.

### Deteccion de crisis

El chatbot analiza cada mensaje en busca de señales de crisis:

- **Emergencia medica** (sobredosis, autolesion, ingesta de sustancias): responde inmediatamente con un mensaje predefinido que incluye numeros de emergencia (112, 024, 915 620 420), sin esperar a la IA. La prioridad es la seguridad.
- **Crisis emocional** (ideacion suicida, desesperanza extrema, deseo de desaparecer): adjunta automaticamente recursos de ayuda profesional al final de la respuesta de la IA.

El chatbot muestra en todo momento un aviso de que no reemplaza la atencion profesional.

---

## APIs externas utilizadas

| API | Para que se usa |
|-----|-----------------|
| GNews | Muestra noticias positivas de bienestar y salud mental en español |
| Ollama (local) | Ejecuta el modelo de IA llama3.2 para el chatbot de apoyo emocional |

---

## Casos de uso resumidos

1. **Registro y login**: El usuario crea una cuenta e inicia sesion.
2. **Gestionar prácticas de bienestar**: Crear prácticas categorizadas, marcarlas como completados...
3. **Definir metas**: Establecer metas personales y de salud mental con hitos, seguimiento de progreso.
4. **Desbloquear logros**: Ganar logros y puntos al cumplir metas y mantener rachas. Crear recompensas personalizadas.
5. **Escribir en el diario**: Registrar el estado de animo diario, ver la evolucion en un calendario y analizar tendencias.
6. **Hablar con el chatbot**: Conversar con un asistente de IA que ofrece apoyo emocional y detecta situaciones de crisis.
7. **Participar en comunidades**: Unirse a comunidades y compartir reflexiones, consejos y logros.
8. **Administrar la plataforma**: Gestionar usuarios, revisar estadisticas y moderar comunidades (solo administradores).

---

Disclaimer: MindTogether es una herramienta de apoyo y no reemplaza la atencion profesional de salud mental. Si estas en crisis, contacta con los servicios de emergencia de tu pais.
