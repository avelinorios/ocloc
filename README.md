# Mi Horario

Aplicación de gestión de horarios de trabajo con frontend Vue.js y backend Java Spring Boot.

## Estructura del Proyecto

- `backend/` - Backend Java 21 con Spring Boot y arquitectura de vertical slicing
- `frontend/` - Frontend Vue 3 con Vite
- `docker-compose.yml` - Configuración de servicios Docker

## Requisitos

- Docker y Docker Compose
- Variables de entorno (crear archivo `.env`):
  ```
  POSTGRES_USER=postgres
  POSTGRES_PASSWORD=postgres
  POSTGRES_DB=horario
  ```

## Ejecución

```bash
docker compose up -d
```

Los servicios estarán disponibles en:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Adminer: http://localhost:8081 (interfaz de base de datos)
- pgAdmin: http://localhost:5050 (interfaz de base de datos)

## Inicialización de Base de Datos

La base de datos se inicializa automáticamente al iniciar el backend. El backend crea:
- Las tablas necesarias (`users` y `time_entries`)
- Los índices correspondientes
- Un usuario demo (ID: 1, email: demo@acme.test)

## Arquitectura Backend

El backend utiliza **vertical slicing** donde cada feature tiene su propia estructura:

```
features/
  ├── users/
  │   ├── UserController.java
  │   ├── UserService.java
  │   ├── UserRepository.java
  │   └── User.java
  ├── clockin/
  │   ├── ClockInController.java
  │   ├── ClockInService.java
  │   ├── ClockInRepository.java
  │   └── TimeEntry.java
  ├── clockout/
  ├── entries/
  └── summary/
```

## Desarrollo

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Testing

### Ejecutar tests con Karate
```bash
cd backend
mvn test
```

### Ejecutar tests de una feature específica
```bash
mvn test -Dtest=UsersKarateTest
mvn test -Dtest=ClockInKarateTest
mvn test -Dtest=ClockOutKarateTest
mvn test -Dtest=EntriesKarateTest
mvn test -Dtest=SummaryKarateTest
mvn test -Dtest=IntegrationKarateTest
```

Los tests están ubicados en `backend/src/test/resources/` y utilizan Karate para pruebas de API REST.
