package conductor.service;

import conductor.exception.ConductorNotFoundException;
import conductor.exception.InvalidConductorDataException;
import conductor.model.Conductor;

import java.sql.SQLException;
import java.util.List;

public interface ConductorServiceInterface {

    List<Conductor> listAllDrivers() throws InvalidConductorDataException, SQLException;

    Conductor getById(Long id) throws SQLException, ConductorNotFoundException;

    Conductor getByIdDocument(String idDocument) throws SQLException, ConductorNotFoundException;

    Conductor save(Conductor conductor) throws InvalidConductorDataException, SQLException;

    void delete(Long id) throws SQLException, ConductorNotFoundException;

    Conductor update(Conductor conductor) throws ConductorNotFoundException, SQLException;

    boolean existsById(Long id) throws SQLException, ConductorNotFoundException;

    boolean existsByIdDocument(String idDocument) throws SQLException;

}