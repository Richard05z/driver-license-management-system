package licencia.service;

import entidad.exception.InvalidEntidadDataException;
import licencia.dtos.LicenciaRequestDto;
import licencia.exception.InvalidLicenciaDataException;

import java.util.Date;

public class LicenciaValidator {
    public static void validate(LicenciaRequestDto licenciaRequestDto) throws InvalidLicenciaDataException {
        if (licenciaRequestDto.tipoLicencia().toString().isBlank()) {
            throw new InvalidLicenciaDataException("El tipoLicencia es requerido");
        }
        if (licenciaRequestDto.categoriaLicencia().toString().isBlank()) {
            throw new InvalidLicenciaDataException("La categoriaLicencia es requerida");
        }
        if (licenciaRequestDto.fechaEmision().getTime() < new Date().getTime()) {
            throw new InvalidLicenciaDataException("La fecha de emision no puede ser anterior a la fecha actual");
        }
        if (licenciaRequestDto.fechaVencimiento().getTime() < licenciaRequestDto.fechaEmision().getTime()) {
            throw new InvalidLicenciaDataException("La fecha de vencimiento no puede ser anterior a la fecha de emision");
        }
        if (licenciaRequestDto.restricciones().isBlank()) {
            throw new InvalidLicenciaDataException("Las restricciones son requeridas");
        }
        if (licenciaRequestDto.idConductor() == null) {
            throw new InvalidLicenciaDataException("El idConductor es requerido");
        }
        if (!licenciaRequestDto.renovada()) {
            throw new InvalidLicenciaDataException("El campo renovada es requerido");
        }
        if (licenciaRequestDto.puntos() < 0 ) {
            throw new InvalidLicenciaDataException("Los puntos deben ser un numero positivo");
        }
    }
}
