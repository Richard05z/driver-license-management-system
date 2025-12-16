-- ============================================
-- SISTEMA DE GESTIÓN DE LICENCIAS DE CONDUCCIÓN
-- PostgreSQL 15+
-- ============================================

-- 1. ELIMINAR BASES DE DATOS EXISTENTES (SI ES NECESARIO)
-- DROP DATABASE IF EXISTS driver_license_db;
-- CREATE DATABASE driver_license_db;
-- \c driver_license_db;

-- ============================================
-- 1. ENUMERACIONES (TIPOS PERSONALIZADOS)
-- ============================================

-- Tipo de entidad (clínica o autoescuela)
CREATE TYPE "tipo_entidad_enum" AS ENUM (
    'clinica',
    'autoescuela'
);

-- Estado de la licencia
CREATE TYPE "estado_licencia_enum" AS ENUM (
    'vigente',
    'vencida',
    'suspendida',
    'revocada'
);

-- Tipo de licencia (A, B, C, D, E, F)
CREATE TYPE "tipo_licencia_enum" AS ENUM (
    'A',
    'B',
    'C',
    'D',
    'E',
    'F'
);

-- Categoría de licencia
CREATE TYPE "categoria_licencia_enum" AS ENUM (
    'camion',
    'moto',
    'automovil',
    'autobus'
);

-- Tipo de examen
CREATE TYPE "tipo_examen_enum" AS ENUM (
    'medico',
    'teorico',
    'practico'
);

-- Resultado del examen
CREATE TYPE "resultado_examen_enum" AS ENUM (
    'aprobado',
    'reprobado'
);

-- Tipo de infracción
CREATE TYPE "tipo_infraccion_enum" AS ENUM (
    'leve',
    'grave',
    'muy_grave'
);

-- ============================================
-- 2. TABLAS PRINCIPALES
-- ============================================

-- Tabla: CENTRO (centros administrativos)
CREATE TABLE "centro" (
    "id_centro" SERIAL PRIMARY KEY,
    "nombre" VARCHAR(150) NOT NULL,
    "codigo" VARCHAR(50) UNIQUE NOT NULL,
    "direccion_postal" VARCHAR(200),
    "telefono" VARCHAR(50),
    "email" VARCHAR(50) UNIQUE NOT NULL,
    "director_general" VARCHAR(150),
    "jefe_rrhh" VARCHAR(150),
    "jefe_contabilidad" VARCHAR(150),
    "secretario_sindicato" VARCHAR(150),
    "logo" TEXT
);

-- Tabla: ENTIDAD (clínicas y autoescuelas)
CREATE TABLE "entidad" (
    "id_entidad" SERIAL PRIMARY KEY,
    "nombre" VARCHAR(150) NOT NULL,
    "tipo_entidad" tipo_entidad_enum NOT NULL,
    "direccion" VARCHAR(200),
    "telefono" VARCHAR(50),
    "email" VARCHAR(150),
    "director" VARCHAR(150),
    "id_centro" INT
);

-- Tabla: CONDUCTOR (conductores/aspirantes)
CREATE TABLE "conductor" (
    "id_conductor" SERIAL PRIMARY KEY,
    "nombre" VARCHAR(100) NOT NULL,
    "apellidos" VARCHAR(150) NOT NULL,
    "documento_identidad" VARCHAR(50) UNIQUE NOT NULL,
    "fecha_nacimiento" DATE NOT NULL,
    "direccion" VARCHAR(200),
    "telefono" VARCHAR(50),
    "email" VARCHAR(150),
    "estado_licencia" estado_licencia_enum
);

-- Tabla: LICENCIA (licencias emitidas)
CREATE TABLE "licencia" (
    "id_licencia" SERIAL PRIMARY KEY,
    "id_conductor" INT NOT NULL,
    "tipo_licencia" tipo_licencia_enum NOT NULL,
    "categoria" categoria_licencia_enum NOT NULL,
    "fecha_emision" DATE NOT NULL,
    "fecha_vencimiento" DATE NOT NULL,
    "puntos" INT NOT NULL DEFAULT 20 CHECK (puntos >= 0 AND puntos <= 20),
    "restricciones" TEXT,
    "renovada" BOOLEAN NOT NULL DEFAULT false
);

