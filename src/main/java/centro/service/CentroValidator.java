package centro.service;

import centro.exception.InvalidCentroDataException;
import centro.model.Centro;

public class CentroValidator {
    public static void validate(Centro centro) throws InvalidCentroDataException{
        if (centro.getNombre().isBlank()){
            throw new InvalidCentroDataException("El nombre es requerido");
        }
        if (centro.getCodigo().isBlank()){
            throw new InvalidCentroDataException("El codigo es requerido");
        }
        if (centro.getDireccionPostal().isBlank()){
            throw new InvalidCentroDataException("La direccion es requerida");
        }
        if (centro.getTelefono().isBlank()){
            throw new InvalidCentroDataException("El telefono es requerido");
        }
        if (centro.getEmail().isBlank()){
            throw new InvalidCentroDataException("El email es requerido");
        }
        if (centro.getDirectorGeneral().isBlank()){
            throw new InvalidCentroDataException("El director general es requerido");
        }
        if (centro.getJefeContabilidad().isBlank()){
            throw new InvalidCentroDataException("El jefe contabilidad es requerido");
        }
        if (centro.getJefeRRHH().isBlank()){
            throw new InvalidCentroDataException("El jefe rrhh es requerido");
        }
        if (centro.getSecretarioSindicato().isBlank()){
            throw new InvalidCentroDataException("El secretario es requerido");
        }
        if (centro.getLogo().isBlank()){
            throw new InvalidCentroDataException("El logo es requerido");
        }
    }
}
