package conductor.controller;

import conductor.dto.DriverResponseDto;
import conductor.exception.ConductorNotFoundException;
import conductor.exception.InvalidConductorDataException;
import conductor.mapper.DriverMapper;
import conductor.model.Conductor;
import conductor.service.ConductorServiceInterface;
import conductor.validator.DriverValidator;

import java.sql.SQLException;
import java.util.List;

public class DriverController {
    private final ConductorServiceInterface driverService;

    public DriverController(ConductorServiceInterface driverService) {
        this.driverService = driverService;
    }

    public Conductor addDriver(Conductor conductor) throws InvalidConductorDataException, SQLException {
        validateDriverNotNull(conductor);
        DriverValidator.validate(conductor);
        return this.driverService.save(conductor);
    }

    public List<DriverResponseDto> getAllDrivers() throws InvalidConductorDataException, SQLException {
        return this.driverService.listAllDrivers().stream()
                .map(DriverMapper::toDriverResponseDto)
                .toList();
    }

    public DriverResponseDto getDriverResponseById(Long id) throws InvalidConductorDataException, SQLException, ConductorNotFoundException {
        validateIdNotNull(id);
        return DriverMapper.toDriverResponseDto(this.driverService.getById(id));
    }

    public DriverResponseDto getDriverByIdDocument(String idDocument) throws InvalidConductorDataException, SQLException, ConductorNotFoundException {
        validateTextNotNull(idDocument, "Identity document");
        return DriverMapper.toDriverResponseDto(this.driverService.getByIdDocument(idDocument));
    }

    public Conductor updateDriver(Conductor conductor) throws InvalidConductorDataException, SQLException, ConductorNotFoundException {
        validateDriverNotNull(conductor);
        validateIdNotNull(conductor.getId());
        return this.driverService.update(conductor);
    }

    public void deleteDriver(Long id) throws InvalidConductorDataException, SQLException, ConductorNotFoundException {
        driverService.delete(id);
    }

    public boolean checkDriverExistsById(Long id) throws SQLException, ConductorNotFoundException {
        return this.driverService.existsById(id);
    }

    public boolean checkDriverExistsByIdDocument(String idDocument) throws SQLException {
        return this.driverService.existsByIdDocument(idDocument);
    }

    private void validateDriverNotNull(Conductor conductor) throws InvalidConductorDataException {
        if (conductor == null) {
            throw new InvalidConductorDataException("Driver cannot be null");
        }
    }

    private void validateIdNotNull(Long id) throws InvalidConductorDataException {
        if (id == null) {
            throw new InvalidConductorDataException("ID cannot be null");
        }
    }

    private void validateTextNotNull(String text, String fieldName) throws InvalidConductorDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidConductorDataException(fieldName + " cannot be null or empty");
        }
    }
}