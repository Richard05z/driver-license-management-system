package licencia.repository;

import licencia.dtos.LicenciaDaoResponseDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.model.Licencia;
import licencia.persistence.LicenciaDao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LicenciaRepositoryImpl implements LicenciaRepository {

    private final LicenciaDao dao;

    public LicenciaRepositoryImpl(LicenciaDao dao) {
        this.dao = dao;
    }

    @Override
    public Licencia guardar(Licencia licencia) throws InvalidLicenciaDataException, SQLException {
        return this.dao.guardar(licencia);
    }

    @Override
    public Licencia actualizar(Licencia licencia) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException {
        return this.dao.actualizar(licencia);
    }

    @Override
    public List<LicenciaDaoResponseDto> listarLicencias() throws InvalidLicenciaDataException, SQLException {
        return this.dao.listarLicencias();
    }

    @Override
    public List<LicenciaDaoResponseDto> licenciasEmitidasPorRangoFecha(Date fechaInicial, Date fechaFinal) throws InvalidLicenciaDataException, SQLException {
        return this.dao.licenciasEmitidasPorRangoFecha(fechaInicial,fechaFinal);
    }

    @Override
    public LicenciaDaoResponseDto buscarPorId(Long id) throws SQLException, LicenciaNotFoundException {
        return this.dao.buscarPorId(id);
    }

    @Override
    public boolean existePorId(Long id) throws SQLException {
        return this.dao.existePorId(id);
    }
}
