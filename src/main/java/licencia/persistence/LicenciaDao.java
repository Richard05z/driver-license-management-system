package licencia.persistence;

import licencia.dtos.LicenciaDaoResponseDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.model.Licencia;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LicenciaDao {

    Licencia guardar(Licencia licencia) throws InvalidLicenciaDataException, SQLException;

    Licencia actualizar(Licencia licencia) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException;

    LicenciaDaoResponseDto buscarPorId(Long id) throws SQLException, LicenciaNotFoundException;

    List<LicenciaDaoResponseDto> listarLicencias() throws InvalidLicenciaDataException, SQLException;

    List<LicenciaDaoResponseDto> licenciasEmitidasPorRangoFecha(Date fechaInicial, Date fechaFinal) throws
            InvalidLicenciaDataException, SQLException;

    boolean existePorId(Long id) throws SQLException;

}
