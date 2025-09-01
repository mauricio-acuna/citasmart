# Frontend - CitaSmart

Aplicación web desarrollada con Angular 17+ que proporciona una interfaz moderna y responsive para el sistema de gestión de citas.

## 🎨 Stack Tecnológico

- **Framework**: Angular 17+
- **Language**: TypeScript 5+
- **UI Library**: Angular Material 17 + PrimeNG 17
- **State Management**: NgRx 17
- **Styling**: SCSS + Tailwind CSS
- **Testing**: Jest + Cypress
- **Build Tool**: Angular CLI + Webpack

## 🏗️ Arquitectura Frontend

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                 # Servicios core y guards
│   │   │   ├── guards/
│   │   │   ├── interceptors/
│   │   │   ├── services/
│   │   │   └── models/
│   │   ├── shared/               # Componentes compartidos
│   │   │   ├── components/
│   │   │   ├── directives/
│   │   │   ├── pipes/
│   │   │   └── utils/
│   │   ├── features/             # Módulos de características
│   │   │   ├── auth/
│   │   │   ├── dashboard/
│   │   │   ├── booking/
│   │   │   ├── users/
│   │   │   ├── professionals/
│   │   │   ├── payments/
│   │   │   └── reports/
│   │   ├── layout/               # Layout components
│   │   │   ├── header/
│   │   │   ├── sidebar/
│   │   │   └── footer/
│   │   └── store/                # NgRx Store
│   │       ├── actions/
│   │       ├── reducers/
│   │       ├── effects/
│   │       └── selectors/
│   ├── assets/                   # Assets estáticos
│   │   ├── images/
│   │   ├── icons/
│   │   ├── fonts/
│   │   └── i18n/
│   ├── environments/             # Configuraciones por ambiente
│   └── styles/                   # Estilos globales
├── cypress/                      # Tests E2E
├── docs/                         # Documentación específica
└── dist/                         # Build output
```

## 🚀 Setup Local

### Prerrequisitos

```bash
# Verificar versiones
node --version   # Debe ser 18+
npm --version    # Debe ser 9+
ng version       # Angular CLI 17+
```

### Instalación

```bash
# 1. Instalar dependencias
cd frontend
npm install

# 2. Configurar environment
cp src/environments/environment.example.ts src/environments/environment.ts

# 3. Ejecutar en modo desarrollo
ng serve

# O con configuración específica
ng serve --configuration=development
```

### Scripts Disponibles

```bash
# Desarrollo
npm start              # ng serve
npm run dev            # ng serve con proxy
npm run dev:open       # ng serve y abrir browser

# Build
npm run build          # ng build
npm run build:prod     # ng build --configuration=production
npm run build:staging  # ng build --configuration=staging

# Testing
npm test               # Jest unit tests
npm run test:watch     # Jest en modo watch
npm run e2e            # Cypress E2E tests
npm run e2e:open       # Cypress interactive mode

# Quality
npm run lint           # ESLint
npm run lint:fix       # ESLint con auto-fix
npm run format         # Prettier
npm run analyze        # Bundle analyzer

# i18n
npm run extract-i18n   # Extraer strings para traducir
npm run build:i18n     # Build con múltiples idiomas
```

## 🎯 Características Principales

### 🔐 Autenticación
- Login/logout con JWT
- Recuperación de contraseña
- Autenticación de dos factores (2FA)
- Guards de ruta para proteger páginas

### 📱 Responsive Design
- Mobile-first approach
- Breakpoints: 576px, 768px, 992px, 1200px
- Touch-friendly para tablets
- PWA capabilities

### 🎨 UI/UX
- Design system cohesivo
- Dark/light theme
- Componentes reutilizables
- Animaciones fluidas
- Accessibility (WCAG 2.1)

### 📊 Gestión de Estado
- NgRx para estado complejo
- RxJS para programación reactiva
- Local storage para persistencia
- Cache inteligente

## 📋 Módulos Principales

### Dashboard
```typescript
// Acceso: /dashboard
// Características:
- KPIs en tiempo real
- Gráficos interactivos
- Resumen de citas del día
- Notificaciones importantes
```

### Gestión de Citas
```typescript
// Acceso: /bookings
// Características:
- Calendario interactivo
- Búsqueda de disponibilidad
- Reserva en línea
- Gestión de lista de espera
```

### Gestión de Usuarios
```typescript
// Acceso: /users
// Características:
- CRUD de clientes
- Perfiles de profesionales
- Gestión de roles
- Historial de servicios
```

### Reportes
```typescript
// Acceso: /reports
// Características:
- Dashboards personalizables
- Exportación de datos
- Filtros avanzados
- Visualizaciones dinámicas
```

## 🔧 Configuración

### Environment Variables

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  wsUrl: 'ws://localhost:8080/ws',
  appName: 'CitaSmart',
  version: '1.0.0',
  features: {
    enablePWA: true,
    enableNotifications: true,
    enableOfflineMode: false
  },
  auth: {
    tokenKey: 'citasmart_token',
    refreshTokenKey: 'citasmart_refresh_token',
    tokenExpiration: 3600000 // 1 hour
  },
  api: {
    timeout: 30000,
    retries: 3
  }
};
```

