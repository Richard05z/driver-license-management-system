package conductor.service;

import conductor.exception.ConductorNotFoundException;
import conductor.exception.InvalidConductorDataException;
import conductor.model.Conductor;
import conductor.repository.ConductorRepositoryInterface;
import conductor.validator.DriverValidator;

import java.sql.SQLException;
import java.util.List;

public class ConductorService implements ConductorServiceInterface {

    private final ConductorRepositoryInterface driverRepository;

    public ConductorService(ConductorRepositoryInterface driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Conductor> listAllDrivers() throws InvalidConductorDataException, SQLException {
        return this.driverRepository.listAllDrivers();
    }

    @Override
    public Conductor getById(Long id) throws SQLException, ConductorNotFoundException {
        this.existsById(id);
        return this.driverRepository.getById(id);
    }

    @Override
    public Conductor getByIdDocument(String idDocument) throws SQLException, ConductorNotFoundException {
        return this.driverRepository.getByIdDocument(idDocument);
    }

    @Override
    public Conductor save(Conductor conductor) throws InvalidConductorDataException, SQLException {
        DriverValidator.validate(conductor);
        return this.driverRepository.save(conductor);
    }

    @Override
    public void delete(Long id) throws SQLException, ConductorNotFoundException {
        this.existsById(id);
        this.driverRepository.delete(id);
    }

    @Override
    public Conductor update(Conductor conductor) throws ConductorNotFoundException, SQLException {
        this.existsById(conductor.getId());
        return this.driverRepository.update(conductor);
    }

    @Override
    public boolean existsById(Long id) throws SQLException, ConductorNotFoundException {
        if (!this.driverRepository.existsById(id)) {
            throw new ConductorNotFoundException("El conductor con ID " + id + " no fue encontrado");
        }
        return true;
    }

    @Override
    public boolean existsByIdDocument(String idDocument) throws SQLException {
        return this.driverRepository.existsByIdDocument(idDocument);
    }
}