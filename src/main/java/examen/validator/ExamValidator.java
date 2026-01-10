package examen.validator;

import examen.exception.InvalidExamDataException;
import examen.model.Exam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class ExamValidator {

    // Valid exam types from enum
    private static final List<String> VALID_EXAM_TYPES = Arrays.asList("medico", "teorico", "practico");
    
    // Valid exam results from enum
    private static final List<String> VALID_RESULTS = Arrays.asList("aprobado", "reprobado");
    
    // Valid entity types from enum
    private static final List<String> VALID_ENTITY_TYPES = Arrays.asList("clinica", "autoescuela");
    
    // Business rule: which exam types can be taken at which entity types
    private static final String CLINIC_EXAM_TYPE = "medico";
    private static final List<String> DRIVING_SCHOOL_EXAM_TYPES = Arrays.asList("teorico", "practico");

    public static void validate(Exam exam) throws InvalidExamDataException {
        if (exam == null) {
            throw new InvalidExamDataException("El examen no puede ser nulo");
        }

        validateExamType(exam.getExamType());
        validateDate(exam.getDate());
        validateResult(exam.getResult());
        validateEntityId(exam.getEntityId());
        validateDriverId(exam.getDriverId());
        validateExaminer(exam.getExaminer());
    }

    public static void validateExamType(String examType) throws InvalidExamDataException {
        if (examType == null || examType.trim().isEmpty()) {
            throw new InvalidExamDataException("El tipo de examen es obligatorio");
        }
        
        String normalizedType = examType.trim().toLowerCase();
        if (!VALID_EXAM_TYPES.contains(normalizedType)) {
            throw new InvalidExamDataException(
                "Tipo de examen inválido. Valores permitidos: " + 
                String.join(", ", VALID_EXAM_TYPES)
            );
        }
    }

    public static void validateDate(String dateStr) throws InvalidExamDataException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new InvalidExamDataException("La fecha del examen es obligatoria");
        }

        try {
            LocalDate examDate = LocalDate.parse(dateStr);
            LocalDate now = LocalDate.now();
            
            // Check if date is in the future
            if (examDate.isAfter(now)) {
                throw new InvalidExamDataException("La fecha del examen no puede ser futura");
            }
            
            // Check if date is too far in the past (e.g., more than 10 years)
            if (examDate.isBefore(now.minusYears(10))) {
                throw new InvalidExamDataException("La fecha del examen no puede ser de hace más de 10 años");
            }
            
        } catch (DateTimeParseException e) {
            throw new InvalidExamDataException("Formato de fecha inválido. Use formato YYYY-MM-DD");
        }
    }

    public static void validateResult(String result) throws InvalidExamDataException {
        if (result == null || result.trim().isEmpty()) {
            throw new InvalidExamDataException("El resultado del examen es obligatorio");
        }
        
        String normalizedResult = result.trim().toLowerCase();
        if (!VALID_RESULTS.contains(normalizedResult)) {
            throw new InvalidExamDataException(
                "Resultado inválido. Valores permitidos: " + 
                String.join(", ", VALID_RESULTS)
            );
        }
    }

    public static void validateEntityId(Long entityId) throws InvalidExamDataException {
        if (entityId == null) {
            throw new InvalidExamDataException("El ID de la entidad es obligatorio");
        }
        
        if (entityId <= 0) {
            throw new InvalidExamDataException("El ID de la entidad debe ser un número positivo");
        }
    }

    public static void validateDriverId(Long driverId) throws InvalidExamDataException {
        if (driverId == null) {
            throw new InvalidExamDataException("El ID del conductor es obligatorio");
        }
        
        if (driverId <= 0) {
            throw new InvalidExamDataException("El ID del conductor debe ser un número positivo");
        }
    }

    public static void validateExaminer(String examiner) throws InvalidExamDataException {
        if (examiner != null && examiner.length() > 150) {
            throw new InvalidExamDataException("El nombre del examinador no puede exceder 150 caracteres");
        }
        
        if (examiner != null && !examiner.trim().isEmpty()) {
            // Validate examiner name format (letters and spaces)
            if (!examiner.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s.,]+$")) {
                throw new InvalidExamDataException("El nombre del examinador solo puede contener letras, puntos, comas y espacios");
            }
        }
    }

    public static void validateEntityType(String entityType) throws InvalidExamDataException {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new InvalidExamDataException("El tipo de entidad es obligatorio para la validación");
        }
        
        String normalizedType = entityType.trim().toLowerCase();
        if (!VALID_ENTITY_TYPES.contains(normalizedType)) {
            throw new InvalidExamDataException(
                "Tipo de entidad inválido. Valores permitidos: " + 
                String.join(", ", VALID_ENTITY_TYPES)
            );
        }
    }

    public static void validateExamTypeForEntity(String examType, String entityType) throws InvalidExamDataException {
        validateExamType(examType);
        validateEntityType(entityType);
        
        String normalizedExamType = examType.trim().toLowerCase();
        String normalizedEntityType = entityType.trim().toLowerCase();
        
        // Business rule validation
        if (CLINIC_EXAM_TYPE.equals(normalizedExamType) && !"clinica".equals(normalizedEntityType)) {
            throw new InvalidExamDataException("Los exámenes médicos solo pueden realizarse en clínicas");
        }
        
        if (DRIVING_SCHOOL_EXAM_TYPES.contains(normalizedExamType) && !"autoescuela".equals(normalizedEntityType)) {
            throw new InvalidExamDataException("Los exámenes teóricos y prácticos solo pueden realizarse en autoescuelas");
        }
    }

    public static void validateDateRange(String startDate, String endDate) throws InvalidExamDataException {
        validateDate(startDate);
        validateDate(endDate);
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            if (start.isAfter(end)) {
                throw new InvalidExamDataException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
            
            // Check if range is too large (e.g., more than 1 year)
            if (start.plusYears(1).isBefore(end)) {
                throw new InvalidExamDataException("El rango de fechas no puede exceder 1 año");
            }
            
        } catch (DateTimeParseException e) {
            throw new InvalidExamDataException("Formato de fecha inválido en el rango de fechas");
        }
    }

    public static void validateExamRetake(String examType, boolean hasPreviousAttempts, boolean hasPassed) 
            throws InvalidExamDataException {
        
        if (hasPassed) {
            throw new InvalidExamDataException("No se puede repetir un examen que ya ha sido aprobado");
        }
        
        // Optional: Limit number of attempts per exam type
        // This could be expanded based on business rules
        // For example: maximum 3 attempts per exam type
    }
}