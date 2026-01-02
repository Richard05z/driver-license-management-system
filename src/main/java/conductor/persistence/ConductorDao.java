package conductor.persistence;

import conductor.exception.ConductorNotFoundException;
import conductor.exception.InvalidConductorDataException;
import conductor.model.Conductor;
import db.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConductorDao implements ConductorDaoInterface {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    @Override
    public List<Conductor> listAllDrivers() throws InvalidConductorDataException, SQLException {
        String sql = "SELECT * FROM conductor ORDER BY apellidos, nombre";
        List<Conductor> conductors = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Conductor conductor = mapResultSetToDriver(resultSet);
                conductors.add(conductor);
            }
        } catch (SQLException e) {
            throw new InvalidConductorDataException("Error al listar conductores", e);
        }
        
        return conductors;
    }

    @Override
    public Conductor getById(Long id) throws SQLException, ConductorNotFoundException {
        String sql = "SELECT * FROM conductor WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDriver(resultSet);
                }
            }
        }
        
        throw new ConductorNotFoundException("Conductor con ID " + id + " no encontrado");
    }

    @Override
    public Conductor getByIdDocument(String idDocument) throws SQLException, ConductorNotFoundException {
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
        
        throw new ConductorNotFoundException("Conductor con documento " + idDocument + " no encontrado");
    }

    @Override
    public Conductor save(Conductor conductor) throws InvalidConductorDataException, SQLException {
    String sql = "INSERT INTO conductor (nombre, apellidos, documento_identidad, fecha_nacimiento, " +
                "direccion, telefono, email, estado_licencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?::estado_licencia_enum)";
    
    try (
        Connection conn = this.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    ) {
        statement.setString(1, conductor.getFirstName());
        statement.setString(2, conductor.getLastName());
        statement.setString(3, conductor.getIdDocument());
        statement.setDate(4, Date.valueOf(conductor.getBirthDate()));
        statement.setString(5, conductor.getAddress());
        statement.setString(6, conductor.getPhone());
        statement.setString(7, conductor.getEmail());
        statement.setString(8, conductor.getLicenseStatus()); // Se convertirá automáticamente
        
        int affectedRows = statement.executeUpdate();
        
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conductor.setId(generatedKeys.getLong(1));
                    return conductor;
                }
            }
        }
        
        throw new SQLException("No se pudo guardar el conductor, no se generó ID");
        
    } catch (SQLException e) {
        if (e.getSQLState().equals("23505")) { // Unique constraint violation
            throw new InvalidConductorDataException("Ya existe un conductor con este documento de identidad", e);
        }
        throw new InvalidConductorDataException("Error al guardar el conductor", e);
    }
}

    @Override
    public void delete(Long id) throws SQLException, ConductorNotFoundException {
        String sql = "DELETE FROM conductor WHERE id_conductor = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new ConductorNotFoundException("Conductor con ID " + id + " no encontrado para eliminar");
            }
        }
    }

    @Override
    public Conductor update(Conductor conductor) throws ConductorNotFoundException, SQLException {
    String sql = "UPDATE conductor SET nombre = ?, apellidos = ?, documento_identidad = ?, " +
                "fecha_nacimiento = ?, direccion = ?, telefono = ?, email = ?, estado_licencia = ?::estado_licencia_enum " +
                "WHERE id_conductor = ?";
    
    try (
        Connection conn = this.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql)
    ) {
        statement.setString(1, conductor.getFirstName());
        statement.setString(2, conductor.getLastName());
        statement.setString(3, conductor.getIdDocument());
        statement.setDate(4, Date.valueOf(conductor.getBirthDate()));
        statement.setString(5, conductor.getAddress());
        statement.setString(6, conductor.getPhone());
        statement.setString(7, conductor.getEmail());
        statement.setString(8, conductor.getLicenseStatus());
        statement.setLong(9, conductor.getId());
        
        int affectedRows = statement.executeUpdate();
        
        if (affectedRows == 0) {
            throw new ConductorNotFoundException("Conductor con ID " + conductor.getId() + " no encontrado para actualizar");
        }
        
        return conductor;
        
    } catch (SQLException e) {
        if (e.getSQLState().equals("23505")) { // Unique constraint violation
            // throw new InvalidDriverDataException("Ya existe otro conductor con este documento de identidad", e);
        }
        throw e;
    }
}
    
    @Override
    public boolean existsById(Long id) throws SQLException {
        if (id == null) return false;
        
        String sql = "SELECT COUNT(*) FROM conductor WHERE id_conductor = ?";
        
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

    private Conductor mapResultSetToDriver(ResultSet resultSet) throws SQLException {
        String firstName = resultSet.getString("nombre");
        String lastName = resultSet.getString("apellidos");
        String idDocument = resultSet.getString("documento_identidad");
        
        Date birthDate = resultSet.getDate("fecha_nacimiento");
        String birthDateStr = birthDate != null ? birthDate.toString() : null;
        
        String address = resultSet.getString("direccion");
        String phone = resultSet.getString("telefono");
        String email = resultSet.getString("email");
        String licenseStatus = resultSet.getString("estado_licencia");
        
        Conductor conductor = new Conductor(firstName, lastName, idDocument, birthDateStr, address, phone, email, licenseStatus);
        conductor.setId(resultSet.getLong("id_conductor"));
        
        return conductor;
    }
}