### Proxy Configuration

```json
// proxy.conf.json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  },
  "/ws/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "ws": true
  }
}
```

## 🎨 Theming

### Custom Theme

```scss
// src/styles/themes/_citasmart-theme.scss
@use '@angular/material' as mat;

$primary-palette: mat.define-palette(mat.$blue-palette, 600);
$accent-palette: mat.define-palette(mat.$green-palette, 500);
$warn-palette: mat.define-palette(mat.$red-palette, 500);

$citasmart-theme: mat.define-light-theme((
  color: (
    primary: $primary-palette,
    accent: $accent-palette,
    warn: $warn-palette,
  ),
  typography: mat.define-typography-config(),
  density: 0,
));

@include mat.all-component-themes($citasmart-theme);
```

### Dark Theme

```scss
// src/styles/themes/_dark-theme.scss
$dark-theme: mat.define-dark-theme((
  color: (
    primary: $primary-palette,
    accent: $accent-palette,
    warn: $warn-palette,
  )
));

.dark-theme {
  @include mat.all-component-colors($dark-theme);
}
```

## 🧪 Testing

### Unit Tests (Jest)

```typescript
// Ejemplo: user.service.spec.ts
describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should create user', () => {
    const mockUser = { id: 1, name: 'John Doe' };
    
    service.createUser(mockUser).subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('/api/v1/users');
    expect(req.request.method).toBe('POST');
    req.flush(mockUser);
  });
});
```

### E2E Tests (Cypress)

```typescript
// cypress/e2e/booking.cy.ts
describe('Booking Flow', () => {
  beforeEach(() => {
    cy.login('client@example.com', 'password');
  });

  it('should create new booking', () => {
    cy.visit('/bookings/new');
    cy.get('[data-cy=service-select]').select('Masaje Relajante');
    cy.get('[data-cy=date-picker]').click();
    cy.get('[data-cy=date-today]').click();
    cy.get('[data-cy=time-slot]').first().click();
    cy.get('[data-cy=confirm-booking]').click();
    
    cy.get('[data-cy=success-message]')
      .should('contain', 'Cita reservada exitosamente');
  });
});
```

## 📱 PWA Features

### Service Worker

```typescript
// src/app/app.module.ts
import { ServiceWorkerModule } from '@angular/service-worker';

@NgModule({
  imports: [
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      registrationStrategy: 'registerWhenStable:30000'
    })
  ]
})
export class AppModule { }
```

### Manifest

```json
// src/manifest.json
{
  "name": "CitaSmart",
  "short_name": "CitaSmart",
  "theme_color": "#1976d2",
  "background_color": "#fafafa",
  "display": "standalone",
  "scope": "./",
  "start_url": "./"
}
```

## 🌍 Internacionalización (i18n)

### Configuración

```bash
# Agregar paquete de i18n
ng add @angular/localize

# Extraer strings
ng extract-i18n

# Build con idiomas específicos
ng build --localize
```

### Uso en Componentes

```typescript
// En template
<p i18n="@@welcome.message">Bienvenido a CitaSmart</p>

// En código
import { $localize } from '@angular/localize/init';

const message = $localize`Cita creada exitosamente`;
```

## 🚀 Deployment

### Build de Producción

```bash
# Build optimizado
npm run build:prod

# Verificar bundle size
npm run analyze

# Test build local
npx http-server dist/citasmart
```

### Docker

```dockerfile
# Dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build:prod

FROM nginx:alpine
COPY --from=builder /app/dist/citasmart /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

### Environment-Specific Builds

```bash
# Staging
ng build --configuration=staging

# Production
ng build --configuration=production

# Con base href específico
ng build --base-href=/app/
```

## 📊 Performance

### Bundle Optimization

```typescript
// app-routing.module.ts - Lazy loading
const routes: Routes = [
  {
    path: 'bookings',
    loadChildren: () => import('./features/booking/booking.module')
      .then(m => m.BookingModule)
  }
];
```

### OnPush Change Detection

```typescript
@Component({
  selector: 'app-booking-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `...`
})
export class BookingListComponent {
  constructor(private cdr: ChangeDetectorRef) {}
}
```

## 🔗 APIs y Servicios

### HTTP Interceptors

```typescript
// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req);
  }
}
```

### Error Handling

```typescript
// error.interceptor.ts
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        
        this.notificationService.showError(error.message);
        return throwError(() => error);
      })
    );
  }
}
```

## 📋 Próximos Pasos

1. ✅ **Setup proyecto base**
2. 🔄 **Implementar autenticación**
3. ⏳ **Crear componentes principales**
4. ⏳ **Integrar con backend APIs**
5. ⏳ **Implementar PWA features**
6. ⏳ **Testing completo**
7. ⏳ **Optimización de performance**
8. ⏳ **Deployment a staging**

## 🤝 Contribución

Ver [CONTRIBUTING.md](../CONTRIBUTING.md) para guías de desarrollo y estándares de código específicos para Angular.

## 🔗 Links Útiles

- [Angular Documentation](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [PrimeNG](https://primeng.org/)
- [NgRx](https://ngrx.io/)
- [RxJS](https://rxjs.dev/)
- [Cypress](https://www.cypress.io/)