-- Tabla: EXAMEN (exámenes realizados)
CREATE TABLE "examen" (
    "id_examen" SERIAL PRIMARY KEY,
    "tipo_examen" tipo_examen_enum NOT NULL,
    "fecha" DATE NOT NULL,
    "resultado" resultado_examen_enum NOT NULL,
    "id_entidad" INT NOT NULL,
    "id_conductor" INT NOT NULL,
    "examinador" VARCHAR(150)
);

-- Tabla: INFRACCIÓN (infracciones cometidas)
CREATE TABLE "infraccion" (
    "id_infraccion" SERIAL PRIMARY KEY,
    "id_conductor" INT NOT NULL,
    "id_licencia" INT NOT NULL,
    "codigo" VARCHAR(50) NOT NULL,
    "tipo" tipo_infraccion_enum NOT NULL,
    "fecha" DATE NOT NULL,
    "lugar" VARCHAR(200),
    "descripcion" TEXT,
    "puntos_deducidos" INT CHECK (puntos_deducidos >= 0),
    "pagada" BOOLEAN NOT NULL DEFAULT false
);

-- ============================================
-- 3. ÍNDICES PARA MEJORAR EL RENDIMIENTO
-- ============================================

-- Índices para tabla CENTRO
CREATE INDEX "idx_codigo" ON "centro" ("codigo");
CREATE INDEX "idx_direccion_postal" ON "centro" ("direccion_postal");
CREATE INDEX "idx_email_centro" ON "centro" ("email");

-- Índices para tabla ENTIDAD
CREATE INDEX "idx_id_centro" ON "entidad" ("id_centro");
CREATE INDEX "idx_tipo_entidad" ON "entidad" ("tipo_entidad");
CREATE INDEX "idx_email_entidad" ON "entidad" ("email");

-- Índices para tabla CONDUCTOR
CREATE INDEX "idx_documento_identidad" ON "conductor" ("documento_identidad");
CREATE INDEX "idx_estado_licencia" ON "conductor" ("estado_licencia");
CREATE INDEX "idx_email_conductor" ON "conductor" ("email");
CREATE INDEX "idx_fecha_nacimiento" ON "conductor" ("fecha_nacimiento");

-- Índices para tabla LICENCIA
CREATE INDEX "idx_id_conductor_licencia" ON "licencia" ("id_conductor");
CREATE INDEX "idx_tipo_licencia" ON "licencia" ("tipo_licencia");
CREATE INDEX "idx_categoria" ON "licencia" ("categoria");
CREATE INDEX "idx_renovada" ON "licencia" ("renovada");
CREATE INDEX "idx_fecha_vencimiento" ON "licencia" ("fecha_vencimiento");
CREATE INDEX "idx_fecha_emision" ON "licencia" ("fecha_emision");
CREATE INDEX "idx_puntos" ON "licencia" ("puntos");

-- Índices para tabla EXAMEN
CREATE INDEX "idx_id_conductor_examen" ON "examen" ("id_conductor");
CREATE INDEX "idx_id_entidad" ON "examen" ("id_entidad");
CREATE INDEX "idx_resultado" ON "examen" ("resultado");
CREATE INDEX "idx_tipo_examen" ON "examen" ("tipo_examen");
CREATE INDEX "idx_fecha_examen" ON "examen" ("fecha");

-- Índices para tabla INFRACCIÓN
CREATE INDEX "idx_id_conductor_infraccion" ON "infraccion" ("id_conductor");
CREATE INDEX "idx_id_licencia" ON "infraccion" ("id_licencia");
CREATE INDEX "idx_codigo" ON "infraccion" ("codigo");
CREATE INDEX "idx_tipo_infraccion" ON "infraccion" ("tipo");
CREATE INDEX "idx_pagada" ON "infraccion" ("pagada");
CREATE INDEX "idx_fecha_infraccion" ON "infraccion" ("fecha");

-- ============================================
-- 4. CLAVES FORÁNEAS (RELACIONES)
-- ============================================

-- Relación: Entidad → Centro
ALTER TABLE "entidad" 
    ADD FOREIGN KEY ("id_centro") 
    REFERENCES "centro" ("id_centro") 
    ON DELETE SET NULL 
    ON UPDATE CASCADE;

-- Relación: Licencia → Conductor
ALTER TABLE "licencia" 
    ADD FOREIGN KEY ("id_conductor") 
    REFERENCES "conductor" ("id_conductor") 
    ON DELETE CASCADE 
    ON UPDATE CASCADE;

