package entidad.service;

import entidad.dto.EntidadRequestDto;
import entidad.exception.InvalidEntidadDataException;

public class EntidadValidator {
    public static void validate(EntidadRequestDto entidadRequestDto) throws InvalidEntidadDataException{
        if (entidadRequestDto.nombre().isBlank()){
            throw new InvalidEntidadDataException("El nombre es requerido");
        }
        if (entidadRequestDto.tipoEntidad().toString().isBlank()){
            throw new InvalidEntidadDataException("El tipoEntidad es requerido");
        }
        if (entidadRequestDto.direccion().isBlank()){
            throw new InvalidEntidadDataException("La direccion es requerida");
        }
        if (entidadRequestDto.telefono().isBlank()){
            throw new InvalidEntidadDataException("El telefono es requerido");
        }
        if (entidadRequestDto.email().isBlank()){
            throw new InvalidEntidadDataException("El email es requerido");
        }
        if (entidadRequestDto.directorGeneral().isBlank()){
            throw new InvalidEntidadDataException("El director general es requerido");
        }
        if (entidadRequestDto.idCentro() == null){
            throw new InvalidEntidadDataException("El jefe id del centro es requerido");
        }
    }
}
