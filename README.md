# MindTogether

Plataforma web de salud mental y bienestar comunitario basada en una arquitectura de microservicios con Spring Boot y Vue.js.

## Descripcion

MindTogether es una plataforma orientada al apoyo mutuo en salud mental. Permite a los usuarios realizar un seguimiento de su bienestar emocional, gestionar habitos y metas, participar en comunidades de apoyo, llevar un diario emocional con analisis de IA, y recibir asistencia mediante un chatbot conversacional.

## Arquitectura

El proyecto sigue una arquitectura de microservicios con service discovery mediante Consul. Cada microservicio tiene su propia base de datos PostgreSQL (patron database-per-service) y se comunican entre si a traves de OpenFeign. El gateway centraliza todas las peticiones y gestiona la autenticacion JWT.

| Servicio | Puerto | Descripcion |
|----------|--------|-------------|
| `gateway` | 8080 | API Gateway, validacion JWT y enrutamiento | 
| `user` | 9091 | Gestion de usuarios, autenticacion y perfiles |
| `habits` | 9094 | Gestion de habitos con integracion meteorologica |
| `goals` | 9095 | Metas, logros, recompensas y snapshots de progreso |
| `community` | 9097 | Comunidades, miembros y publicaciones |
| `client` | 1234 | Frontend SPA |
| `consul` | 8500 | Service discovery y configuracion |

Cada microservicio backend tiene su propia base de datos PostgreSQL:

| Base de datos | Puerto externo | Servicio |
|---------------|----------------|----------|
| `users` | 5455 | user |
| `habits_db` | 5457 | habits |
| `mental_health_goals_db` | 5458 | goals |
| `community_db` | 5459 | community |

## Requisitos previos

- [Docker](https://www.docker.com/) y Docker Compose
- [Java 17+](https://adoptium.net/) (para desarrollo local)
- [Node.js 20+](https://nodejs.org/) (para desarrollo del cliente)
- [Ollama](https://ollama.ai/) con el modelo `llama3.2` (para el chatbot de IA)

## Puesta en marcha

### Con Docker Compose (recomendado)

```bash
docker compose up --build
```

Esto levanta todos los microservicios, las bases de datos PostgreSQL y Consul. La aplicacion estara disponible en:

- Frontend: `http://localhost:1234`
- API Gateway: `http://localhost:8080`
- Consul UI: `http://localhost:8500`

### Chatbot de IA (Ollama)

El chatbot de apoyo emocional usa Ollama de forma local. Para habilitarlo:

```bash
ollama pull llama3.2
ollama serve
```

Ollama debe estar corriendo en `http://localhost:11434`.

### Desarrollo local

Cada microservicio backend se puede arrancar individualmente:

```bash
cd user
./mvnw spring-boot:run
```

Para el cliente frontend:

```bash
cd client
npm install
npm run dev
```

## Estructura del proyecto

```
mindtogether/
├── gateway/        # API Gateway (Spring Cloud Gateway)
├── user/           # Microservicio de usuarios y autenticacion
├── habits/         # Microservicio de habitos
├── goals/          # Microservicio de metas y logros
├── community/      # Microservicio de comunidades
├── client/         # Frontend Vue.js
├── compose.yaml    # Configuracion Docker Compose
└── LICENSE         # Licencia MIT
```

## Tecnologias utilizadas

**Backend**
- Java 17 + Spring Boot 3
- Spring Cloud Gateway (WebFlux)
- Spring Cloud Consul (service discovery)
- Spring Cloud OpenFeign (comunicacion entre servicios)
- Spring Security con JWT personalizado (jjwt)
- Spring Data JPA + PostgreSQL 14
- Lombok

**Frontend**
- Vue.js 3 (Composition API)
- Vue Router
- Axios
- Bootstrap 5 + Bootstrap Icons
- Vite
- Sass

**IA y APIs externas**
- Ollama + llama3.2 (chatbot de apoyo emocional)
- Open-Meteo API (recomendaciones meteorologicas para habitos)
- GNews API (noticias positivas de bienestar)

**Infraestructura**
- Docker / Docker Compose
- PostgreSQL 14 (una instancia por microservicio)
- HashiCorp Consul 1.17

## Usuarios de prueba

La aplicacion incluye datos de prueba precargados:

| Usuario | Rol | Estado |
|---------|-----|--------|
| `pepemin` | ADMIN | Activo |
| `mariadmin` | ADMIN | Activo |
| `laura` | USER | Activo |
| `pedroff` | USER | Inactivo |
| `ramon` | USER | Activo |

## Licencia

Proyecto de codigo abierto bajo la [Licencia MIT](LICENSE). Consulta [LICENSE.md](LICENSE.md) para mas detalles sobre lo que esto permite.
