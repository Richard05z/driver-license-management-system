package licencia.service;

import conductor.exception.ConductorNotFoundException;
import conductor.model.Conductor;
import licencia.dtos.LicenciaRequestDto;
import licencia.dtos.LicenciaUpdateRequestDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.model.Licencia;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface LicenciaService {

    Licencia guardar(LicenciaRequestDto licencia) throws InvalidLicenciaDataException, SQLException, ConductorNotFoundException;

    Licencia actualizar(Long id, LicenciaUpdateRequestDto licenciaToUpdate) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException, ConductorNotFoundException;

    List<Licencia> listarLicencias() throws InvalidLicenciaDataException, SQLException;

    List<Licencia> licenciasEmitidasPorRangoFecha(Date fechaInicial, Date fechaFinal) throws
            InvalidLicenciaDataException, SQLException;

    public Licencia buscarPorId(Long id) throws SQLException, LicenciaNotFoundException, ConductorNotFoundException;

    boolean existePorId(Long id) throws SQLException, LicenciaNotFoundException;
}
