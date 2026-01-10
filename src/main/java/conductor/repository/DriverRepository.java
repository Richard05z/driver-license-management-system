package conductor.repository;

import conductor.exception.DriverNotFoundException;
import conductor.exception.InvalidDriverDataException;
import conductor.model.Driver;
import conductor.persistence.DriverDao;

import java.sql.SQLException;
import java.util.List;

public class DriverRepository implements DriverRepositoryInterface {

    private final DriverDao driverDao;

    public DriverRepository(DriverDao driverDao) throws SQLException {
        this.driverDao = driverDao;
    }

    @Override
    public List<Driver> listAllDrivers() throws InvalidDriverDataException, SQLException {
        return this.driverDao.listAllDrivers();
    }

    @Override
    public Driver getById(Long id) throws SQLException, DriverNotFoundException {
        return this.driverDao.getById(id);
    }

    @Override
    public Driver getByIdDocument(String idDocument) throws SQLException, DriverNotFoundException {
        return this.driverDao.getByIdDocument(idDocument);
    }

    @Override
    public Driver save(Driver driver) throws InvalidDriverDataException, SQLException {
        return this.driverDao.save(driver);
    }

    @Override
    public void delete(Long id) throws SQLException, DriverNotFoundException {
        this.driverDao.delete(id);
    }

    @Override
    public Driver update(Driver driver) throws DriverNotFoundException, SQLException {
        return this.driverDao.update(driver);
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