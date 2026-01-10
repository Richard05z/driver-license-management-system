package examen.exception;

public class InvalidExamDataException extends Exception {
    public InvalidExamDataException(String message) {
        super(message);
    }
    
    public InvalidExamDataException(String message, Throwable cause) {
        super(message, cause);
    }
}