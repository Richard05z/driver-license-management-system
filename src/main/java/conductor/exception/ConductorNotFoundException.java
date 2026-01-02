package conductor.exception;

public class ConductorNotFoundException extends Exception {
    public ConductorNotFoundException(String message) {
        super(message);
    }
    
    public ConductorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}