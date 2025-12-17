package conductor.exception;

public class DriverNotFoundException extends Exception {
    public DriverNotFoundException(String message) {
        super(message);
    }
    
    public DriverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}