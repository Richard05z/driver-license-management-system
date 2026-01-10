package licencia.persistence;

import licencia.exception.LicenseNotFoundException;
import licencia.exception.InvalidLicenseDataException;
import licencia.model.License;
import db.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LicenseDao implements LicenseDaoInterface {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    @Override
    public List<License> listAllLicenses() throws InvalidLicenseDataException, SQLException {
        String sql = "SELECT * FROM licencia ORDER BY fecha_emision DESC, id_licencia DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                License license = mapResultSetToLicense(resultSet);
                licenses.add(license);
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al listar licencias", e);
        }
        
        return licenses;
    }

    @Override
    public License getById(Long id) throws SQLException, LicenseNotFoundException {
        String sql = "SELECT * FROM licencia WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToLicense(resultSet);
                }
            }
        }
        
        throw new LicenseNotFoundException("Licencia con ID " + id + " no encontrada");
    }

    @Override
    public License save(License license) throws InvalidLicenseDataException, SQLException {
        // First validate driver exists
        if (!driverExists(license.getDriverId())) {
            throw new InvalidLicenseDataException("El conductor con ID " + license.getDriverId() + " no existe en la base de datos");
        }
        
        String sql = "INSERT INTO licencia (id_conductor, tipo_licencia, categoria, fecha_emision, " +
                    "fecha_vencimiento, puntos, restricciones, renovada) " +
                    "VALUES (?, ?::tipo_licencia_enum, ?::categoria_licencia_enum, ?, ?, ?, ?, ?)";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setLong(1, license.getDriverId());
            statement.setString(2, license.getLicenseType());
            statement.setString(3, license.getCategory());
            statement.setDate(4, Date.valueOf(license.getIssueDate()));
            statement.setDate(5, Date.valueOf(license.getExpiryDate()));
            statement.setInt(6, license.getPoints());
            statement.setString(7, license.getRestrictions());
            statement.setBoolean(8, license.getRenewed() != null ? license.getRenewed() : false);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        license.setId(generatedKeys.getLong(1));
                        return license;
                    }
                }
            }
            
            throw new SQLException("No se pudo guardar la licencia, no se generó ID");
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Unique constraint violation
                throw new InvalidLicenseDataException("Ya existe una licencia con estos datos", e);
            } else if (e.getSQLState().equals("23514")) { // Check constraint violation
                throw new InvalidLicenseDataException("Error de validación: " + e.getMessage(), e);
            } else if (e.getSQLState().equals("23503")) { // Foreign key violation
                if (e.getMessage().contains("conductor")) {
                    throw new InvalidLicenseDataException("El conductor especificado no existe", e);
                }
            }
            throw new InvalidLicenseDataException("Error al guardar la licencia", e);
        }
    }

    @Override
    public void delete(Long id) throws SQLException, LicenseNotFoundException {
        String sql = "DELETE FROM licencia WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new LicenseNotFoundException("Licencia con ID " + id + " no encontrada para eliminar");
            }
        }
    }

    @Override
    public License update(License license) throws LicenseNotFoundException, SQLException, InvalidLicenseDataException {
        // Validate driver exists before update
        if (!driverExists(license.getDriverId())) {
            throw new InvalidLicenseDataException("El conductor con ID " + license.getDriverId() + " no existe en la base de datos");
        }
        
        String sql = "UPDATE licencia SET id_conductor = ?, tipo_licencia = ?::tipo_licencia_enum, " +
                    "categoria = ?::categoria_licencia_enum, fecha_emision = ?, fecha_vencimiento = ?, " +
                    "puntos = ?, restricciones = ?, renovada = ? WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, license.getDriverId());
            statement.setString(2, license.getLicenseType());
            statement.setString(3, license.getCategory());
            statement.setDate(4, Date.valueOf(license.getIssueDate()));
            statement.setDate(5, Date.valueOf(license.getExpiryDate()));
            statement.setInt(6, license.getPoints());
            statement.setString(7, license.getRestrictions());
            statement.setBoolean(8, license.getRenewed() != null ? license.getRenewed() : false);
            statement.setLong(9, license.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new LicenseNotFoundException("Licencia con ID " + license.getId() + " no encontrada para actualizar");
            }
            
            return license;
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new InvalidLicenseDataException("Ya existe una licencia con estos datos", e);
            } else if (e.getSQLState().equals("23514")) {
                throw new InvalidLicenseDataException("Error de validación: " + e.getMessage(), e);
            } else if (e.getSQLState().equals("23503")) {
                if (e.getMessage().contains("conductor")) {
                    throw new InvalidLicenseDataException("El conductor especificado no existe", e);
                }
            }
            throw e;
        }
    }
    
    @Override
    public boolean existsById(Long id) throws SQLException {
        if (id == null) return false;
        
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    @Override
    public List<License> findByDriverId(Long driverId) throws SQLException, InvalidLicenseDataException {
        if (!driverExists(driverId)) {
            throw new InvalidLicenseDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM licencia WHERE id_conductor = ? ORDER BY fecha_emision DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias por conductor", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findByLicenseType(String licenseType) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE tipo_licencia = ?::tipo_licencia_enum ORDER BY fecha_emision DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, licenseType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias por tipo", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findByCategory(String category) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE categoria = ?::categoria_licencia_enum ORDER BY fecha_emision DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, category);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias por categoría", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findByRenewalStatus(Boolean renewed) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE renovada = ? ORDER BY fecha_emision DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setBoolean(1, renewed != null ? renewed : false);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias por estado de renovación", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findByDriverAndType(Long driverId, String licenseType) throws SQLException, InvalidLicenseDataException {
        if (!driverExists(driverId)) {
            throw new InvalidLicenseDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM licencia WHERE id_conductor = ? AND tipo_licencia = ?::tipo_licencia_enum ORDER BY fecha_emision DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            statement.setString(2, licenseType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias por conductor y tipo", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findActiveLicenses() throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE fecha_vencimiento >= CURRENT_DATE ORDER BY fecha_vencimiento ASC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                License license = mapResultSetToLicense(resultSet);
                licenses.add(license);
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias activas", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findExpiredLicenses() throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE fecha_vencimiento < CURRENT_DATE ORDER BY fecha_vencimiento DESC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                License license = mapResultSetToLicense(resultSet);
                licenses.add(license);
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias vencidas", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findLicensesExpiringSoon(int daysThreshold) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE fecha_vencimiento BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '? days' ORDER BY fecha_vencimiento ASC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, daysThreshold);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias próximas a vencer", e);
        }
        
        return licenses;
    }

    @Override
    public int countLicensesByDriver(Long driverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }

    @Override
    public int countLicensesByType(String licenseType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE tipo_licencia = ?::tipo_licencia_enum";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, licenseType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }

    @Override
    public int countActiveLicenses() throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE fecha_vencimiento >= CURRENT_DATE";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        
        return 0;
    }

    @Override
    public int countExpiredLicenses() throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE fecha_vencimiento < CURRENT_DATE";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        
        return 0;
    }

    @Override
    public boolean deductPoints(Long licenseId, int pointsToDeduct) throws SQLException, InvalidLicenseDataException {
        // First get current points
        String selectSql = "SELECT puntos FROM licencia WHERE id_licencia = ?";
        int currentPoints = 0;
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSql)
        ) {
            selectStmt.setLong(1, licenseId);
            
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    currentPoints = resultSet.getInt("puntos");
                } else {
                    throw new InvalidLicenseDataException("Licencia con ID " + licenseId + " no encontrada");
                }
            }
        }
        
        // Validate deduction
        if (pointsToDeduct > currentPoints) {
            throw new InvalidLicenseDataException(
                "No se pueden deducir " + pointsToDeduct + " puntos. La licencia solo tiene " + currentPoints + " puntos"
            );
        }
        
        // Deduct points
        String updateSql = "UPDATE licencia SET puntos = ? WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement updateStmt = conn.prepareStatement(updateSql)
        ) {
            updateStmt.setInt(1, currentPoints - pointsToDeduct);
            updateStmt.setLong(2, licenseId);
            
            int affectedRows = updateStmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean restorePoints(Long licenseId, int pointsToRestore) throws SQLException, InvalidLicenseDataException {
        // First get current points
        String selectSql = "SELECT puntos FROM licencia WHERE id_licencia = ?";
        int currentPoints = 0;
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSql)
        ) {
            selectStmt.setLong(1, licenseId);
            
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    currentPoints = resultSet.getInt("puntos");
                } else {
                    throw new InvalidLicenseDataException("Licencia con ID " + licenseId + " no encontrada");
                }
            }
        }
        
        // Validate restoration (max 20 points)
        int newPoints = currentPoints + pointsToRestore;
        if (newPoints > 20) {
            throw new InvalidLicenseDataException(
                "No se pueden restaurar " + pointsToRestore + " puntos. Excedería el máximo de 20 puntos. Puntos actuales: " + currentPoints
            );
        }
        
        // Restore points
        String updateSql = "UPDATE licencia SET puntos = ? WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement updateStmt = conn.prepareStatement(updateSql)
        ) {
            updateStmt.setInt(1, newPoints);
            updateStmt.setLong(2, licenseId);
            
            int affectedRows = updateStmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean resetPoints(Long licenseId) throws SQLException, InvalidLicenseDataException {
        String sql = "UPDATE licencia SET puntos = 20 WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, licenseId);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new InvalidLicenseDataException("Licencia con ID " + licenseId + " no encontrada");
            }
            
            return true;
        }
    }

    @Override
    public boolean renewLicense(Long licenseId, String newExpiryDate) throws SQLException, InvalidLicenseDataException {
        // First check if license can be renewed
        String checkSql = "SELECT fecha_vencimiento, renovada FROM licencia WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkSql)
        ) {
            checkStmt.setLong(1, licenseId);
            
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidLicenseDataException("Licencia con ID " + licenseId + " no encontrada");
                }
                
                Date currentExpiry = resultSet.getDate("fecha_vencimiento");
                boolean alreadyRenewed = resultSet.getBoolean("renovada");
                
                // Check if already renewed
                if (alreadyRenewed) {
                    throw new InvalidLicenseDataException("La licencia ya ha sido renovada anteriormente");
                }
                
                // Check if expired for too long (more than 1 year)
                java.util.Date currentDate = new java.util.Date();
                long diffInMillis = currentDate.getTime() - currentExpiry.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
                
                if (diffInDays > 365) {
                    throw new InvalidLicenseDataException(
                        "La licencia está vencida por más de 1 año. No se puede renovar, debe solicitar una nueva"
                    );
                }
            }
        }
        
        // Renew license
        String updateSql = "UPDATE licencia SET fecha_vencimiento = ?, renovada = true WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement updateStmt = conn.prepareStatement(updateSql)
        ) {
            updateStmt.setDate(1, Date.valueOf(newExpiryDate));
            updateStmt.setLong(2, licenseId);
            
            int affectedRows = updateStmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean isLicenseValid(Long licenseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_licencia = ? AND fecha_vencimiento >= CURRENT_DATE";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, licenseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean isLicenseExpired(Long licenseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_licencia = ? AND fecha_vencimiento < CURRENT_DATE";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, licenseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean canLicenseBeRenewed(Long licenseId) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT fecha_vencimiento, renovada FROM licencia WHERE id_licencia = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, licenseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidLicenseDataException("Licencia con ID " + licenseId + " no encontrada");
                }
                
                Date expiryDate = resultSet.getDate("fecha_vencimiento");
                boolean renewed = resultSet.getBoolean("renovada");
                
                // Cannot renew if already renewed
                if (renewed) {
                    return false;
                }
                
                // Check if expired for more than 1 year
                java.util.Date currentDate = new java.util.Date();
                long diffInMillis = currentDate.getTime() - expiryDate.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
                
                return diffInDays <= 365;
            }
        }
    }

    @Override
    public boolean driverHasValidLicense(Long driverId, String licenseType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM licencia WHERE id_conductor = ? AND tipo_licencia = ?::tipo_licencia_enum " +
                    "AND fecha_vencimiento >= CURRENT_DATE";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            statement.setString(2, licenseType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    @Override
    public List<String> getDriverLicenseTypes(Long driverId) throws SQLException, InvalidLicenseDataException {
        if (!driverExists(driverId)) {
            throw new InvalidLicenseDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT DISTINCT tipo_licencia FROM licencia WHERE id_conductor = ? AND fecha_vencimiento >= CURRENT_DATE";
        List<String> licenseTypes = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    licenseTypes.add(resultSet.getString("tipo_licencia"));
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al obtener tipos de licencia del conductor", e);
        }
        
        return licenseTypes;
    }

    @Override
    public List<License> findLicensesIssuedBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE fecha_emision BETWEEN ? AND ? ORDER BY fecha_emision ASC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias emitidas en el rango de fechas", e);
        }
        
        return licenses;
    }

    @Override
    public List<License> findLicensesExpiringBetween(String startDate, String endDate) throws SQLException, InvalidLicenseDataException {
        String sql = "SELECT * FROM licencia WHERE fecha_vencimiento BETWEEN ? AND ? ORDER BY fecha_vencimiento ASC";
        List<License> licenses = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    License license = mapResultSetToLicense(resultSet);
                    licenses.add(license);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLicenseDataException("Error al buscar licencias que vencen en el rango de fechas", e);
        }
        
        return licenses;
    }
    
    // Helper methods
    private boolean driverExists(Long driverId) throws SQLException {
        if (driverId == null) return false;
        
        String sql = "SELECT COUNT(*) FROM conductor WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    private License mapResultSetToLicense(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id_licencia");
        Long driverId = resultSet.getLong("id_conductor");
        String licenseType = resultSet.getString("tipo_licencia");
        String category = resultSet.getString("categoria");
        
        Date issueDate = resultSet.getDate("fecha_emision");
        String issueDateStr = issueDate != null ? issueDate.toString() : null;
        
        Date expiryDate = resultSet.getDate("fecha_vencimiento");
        String expiryDateStr = expiryDate != null ? expiryDate.toString() : null;
        
        Integer points = resultSet.getInt("puntos");
        String restrictions = resultSet.getString("restricciones");
        Boolean renewed = resultSet.getBoolean("renovada");
        
        License license = new License(driverId, licenseType, category, issueDateStr, expiryDateStr, points, restrictions, renewed);
        license.setId(id);
        
        return license;
    }
}