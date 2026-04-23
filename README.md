# ShareYourTrip_ms_users

Microservicio de gestión de usuarios y autenticación para la plataforma ShareYourTrip.

## Descripción

Este microservicio es responsable de la gestión completa de usuarios del sistema, incluyendo autenticación, perfil de usuario y gestión de roles. Actúa como el servicio central de identidad de la plataforma.

## Características Principales

- **Gestión de usuarios**: CRUD completo de usuarios con información personal
- **Autenticación**: Login con generación de tokens JWT
- **Gestión de roles**: Sistema de roles (HOST, TRAVELER, ADMIN)
- **Seguridad**: Hash de contraseñas con BCrypt
- **Validación de origen**: Filtro que valida peticiones provenientes del API Gateway

## Arquitectura

```
API Gateway (8080)
   ↓ (valida JWT + añade headers X-User-Id, X-User-Roles)
Users Microservice (8081)
   ↓ (valida header X-User-Id)
Procesa la petición
```

## Modelo de Datos

### Entidad User

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único del usuario |
| firstName | String (100) | Nombre del usuario |
| lastName | String (100) | Apellido del usuario |
| email | String (150) | Email único del usuario |
| passwordHash | String (255) | Hash de la contraseña (BCrypt) |
| phone | String (30) | Teléfono del usuario (opcional) |
| profilePhotoUrl | String (255) | URL de la foto de perfil (opcional) |
| bio | Text | Biografía del usuario (opcional) |
| isActive | Boolean | Estado del usuario (activo/inactivo) |
| createdAt | LocalDateTime | Fecha de creación |
| updatedAt | LocalDateTime | Fecha de última actualización |
| language | String | Idioma preferido del usuario |
| birthDate | LocalDate | Fecha de nacimiento |
| roles | Set<Role> | Roles del usuario (HOST, TRAVELER, ADMIN) |

### Entidad Role

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único del rol |
| name | String | Nombre del rol (HOST, TRAVELER, ADMIN) |

## Endpoints

### Autenticación

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "usuario@example.com",
  "roles": ["TRAVELER"],
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Respuesta de error (401):**
```json
{
  "error": "Credenciales inválidas"
}
```

### Gestión de Usuarios

#### Obtener todos los usuarios
```http
GET /users
Authorization: Bearer {{token}}
```

**Respuesta (200):**
```json
[
  {
    "id": 1,
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "phone": "+34612345678",
    "profilePhotoUrl": "https://example.com/photo.jpg",
    "bio": "Viajero apasionado",
    "language": "ES",
    "birthDate": "1990-05-15",
    "roles": ["TRAVELER"]
  }
]
```

#### Obtener usuario por ID
```http
GET /users/{id}
Authorization: Bearer {{token}}
```

**Respuesta (200):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "phone": "+34612345678",
  "profilePhotoUrl": "https://example.com/photo.jpg",
  "bio": "Viajero apasionado",
  "language": "ES",
  "birthDate": "1990-05-15",
  "roles": ["TRAVELER"]
}
```

**Respuesta de error (404):**
```json
{
  "error": "Usuario no encontrado"
}
```

#### Obtener usuario por email
```http
GET /users/email/{email}
Authorization: Bearer {{token}}
```

**Respuesta (200):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "phone": "+34612345678",
  "profilePhotoUrl": "https://example.com/photo.jpg",
  "bio": "Viajero apasionado",
  "language": "ES",
  "birthDate": "1990-05-15",
  "roles": ["TRAVELER"]
}
```

#### Obtener IDs de usuarios por idioma
```http
GET /users/lan/{language}
Authorization: Bearer {{token}}
```

**Respuesta (200):**
```json
[1, 5, 10, 15]
```

## Seguridad

### Validación de Origen

El microservicio implementa un filtro `GatewayAuthenticationFilter` que:
- Valida la presencia del header `X-User-Id` enviado por el API Gateway
- Rechaza peticiones directas sin este header (401)
- Permite rutas `/auth/**` sin validación (para login)

### Configuración Spring Security

- Spring Security configurado con `permitAll()` (validación manual)
- CSRF deshabilitado
- Filtro `GatewayAuthenticationFilter` añadido antes de `UsernamePasswordAuthenticationFilter`
- PasswordEncoder: BCrypt

### Generación de JWT

El servicio utiliza `JwtUtil` para generar tokens JWT con:
- **Algoritmo**: HS512
- **Expiración**: 24 horas (86400000 ms)
- **Claims**:
  - `sub`: ID del usuario
  - `roles`: Lista de roles del usuario
  - `email`: Email del usuario

## Configuración

### application.yaml

```yaml
server:
  port: 8081
  servlet:
    context-path: /

spring:
  application:
    name: share-your-trip-users
  datasource:
    url: jdbc:postgresql://localhost:54320/product
    username: product
    password: product
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

# JWT Configuration
jwt:
  secret: shareYourTripSecretKey2024ParaFirmarTokensJWTDebeSerMuyLargaParaSeguridad
  expiration: 86400000

# Users Service URL
users:
  service:
    url: http://localhost:8081
```

## Tecnologías

- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- Spring Boot Starter Web
- PostgreSQL
- Lombok
- JJWT (JWT generation/validation)
- Jakarta Validation API

## DTOs

### LoginRequestDto
```java
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

### LoginResponseDto
```java
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "usuario@example.com",
  "roles": ["TRAVELER"],
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### UserResponseDto
```java
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "phone": "+34612345678",
  "profilePhotoUrl": "https://example.com/photo.jpg",
  "bio": "Viajero apasionado",
  "language": "ES",
  "birthDate": "1990-05-15",
  "roles": ["TRAVELER"]
}
```

## Excepciones

- `UserNotFoundException`: Usuario no encontrado (404)
- `UnauthorizedException`: Credenciales inválidas o usuario inactivo (401)
- `GlobalExceptionHandler`: Manejo centralizado de excepciones

## Cómo Ejecutar

### Compilar
```bash
mvnw.cmd clean package -DskipTests
```

### Ejecutar
```bash
java -jar target/*.jar
```

El servicio estará disponible en `http://localhost:8081`

## Notas Importantes

- El microservicio debe recibir peticiones únicamente a través del API Gateway
- Las contraseñas se almacenan como hash BCrypt, nunca en texto plano
- El token JWT tiene una validez de 24 horas
- Los roles determinan los permisos del usuario en otros microservicios
- El campo `isActive` permite desactivar usuarios sin eliminarlos
