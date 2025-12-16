# CHANGELOG - Sistema de Gestión de Licencias de Conducción

## [1.1.0] - 2024-XX-XX

### Added
- **Campo `puntos` en tabla `licencia`**:
  - Tipo: `INT NOT NULL`
  - Valor por defecto: `20`
  - Constraint: `CHECK (puntos >= 0 AND puntos <= 20)`
  - Representa el sistema de puntos de la licencia de conducir (máximo 20 puntos)

### Changed
- **Campo `estado_licencia` en tabla `conductor`**:
  - Cambiado de `NOT NULL` a `NULLABLE`
  - Ahora permite valores nulos para conductores que aún no tienen licencia

- **Índices**:
  - Agregado nuevo índice `idx_puntos` en tabla `licencia` para optimizar consultas por puntos

- **Función `generar_reporte_licencias_emitidas`**:
  - Modificada para incluir el campo `puntos` en el resultado
  - Campos retornados actualizados: `id_licencia, tipo_licencia, fecha_emision, fecha_vencimiento, puntos, nombre_conductor, documento_identidad`

- **Función `obtener_conductor_por_id`**:
  - Modificada para incluir `puntos_licencia` en el resultado
  - Campos retornados actualizados para mostrar los puntos de la licencia

### Database Updates
- **Estructura modificada**:
  ```sql
  -- Tabla conductor modificada
  "estado_licencia" estado_licencia_enum -- (antes: NOT NULL)
  
  -- Tabla licencia modificada
  "puntos" INT NOT NULL DEFAULT 20 CHECK (puntos >= 0 AND puntos <= 20)