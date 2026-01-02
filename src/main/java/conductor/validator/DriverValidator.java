package conductor.validator;

import conductor.exception.InvalidConductorDataException;
import conductor.model.Conductor;

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

    public static void validate(Conductor conductor) throws InvalidConductorDataException {
        if (conductor == null) {
            throw new InvalidConductorDataException("El conductor no puede ser nulo");
        }

        validateFirstName(conductor.getFirstName());
        validateLastName(conductor.getLastName());
        validateIdDocument(conductor.getIdDocument());
        validateBirthDate(conductor.getBirthDate());
        validateAddress(conductor.getAddress());
        validatePhone(conductor.getPhone());
        validateEmail(conductor.getEmail());
        validateLicenseStatus(conductor.getLicenseStatus());
    }

    private static void validateFirstName(String firstName) throws InvalidConductorDataException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidConductorDataException("El nombre es obligatorio");
        }
        
        if (firstName.length() > 100) {
            throw new InvalidConductorDataException("El nombre no puede exceder 100 caracteres");
        }
        
        if (!firstName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new InvalidConductorDataException("El nombre solo puede contener letras y espacios");
        }
    }

    private static void validateLastName(String lastName) throws InvalidConductorDataException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidConductorDataException("Los apellidos son obligatorios");
        }
        
        if (lastName.length() > 150) {
            throw new InvalidConductorDataException("Los apellidos no pueden exceder 150 caracteres");
        }
        
        if (!lastName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new InvalidConductorDataException("Los apellidos solo pueden contener letras y espacios");
        }
    }

    private static void validateIdDocument(String idDocument) throws InvalidConductorDataException {
        if (idDocument == null || idDocument.trim().isEmpty()) {
            throw new InvalidConductorDataException("El documento de identidad es obligatorio");
        }
        
        if (!ID_DOCUMENT_PATTERN.matcher(idDocument.trim()).matches()) {
            throw new InvalidConductorDataException("El documento de identidad debe tener 11 dígitos numéricos");
        }
    }

    private static void validateBirthDate(String birthDateStr) throws InvalidConductorDataException {
        if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
            throw new InvalidConductorDataException("La fecha de nacimiento es obligatoria");
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            LocalDate now = LocalDate.now();
            
            // Check if date is in the future
            if (birthDate.isAfter(now)) {
                throw new InvalidConductorDataException("La fecha de nacimiento no puede ser futura");
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
                throw new InvalidConductorDataException("El conductor debe tener al menos 18 años");
            }
            
            // Validate maximum reasonable age
            if (age > 100) {
                throw new InvalidConductorDataException("La edad parece incorrecta. Por favor verifique la fecha de nacimiento");
            }
            
        } catch (DateTimeParseException e) {
            throw new InvalidConductorDataException("Formato de fecha inválido. Use formato YYYY-MM-DD");
        }
    }

    private static void validateAddress(String address) throws InvalidConductorDataException {
        if (address != null && address.length() > 200) {
            throw new InvalidConductorDataException("La dirección no puede exceder 200 caracteres");
        }
    }

    private static void validatePhone(String phone) throws InvalidConductorDataException {
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.length() > 50) {
                throw new InvalidConductorDataException("El teléfono no puede exceder 50 caracteres");
            }
            
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new InvalidConductorDataException("Formato de teléfono inválido. Use números, espacios, guiones o el signo +");
            }
        }
    }

    private static void validateEmail(String email) throws InvalidConductorDataException {
        if (email != null && !email.trim().isEmpty()) {
            if (email.length() > 150) {
                throw new InvalidConductorDataException("El email no puede exceder 150 caracteres");
            }
            
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                throw new InvalidConductorDataException("Formato de email inválido");
            }
        }
    }

    private static void validateLicenseStatus(String licenseStatus) throws InvalidConductorDataException {
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
            throw new InvalidConductorDataException(
                "Estado de licencia inválido. Valores permitidos: " + 
                String.join(", ", VALID_LICENSE_STATUS)
            );
        }
    }
}