package conductor.repository;

import conductor.exception.DriverNotFoundException;
import conductor.exception.InvalidDriverDataException;
import conductor.model.Driver;

import java.sql.SQLException;
import java.util.List;

public interface DriverRepositoryInterface {
    List<Driver> listAllDrivers() throws InvalidDriverDataException, SQLException;

    Driver getById(Integer id) throws SQLException, DriverNotFoundException;

    Driver getByIdDocument(String idDocument) throws SQLException, DriverNotFoundException;

    Driver save(Driver driver) throws InvalidDriverDataException, SQLException;

    void delete(Integer id) throws SQLException, DriverNotFoundException;

    Driver update(Driver driver) throws DriverNotFoundException, SQLException;

    boolean existsById(Integer id) throws SQLException;

    boolean existsByIdDocument(String idDocument) throws SQLException;
}