# Backend - CitaSmart

Este directorio contiene todos los microservicios del backend desarrollados con Spring Boot.

## 🏗️ Arquitectura de Microservicios

```
backend/
├── api-gateway/           # API Gateway principal
├── user-service/          # Gestión de usuarios
├── booking-service/       # Sistema de reservas
├── payment-service/       # Procesamiento de pagos
├── professional-service/  # Gestión de profesionales
├── facility-service/      # Gestión de espacios
├── notification-service/  # Envío de notificaciones
├── report-service/        # Generación de reportes
├── shared/               # Librerías compartidas
│   ├── common/           # Utilidades comunes
│   ├── security/         # Configuración de seguridad
│   └── models/           # DTOs y entidades compartidas
└── docker/               # Dockerfiles específicos
```

## 🚀 Stack Tecnológico

- **Framework**: Spring Boot 3.2+
- **Java**: OpenJDK 17+
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 15+
- **Cache**: Redis 7+
- **Security**: Spring Security + JWT
- **Documentation**: OpenAPI 3.0 + Swagger UI
- **Testing**: JUnit 5 + Mockito + TestContainers

## 🔧 Setup Rápido

### Prerrequisitos

```bash
# Verificar versiones
java -version    # Debe ser 17+
mvn -version     # Debe ser 3.9+
docker --version # Para base de datos local
```

### Configuración Local

```bash
# 1. Levantar infraestructura
docker-compose up postgres redis

# 2. Construir proyecto
cd backend
./mvnw clean install

# 3. Ejecutar servicios (en orden)
cd api-gateway && ./mvnw spring-boot:run &
cd user-service && ./mvnw spring-boot:run &
cd booking-service && ./mvnw spring-boot:run &
```

## 📋 Servicios Disponibles

### API Gateway (Puerto 8080)
- **Función**: Punto de entrada único, routing, autenticación
- **Endpoints**: `/api/v1/*`
- **Health Check**: `GET /actuator/health`

### User Service (Puerto 8081)
- **Función**: Gestión completa de usuarios
- **Endpoints**: `/api/v1/users/*`
- **Features**: CRUD, autenticación, roles

### Booking Service (Puerto 8082)
- **Función**: Motor de reservas y citas
- **Endpoints**: `/api/v1/bookings/*`
- **Features**: Disponibilidad, reservas, calendario

### Payment Service (Puerto 8083)
- **Función**: Procesamiento de pagos
- **Endpoints**: `/api/v1/payments/*`
- **Features**: Múltiples métodos, facturación

## 🧪 Testing

```bash
# Tests unitarios
./mvnw test

# Tests de integración
./mvnw test -Pintegration

# Coverage report
./mvnw jacoco:report

# Quality checks
./mvnw sonar:sonar
```

## 📚 Documentación API

Una vez que los servicios estén corriendo:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Actuator**: http://localhost:8080/actuator

## 🔒 Seguridad

### JWT Authentication

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@citasmart.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "email": "admin@citasmart.com",
    "role": "ADMIN"
  }
}
```

### Authorized Requests

```http
GET /api/v1/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

## 📊 Monitoreo

### Health Checks

```bash
# API Gateway
curl http://localhost:8080/actuator/health

# User Service
curl http://localhost:8081/actuator/health

# Booking Service  
curl http://localhost:8082/actuator/health
```

### Métricas

- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Application Info**: http://localhost:8080/actuator/info

## 🚀 Deployment

### Docker Build

```bash
# Build todas las imágenes
./build-all.sh

# Build servicio específico
cd user-service
docker build -t citasmart/user-service .
```

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/citasmart_dev
DATABASE_USERNAME=citasmart
DATABASE_PASSWORD=citasmart123

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400

# External APIs
PAYMENT_GATEWAY_API_KEY=your-payment-key
EMAIL_API_KEY=your-email-key
```

## 📋 Próximos Pasos

1. ✅ **Configurar proyecto base**
2. 🔄 **Implementar User Service**
3. ⏳ **Implementar Booking Service**
4. ⏳ **Implementar Payment Service**
5. ⏳ **Configurar API Gateway**
6. ⏳ **Integrar servicios**
7. ⏳ **Testing completo**
8. ⏳ **Deployment a staging**

## 🤝 Contribución

Ver [CONTRIBUTING.md](../CONTRIBUTING.md) para guías de desarrollo y estándares de código.

## 🔗 Links Útiles

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)
