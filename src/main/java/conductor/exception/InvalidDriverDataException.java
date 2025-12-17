package conductor.exception;

public class InvalidDriverDataException extends Exception {
    public InvalidDriverDataException(String message) {
        super(message);
    }
    
    public InvalidDriverDataException(String message, Throwable cause) {
        super(message, cause);
    }
}