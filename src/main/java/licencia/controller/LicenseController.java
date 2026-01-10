package licencia.controller;

import licencia.dto.LicenseResponseDto;
import licencia.exception.LicenseNotFoundException;
import licencia.exception.InvalidLicenseDataException;
import licencia.mapper.LicenseMapper;
import licencia.model.License;
import licencia.service.LicenseServiceInterface;
import licencia.validator.LicenseValidator;

import java.sql.SQLException;
import java.util.List;

public class LicenseController {
    private final LicenseServiceInterface licenseService;

    public LicenseController(LicenseServiceInterface licenseService) {
        this.licenseService = licenseService;
    }

    public License addLicense(License license) throws InvalidLicenseDataException, SQLException {
        validateLicenseNotNull(license);
        LicenseValidator.validate(license);
        return this.licenseService.save(license);
    }

    public List<LicenseResponseDto> getAllLicenses() throws InvalidLicenseDataException, SQLException {
        return this.licenseService.listAllLicenses().stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public LicenseResponseDto getLicenseResponseById(Long id) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(id);
        return LicenseMapper.toLicenseResponseDto(this.licenseService.getById(id));
    }

    public License updateLicense(License license) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateLicenseNotNull(license);
        validateIdNotNull(license.getId());
        return this.licenseService.update(license);
    }

    public void deleteLicense(Long id) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        licenseService.delete(id);
    }

    public boolean checkLicenseExistsById(Long id) throws SQLException, LicenseNotFoundException {
        return this.licenseService.existsById(id);
    }

    // Find operations
    public List<LicenseResponseDto> getLicensesByDriverId(Long driverId) throws InvalidLicenseDataException, SQLException {
        validateIdNotNull(driverId);
        return this.licenseService.findByDriverId(driverId).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public List<LicenseResponseDto> getLicensesByType(String licenseType) throws InvalidLicenseDataException, SQLException {
        validateTextNotNull(licenseType, "License type");
        return this.licenseService.findByLicenseType(licenseType).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public List<LicenseResponseDto> getLicensesByCategory(String category) throws InvalidLicenseDataException, SQLException {
        validateTextNotNull(category, "Category");
        return this.licenseService.findByCategory(category).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public List<LicenseResponseDto> getLicensesByRenewalStatus(Boolean renewed) throws InvalidLicenseDataException, SQLException {
        return this.licenseService.findByRenewalStatus(renewed).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    // Business operations
    public List<LicenseResponseDto> getActiveLicenses() throws InvalidLicenseDataException, SQLException {
        return this.licenseService.findActiveLicenses().stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public List<LicenseResponseDto> getExpiredLicenses() throws InvalidLicenseDataException, SQLException {
        return this.licenseService.findExpiredLicenses().stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public List<LicenseResponseDto> getLicensesExpiringSoon(int daysThreshold) throws InvalidLicenseDataException, SQLException {
        if (daysThreshold <= 0) {
            throw new InvalidLicenseDataException("El umbral de días debe ser mayor a cero");
        }
        return this.licenseService.findLicensesExpiringSoon(daysThreshold).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    // Points operations
    public boolean deductPoints(Long licenseId, int pointsToDeduct) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        if (pointsToDeduct <= 0) {
            throw new InvalidLicenseDataException("Los puntos a deducir deben ser positivos");
        }
        return this.licenseService.deductPoints(licenseId, pointsToDeduct);
    }

    public boolean restorePoints(Long licenseId, int pointsToRestore) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        if (pointsToRestore <= 0) {
            throw new InvalidLicenseDataException("Los puntos a restaurar deben ser positivos");
        }
        return this.licenseService.restorePoints(licenseId, pointsToRestore);
    }

    public boolean resetPoints(Long licenseId) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        return this.licenseService.resetPoints(licenseId);
    }

    // Renewal operations
    public boolean renewLicense(Long licenseId, String newExpiryDate) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        validateTextNotNull(newExpiryDate, "New expiry date");
        return this.licenseService.renewLicense(licenseId, newExpiryDate);
    }

    // Validation operations
    public boolean isLicenseValid(Long licenseId) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        return this.licenseService.isLicenseValid(licenseId);
    }

    public boolean isLicenseExpired(Long licenseId) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        return this.licenseService.isLicenseExpired(licenseId);
    }

    public boolean canLicenseBeRenewed(Long licenseId) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        return this.licenseService.canLicenseBeRenewed(licenseId);
    }

    // Driver-specific operations
    public boolean driverHasValidLicense(Long driverId, String licenseType) throws InvalidLicenseDataException, SQLException {
        validateIdNotNull(driverId);
        validateTextNotNull(licenseType, "License type");
        return this.licenseService.driverHasValidLicense(driverId, licenseType);
    }

    public List<String> getDriverLicenseTypes(Long driverId) throws InvalidLicenseDataException, SQLException {
        validateIdNotNull(driverId);
        return this.licenseService.getDriverLicenseTypes(driverId);
    }

    // Report operations
    public List<LicenseResponseDto> getLicensesIssuedBetween(String startDate, String endDate) throws InvalidLicenseDataException, SQLException {
        validateTextNotNull(startDate, "Start date");
        validateTextNotNull(endDate, "End date");
        return this.licenseService.findLicensesIssuedBetween(startDate, endDate).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public List<LicenseResponseDto> getLicensesExpiringBetween(String startDate, String endDate) throws InvalidLicenseDataException, SQLException {
        validateTextNotNull(startDate, "Start date");
        validateTextNotNull(endDate, "End date");
        return this.licenseService.findLicensesExpiringBetween(startDate, endDate).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    // Advanced business operations
    public License issueNewLicense(Long driverId, String licenseType, String category, int validityYears) 
            throws InvalidLicenseDataException, SQLException {
        validateIdNotNull(driverId);
        validateTextNotNull(licenseType, "License type");
        validateTextNotNull(category, "Category");
        return this.licenseService.issueNewLicense(driverId, licenseType, category, validityYears);
    }

    public boolean suspendLicense(Long licenseId, String reason) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        validateTextNotNull(reason, "Suspension reason");
        return this.licenseService.suspendLicense(licenseId, reason);
    }

    public boolean revokeLicense(Long licenseId, String reason) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        validateTextNotNull(reason, "Revocation reason");
        return this.licenseService.revokeLicense(licenseId, reason);
    }

    public boolean transferLicense(Long licenseId, Long newDriverId) throws InvalidLicenseDataException, SQLException, LicenseNotFoundException {
        validateIdNotNull(licenseId);
        validateIdNotNull(newDriverId);
        return this.licenseService.transferLicense(licenseId, newDriverId);
    }

    public List<LicenseResponseDto> getDriverLicensesHistory(Long driverId) throws InvalidLicenseDataException, SQLException {
        validateIdNotNull(driverId);
        return this.licenseService.getDriverLicensesHistory(driverId).stream()
                .map(LicenseMapper::toLicenseResponseDto)
                .toList();
    }

    public boolean validateLicenseCompatibility(String licenseType, String category) throws InvalidLicenseDataException {
        validateTextNotNull(licenseType, "License type");
        validateTextNotNull(category, "Category");
        return this.licenseService.validateLicenseCompatibility(licenseType, category);
    }

    // Statistics operations
    public int countLicensesByDriver(Long driverId) throws SQLException, InvalidLicenseDataException {
        validateIdNotNull(driverId);
        return this.licenseService.countLicensesByDriver(driverId);
    }

    public int countLicensesByType(String licenseType) throws SQLException, InvalidLicenseDataException {
        validateTextNotNull(licenseType, "License type");
        return this.licenseService.countLicensesByType(licenseType);
    }

    public int countActiveLicenses() throws SQLException {
        return this.licenseService.countActiveLicenses();
    }

    public int countExpiredLicenses() throws SQLException {
        return this.licenseService.countExpiredLicenses();
    }

    // Validation helper methods
    private void validateLicenseNotNull(License license) throws InvalidLicenseDataException {
        if (license == null) {
            throw new InvalidLicenseDataException("La licencia no puede ser nula");
        }
    }

    private void validateIdNotNull(Long id) throws InvalidLicenseDataException {
        if (id == null) {
            throw new InvalidLicenseDataException("El ID no puede ser nulo");
        }
    }

    private void validateTextNotNull(String text, String fieldName) throws InvalidLicenseDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidLicenseDataException(fieldName + " no puede ser nulo o vacío");
        }
    }
}