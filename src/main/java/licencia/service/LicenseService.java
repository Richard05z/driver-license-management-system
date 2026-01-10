package licencia.service;

import licencia.exception.LicenseNotFoundException;
import licencia.exception.InvalidLicenseDataException;
import licencia.model.License;
import licencia.repository.LicenseRepositoryInterface;
import licencia.validator.LicenseValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LicenseService implements LicenseServiceInterface {

    private final LicenseRepositoryInterface licenseRepository;

    public LicenseService(LicenseRepositoryInterface licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    @Override
    public List<License> listAllLicenses() throws InvalidLicenseDataException, SQLException {
        return this.licenseRepository.listAllLicenses();
    }

    @Override
    public License getById(Long id) throws SQLException, LicenseNotFoundException {
        return this.licenseRepository.getById(id);
    }

    @Override
    public License save(License license) throws InvalidLicenseDataException, SQLException {
        // Validate license data
        LicenseValidator.validate(license);
        
        // Validate license type and category compatibility
        LicenseValidator.validateLicenseCompatibility(license.getLicenseType(), license.getCategory());
        
        // Set default renewed status if null
        if (license.getRenewed() == null) {
            license.setRenewed(false);
        }
        
        return this.licenseRepository.save(license);
    }

    @Override
    public void delete(Long id) throws SQLException, LicenseNotFoundException {
        this.existsById(id);
        this.licenseRepository.delete(id);
    }

    @Override
    public License update(License license) throws LicenseNotFoundException, SQLException, InvalidLicenseDataException {
        this.existsById(license.getId());
        
        // Validate license data
        LicenseValidator.validate(license);
        
        // Validate license type and category compatibility
        LicenseValidator.validateLicenseCompatibility(license.getLicenseType(), license.getCategory());
        
        // Ensure renewed status is set
        if (license.getRenewed() == null) {
            license.setRenewed(false);
        }
        
        return this.licenseRepository.update(license);
    }

    @Override
    public boolean existsById(Long id) throws SQLException, LicenseNotFoundException {
        if (!this.licenseRepository.existsById(id)) {
            throw new LicenseNotFoundException("La licencia con ID " + id + " no fue encontrada");
        }
        return true;
    }

    @Override
    public List<License> findByDriverId(Long driverId) throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findByDriverId(driverId);
    }

    @Override
    public List<License> findByLicenseType(String licenseType) throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findByLicenseType(licenseType);
    }

    @Override
    public List<License> findByCategory(String category) throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findByCategory(category);
    }

    @Override
    public List<License> findByRenewalStatus(Boolean renewed) throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findByRenewalStatus(renewed);
    }

    @Override
    public List<License> findByDriverAndType(Long driverId, String licenseType) throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findByDriverAndType(driverId, licenseType);
    }

    @Override
    public List<License> findActiveLicenses() throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findActiveLicenses();
    }

    @Override
    public List<License> findExpiredLicenses() throws SQLException, InvalidLicenseDataException {
        return this.licenseRepository.findExpiredLicenses();
    }

    @Override
    public List<License> findLicensesExpiringSoon(int daysThreshold) throws SQLException, InvalidLicenseDataException {
        if (daysThreshold <= 0) {
            throw new InvalidLicenseDataException("El umbral de días debe ser mayor a cero");
        }
        return this.licenseRepository.findLicensesExpiringSoon(daysThreshold);
    }

    @Override
    public int countLicensesByDriver(Long driverId) throws SQLException {
        return this.licenseRepository.countLicensesByDriver(driverId);
    }

    @Override
    public int countLicensesByType(String licenseType) throws SQLException {
        return this.licenseRepository.countLicensesByType(licenseType);
    }

    @Override
    public int countActiveLicenses() throws SQLException {
        return this.licenseRepository.countActiveLicenses();
    }

    @Override
    public int countExpiredLicenses() throws SQLException {
        return this.licenseRepository.countExpiredLicenses();
    }

    @Override
    public boolean deductPoints(Long licenseId, int pointsToDeduct) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        
        // Get current license to validate points deduction
        License license = this.licenseRepository.getById(licenseId);
        LicenseValidator.validateForPointsDeduction(license, pointsToDeduct);
        
        return this.licenseRepository.deductPoints(licenseId, pointsToDeduct);
    }

    @Override
    public boolean restorePoints(Long licenseId, int pointsToRestore) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        
        if (pointsToRestore <= 0) {
            throw new InvalidLicenseDataException("Los puntos a restaurar deben ser positivos");
        }
        
        return this.licenseRepository.restorePoints(licenseId, pointsToRestore);
    }

    @Override
    public boolean resetPoints(Long licenseId) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        return this.licenseRepository.resetPoints(licenseId);
    }

    @Override
    public boolean renewLicense(Long licenseId, String newExpiryDate) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        
        // Get current license to validate renewal
        License license = this.licenseRepository.getById(licenseId);
        LicenseValidator.validateForRenewal(license);
        
        // Validate new expiry date
        try {
            LocalDate newExpiry = LocalDate.parse(newExpiryDate);
            LocalDate currentExpiry = LocalDate.parse(license.getExpiryDate());
            
            if (!newExpiry.isAfter(currentExpiry)) {
                throw new InvalidLicenseDataException("La nueva fecha de vencimiento debe ser posterior a la fecha actual de vencimiento");
            }
            
            // Validate reasonable renewal period (max 10 years from current date)
            LocalDate today = LocalDate.now();
            long yearsBetween = java.time.temporal.ChronoUnit.YEARS.between(today, newExpiry);
            
            if (yearsBetween > 10) {
                throw new InvalidLicenseDataException("El período de renovación no puede exceder 10 años");
            }
            
        } catch (java.time.format.DateTimeParseException e) {
            throw new InvalidLicenseDataException("Formato de fecha inválido para nueva fecha de vencimiento. Use formato YYYY-MM-DD");
        }
        
        return this.licenseRepository.renewLicense(licenseId, newExpiryDate);
    }

    @Override
    public boolean isLicenseValid(Long licenseId) throws SQLException, LicenseNotFoundException {
        this.existsById(licenseId);
        return this.licenseRepository.isLicenseValid(licenseId);
    }

    @Override
    public boolean isLicenseExpired(Long licenseId) throws SQLException, LicenseNotFoundException {
        this.existsById(licenseId);
        return this.licenseRepository.isLicenseExpired(licenseId);
    }

    @Override
    public boolean canLicenseBeRenewed(Long licenseId) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        return this.licenseRepository.canLicenseBeRenewed(licenseId);
    }

    @Override
    public boolean driverHasValidLicense(Long driverId, String licenseType) throws SQLException, InvalidLicenseDataException {
        if (driverId == null || driverId <= 0) {
            throw new InvalidLicenseDataException("ID de conductor inválido");
        }
        
        if (licenseType == null || licenseType.trim().isEmpty()) {
            throw new InvalidLicenseDataException("Tipo de licencia inválido");
        }
        
        return this.licenseRepository.driverHasValidLicense(driverId, licenseType);
    }

    @Override
    public List<String> getDriverLicenseTypes(Long driverId) throws SQLException, InvalidLicenseDataException {
        if (driverId == null || driverId <= 0) {
            throw new InvalidLicenseDataException("ID de conductor inválido");
        }
        
        return this.licenseRepository.getDriverLicenseTypes(driverId);
    }

    @Override
    public List<License> findLicensesIssuedBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException {
        // Validate date parameters
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            if (!end.isAfter(start)) {
                throw new InvalidLicenseDataException("La fecha final debe ser posterior a la fecha inicial");
            }
            
        } catch (java.time.format.DateTimeParseException e) {
            throw new InvalidLicenseDataException("Formato de fecha inválido. Use formato YYYY-MM-DD");
        }
        
        return this.licenseRepository.findLicensesIssuedBetween(startDate, endDate);
    }

    @Override
    public List<License> findLicensesExpiringBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException {
        // Validate date parameters
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            if (!end.isAfter(start)) {
                throw new InvalidLicenseDataException("La fecha final debe ser posterior a la fecha inicial");
            }
            
        } catch (java.time.format.DateTimeParseException e) {
            throw new InvalidLicenseDataException("Formato de fecha inválido. Use formato YYYY-MM-DD");
        }
        
        return this.licenseRepository.findLicensesExpiringBetween(startDate, endDate);
    }

    @Override
    public License issueNewLicense(Long driverId, String licenseType, String category, int validityYears) 
            throws SQLException, InvalidLicenseDataException {
        
        if (driverId == null || driverId <= 0) {
            throw new InvalidLicenseDataException("ID de conductor inválido");
        }
        
        if (licenseType == null || licenseType.trim().isEmpty()) {
            throw new InvalidLicenseDataException("Tipo de licencia requerido");
        }
        
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidLicenseDataException("Categoría requerida");
        }
        
        if (validityYears <= 0 || validityYears > 10) {
            throw new InvalidLicenseDataException("Los años de validez deben estar entre 1 y 10");
        }
        
        // Validate license type and category compatibility
        LicenseValidator.validateLicenseCompatibility(licenseType, category);
        
        // Create new license
        LocalDate issueDate = LocalDate.now();
        LocalDate expiryDate = issueDate.plusYears(validityYears);
        
        License newLicense = new License(
            driverId,
            licenseType,
            category,
            issueDate.toString(),
            expiryDate.toString(),
            20, // Full points for new license
            null, // No restrictions initially
            false // Not renewed
        );
        
        // Validate and save
        return this.save(newLicense);
    }

    @Override
    public boolean suspendLicense(Long licenseId, String reason) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidLicenseDataException("Se requiere una razón para la suspensión");
        }
        
        // In a real implementation, this would update the license status
        // For now, we'll just return true to indicate operation would succeed
        // TODO: Implement actual suspension logic
        return true;
    }

    @Override
    public boolean revokeLicense(Long licenseId, String reason) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidLicenseDataException("Se requiere una razón para la revocación");
        }
        
        // In a real implementation, this would update the license status to revoked
        // For now, we'll just return true to indicate operation would succeed
        // TODO: Implement actual revocation logic
        return true;
    }

    @Override
    public boolean transferLicense(Long licenseId, Long newDriverId) throws SQLException, InvalidLicenseDataException, LicenseNotFoundException {
        this.existsById(licenseId);
        
        if (newDriverId == null || newDriverId <= 0) {
            throw new InvalidLicenseDataException("ID del nuevo conductor inválido");
        }
        
        // Get the current license
        License license = this.licenseRepository.getById(licenseId);
        
        // Check if license is valid (not expired)
        if (!this.isLicenseValid(licenseId)) {
            throw new InvalidLicenseDataException("No se puede transferir una licencia vencida");
        }
        
        // Update driver ID
        license.setDriverId(newDriverId);
        
        // Save the updated license
        this.licenseRepository.update(license);
        
        return true;
    }

    @Override
    public List<License> getDriverLicensesHistory(Long driverId) throws SQLException, InvalidLicenseDataException {
        if (driverId == null || driverId <= 0) {
            throw new InvalidLicenseDataException("ID de conductor inválido");
        }
        
        return this.licenseRepository.findByDriverId(driverId);
    }

    @Override
    public boolean validateLicenseCompatibility(String licenseType, String category) throws InvalidLicenseDataException {
        LicenseValidator.validateLicenseCompatibility(licenseType, category);
        return true;
    }
}