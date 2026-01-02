package licencia.utils;

import licencia.exception.InvalidLicenciaDataException;

public class Validates {
    public static <T extends Number> void validate( T value, String message ) throws InvalidLicenciaDataException {
        if(value==null){
            throw new InvalidLicenciaDataException(message);
        }
    }
    public static <T> void validateObject( T obj, String message ) throws InvalidLicenciaDataException {
        if(obj==null){
            throw new InvalidLicenciaDataException(message);
        }
    }

    public static void validateText( String text, String message ) throws InvalidLicenciaDataException {
        if(text==null || text.isEmpty()){
            throw new InvalidLicenciaDataException(message);
        }
    }
}
