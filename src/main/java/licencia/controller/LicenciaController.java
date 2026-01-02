package licencia.controller;

import centro.exception.InvalidCentroDataException;
import centro.utils.Validates;
import conductor.exception.ConductorNotFoundException;
import licencia.dtos.LicenciaRequestDto;
import licencia.dtos.LicenciaUpdateRequestDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.model.Licencia;
import licencia.service.LicenciaService;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LicenciaController {

    private final LicenciaService licenciaService;

    public LicenciaController(LicenciaService licenciaService) {
        this.licenciaService = licenciaService;
    }

    public Licencia emitirLicencia(LicenciaRequestDto licenciaRequestDto) throws SQLException, InvalidLicenciaDataException, ConductorNotFoundException, InvalidCentroDataException {
        Validates.validateObject(licenciaRequestDto,"licenciaRequestDto no puede ser nulo");
        return this.licenciaService.guardar(licenciaRequestDto);
    }

    public Licencia actualizarLicencia(Long idLicenciaParaActualizar, LicenciaUpdateRequestDto licenciaUpdateRequestDto) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException, InvalidCentroDataException, ConductorNotFoundException {
        Validates.validate(idLicenciaParaActualizar,"El id de la licencia a actualizar es requerido");
        Validates.validateObject(licenciaUpdateRequestDto,"El licenciaUpdateRequestDto no puede ser nulo");
        return this.licenciaService.actualizar(idLicenciaParaActualizar,licenciaUpdateRequestDto);
    }

    public List<Licencia> buscarTodas() throws SQLException, InvalidLicenciaDataException {
        return this.licenciaService.listarLicencias();
    }

    public List<Licencia> buscarPorRangoFecha(Date fechaInicial, Date fechaFinal) throws SQLException, InvalidLicenciaDataException {
        return this.licenciaService.licenciasEmitidasPorRangoFecha(fechaInicial, fechaFinal);
    }
}
