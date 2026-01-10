package licencia.repository;

import licencia.exception.LicenseNotFoundException;
import licencia.exception.InvalidLicenseDataException;
import licencia.model.License;

import java.sql.SQLException;
import java.util.List;

public interface LicenseRepositoryInterface {
    // Basic CRUD operations
    List<License> listAllLicenses() throws InvalidLicenseDataException, SQLException;
    License getById(Long id) throws SQLException, LicenseNotFoundException;
    License save(License license) throws InvalidLicenseDataException, SQLException;
    void delete(Long id) throws SQLException, LicenseNotFoundException;
    License update(License license) throws LicenseNotFoundException, SQLException, InvalidLicenseDataException;
    boolean existsById(Long id) throws SQLException;
    
    // Find operations
    List<License> findByDriverId(Long driverId) throws SQLException, InvalidLicenseDataException;
    List<License> findByLicenseType(String licenseType) throws SQLException, InvalidLicenseDataException;
    List<License> findByCategory(String category) throws SQLException, InvalidLicenseDataException;
    List<License> findByRenewalStatus(Boolean renewed) throws SQLException, InvalidLicenseDataException;
    
    // Find with filters
    List<License> findByDriverAndType(Long driverId, String licenseType) throws SQLException, InvalidLicenseDataException;
    List<License> findActiveLicenses() throws SQLException, InvalidLicenseDataException;
    List<License> findExpiredLicenses() throws SQLException, InvalidLicenseDataException;
    List<License> findLicensesExpiringSoon(int daysThreshold) throws SQLException, InvalidLicenseDataException;
    
    // Business operations
    int countLicensesByDriver(Long driverId) throws SQLException;
    int countLicensesByType(String licenseType) throws SQLException;
    int countActiveLicenses() throws SQLException;
    int countExpiredLicenses() throws SQLException;
    
    // Points operations
    boolean deductPoints(Long licenseId, int pointsToDeduct) throws SQLException, InvalidLicenseDataException;
    boolean restorePoints(Long licenseId, int pointsToRestore) throws SQLException, InvalidLicenseDataException;
    boolean resetPoints(Long licenseId) throws SQLException, InvalidLicenseDataException;
    
    // Renewal operations
    boolean renewLicense(Long licenseId, String newExpiryDate) throws SQLException, InvalidLicenseDataException;
    
    // Validation operations
    boolean isLicenseValid(Long licenseId) throws SQLException;
    boolean isLicenseExpired(Long licenseId) throws SQLException;
    boolean canLicenseBeRenewed(Long licenseId) throws SQLException, InvalidLicenseDataException;
    
    // Driver-specific operations
    boolean driverHasValidLicense(Long driverId, String licenseType) throws SQLException;
    List<String> getDriverLicenseTypes(Long driverId) throws SQLException, InvalidLicenseDataException;
    
    // Report operations
    List<License> findLicensesIssuedBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException;
    List<License> findLicensesExpiringBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException;
}