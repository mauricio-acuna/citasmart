# CitaSmart 🏥

Sistema integral de gestión de citas y servicios para centros de bienestar, spas, clínicas y espacios multifuncionales.

## 🚀 Descripción del Proyecto

CitaSmart es una plataforma web moderna desarrollada con **Java Spring Boot** (backend) y **Angular** (frontend) que revoluciona la gestión de citas y servicios en centros especializados como:

- 💆‍♀️ Centros de masajes y spa
- 🧘‍♀️ Estudios de yoga y pilates  
- 🏥 Clínicas de terapias
- 📚 Centros de idiomas
- 🎯 Espacios multifuncionales

## ✨ Características Principales

### 🎯 Para Clientes
- **Reserva Online**: Sistema intuitivo de reservas 24/7
- **Búsqueda Avanzada**: Filtros por servicio, profesional, fecha y ubicación
- **Gestión de Perfil**: Historial de servicios y preferencias
- **Recordatorios**: Notificaciones automáticas por email/SMS
- **Múltiples Pagos**: Efectivo, tarjeta, transferencias y billeteras digitales

### 👨‍💼 Para Profesionales
- **Agenda Digital**: Calendario inteligente con múltiples vistas
- **Gestión de Disponibilidad**: Control total de horarios y días libres
- **Historial de Clientes**: Notas y seguimiento personalizado
- **Reportes de Ingresos**: Análisis detallado de performance
- **Gestión de Suplencias**: Sistema para cubrir ausencias

### 🏢 Para Administradores
- **Dashboard Ejecutivo**: KPIs y métricas en tiempo real
- **Gestión Completa**: Clientes, profesionales, servicios y espacios
- **Reportes Avanzados**: Analytics de ocupación, ingresos y tendencias
- **Control Financiero**: Facturación, comisiones y cuentas corrientes
- **Gestión Multicentro**: Soporte para múltiples sucursales

## 🛠️ Stack Tecnológico

### Backend
- **Framework**: Spring Boot 3.x
- **Lenguaje**: Java 17+
- **Base de Datos**: PostgreSQL 15+
- **Cache**: Redis
- **Seguridad**: Spring Security + JWT
- **APIs**: RESTful + OpenAPI 3.0

### Frontend  
- **Framework**: Angular 17+
- **Lenguaje**: TypeScript
- **UI Library**: Angular Material + PrimeNG
- **Estado**: NgRx
- **Testing**: Jest + Cypress

### DevOps & Infrastructure
- **Contenedores**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Cloud**: AWS/Azure
- **Monitoring**: Prometheus + Grafana
- **Logs**: ELK Stack

## 📋 Requerimientos del Sistema

### Mínimos
- **Java**: 17 o superior
- **Node.js**: 18 o superior
- **PostgreSQL**: 15 o superior
- **Redis**: 6 o superior
- **Docker**: 20 o superior (opcional)

### Recomendados
- **RAM**: 8GB mínimo, 16GB recomendado
- **Storage**: 20GB espacio libre
- **CPU**: 4 cores mínimo

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/mauricio-acuna/citasmart.git
cd citasmart
```

### 2. Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### 3. Frontend Setup
```bash
cd frontend
npm install
ng serve
```

### 4. Base de Datos
```bash
# Crear base de datos PostgreSQL
createdb citasmart_dev

# Ejecutar migraciones
./mvnw flyway:migrate
```

### 5. Docker (Alternativa)
```bash
docker-compose up -d
```

## 📖 Documentación

- **[PRD Completo](./PRD.md)**: Product Requirements Document detallado
- **[API Docs](./docs/api.md)**: Documentación de APIs REST
- **[User Guide](./docs/user-guide.md)**: Guía de usuario
- **[Admin Guide](./docs/admin-guide.md)**: Guía de administrador
- **[Developer Guide](./docs/developer-guide.md)**: Guía para desarrolladores

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│   Frontend      │    │    API Gateway       │    │   Microservicios    │
│   Angular 17    │◄──►│   Spring Gateway     │◄──►│   Spring Boot 3     │
│                 │    │                      │    │                     │
└─────────────────┘    └──────────────────────┘    └─────────────────────┘
                                │                            │
                                ▼                            ▼
                       ┌──────────────────┐         ┌─────────────────┐
                       │   Load Balancer  │         │   Databases     │
                       │   Nginx/HAProxy  │         │   PostgreSQL    │
                       └──────────────────┘         │   Redis Cache   │
                                                    └─────────────────┘
```

## 🎯 Roadmap de Desarrollo

### 📅 Fase 1: MVP (Meses 1-4)
- ✅ Autenticación y gestión de usuarios
- ✅ Catálogo de servicios básico
- ✅ Sistema de reservas core
- ✅ Dashboard administrativo
- ✅ Procesamiento de pagos básico

### 📅 Fase 2: Funcionalidad Avanzada (Meses 5-8)
- 🔄 Calendario avanzado con múltiples vistas
- 🔄 Sistema de notificaciones push
- 🔄 Gestión de espacios e inventario
- 🔄 Reportes y analytics
- 🔄 Mobile app (PWA)

### 📅 Fase 3: Enterprise Features (Meses 9-12)
- ⏳ Integraciones externas (calendarios, ERP)
- ⏳ IA para optimización de horarios
- ⏳ Multi-tenancy y escalabilidad
- ⏳ APIs públicas para terceros
- ⏳ Marketplace de servicios

## 🤝 Contribución

¡Las contribuciones son bienvenidas! Por favor lee nuestras [guías de contribución](./CONTRIBUTING.md) antes de enviar un PR.

### Proceso de Contribución
1. Fork del repositorio
2. Crear feature branch (`git checkout -b feature/amazing-feature`)
3. Commit de cambios (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Abrir Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver [LICENSE](./LICENSE) para más detalles.

## 👥 Equipo

- **Product Owner**: [Mauricio Acuña](https://github.com/mauricio-acuna)
- **Tech Lead**: TBD
- **Frontend Team**: TBD
- **Backend Team**: TBD
- **DevOps Engineer**: TBD

## 📞 Soporte

- **Email**: support@citasmart.com
- **Issues**: [GitHub Issues](https://github.com/mauricio-acuna/citasmart/issues)
- **Documentación**: [Wiki del Proyecto](https://github.com/mauricio-acuna/citasmart/wiki)

## 🌟 Estado del Proyecto

[![Build Status](https://github.com/mauricio-acuna/citasmart/workflows/CI/badge.svg)](https://github.com/mauricio-acuna/citasmart/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=citasmart&metric=alert_status)](https://sonarcloud.io/dashboard?id=citasmart)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=citasmart&metric=coverage)](https://sonarcloud.io/dashboard?id=citasmart)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

<div align="center">
  <b>💡 ¿Tienes una idea para mejorar CitaSmart?</b><br>
  <a href="https://github.com/mauricio-acuna/citasmart/issues/new">¡Cuéntanos!</a>
</div>

---

**Desarrollado con ❤️ para revolucionar la gestión de servicios de bienestar**
