package conductor.controller;

import conductor.dto.DriverResponseDto;
import conductor.exception.DriverNotFoundException;
import conductor.exception.InvalidDriverDataException;
import conductor.mapper.DriverMapper;
import conductor.model.Driver;
import conductor.service.DriverServiceInterface;
import conductor.validator.DriverValidator;

import java.sql.SQLException;
import java.util.List;

public class DriverController {
    private final DriverServiceInterface driverService;

    public DriverController(DriverServiceInterface driverService) {
        this.driverService = driverService;
    }

    public Driver addDriver(Driver driver) throws InvalidDriverDataException, SQLException {
        validateDriverNotNull(driver);
        DriverValidator.validate(driver);
        return this.driverService.save(driver);
    }

    public List<DriverResponseDto> getAllDrivers() throws InvalidDriverDataException, SQLException {
        return this.driverService.listAllDrivers().stream()
                .map(DriverMapper::toDriverResponseDto)
                .toList();
    }

    public DriverResponseDto getDriverResponseById(Long id) throws InvalidDriverDataException, SQLException, DriverNotFoundException {
        validateIdNotNull(id);
        return DriverMapper.toDriverResponseDto(this.driverService.getById(id));
    }

    public DriverResponseDto getDriverResponseByIdDocument(String idDocument) throws InvalidDriverDataException, SQLException, DriverNotFoundException {
        validateTextNotNull(idDocument, "Identity document");
        return DriverMapper.toDriverResponseDto(this.driverService.getByIdDocument(idDocument));
    }

    public Driver updateDriver(Driver driver) throws InvalidDriverDataException, SQLException, DriverNotFoundException {
        validateDriverNotNull(driver);
        validateIdNotNull(driver.getId());
        DriverValidator.validate(driver);
        return this.driverService.update(driver);
    }

    public void deleteDriver(Long id) throws InvalidDriverDataException, SQLException, DriverNotFoundException {
        driverService.delete(id);
    }

    public boolean checkDriverExistsById(Long id) throws SQLException, DriverNotFoundException {
        return this.driverService.existsById(id);
    }

    public boolean checkDriverExistsByIdDocument(String idDocument) throws SQLException {
        return this.driverService.existsByIdDocument(idDocument);
    }

    private void validateDriverNotNull(Driver driver) throws InvalidDriverDataException {
        if (driver == null) {
            throw new InvalidDriverDataException("Driver cannot be null");
        }
    }

    private void validateIdNotNull(Long id) throws InvalidDriverDataException {
        if (id == null) {
            throw new InvalidDriverDataException("ID cannot be null");
        }
    }

    private void validateTextNotNull(String text, String fieldName) throws InvalidDriverDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidDriverDataException(fieldName + " cannot be null or empty");
        }
    }
}