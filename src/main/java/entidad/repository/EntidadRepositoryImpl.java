package entidad.repository;

import entidad.dto.EntidadDaoResponseDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.model.Entidad;
import entidad.persistence.EntidadDao;

import java.sql.SQLException;
import java.util.List;

public class EntidadRepositoryImpl implements EntidadRepository {

    private final EntidadDao entidadDao;

    public EntidadRepositoryImpl(EntidadDao entidadDao) {
        this.entidadDao = entidadDao;
    }

    @Override
    public List<EntidadDaoResponseDto> listarEntidades() throws InvalidEntidadDataException, SQLException {
        return this.entidadDao.listarEntidades();
    }

    @Override
    public List<EntidadDaoResponseDto> buscarEntidadesPorCentroId(Long idCentro) throws SQLException, EntidadNotFoundException, InvalidEntidadDataException {
        return this.entidadDao.buscarEntidadesPorCentroId(idCentro);
    }

    @Override
    public Entidad guardar(Entidad entidad) throws InvalidEntidadDataException, SQLException {
        return this.entidadDao.guardar(entidad);
    }

    @Override
    public void eliminar(Long id) throws SQLException, EntidadNotFoundException {
        this.entidadDao.eliminar(id);
    }

    @Override
    public boolean existePorId(Long id) throws SQLException {
        return this.entidadDao.existePorId(id);
    }
}
