package licencia.repository;

import licencia.dtos.LicenciaDaoResponseDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.model.Licencia;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface LicenciaRepository {
    Licencia guardar(Licencia licencia) throws InvalidLicenciaDataException, SQLException;

    Licencia actualizar(Licencia licencia) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException;

    List<LicenciaDaoResponseDto> listarLicencias() throws InvalidLicenciaDataException, SQLException;

    List<LicenciaDaoResponseDto> licenciasEmitidasPorRangoFecha(Date fechaInicial, Date fechaFinal) throws
            InvalidLicenciaDataException, SQLException;

    LicenciaDaoResponseDto buscarPorId(Long id) throws SQLException, LicenciaNotFoundException;

    boolean existePorId(Long id) throws SQLException;
}