-- Relación: Examen → Entidad
ALTER TABLE "examen" 
    ADD FOREIGN KEY ("id_entidad") 
    REFERENCES "entidad" ("id_entidad") 
    ON DELETE RESTRICT 
    ON UPDATE CASCADE;

-- Relación: Examen → Conductor
ALTER TABLE "examen" 
    ADD FOREIGN KEY ("id_conductor") 
    REFERENCES "conductor" ("id_conductor") 
    ON DELETE CASCADE 
    ON UPDATE CASCADE;

-- Relación: Infracción → Conductor
ALTER TABLE "infraccion" 
    ADD FOREIGN KEY ("id_conductor") 
    REFERENCES "conductor" ("id_conductor") 
    ON DELETE CASCADE 
    ON UPDATE CASCADE;

-- Relación: Infracción → Licencia
ALTER TABLE "infraccion" 
    ADD FOREIGN KEY ("id_licencia") 
    REFERENCES "licencia" ("id_licencia") 
    ON DELETE RESTRICT 
    ON UPDATE CASCADE;

-- ============================================
-- 5. DATOS DE PRUEBA (SEED DATA)
-- ============================================

-- Insertar Centros
INSERT INTO centro (nombre, codigo, direccion_postal, telefono, email, director_general, jefe_rrhh, jefe_contabilidad, secretario_sindicato) VALUES
('Centro Principal Habana', 'CPHAB001', 'Calle 10 #123, Vedado', '78325000', 'contacto@cphabana.cu', 'Dr. Raúl Pérez', 'Lic. Ana Soto', 'Msc. Carlos Díaz', 'Téc. Elena Cruz'),
('Centro Oriental Santiago', 'CPOST002', 'Avenida Libertadores, Santiago', '22684000', 'contacto@stg.cu', 'Ing. Javier Ruiz', 'Lic. Rosa Gómez', 'Téc. Miguel León', 'Téc. Patricia Vega');

-- Insertar Entidades
INSERT INTO entidad (nombre, tipo_entidad, direccion, telefono, email, director, id_centro) VALUES
('Clínica Central de Tránsito', 'clinica', 'Calle Salud #45', '78761000', 'clinicacentral@salud.cu', 'Dra. Isabel Mena', 1),
('Autoescuela Rápida', 'autoescuela', 'Via Blanca Km 5', '77002000', 'info@rapida.cu', 'Sr. Luis Garcés', 1),
('Clínica Zonal Este', 'clinica', 'Ave 25 #567', '22695000', 'clinicazonal@este.cu', 'Dra. Marta Vidal', 2),
('Autoescuela Segura', 'autoescuela', 'Calle Pinar #12', '22634000', 'contacto@segura.cu', 'Sr. Juan Torres', 2);

-- Insertar Conductores
INSERT INTO conductor (nombre, apellidos, documento_identidad, fecha_nacimiento, direccion, telefono, email, estado_licencia) VALUES
('Alejandro', 'Díaz Soto', '85010145678', '1985-01-01', 'Calle Falsa 123', '5351234567', 'alejandro.d@mail.com', 'vigente'),
('Beatriz', 'Gómez Vidal', '92051512345', '1992-05-15', 'Avenida Siempre Viva 742', '5352345678', 'beatriz.g@mail.com', 'vigente'),
('Camilo', 'López Vega', '70123098765', '1970-12-30', 'Paseo del Prado', '5353456789', 'camilo.l@mail.com', 'vencida'),
('Daniela', 'Martínez Ruiz', '95102065432', '1995-10-20', 'Calle 42 #1501', '5354567890', 'daniela.m@mail.com', 'suspendida'),
('Eduardo', 'Fernández Castro', '88071578901', '1988-07-15', 'Avenida 5ta #608', '5355678901', 'eduardo.f@mail.com', 'vigente');

INSERT INTO licencia (id_conductor, tipo_licencia, categoria, fecha_emision, fecha_vencimiento, puntos, restricciones, renovada) VALUES
(1, 'B', 'automovil', '2018-03-10', '2028-03-10', 20, 'Usa lentes', false),
(1, 'A', 'moto', '2018-03-10', '2028-03-10', 20, NULL, false),
(2, 'D', 'autobus', '2020-07-20', '2025-07-20', 20, 'Solo rutas urbanas', false),
(3, 'C', 'camion', '2000-01-01', '2005-01-01', 20, NULL, true),
(4, 'B', 'automovil', '2019-05-15', '2029-05-15', 20, 'Lentes, solo automático', false),
(5, 'A', 'moto', '2021-08-30', '2031-08-30', 20, 'Solo motos hasta 250cc', false),
(5, 'C', 'camion', '2021-08-30', '2031-08-30', 20, 'Sin remolques', false);

