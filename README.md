# MindTogether

Aplicación web de gestión de hábitos y metas personales basada en una arquitectura de **microservicios** con Spring Boot y Vue.js.

## Descripción

MindTogether permite a los usuarios:
- Crear y hacer seguimiento de **hábitos** diarios
- Definir y gestionar **metas** personales
- Unirse a **comunidades** para compartir progreso
- Ver un **dashboard** con estadísticas personales
- Panel de **administración** para gestión de usuarios y contenido

## Arquitectura

El proyecto sigue una arquitectura de microservicios compuesta por los siguientes servicios:

| Servicio | Descripción | Tecnología |
|----------|-------------|------------|
| `gateway` | API Gateway y punto de entrada único | Spring Cloud Gateway |
| `user` | Gestión de usuarios y autenticación | Spring Boot |
| `habits` | Gestión de hábitos | Spring Boot |
| `goals` | Gestión de metas | Spring Boot |
| `community` | Gestión de comunidades | Spring Boot |
| `client` | Frontend SPA | Vue.js + Vite |

## Requisitos previos

- [Docker](https://www.docker.com/) y Docker Compose
- [Java 21+](https://adoptium.net/) (para desarrollo local)
- [Node.js 20+](https://nodejs.org/) (para desarrollo del cliente)

## Puesta en marcha

### Con Docker Compose (recomendado)

```bash
docker compose up --build
```

La aplicación estará disponible en `http://localhost:8080`.

### Desarrollo local

Cada microservicio backend se puede arrancar individualmente desde su carpeta:

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
├── user/           # Microservicio de usuarios
├── habits/         # Microservicio de hábitos
├── goals/          # Microservicio de metas
├── community/      # Microservicio de comunidades
├── client/         # Frontend Vue.js
└── compose.yaml    # Configuración Docker Compose
```

## Tecnologías utilizadas

**Backend**
- Java 21 + Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA + PostgreSQL
- Spring Cloud Gateway

**Frontend**
- Vue.js 3 (Composition API)
- Vite
- Pinia (gestión de estado)
- Vue Router

**Infraestructura**
- Docker / Docker Compose
- PostgreSQL

## Licencia

Proyecto académico desarrollado para la asignatura ASI — Universidade da Coruña.
