package conductor.persistence;

import conductor.exception.DriverNotFoundException;
import conductor.exception.InvalidDriverDataException;
import conductor.model.Driver;
import db.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDao implements DriverDaoInterface {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    @Override
    public List<Driver> listAllDrivers() throws InvalidDriverDataException, SQLException {
        String sql = "SELECT * FROM conductor ORDER BY apellidos, nombre";
        List<Driver> drivers = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Driver driver = mapResultSetToDriver(resultSet);
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new InvalidDriverDataException("Error al listar conductores", e);
        }
        
        return drivers;
    }

    @Override
    public Driver getById(Integer id) throws SQLException, DriverNotFoundException {
        String sql = "SELECT * FROM conductor WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDriver(resultSet);
                }
            }
        }
        
        throw new DriverNotFoundException("Conductor con ID " + id + " no encontrado");
    }

    @Override
    public Driver getByIdDocument(String idDocument) throws SQLException, DriverNotFoundException {
        String sql = "SELECT * FROM conductor WHERE documento_identidad = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, idDocument);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDriver(resultSet);
                }
            }
        }
        
        throw new DriverNotFoundException("Conductor con documento " + idDocument + " no encontrado");
    }

    @Override
    public Driver save(Driver driver) throws InvalidDriverDataException, SQLException {
    String sql = "INSERT INTO conductor (nombre, apellidos, documento_identidad, fecha_nacimiento, " +
                "direccion, telefono, email, estado_licencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?::estado_licencia_enum)";
    
    try (
        Connection conn = this.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    ) {
        statement.setString(1, driver.getFirstName());
        statement.setString(2, driver.getLastName());
        statement.setString(3, driver.getIdDocument());
        statement.setDate(4, Date.valueOf(driver.getBirthDate()));
        statement.setString(5, driver.getAddress());
        statement.setString(6, driver.getPhone());
        statement.setString(7, driver.getEmail());
        statement.setString(8, driver.getLicenseStatus()); // Se convertirá automáticamente
        
        int affectedRows = statement.executeUpdate();
        
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    driver.setId(generatedKeys.getInt(1));
                    return driver;
                }
            }
        }
        
        throw new SQLException("No se pudo guardar el conductor, no se generó ID");
        
    } catch (SQLException e) {
        if (e.getSQLState().equals("23505")) { // Unique constraint violation
            throw new InvalidDriverDataException("Ya existe un conductor con este documento de identidad", e);
        }
        throw new InvalidDriverDataException("Error al guardar el conductor", e);
    }
}

    @Override
    public void delete(Integer id) throws SQLException, DriverNotFoundException {
        String sql = "DELETE FROM conductor WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DriverNotFoundException("Conductor con ID " + id + " no encontrado para eliminar");
            }
        }
    }

    @Override
    public Driver update(Driver driver) throws DriverNotFoundException, SQLException {
    String sql = "UPDATE conductor SET nombre = ?, apellidos = ?, documento_identidad = ?, " +
                "fecha_nacimiento = ?, direccion = ?, telefono = ?, email = ?, estado_licencia = ?::estado_licencia_enum " +
                "WHERE id_conductor = ?";
    
    try (
        Connection conn = this.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql)
    ) {
        statement.setString(1, driver.getFirstName());
        statement.setString(2, driver.getLastName());
        statement.setString(3, driver.getIdDocument());
        statement.setDate(4, Date.valueOf(driver.getBirthDate()));
        statement.setString(5, driver.getAddress());
        statement.setString(6, driver.getPhone());
        statement.setString(7, driver.getEmail());
        statement.setString(8, driver.getLicenseStatus());
        statement.setInt(9, driver.getId());
        
        int affectedRows = statement.executeUpdate();
        
        if (affectedRows == 0) {
            throw new DriverNotFoundException("Conductor con ID " + driver.getId() + " no encontrado para actualizar");
        }
        
        return driver;
        
    } catch (SQLException e) {
        if (e.getSQLState().equals("23505")) { // Unique constraint violation
            // throw new InvalidDriverDataException("Ya existe otro conductor con este documento de identidad", e);
        }
        throw e;
    }
}
    
    @Override
    public boolean existsById(Integer id) throws SQLException {
        if (id == null) return false;
        
        String sql = "SELECT COUNT(*) FROM conductor WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean existsByIdDocument(String idDocument) throws SQLException {
        if (idDocument == null || idDocument.trim().isEmpty()) return false;
        
        String sql = "SELECT COUNT(*) FROM conductor WHERE documento_identidad = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, idDocument.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    private Driver mapResultSetToDriver(ResultSet resultSet) throws SQLException {
        String firstName = resultSet.getString("nombre");
        String lastName = resultSet.getString("apellidos");
        String idDocument = resultSet.getString("documento_identidad");
        
        Date birthDate = resultSet.getDate("fecha_nacimiento");
        String birthDateStr = birthDate != null ? birthDate.toString() : null;
        
        String address = resultSet.getString("direccion");
        String phone = resultSet.getString("telefono");
        String email = resultSet.getString("email");
        String licenseStatus = resultSet.getString("estado_licencia");
        
        Driver driver = new Driver(firstName, lastName, idDocument, birthDateStr, address, phone, email, licenseStatus);
        driver.setId(resultSet.getInt("id_conductor"));
        
        return driver;
    }
}