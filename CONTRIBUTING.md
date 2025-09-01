# Contribuyendo a CitaSmart 🤝

¡Gracias por tu interés en contribuir a CitaSmart! Este documento proporciona las pautas y procesos para contribuir efectivamente al proyecto.

## 📋 Tabla de Contenidos

- [Código de Conducta](#código-de-conducta)
- [¿Cómo Puedo Contribuir?](#cómo-puedo-contribuir)
- [Reportar Bugs](#reportar-bugs)
- [Sugerir Mejoras](#sugerir-mejoras)
- [Contribuir con Código](#contribuir-con-código)
- [Pull Request Process](#pull-request-process)
- [Estándares de Código](#estándares-de-código)
- [Configuración del Entorno](#configuración-del-entorno)

## 📜 Código de Conducta

Este proyecto se adhiere al [Contributor Covenant](https://www.contributor-covenant.org/). Al participar, se espera que mantengas este código. Por favor reporta comportamientos inaceptables a [conduct@citasmart.com](mailto:conduct@citasmart.com).

## 🤔 ¿Cómo Puedo Contribuir?

### 🐛 Reportar Bugs

Los bugs se rastrean como [GitHub issues](https://github.com/mauricio-acuna/citasmart/issues). Antes de crear un issue:

1. **Verifica** que el bug no haya sido reportado anteriormente
2. **Reproduce** el problema con la última versión
3. **Incluye** la información solicitada en el template

**Template para Bug Reports:**
```markdown
**Descripción del Bug**
Una descripción clara y concisa del comportamiento inesperado.

**Pasos para Reproducir**
1. Ve a '...'
2. Haz clic en '....'
3. Desplázate hacia abajo hasta '....'
4. Ve el error

**Comportamiento Esperado**
Descripción clara de lo que esperabas que ocurriera.

**Screenshots**
Si es aplicable, agrega screenshots para explicar el problema.

**Información del Entorno:**
 - OS: [e.g. Windows 11, macOS 12.6, Ubuntu 22.04]
 - Browser: [e.g. Chrome 118, Firefox 119, Safari 17]
 - Versión de CitaSmart: [e.g. v1.2.3]

**Contexto Adicional**
Cualquier otra información sobre el problema.
```

### 💡 Sugerir Mejoras

Las mejoras también se rastrean como [GitHub issues](https://github.com/mauricio-acuna/citasmart/issues/new?template=feature_request.md). 

**Template para Feature Requests:**
```markdown
**¿Tu feature request está relacionado con un problema?**
Una descripción clara del problema. Ej. Siempre me frustra cuando [...]

**Describe la solución que te gustaría**
Descripción clara y concisa de lo que quieres que suceda.

**Describe alternativas que hayas considerado**
Descripción de soluciones o features alternativos que hayas considerado.

**Mockups/Wireframes**
Si tienes ideas visuales, compártelas.

**Contexto adicional**
Cualquier otra información sobre el feature request.
```

## 💻 Contribuir con Código

### 🔧 Configuración del Entorno

1. **Fork** el repositorio
2. **Clona** tu fork:
   ```bash
   git clone https://github.com/tu-usuario/citasmart.git
   cd citasmart
   ```

3. **Configura** el upstream:
   ```bash
   git remote add upstream https://github.com/mauricio-acuna/citasmart.git
   ```

4. **Instala** dependencias:
   ```bash
   # Backend
   cd backend
   ./mvnw clean install
   
   # Frontend
   cd ../frontend
   npm install
   ```

5. **Configura** la base de datos:
   ```bash
   # Crea la base de datos
   createdb citasmart_dev
   
   # Ejecuta migraciones
   ./mvnw flyway:migrate
   ```

### 🌿 Branching Strategy

Utilizamos **Git Flow** modificado:

- `main`: Código de producción estable
- `develop`: Rama de desarrollo principal
- `feature/feature-name`: Nuevas características
- `bugfix/bug-description`: Corrección de bugs
- `hotfix/critical-fix`: Fixes críticos para producción
- `release/version-number`: Preparación de releases

**Convención de Nombres:**
```bash
feature/user-authentication
bugfix/payment-processing-error
hotfix/security-vulnerability
release/v1.2.0
```

### 📝 Commit Messages

Seguimos la [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**Tipos válidos:**
- `feat`: Nueva característica
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Cambios que no afectan el significado del código
- `refactor`: Refactoring de código
- `perf`: Mejoras de performance
- `test`: Agregar o corregir tests
- `chore`: Cambios en build process o herramientas auxiliares

**Ejemplos:**
```bash
feat(auth): add OAuth2 integration
fix(booking): resolve double booking issue
docs(api): update endpoint documentation
style(frontend): format code with prettier
refactor(service): extract payment logic to separate service
test(booking): add unit tests for reservation service
```

## 🔄 Pull Request Process

### 1. Preparación

1. **Sincroniza** con upstream:
   ```bash
   git checkout develop
   git pull upstream develop
   ```

2. **Crea** una nueva rama:
   ```bash
   git checkout -b feature/mi-nueva-caracteristica
   ```

3. **Desarrolla** tu característica con commits atómicos

### 2. Testing

Antes de crear el PR, asegúrate de que:

- [ ] Todos los tests pasan
- [ ] Código cumple con estándares de calidad
- [ ] Documentación está actualizada
- [ ] No hay conflictos con develop

```bash
# Backend tests
./mvnw test

# Frontend tests  
npm run test
npm run e2e

# Code quality
./mvnw sonar:sonar
npm run lint
```

### 3. Crear Pull Request

1. **Push** tu rama:
   ```bash
   git push origin feature/mi-nueva-caracteristica
   ```

2. **Crea** el PR usando el template:

```markdown
## 📋 Descripción

Descripción clara de los cambios realizados.

## 🔧 Tipo de Cambio

- [ ] 🐛 Bug fix (cambio que corrige un issue)
- [ ] ✨ Nueva característica (cambio que agrega funcionalidad)
- [ ] 💥 Breaking change (fix o feature que causaría cambios incompatibles)
- [ ] 📚 Documentación
- [ ] 🎨 Mejora de código (formatting, renaming, etc)
- [ ] ⚡ Mejora de performance
- [ ] ✅ Tests

## 🧪 ¿Cómo se ha probado?

Describe las pruebas realizadas para verificar los cambios.

- [ ] Tests unitarios
- [ ] Tests de integración
- [ ] Tests manuales
- [ ] Tests E2E

## 📋 Checklist

- [ ] Mi código sigue las guías de estilo del proyecto
- [ ] He realizado una auto-revisión de mi código
- [ ] He comentado el código en áreas difíciles de entender
- [ ] He actualizado la documentación correspondiente
- [ ] Mis cambios no generan nuevas advertencias
- [ ] He agregado tests que prueban mi fix/feature
- [ ] Tests nuevos y existentes pasan localmente

## 📸 Screenshots (si aplica)

Agrega screenshots para cambios en UI.

## 🔗 Issues Relacionados

Fixes #(issue number)
```

### 4. Review Process

1. **Automated checks** deben pasar
2. **Code review** por al menos 2 maintainers
3. **Resolución** de comentarios de review
4. **Aprobación** final antes de merge

## 📏 Estándares de Código

### Backend (Java/Spring Boot)

#### Code Style
- **Google Java Style Guide** como base
- **Checkstyle** configurado para enforcement
- **SpotBugs** para análisis estático
- **PMD** para detección de code smells

#### Convenciones
```java
// Clases: PascalCase
public class UserService { }

// Métodos y variables: camelCase
public void createUser() { }
private String userName;

// Constantes: UPPER_SNAKE_CASE
private static final String API_VERSION = "v1";

// Packages: lowercase
com.citasmart.booking.service
```

#### Estructura de Packages
```
com.citasmart
├── booking
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   └── config
├── user
├── payment
└── notification
```

#### Testing
- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: TestContainers
- **Coverage mínimo**: 80%

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest("john@example.com");
        
        // When
        UserResponse response = userService.createUser(request);
        
        // Then
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }
}
```

### Frontend (Angular/TypeScript)

#### Code Style
- **Angular Style Guide** oficial
- **ESLint** + **Prettier** para formatting
- **Husky** para pre-commit hooks

#### Estructura de Archivos
```
src/
├── app/
│   ├── core/
│   │   ├── guards/
│   │   ├── interceptors/
│   │   └── services/
│   ├── shared/
│   │   ├── components/
│   │   ├── directives/
│   │   └── pipes/
│   ├── features/
│   │   ├── booking/
│   │   ├── user/
│   │   └── dashboard/
│   └── layout/
├── assets/
└── environments/
```

#### Convenciones
```typescript
// Interfaces: PascalCase con I prefix
interface IUser {
  id: string;
  email: string;
}

// Components: PascalCase + Component suffix
@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html'
})
export class UserProfileComponent { }

// Services: PascalCase + Service suffix
@Injectable({
  providedIn: 'root'
})
export class UserService { }

// Variables y métodos: camelCase
private userName: string;
public getUserById(id: string): Observable<User> { }
```

#### Testing
```typescript
describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;
  let userService: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('UserService', ['getUser']);

    await TestBed.configureTestingModule({
      declarations: [UserComponent],
      providers: [
        { provide: UserService, useValue: spy }
      ]
    }).compileComponents();

    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
```

### Database

#### Naming Conventions
- **Tables**: snake_case, plural
- **Columns**: snake_case
- **Indexes**: `idx_table_column`
- **Foreign Keys**: `fk_table_referenced_table`

```sql
-- ✅ Correcto
CREATE TABLE appointment_bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    booking_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_service FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE INDEX idx_bookings_user_id ON appointment_bookings(user_id);
CREATE INDEX idx_bookings_date ON appointment_bookings(booking_date);
```

## 🎯 Proceso de Release

### 1. Feature Freeze
- No se aceptan nuevas features para el release
- Solo bugfixes y documentation updates

### 2. Release Branch
```bash
git checkout develop
git pull origin develop
git checkout -b release/v1.2.0
```

### 3. Release Preparation
- Actualizar version numbers
- Actualizar CHANGELOG.md
- Final testing round
- Documentation review

### 4. Release
```bash
git checkout main
git merge release/v1.2.0
git tag v1.2.0
git push origin main --tags
```

### 5. Hotfixes
Para fixes críticos en producción:
```bash
git checkout main
git checkout -b hotfix/critical-security-fix
# Hacer cambios
git checkout main
git merge hotfix/critical-security-fix
git tag v1.2.1
git checkout develop
git merge main
```

## 📊 Métricas de Calidad

Mantenemos altos estándares de calidad:

### Code Coverage
- **Backend**: Mínimo 80%
- **Frontend**: Mínimo 75%

### Performance
- **API Response Time**: < 500ms (95th percentile)
- **Page Load Time**: < 3 segundos
- **Bundle Size**: < 2MB (gzipped)

### Security
- **OWASP Top 10** compliance
- **Dependency vulnerabilities**: Zero high/critical
- **Code quality**: SonarQube Quality Gate passed

## 🔍 Code Review Guidelines

### Para Reviewers

**Technical Review:**
- [ ] ¿El código hace lo que dice que hace?
- [ ] ¿Es eficiente y performante?
- [ ] ¿Sigue las convenciones del proyecto?
- [ ] ¿Está bien testeado?
- [ ] ¿Es seguro?

**Design Review:**
- [ ] ¿Es la solución simple y elegante?
- [ ] ¿Es extensible y mantenible?
- [ ] ¿Sigue los principios SOLID?
- [ ] ¿Es consistent con la arquitectura?

**User Experience:**
- [ ] ¿Es intuitivo para el usuario?
- [ ] ¿Maneja errores gracefully?
- [ ] ¿Es accesible?
- [ ] ¿Es responsive?

### Para Authors

**Before Submitting:**
- [ ] Self-review completado
- [ ] Tests agregados y pasando
- [ ] Documentation actualizada
- [ ] No hay console.logs o TODOs

**During Review:**
- Responde a comentarios constructivamente
- Pregunta si no entiendes un comentario
- Agradece el feedback
- Haz cambios solicitados promptly

## 🏆 Reconocimiento

Los contribuidores son reconocidos en:

- **CONTRIBUTORS.md**: Lista de todos los contribuidores
- **Release Notes**: Menciones especiales
- **Hall of Fame**: Top contributors del mes/año
- **Swag**: Stickers, camisetas para contribuidores activos

## 📞 ¿Necesitas Ayuda?

- **Discord**: [CitaSmart Community](https://discord.gg/citasmart)
- **Email**: [dev@citasmart.com](mailto:dev@citasmart.com)
- **GitHub Discussions**: [Community Discussions](https://github.com/mauricio-acuna/citasmart/discussions)

---

¡Gracias por contribuir a CitaSmart! 🎉

Juntos estamos construyendo el futuro de la gestión de servicios de bienestar.
