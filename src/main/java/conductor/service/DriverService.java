package conductor.service;

import conductor.exception.DriverNotFoundException;
import conductor.exception.InvalidDriverDataException;
import conductor.model.Driver;
import conductor.repository.DriverRepositoryInterface;
import conductor.validator.DriverValidator;

import java.sql.SQLException;
import java.util.List;

public class DriverService implements DriverServiceInterface {

    private final DriverRepositoryInterface driverRepository;

    public DriverService(DriverRepositoryInterface driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Driver> listAllDrivers() throws InvalidDriverDataException, SQLException {
        return this.driverRepository.listAllDrivers();
    }

    @Override
    public Driver getById(Integer id) throws SQLException, DriverNotFoundException {
        return this.driverRepository.getById(id);
    }

    @Override
    public Driver getByIdDocument(String idDocument) throws SQLException, DriverNotFoundException {
        return this.driverRepository.getByIdDocument(idDocument);
    }

    @Override
    public Driver save(Driver driver) throws InvalidDriverDataException, SQLException {
        DriverValidator.validate(driver);
        return this.driverRepository.save(driver);
    }

    @Override
    public void delete(Integer id) throws SQLException, DriverNotFoundException {
        this.existsById(id);
        this.driverRepository.delete(id);
    }

    @Override
    public Driver update(Driver driver) throws DriverNotFoundException, SQLException {
        this.existsById(driver.getId());
        return this.driverRepository.update(driver);
    }

    @Override
    public boolean existsById(Integer id) throws SQLException, DriverNotFoundException {
        if (!this.driverRepository.existsById(id)) {
            throw new DriverNotFoundException("El conductor con ID " + id + " no fue encontrado");
        }
        return true;
    }

    @Override
    public boolean existsByIdDocument(String idDocument) throws SQLException {
        return this.driverRepository.existsByIdDocument(idDocument);
    }
}