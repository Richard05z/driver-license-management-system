package conductor.persistence;

import conductor.exception.DriverNotFoundException;
import conductor.exception.InvalidDriverDataException;
import conductor.model.Driver;

import java.sql.SQLException;
import java.util.List;

public interface DriverDaoInterface {
    List<Driver> listAllDrivers() throws InvalidDriverDataException, SQLException;

    Driver getById(Long id) throws SQLException, DriverNotFoundException;

    Driver getByIdDocument(String idDocument) throws SQLException, DriverNotFoundException;

    Driver save(Driver driver) throws InvalidDriverDataException, SQLException;

    void delete(Long id) throws SQLException, DriverNotFoundException;

    Driver update(Driver driver) throws DriverNotFoundException, SQLException;

    boolean existsById(Long id) throws SQLException;

    boolean existsByIdDocument(String idDocument) throws SQLException;
}