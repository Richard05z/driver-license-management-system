package licencia.exception;

public class LicenseNotFoundException extends Exception {
    public LicenseNotFoundException(String message) {
        super(message);
    }
    
    public LicenseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}