-- Insertar Infracciones
INSERT INTO infraccion (id_conductor, id_licencia, codigo, tipo, fecha, lugar, descripcion, puntos_deducidos, pagada) VALUES
(1, 1, 'EXCVEL102', 'grave', '2024-03-15', 'Autopista Nacional Km 209', 'Exceso de velocidad (80 km/h sobre el límite)', 4, false),
(2, 3, 'MALPAR207', 'grave', '2024-03-01', 'Zona Comercial', 'Estacionamiento en zona prohibida', 1, true),
(3, 4, 'CONVENC303', 'muy_grave', '2024-04-10', 'Punto de control', 'Conducción con licencia vencida', 10, false),
(4, 5, 'SINLUZ401', 'leve', '2024-02-20', 'Calle Principal', 'Circular sin luces en horario nocturno', 2, true),
(5, 6, 'EXCVEL505', 'grave', '2024-05-05', 'Carretera Central', 'Exceso de velocidad en moto', 4, false);

-- Insertar Exámenes
INSERT INTO examen (tipo_examen, fecha, resultado, id_entidad, id_conductor, examinador) VALUES
('medico', '2023-10-01', 'aprobado', 1, 1, 'Dr. Jorge Vidal'),
('teorico', '2023-10-15', 'aprobado', 2, 1, 'Prof. Elisa Sanz'),
('practico', '2023-10-25', 'reprobado', 2, 1, 'Prof. Raúl Maza'),
('practico', '2023-11-05', 'aprobado', 2, 1, 'Prof. Raúl Maza'),
('medico', '2024-01-05', 'aprobado', 3, 2, 'Dra. Carmen Pardo'),
('teorico', '2024-02-10', 'aprobado', 4, 3, 'Prof. Laura Montes'),
('medico', '2024-03-15', 'reprobado', 1, 4, 'Dr. Roberto Paz'),
('teorico', '2024-04-20', 'aprobado', 2, 5, 'Prof. Ana Beltrán');

-- ============================================
-- 6. FUNCIONES DE REPORTES
-- ============================================

-- Función 1: Reporte de licencias emitidas en un rango de fecha
CREATE OR REPLACE FUNCTION generar_reporte_licencias_emitidas(
    fecha_inicio DATE,
    fecha_fin DATE
)
RETURNS TABLE (
    id_licencia INT,
    tipo_licencia VARCHAR,
    fecha_emision DATE,
    fecha_vencimiento DATE,
    puntos INT,
    nombre_conductor VARCHAR,
    documento_identidad VARCHAR
)
LANGUAGE sql
AS $$
SELECT
    l.id_licencia,
    l.tipo_licencia::VARCHAR,
    l.fecha_emision,
    l.fecha_vencimiento,
    l.puntos,
    c.nombre || ' ' || c.apellidos AS nombre_conductor,
    c.documento_identidad
FROM
    licencia l
JOIN
    conductor c ON l.id_conductor = c.id_conductor
WHERE
    l.fecha_emision BETWEEN fecha_inicio AND fecha_fin
ORDER BY
    l.fecha_emision;
$$;

-- Función 2: Fichas de centros
CREATE OR REPLACE FUNCTION obtener_fichas_de_centros()
RETURNS TABLE(
    nombre VARCHAR,
    direccion_postal VARCHAR,
    logo TEXT,
    telefono VARCHAR,
    email VARCHAR,
    director_general VARCHAR,
    jefe_rrhh VARCHAR,
    jefe_contabilidad VARCHAR,
    secretario_sindicato VARCHAR
)
LANGUAGE SQL
AS $$
SELECT
    nombre,
    direccion_postal,
    logo,
    telefono,
    email,
    director_general,
    jefe_rrhh,
    jefe_contabilidad,
    secretario_sindicato
FROM
    centro;
$$;

