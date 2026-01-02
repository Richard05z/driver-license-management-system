package conductor.repository;

import conductor.exception.ConductorNotFoundException;
import conductor.exception.InvalidConductorDataException;
import conductor.model.Conductor;
import conductor.persistence.ConductorDao;

import java.sql.SQLException;
import java.util.List;

public class ConductorRepository implements ConductorRepositoryInterface {

    private final ConductorDao driverDao;

    public ConductorRepository(ConductorDao driverDao) throws SQLException {
        this.driverDao = driverDao;
    }

    @Override
    public List<Conductor> listAllDrivers() throws InvalidConductorDataException, SQLException {
        return this.driverDao.listAllDrivers();
    }

    @Override
    public Conductor getById(Long id) throws SQLException, ConductorNotFoundException {
        return this.driverDao.getById(id);
    }

    @Override
    public Conductor getByIdDocument(String idDocument) throws SQLException, ConductorNotFoundException {
        return this.driverDao.getByIdDocument(idDocument);
    }

    @Override
    public Conductor save(Conductor conductor) throws InvalidConductorDataException, SQLException {
        return this.driverDao.save(conductor);
    }

    @Override
    public void delete(Long id) throws SQLException, ConductorNotFoundException {
        this.driverDao.delete(id);
    }

    @Override
    public Conductor update(Conductor conductor) throws ConductorNotFoundException, SQLException {
        return this.driverDao.update(conductor);
    }

    @Override
    public boolean existsById(Long id) throws SQLException {
        return this.driverDao.existsById(id);
    }

    @Override
    public boolean existsByIdDocument(String idDocument) throws SQLException {
        return this.driverDao.existsByIdDocument(idDocument);
    }
}