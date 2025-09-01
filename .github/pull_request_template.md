## 📋 Descripción

Proporciona una descripción clara y concisa de los cambios realizados en este pull request.

**Resuelve:** #(número de issue)

## 🔧 Tipo de Cambio

¿Qué tipo de cambio introduce este PR? (Marca todas las que apliquen)

- [ ] 🐛 Bug fix (cambio que corrige un issue)
- [ ] ✨ Nueva característica (cambio que agrega funcionalidad)
- [ ] 💥 Breaking change (fix o feature que causaría que funcionalidad existente no funcione como se esperaba)
- [ ] 📚 Documentación (cambios solo en documentación)
- [ ] 🎨 Mejora de código (formatting, renaming, etc., sin cambios en funcionalidad)
- [ ] ⚡ Mejora de performance
- [ ] ✅ Tests (agregar tests faltantes o corregir tests existentes)
- [ ] 🔧 Chore (cambios en build process, herramientas auxiliares, etc.)

## 🧪 ¿Cómo se ha probado?

Describe las pruebas que ejecutaste para verificar tus cambios. Proporciona instrucciones para que podamos reproducir.

**Configuración de Test:**
- OS: 
- Browser: 
- Versión de Node: 
- Versión de Java: 

**Tests ejecutados:**
- [ ] Tests unitarios (`./mvnw test` / `npm run test`)
- [ ] Tests de integración
- [ ] Tests E2E (`npm run e2e`)
- [ ] Tests manuales
- [ ] Tests de performance
- [ ] Tests de accesibilidad

**Escenarios probados:**
1. 
2. 
3. 

## 📸 Screenshots/Videos

Si tus cambios incluyen modificaciones visuales, por favor agrega screenshots o videos.

**Antes:**
<!-- Imagen del estado anterior -->

**Después:**
<!-- Imagen del nuevo estado -->

## 📋 Checklist

**Antes de solicitar review:**

### 🔍 Code Quality
- [ ] Mi código sigue las guías de estilo del proyecto
- [ ] He realizado una auto-revisión de mi código
- [ ] He comentado mi código, particularmente en áreas difíciles de entender
- [ ] No he dejado código comentado o console.logs
- [ ] No hay errores de linting

### 📚 Documentación
- [ ] He actualizado la documentación correspondiente
- [ ] He actualizado el CHANGELOG.md si es necesario
- [ ] Los comentarios JSDoc/Javadoc están actualizados
- [ ] README.md está actualizado si es necesario

### 🧪 Testing
- [ ] He agregado tests que prueban mi fix o feature
- [ ] Tests nuevos y existentes pasan localmente con mis cambios
- [ ] He verificado que la cobertura de código no haya disminuido
- [ ] He probado en diferentes navegadores (si aplica)

### 🔒 Seguridad
- [ ] He considerado las implicaciones de seguridad de mis cambios
- [ ] No he expuesto información sensible en logs o código
- [ ] He validado todos los inputs de usuario
- [ ] He revisado que no hay vulnerabilidades conocidas en dependencias nuevas

### 📱 UX/UI (si aplica)
- [ ] Los cambios son responsive
- [ ] He considerado la accesibilidad (WCAG 2.1)
- [ ] Los cambios son consistentes con el design system
- [ ] He probado con diferentes tamaños de pantalla

## 🔗 Issues Relacionados

Enumera cualquier issue que este PR resuelve o está relacionado:

- Fixes #
- Closes #
- Related to #

## 🚀 Deployment Notes

¿Hay algo especial que necesite ser considerado durante el deployment?

- [ ] Requiere migraciones de base de datos
- [ ] Requiere variables de entorno nuevas/actualizadas
- [ ] Requiere actualización de dependencias
- [ ] Requiere cambios en configuración de servidor
- [ ] Breaking changes que requieren comunicación especial

**Comando de migración (si aplica):**
```bash
# Incluir comandos específicos aquí
```

**Variables de entorno requeridas:**
```bash
# Listar nuevas variables de entorno
```

## 📊 Performance Impact

¿Este cambio afecta la performance?

- [ ] ✅ No hay impacto en performance
- [ ] ⚡ Mejora la performance
- [ ] ⚠️ Podría impactar la performance (explicar abajo)

**Métricas de performance (si aplica):**
- Bundle size: 
- Load time: 
- API response time: 

## 🔄 Migration Guide

Si este PR introduce breaking changes, proporciona una guía de migración:

```bash
# Pasos para migrar desde la versión anterior
```

## 📝 Notas para Reviewers

¿Hay algo específico en lo que te gustaría que se enfoquen los reviewers?

- Áreas específicas que requieren atención especial
- Decisiones de arquitectura/diseño que necesitan validación
- Performance considerations
- Security considerations

## 🎯 Post-merge Checklist

Tareas a realizar después del merge:

- [ ] Actualizar documentación en wiki
- [ ] Comunicar cambios al equipo
- [ ] Monitorear métricas de performance
- [ ] Verificar en ambiente de staging
- [ ] Actualizar tickets relacionados

---

**Additional Notes:**
<!-- Cualquier información adicional que consideres relevante -->
