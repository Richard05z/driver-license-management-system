package licencia.validator;

import licencia.exception.InvalidLicenseDataException;
import licencia.model.License;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class LicenseValidator {

    // Valid license types from enum
    private static final List<String> VALID_LICENSE_TYPES = Arrays.asList("A", "B", "C", "D", "E", "F");
    
    // Valid categories from enum
    private static final List<String> VALID_CATEGORIES = Arrays.asList("camion", "moto", "automovil", "autobus");
    
    // Business rules
    private static final int MIN_POINTS = 0;
    private static final int MAX_POINTS = 20;
    private static final int MINIMUM_AGE_FOR_LICENSE = 18;
    private static final int LICENSE_VALIDITY_YEARS = 10; // Standard license validity period

    public static void validate(License license) throws InvalidLicenseDataException {
        if (license == null) {
            throw new InvalidLicenseDataException("La licencia no puede ser nula");
        }

        validateDriverId(license.getDriverId());
        validateLicenseType(license.getLicenseType());
        validateCategory(license.getCategory());
        validateDates(license.getIssueDate(), license.getExpiryDate());
        validatePoints(license.getPoints());
        validateRestrictions(license.getRestrictions());
    }

    private static void validateDriverId(Long driverId) throws InvalidLicenseDataException {
        if (driverId == null || driverId <= 0) {
            throw new InvalidLicenseDataException("El ID del conductor es obligatorio y debe ser positivo");
        }
    }

    private static void validateLicenseType(String licenseType) throws InvalidLicenseDataException {
        if (licenseType == null || licenseType.trim().isEmpty()) {
            throw new InvalidLicenseDataException("El tipo de licencia es obligatorio");
        }
        
        if (!VALID_LICENSE_TYPES.contains(licenseType.trim().toUpperCase())) {
            throw new InvalidLicenseDataException(
                "Tipo de licencia inválido. Valores permitidos: " + 
                String.join(", ", VALID_LICENSE_TYPES)
            );
        }
    }

    private static void validateCategory(String category) throws InvalidLicenseDataException {
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidLicenseDataException("La categoría es obligatoria");
        }
        
        if (!VALID_CATEGORIES.contains(category.trim().toLowerCase())) {
            throw new InvalidLicenseDataException(
                "Categoría inválida. Valores permitidos: " + 
                String.join(", ", VALID_CATEGORIES)
            );
        }
    }

    private static void validateDates(String issueDateStr, String expiryDateStr) throws InvalidLicenseDataException {
        if (issueDateStr == null || issueDateStr.trim().isEmpty()) {
            throw new InvalidLicenseDataException("La fecha de emisión es obligatoria");
        }
        
        if (expiryDateStr == null || expiryDateStr.trim().isEmpty()) {
            throw new InvalidLicenseDataException("La fecha de vencimiento es obligatoria");
        }

        try {
            LocalDate issueDate = LocalDate.parse(issueDateStr);
            LocalDate expiryDate = LocalDate.parse(expiryDateStr);
            LocalDate today = LocalDate.now();

            // Validate issue date is not in the future
            if (issueDate.isAfter(today)) {
                throw new InvalidLicenseDataException("La fecha de emisión no puede ser futura");
            }

            // Validate expiry date is after issue date
            if (!expiryDate.isAfter(issueDate)) {
                throw new InvalidLicenseDataException("La fecha de vencimiento debe ser posterior a la fecha de emisión");
            }

            // Validate reasonable license validity (max 20 years)
            long yearsBetween = java.time.temporal.ChronoUnit.YEARS.between(issueDate, expiryDate);
            if (yearsBetween > 20) {
                throw new InvalidLicenseDataException("La validez de la licencia no puede exceder 20 años");
            }

            // Validate standard validity period (typically 10 years for new licenses)
            if (yearsBetween > 0 && yearsBetween < 1) {
                throw new InvalidLicenseDataException("La licencia debe tener al menos 1 año de validez");
            }

        } catch (DateTimeParseException e) {
            throw new InvalidLicenseDataException("Formato de fecha inválido. Use formato YYYY-MM-DD");
        }
    }

    private static void validatePoints(Integer points) throws InvalidLicenseDataException {
        if (points == null) {
            throw new InvalidLicenseDataException("Los puntos de la licencia son obligatorios");
        }
        
        if (points < MIN_POINTS || points > MAX_POINTS) {
            throw new InvalidLicenseDataException(
                String.format("Los puntos deben estar entre %d y %d", MIN_POINTS, MAX_POINTS)
            );
        }
    }

    private static void validateRestrictions(String restrictions) throws InvalidLicenseDataException {
        if (restrictions != null && restrictions.length() > 500) {
            throw new InvalidLicenseDataException("Las restricciones no pueden exceder 500 caracteres");
        }
    }

    public static void validateLicenseCompatibility(String licenseType, String category) throws InvalidLicenseDataException {
        // Business rule: Validate that license type and category are compatible
        switch (licenseType.toUpperCase()) {
            case "A":
                if (!"moto".equals(category.toLowerCase())) {
                    throw new InvalidLicenseDataException(
                        "La licencia tipo A solo puede ser para categoría 'moto'"
                    );
                }
                break;
            case "B":
                if (!"automovil".equals(category.toLowerCase())) {
                    throw new InvalidLicenseDataException(
                        "La licencia tipo B solo puede ser para categoría 'automovil'"
                    );
                }
                break;
            case "C":
                if (!"camion".equals(category.toLowerCase())) {
                    throw new InvalidLicenseDataException(
                        "La licencia tipo C solo puede ser para categoría 'camion'"
                    );
                }
                break;
            case "D":
                if (!"autobus".equals(category.toLowerCase())) {
                    throw new InvalidLicenseDataException(
                        "La licencia tipo D solo puede ser para categoría 'autobus'"
                    );
                }
                break;
            // Types E and F might have multiple valid categories
            case "E":
                if (!Arrays.asList("camion", "autobus").contains(category.toLowerCase())) {
                    throw new InvalidLicenseDataException(
                        "La licencia tipo E solo puede ser para categorías 'camion' o 'autobus'"
                    );
                }
                break;
            case "F":
                // Type F might be for special vehicles, accept multiple categories
                if (!Arrays.asList("camion", "automovil").contains(category.toLowerCase())) {
                    throw new InvalidLicenseDataException(
                        "La licencia tipo F solo puede ser para categorías 'camion' o 'automovil'"
                    );
                }
                break;
        }
    }

    public static void validateForRenewal(License license) throws InvalidLicenseDataException {
        if (license == null) {
            throw new InvalidLicenseDataException("La licencia no puede ser nula para renovación");
        }

        try {
            LocalDate expiryDate = LocalDate.parse(license.getExpiryDate());
            LocalDate today = LocalDate.now();

            // Check if license is already expired for too long (more than 1 year)
            if (expiryDate.isBefore(today.minusYears(1))) {
                throw new InvalidLicenseDataException(
                    "La licencia está vencida por más de 1 año. No se puede renovar, debe solicitar una nueva"
                );
            }

            // Check if license is already renewed
            if (Boolean.TRUE.equals(license.getRenewed())) {
                throw new InvalidLicenseDataException("La licencia ya ha sido renovada anteriormente");
            }

            // Check points for renewal (should have at least 10 points to renew)
            if (license.getPoints() < 10) {
                throw new InvalidLicenseDataException(
                    "No se puede renovar la licencia con menos de 10 puntos. Puntos actuales: " + license.getPoints()
                );
            }

        } catch (DateTimeParseException e) {
            throw new InvalidLicenseDataException("Formato de fecha inválido en la licencia");
        }
    }

    public static void validateForPointsDeduction(License license, int pointsToDeduct) throws InvalidLicenseDataException {
        if (license == null) {
            throw new InvalidLicenseDataException("La licencia no puede ser nula");
        }

        if (pointsToDeduct <= 0) {
            throw new InvalidLicenseDataException("Los puntos a deducir deben ser positivos");
        }

        int currentPoints = license.getPoints();
        
        if (pointsToDeduct > currentPoints) {
            throw new InvalidLicenseDataException(
                String.format(
                    "No se pueden deducir %d puntos. La licencia solo tiene %d puntos disponibles",
                    pointsToDeduct, currentPoints
                )
            );
        }

        // Check if license would be suspended (less than 5 points)
        if (currentPoints - pointsToDeduct < 5) {
            throw new InvalidLicenseDataException(
                "La deducción de puntos dejaría la licencia con menos de 5 puntos, lo que resulta en suspensión"
            );
        }
    }
}