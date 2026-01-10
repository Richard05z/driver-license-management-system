package licencia.exception;

public class InvalidLicenseDataException extends Exception {
    public InvalidLicenseDataException(String message) {
        super(message);
    }
    
    public InvalidLicenseDataException(String message, Throwable cause) {
        super(message, cause);
    }
}