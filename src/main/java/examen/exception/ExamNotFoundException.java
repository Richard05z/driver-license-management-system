package examen.exception;

public class ExamNotFoundException extends Exception {
    public ExamNotFoundException(String message) {
        super(message);
    }
    
    public ExamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}