-- Función 3: Obtener centro por código
CREATE OR REPLACE FUNCTION obtener_ficha_del_centro_por_codigo(
    codigo_param VARCHAR
)
RETURNS TABLE(
    nombre VARCHAR,
    direccion_postal VARCHAR,
    logo TEXT,
    telefono VARCHAR,
    email VARCHAR,
    director_general VARCHAR,
    jefe_rrhh VARCHAR,
    jefe_contabilidad VARCHAR,
    secretario_sindicato VARCHAR
)
LANGUAGE SQL
AS $$
SELECT
    nombre,
    direccion_postal,
    logo,
    telefono,
    email,
    director_general,
    jefe_rrhh,
    jefe_contabilidad,
    secretario_sindicato
FROM
    centro
WHERE codigo = codigo_param;
$$;

-- Función 4: Obtener ficha de conductor por ID
CREATE OR REPLACE FUNCTION obtener_conductor_por_id(id_param INT)
RETURNS TABLE(
    id_conductor INT,
    nombre VARCHAR,
    apellidos VARCHAR,
    documento_identidad VARCHAR,
    direccion VARCHAR,
    telefono VARCHAR,
    estado_licencia VARCHAR,
    tipo_licencia VARCHAR,
    puntos_licencia INT,
    fecha_emision DATE,
    fecha_vencimiento DATE,
    tipo_infraccion VARCHAR,
    fecha_infraccion DATE,
    puntos_deducidos INT
)
LANGUAGE SQL
AS $$
SELECT
    c.id_conductor,
    c.nombre,
    c.apellidos,
    c.documento_identidad,
    c.direccion,
    c.telefono,
    c.estado_licencia::VARCHAR,
    l.tipo_licencia::VARCHAR,
    l.puntos as puntos_licencia,
    l.fecha_emision,
    l.fecha_vencimiento,
    i.tipo::VARCHAR as tipo_infraccion,
    i.fecha as fecha_infraccion,
    i.puntos_deducidos
FROM conductor c
LEFT JOIN licencia l ON c.id_conductor = l.id_conductor
LEFT JOIN infraccion i ON c.id_conductor = i.id_conductor
WHERE c.id_conductor = id_param;
$$;

-- Función 5: Reporte de exámenes realizados en un período
CREATE OR REPLACE FUNCTION generar_reporte_examenes_realizados(
    fecha_inicio DATE,
    fecha_fin DATE
)
RETURNS TABLE(
    id_examen INT,
    nombre_conductor VARCHAR,
    tipo_de_examen VARCHAR,
    fecha_de_examen DATE,
    resultado_examen VARCHAR,
    nombre_entidad VARCHAR
)
LANGUAGE SQL
AS $$
SELECT
    exam.id_examen,
    c.nombre || ' ' || c.apellidos as nombre_conductor,
    exam.tipo_examen::VARCHAR as tipo_de_examen,
    exam.fecha as fecha_de_examen,
    exam.resultado::VARCHAR as resultado_examen,
    ent.nombre as nombre_entidad
FROM examen exam
JOIN conductor c ON exam.id_conductor = c.id_conductor
JOIN entidad ent ON exam.id_entidad = ent.id_entidad
WHERE exam.fecha BETWEEN fecha_inicio AND fecha_fin
ORDER BY exam.fecha ASC;
$$;

-- Función 6: Reporte de infracciones en un rango de tiempo
CREATE OR REPLACE FUNCTION obtener_reporte_infracciones(
    fecha_inicio DATE,
    fecha_fin DATE
)
RETURNS TABLE(
    codigo_infraccion VARCHAR,
    nombre_conductor VARCHAR,
    fecha_infraccion DATE,
    tipo_de_infraccion VARCHAR,
    lugar_infraccion VARCHAR,
    puntos_deducidos INT,
    estado_de_infraccion BOOLEAN
)
LANGUAGE SQL
AS $$
SELECT
    inf.codigo as codigo_infraccion,
    c.nombre || ' ' || c.apellidos as nombre_conductor,
    inf.fecha as fecha_infraccion,
    inf.tipo::VARCHAR as tipo_de_infraccion,
    inf.lugar as lugar_infraccion,
    inf.puntos_deducidos,
    inf.pagada as estado_de_infraccion
FROM infraccion inf
JOIN conductor c ON inf.id_conductor = c.id_conductor
WHERE inf.fecha BETWEEN fecha_inicio AND fecha_fin
ORDER BY inf.fecha ASC;
$$;

