package licencia.repository;

import licencia.exception.LicenseNotFoundException;
import licencia.exception.InvalidLicenseDataException;
import licencia.model.License;
import licencia.persistence.LicenseDao;

import java.sql.SQLException;
import java.util.List;

public class LicenseRepository implements LicenseRepositoryInterface {

    private final LicenseDao licenseDao;

    public LicenseRepository(LicenseDao licenseDao) throws SQLException {
        this.licenseDao = licenseDao;
    }

    @Override
    public List<License> listAllLicenses() throws InvalidLicenseDataException, SQLException {
        return this.licenseDao.listAllLicenses();
    }

    @Override
    public License getById(Long id) throws SQLException, LicenseNotFoundException {
        return this.licenseDao.getById(id);
    }

    @Override
    public License save(License license) throws InvalidLicenseDataException, SQLException {
        return this.licenseDao.save(license);
    }

    @Override
    public void delete(Long id) throws SQLException, LicenseNotFoundException {
        this.licenseDao.delete(id);
    }

    @Override
    public License update(License license) throws LicenseNotFoundException, SQLException, InvalidLicenseDataException {
        return this.licenseDao.update(license);
    }

    @Override
    public boolean existsById(Long id) throws SQLException {
        return this.licenseDao.existsById(id);
    }

    @Override
    public List<License> findByDriverId(Long driverId) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findByDriverId(driverId);
    }

    @Override
    public List<License> findByLicenseType(String licenseType) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findByLicenseType(licenseType);
    }

    @Override
    public List<License> findByCategory(String category) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findByCategory(category);
    }

    @Override
    public List<License> findByRenewalStatus(Boolean renewed) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findByRenewalStatus(renewed);
    }

    @Override
    public List<License> findByDriverAndType(Long driverId, String licenseType) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findByDriverAndType(driverId, licenseType);
    }

    @Override
    public List<License> findActiveLicenses() throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findActiveLicenses();
    }

    @Override
    public List<License> findExpiredLicenses() throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findExpiredLicenses();
    }

    @Override
    public List<License> findLicensesExpiringSoon(int daysThreshold) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findLicensesExpiringSoon(daysThreshold);
    }

    @Override
    public int countLicensesByDriver(Long driverId) throws SQLException {
        return this.licenseDao.countLicensesByDriver(driverId);
    }

    @Override
    public int countLicensesByType(String licenseType) throws SQLException {
        return this.licenseDao.countLicensesByType(licenseType);
    }

    @Override
    public int countActiveLicenses() throws SQLException {
        return this.licenseDao.countActiveLicenses();
    }

    @Override
    public int countExpiredLicenses() throws SQLException {
        return this.licenseDao.countExpiredLicenses();
    }

    @Override
    public boolean deductPoints(Long licenseId, int pointsToDeduct) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.deductPoints(licenseId, pointsToDeduct);
    }

    @Override
    public boolean restorePoints(Long licenseId, int pointsToRestore) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.restorePoints(licenseId, pointsToRestore);
    }

    @Override
    public boolean resetPoints(Long licenseId) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.resetPoints(licenseId);
    }

    @Override
    public boolean renewLicense(Long licenseId, String newExpiryDate) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.renewLicense(licenseId, newExpiryDate);
    }

    @Override
    public boolean isLicenseValid(Long licenseId) throws SQLException {
        return this.licenseDao.isLicenseValid(licenseId);
    }

    @Override
    public boolean isLicenseExpired(Long licenseId) throws SQLException {
        return this.licenseDao.isLicenseExpired(licenseId);
    }

    @Override
    public boolean canLicenseBeRenewed(Long licenseId) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.canLicenseBeRenewed(licenseId);
    }

    @Override
    public boolean driverHasValidLicense(Long driverId, String licenseType) throws SQLException {
        return this.licenseDao.driverHasValidLicense(driverId, licenseType);
    }

    @Override
    public List<String> getDriverLicenseTypes(Long driverId) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.getDriverLicenseTypes(driverId);
    }

    @Override
    public List<License> findLicensesIssuedBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findLicensesIssuedBetween(startDate, endDate);
    }

    @Override
    public List<License> findLicensesExpiringBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException {
        return this.licenseDao.findLicensesExpiringBetween(startDate, endDate);
    }
}