package entidad.utils;

import entidad.exception.InvalidEntidadDataException;

public class Validates {
    public static <T extends Number> void validate( T value, String message ) throws InvalidEntidadDataException {
        if(value==null){
            throw new InvalidEntidadDataException(message);
        }
    }
    public static <T> void validateObject( T obj, String message ) throws InvalidEntidadDataException {
        if(obj==null){
            throw new InvalidEntidadDataException(message);
        }
    }

    public static void validateText( String text, String message ) throws InvalidEntidadDataException {
        if(text==null || text.isEmpty()){
            throw new InvalidEntidadDataException(message);
        }
    }
}
