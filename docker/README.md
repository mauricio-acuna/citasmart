# Docker configuration for CitaSmart

Esta carpeta contiene todas las configuraciones necesarias para Docker y contenedores.

## 📁 Estructura

```
docker/
├── nginx/                 # Configuración de Nginx
│   ├── nginx.conf
│   └── ssl/              # Certificados SSL
├── prometheus/           # Configuración de Prometheus
│   └── prometheus.yml
├── grafana/             # Dashboards de Grafana
│   └── dashboards/
├── init-scripts/        # Scripts de inicialización de DB
│   └── 01-init.sql
└── environments/        # Variables de entorno
    ├── development.env
    ├── staging.env
    └── production.env
```

## 🚀 Quick Start

### Desarrollo Local

```bash
# Levantar solo la infraestructura (DB + Redis)
docker-compose up postgres redis

# Levantar todo el stack
docker-compose up

# En modo detached
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar servicios
docker-compose down
```

### Producción

```bash
# Usar archivo de producción
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Con variables de entorno de producción
docker-compose --env-file ./docker/environments/production.env up -d
```

## 🔧 Configuración por Servicio

### PostgreSQL
- **Puerto**: 5432
- **Base de datos**: citasmart_dev
- **Usuario**: citasmart
- **Password**: citasmart123

### Redis
- **Puerto**: 6379
- **Sin autenticación** (desarrollo)

### Frontend
- **Puerto**: 4200 (desarrollo) / 80 (producción)
- **Build**: Angular production

### Backend Services
- **API Gateway**: 8080
- **User Service**: 8081
- **Booking Service**: 8082
- **Payment Service**: 8083

### Monitoring
- **Prometheus**: 9090
- **Grafana**: 3000 (admin/admin123)

## 📊 Monitoring

### Acceso a Servicios

- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090

### Health Checks

```bash
# Verificar estado de servicios
docker-compose ps

# Logs específicos
docker-compose logs api-gateway
docker-compose logs frontend

# Ejecutar comando en contenedor
docker-compose exec postgres psql -U citasmart -d citasmart_dev
```

## 🔒 Seguridad

### Variables de Entorno Sensibles

```bash
# NO incluir en el repositorio
POSTGRES_PASSWORD=
JWT_SECRET=
PAYMENT_API_KEY=
EMAIL_API_KEY=
```

### SSL/TLS (Producción)

```bash
# Generar certificados
./docker/nginx/generate-ssl.sh

# Configurar en nginx.conf
server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
}
```

## 🔧 Troubleshooting

### Problemas Comunes

**1. Puerto ya en uso**
```bash
# Verificar puertos ocupados
netstat -tulpn | grep :5432

# Cambiar puerto en docker-compose.yml
ports:
  - "5433:5432"  # Usar puerto diferente
```

**2. Problemas de permisos**
```bash
# Dar permisos a volúmenes
sudo chown -R $USER:$USER ./data

# En Windows con WSL2
wsl --shutdown
wsl
```

**3. Memoria insuficiente**
```bash
# Aumentar memoria de Docker Desktop
# Settings > Resources > Memory > 4GB+

# Verificar uso de memoria
docker stats
```

**4. Base de datos no conecta**
```bash
# Verificar logs de PostgreSQL
docker-compose logs postgres

# Conectar manualmente
docker-compose exec postgres psql -U citasmart -d citasmart_dev

# Recrear volumen
docker-compose down -v
docker-compose up postgres
```

## 🚀 Deployment

### Build de Imágenes

```bash
# Build individual
docker build -t citasmart/frontend ./frontend
docker build -t citasmart/api-gateway ./backend/api-gateway

# Build all
docker-compose build

# Build con no-cache
docker-compose build --no-cache
```

### Registry

```bash
# Tag para registry
docker tag citasmart/frontend registry.example.com/citasmart/frontend:v1.0.0

# Push to registry
docker push registry.example.com/citasmart/frontend:v1.0.0
```

## 📋 Maintenance

### Backup de Base de Datos

```bash
# Backup
docker-compose exec postgres pg_dump -U citasmart citasmart_dev > backup.sql

# Restore
docker-compose exec -T postgres psql -U citasmart citasmart_dev < backup.sql
```

### Limpieza

```bash
# Limpiar contenedores parados
docker container prune

# Limpiar imágenes no usadas
docker image prune

# Limpiar volúmenes no usados
docker volume prune

# Limpieza completa
docker system prune -a
```

### Updates

```bash
# Actualizar imágenes base
docker-compose pull

# Recrear servicios
docker-compose up -d --force-recreate

# Rolling update (sin downtime)
docker-compose up -d --no-deps service-name
```
