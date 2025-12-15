package centro.repository;

import centro.exception.CentroNotFoundException;
import centro.exception.InvalidCentroDataException;
import centro.model.Centro;
import centro.persistence.CentroDao;
import centro.persistence.CentroDaoImpl;

import java.sql.SQLException;
import java.util.List;

public class CentroRepositoryImpl implements CentroRepository {

    private final CentroDao centroDao;

    public CentroRepositoryImpl(CentroDaoImpl centroDao) throws SQLException {
        this.centroDao = centroDao;
    }

    @Override
    public List<Centro> listarCentros() throws InvalidCentroDataException, SQLException {
        return this.centroDao.listarCentros();
    }

    @Override
    public Centro listarPorCodigo(String codigo) throws SQLException, CentroNotFoundException {
        return this.centroDao.listarPorCodigo(codigo);
    }

    @Override
    public Centro guardar(Centro centro) throws InvalidCentroDataException, SQLException {
        return this.centroDao.guardar(centro);
    }

    @Override
    public void eliminar(Long id) throws SQLException, CentroNotFoundException {
        this.centroDao.eliminar(id);
    }

    @Override
    public Centro actualizar(Centro centro) throws CentroNotFoundException, SQLException {
        return this.centroDao.actualizar(centro);
    }

    @Override
    public boolean existePorId(Long id) throws SQLException {
        return this.centroDao.existePorId(id);
    }

}