-- Función 7: Reporte de conductores con licencias vencidas
CREATE OR REPLACE FUNCTION generar_reporte_conductores_con_licencias_vencidas(
    fecha_inicio DATE,
    fecha_fin DATE
)
RETURNS TABLE(
    nombre VARCHAR,
    apellidos VARCHAR,
    documento_identidad VARCHAR,
    tipo_licencia VARCHAR,
    fecha_vencimiento DATE,
    estado_licencia VARCHAR
)
LANGUAGE SQL
AS $$
SELECT
    c.nombre,
    c.apellidos,
    c.documento_identidad,
    l.tipo_licencia::VARCHAR,
    l.fecha_vencimiento,
    c.estado_licencia::VARCHAR
FROM conductor c
JOIN licencia l ON c.id_conductor = l.id_conductor
WHERE (l.fecha_vencimiento BETWEEN fecha_inicio AND fecha_fin) 
    AND c.estado_licencia = 'vencida'
ORDER BY l.fecha_vencimiento ASC;
$$;

-- Función 8: Reporte consolidado de infracciones por tipo en un año
CREATE OR REPLACE FUNCTION generar_reporte_infracciones_por_anio(anio INTEGER)
RETURNS TABLE(
    anio_infraccion VARCHAR,
    tipo_infraccion VARCHAR,
    cantidad_infracciones BIGINT,
    puntos_totales_deducidos BIGINT,
    multas_totales_pagadas BIGINT,
    multas_totales_pendientes BIGINT
)
LANGUAGE SQL
AS $$
SELECT
    EXTRACT(YEAR FROM fecha)::VARCHAR AS anio_infraccion,
    tipo::VARCHAR AS tipo_infraccion,
    COUNT(*) AS cantidad_infracciones,
    COALESCE(SUM(puntos_deducidos), 0) AS puntos_totales_deducidos,
    COUNT(*) FILTER (WHERE pagada = true) AS multas_totales_pagadas,
    COUNT(*) FILTER (WHERE pagada = false) AS multas_totales_pendientes
FROM infraccion
WHERE EXTRACT(YEAR FROM fecha) = anio
GROUP BY
    anio_infraccion,
    tipo_infraccion
ORDER BY
    anio_infraccion ASC,
    tipo_infraccion ASC;
$$;

-- ============================================
-- 7. TRIGGER PARA VALIDACIÓN DE EXAMENES
-- ============================================

-- Función del trigger
CREATE OR REPLACE FUNCTION validar_entidad_examen()
RETURNS trigger AS $$
DECLARE
    tipo_entidad_asociada tipo_entidad_enum;
BEGIN
    -- 1. Obtener el tipo de entidad asociado al id_entidad
    SELECT tipo_entidad INTO tipo_entidad_asociada
    FROM entidad
    WHERE id_entidad = NEW.id_entidad;

    -- 2. Verificar la regla de negocio
    IF NEW.tipo_examen = 'medico' THEN
        IF tipo_entidad_asociada != 'clinica' THEN
            RAISE EXCEPTION 'El examen médico debe ser realizado por una entidad tipo CLINICA. Entidad % es de tipo %.', 
                NEW.id_entidad, tipo_entidad_asociada;
        END IF;
    ELSIF NEW.tipo_examen IN ('teorico', 'practico') THEN
        IF tipo_entidad_asociada != 'autoescuela' THEN
            RAISE EXCEPTION 'Los exámenes teórico/práctico deben ser realizados por una entidad tipo AUTOESCUELA. Entidad % es de tipo %.', 
                NEW.id_entidad, tipo_entidad_asociada;
        END IF;
    END IF;

    -- Si la validación es exitosa, permitir la operación
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear el trigger
CREATE TRIGGER chk_tipo_examen_por_entidad_trigger
    BEFORE INSERT OR UPDATE ON examen
    FOR EACH ROW
    EXECUTE FUNCTION validar_entidad_examen();

-- ============================================
-- 8. FUNCIONES ADICIONALES ÚTILES
-- ============================================

