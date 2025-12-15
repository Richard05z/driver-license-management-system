package centro.utils;

import centro.exception.InvalidCentroDataException;

public class Validates {
    public static <T extends Number> void validate( T value, String message ) throws InvalidCentroDataException {
        if(value==null){
            throw new InvalidCentroDataException(message);
        }
    }
    public static <T> void validateObject( T obj, String message ) throws InvalidCentroDataException {
        if(obj==null){
            throw new InvalidCentroDataException(message);
        }
    }

    public static void validateText( String text, String message ) throws InvalidCentroDataException {
        if(text==null || text.isEmpty()){
            throw new InvalidCentroDataException(message);
        }
    }
}
