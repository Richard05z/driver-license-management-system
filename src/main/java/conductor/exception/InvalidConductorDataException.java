package conductor.exception;

public class InvalidConductorDataException extends Exception {
    public InvalidConductorDataException(String message) {
        super(message);
    }
    
    public InvalidConductorDataException(String message, Throwable cause) {
        super(message, cause);
    }
}