-- Función 9: Obtener estadísticas generales
CREATE OR REPLACE FUNCTION obtener_estadisticas_generales()
RETURNS TABLE(
    total_conductores BIGINT,
    total_licencias BIGINT,
    licencias_vigentes BIGINT,
    licencias_vencidas BIGINT,
    total_infracciones BIGINT,
    infracciones_pendientes BIGINT,
    total_examenes BIGINT,
    examenes_aprobados BIGINT
)
LANGUAGE SQL
AS $$
SELECT
    (SELECT COUNT(*) FROM conductor) as total_conductores,
    (SELECT COUNT(*) FROM licencia) as total_licencias,
    (SELECT COUNT(*) FROM conductor WHERE estado_licencia = 'vigente') as licencias_vigentes,
    (SELECT COUNT(*) FROM conductor WHERE estado_licencia = 'vencida') as licencias_vencidas,
    (SELECT COUNT(*) FROM infraccion) as total_infracciones,
    (SELECT COUNT(*) FROM infraccion WHERE pagada = false) as infracciones_pendientes,
    (SELECT COUNT(*) FROM examen) as total_examenes,
    (SELECT COUNT(*) FROM examen WHERE resultado = 'aprobado') as examenes_aprobados;
$$;

-- Función 10: Buscar conductor por documento
CREATE OR REPLACE FUNCTION buscar_conductor_por_documento(doc_identidad VARCHAR)
RETURNS TABLE(
    id_conductor INT,
    nombre_completo VARCHAR,
    documento_identidad VARCHAR,
    fecha_nacimiento DATE,
    edad INT,
    estado_licencia VARCHAR,
    telefono VARCHAR,
    email VARCHAR
)
LANGUAGE SQL
AS $$
SELECT
    id_conductor,
    nombre || ' ' || apellidos as nombre_completo,
    documento_identidad,
    fecha_nacimiento,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, fecha_nacimiento))::INT as edad,
    estado_licencia::VARCHAR,
    telefono,
    email
FROM conductor
WHERE documento_identidad = doc_identidad;
$$;

-- ============================================
-- 9. VISTAS ÚTILES
-- ============================================

-- Vista 1: Vista completa de conductores con licencia
CREATE OR REPLACE VIEW vista_conductores_completa AS
SELECT 
    c.id_conductor,
    c.nombre || ' ' || c.apellidos as nombre_completo,
    c.documento_identidad,
    c.fecha_nacimiento,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, c.fecha_nacimiento)) as edad,
    c.estado_licencia,
    c.telefono,
    c.email,
    l.tipo_licencia,
    l.categoria,
    l.fecha_emision,
    l.fecha_vencimiento,
    CASE 
        WHEN l.fecha_vencimiento < CURRENT_DATE THEN 'VENCIDA'
        WHEN l.fecha_vencimiento <= CURRENT_DATE + INTERVAL '30 days' THEN 'POR VENCER'
        ELSE 'VIGENTE'
    END as estado_vencimiento
FROM conductor c
LEFT JOIN licencia l ON c.id_conductor = l.id_conductor;

-- Vista 2: Resumen de infracciones por conductor
CREATE OR REPLACE VIEW vista_resumen_infracciones AS
SELECT 
    c.id_conductor,
    c.nombre || ' ' || c.apellidos as nombre_conductor,
    COUNT(i.id_infraccion) as total_infracciones,
    SUM(i.puntos_deducidos) as puntos_totales,
    COUNT(i.id_infraccion) FILTER (WHERE i.pagada = true) as infracciones_pagadas,
    COUNT(i.id_infraccion) FILTER (WHERE i.pagada = false) as infracciones_pendientes
FROM conductor c
LEFT JOIN infraccion i ON c.id_conductor = i.id_conductor
GROUP BY c.id_conductor, c.nombre, c.apellidos;

-- ============================================
-- 10. SCRIPT DE VERIFICACIÓN
-- ============================================

-- Verificar que todo se creó correctamente
DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'BASE DE DATOS CREADA EXITOSAMENTE';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Tablas creadas: 6';
    RAISE NOTICE 'Índices creados: 23';
    RAISE NOTICE 'Funciones creadas: 10';
    RAISE NOTICE 'Vistas creadas: 2';
    RAISE NOTICE 'Trigger creado: 1';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Datos de prueba insertados:';
    RAISE NOTICE '  - Centros: 2';
    RAISE NOTICE '  - Entidades: 4';
    RAISE NOTICE '  - Conductores: 5';
    RAISE NOTICE '  - Licencias: 7';
    RAISE NOTICE '  - Infracciones: 5';
    RAISE NOTICE '  - Exámenes: 8';
    RAISE NOTICE '============================================';
END $$;

-- Consulta de verificación final
SELECT '✅ Base de datos lista para usar' as mensaje;