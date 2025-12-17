package conductor.validator;

import conductor.exception.InvalidDriverDataException;
import conductor.model.Driver;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class DriverValidator {

    // Regular expressions for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s-]{7,15}$");
    private static final Pattern ID_DOCUMENT_PATTERN = Pattern.compile("^[0-9]{11}$"); // Assuming 11-digit ID document
    
    // License status values from enum
    private static final String[] VALID_LICENSE_STATUS = {"vigente", "vencida", "suspendida", "revocada"};

    public static void validate(Driver driver) throws InvalidDriverDataException {
        if (driver == null) {
            throw new InvalidDriverDataException("El conductor no puede ser nulo");
        }

        validateFirstName(driver.getFirstName());
        validateLastName(driver.getLastName());
        validateIdDocument(driver.getIdDocument());
        validateBirthDate(driver.getBirthDate());
        validateAddress(driver.getAddress());
        validatePhone(driver.getPhone());
        validateEmail(driver.getEmail());
        validateLicenseStatus(driver.getLicenseStatus());
    }

    private static void validateFirstName(String firstName) throws InvalidDriverDataException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidDriverDataException("El nombre es obligatorio");
        }
        
        if (firstName.length() > 100) {
            throw new InvalidDriverDataException("El nombre no puede exceder 100 caracteres");
        }
        
        if (!firstName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new InvalidDriverDataException("El nombre solo puede contener letras y espacios");
        }
    }

    private static void validateLastName(String lastName) throws InvalidDriverDataException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidDriverDataException("Los apellidos son obligatorios");
        }
        
        if (lastName.length() > 150) {
            throw new InvalidDriverDataException("Los apellidos no pueden exceder 150 caracteres");
        }
        
        if (!lastName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new InvalidDriverDataException("Los apellidos solo pueden contener letras y espacios");
        }
    }

    private static void validateIdDocument(String idDocument) throws InvalidDriverDataException {
        if (idDocument == null || idDocument.trim().isEmpty()) {
            throw new InvalidDriverDataException("El documento de identidad es obligatorio");
        }
        
        if (!ID_DOCUMENT_PATTERN.matcher(idDocument.trim()).matches()) {
            throw new InvalidDriverDataException("El documento de identidad debe tener 11 dígitos numéricos");
        }
    }

    private static void validateBirthDate(String birthDateStr) throws InvalidDriverDataException {
        if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
            throw new InvalidDriverDataException("La fecha de nacimiento es obligatoria");
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            LocalDate now = LocalDate.now();
            
            // Check if date is in the future
            if (birthDate.isAfter(now)) {
                throw new InvalidDriverDataException("La fecha de nacimiento no puede ser futura");
            }
            
            // Calculate age
            int age = now.getYear() - birthDate.getYear();
            
            // Adjust age if birthday hasn't occurred yet this year
            if (now.getMonthValue() < birthDate.getMonthValue() || 
                (now.getMonthValue() == birthDate.getMonthValue() && now.getDayOfMonth() < birthDate.getDayOfMonth())) {
                age--;
            }
            
            // Validate minimum age for driver's license (usually 18)
            if (age < 18) {
                throw new InvalidDriverDataException("El conductor debe tener al menos 18 años");
            }
            
            // Validate maximum reasonable age
            if (age > 100) {
                throw new InvalidDriverDataException("La edad parece incorrecta. Por favor verifique la fecha de nacimiento");
            }
            
        } catch (DateTimeParseException e) {
            throw new InvalidDriverDataException("Formato de fecha inválido. Use formato YYYY-MM-DD");
        }
    }

    private static void validateAddress(String address) throws InvalidDriverDataException {
        if (address != null && address.length() > 200) {
            throw new InvalidDriverDataException("La dirección no puede exceder 200 caracteres");
        }
    }

    private static void validatePhone(String phone) throws InvalidDriverDataException {
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.length() > 50) {
                throw new InvalidDriverDataException("El teléfono no puede exceder 50 caracteres");
            }
            
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new InvalidDriverDataException("Formato de teléfono inválido. Use números, espacios, guiones o el signo +");
            }
        }
    }

    private static void validateEmail(String email) throws InvalidDriverDataException {
        if (email != null && !email.trim().isEmpty()) {
            if (email.length() > 150) {
                throw new InvalidDriverDataException("El email no puede exceder 150 caracteres");
            }
            
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                throw new InvalidDriverDataException("Formato de email inválido");
            }
        }
    }

    private static void validateLicenseStatus(String licenseStatus) throws InvalidDriverDataException {
        if (licenseStatus == null || licenseStatus.trim().isEmpty()) {
            return; // License status is nullable in the database
        }
        
        boolean isValid = false;
        for (String status : VALID_LICENSE_STATUS) {
            if (status.equalsIgnoreCase(licenseStatus.trim())) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new InvalidDriverDataException(
                "Estado de licencia inválido. Valores permitidos: " + 
                String.join(", ", VALID_LICENSE_STATUS)
            );
        }
    }
}