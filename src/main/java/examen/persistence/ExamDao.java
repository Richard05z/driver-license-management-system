package examen.persistence;

import examen.exception.ExamNotFoundException;
import examen.exception.InvalidExamDataException;
import examen.model.Exam;
import db.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDao implements ExamDaoInterface {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }
    
    private boolean entityExists(Long entityId) throws SQLException {
        if (entityId == null) return false;
        
        String sql = "SELECT COUNT(*) FROM entidad WHERE id_entidad = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, entityId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
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
    
    private String getEntityType(Long entityId) throws SQLException {
        if (entityId == null) return null;
        
        String sql = "SELECT tipo_entidad FROM entidad WHERE id_entidad = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, entityId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("tipo_entidad");
                }
            }
        }
        
        return null;
    }
    
    private void validateExamBeforeSave(Exam exam) throws InvalidExamDataException, SQLException {
        // 1. Validar que la entidad exista
        if (!entityExists(exam.getEntityId())) {
            throw new InvalidExamDataException("La entidad con ID " + exam.getEntityId() + " no existe en la base de datos");
        }
        
        // 2. Validar que el conductor exista
        if (!driverExists(exam.getDriverId())) {
            throw new InvalidExamDataException("El conductor con ID " + exam.getDriverId() + " no existe en la base de datos");
        }
        
        // 3. Validar que el tipo de examen sea compatible con el tipo de entidad
        String entityType = getEntityType(exam.getEntityId());
        
        if (entityType == null) {
            throw new InvalidExamDataException("No se pudo determinar el tipo de entidad para ID " + exam.getEntityId());
        }
        
        if ("medico".equals(exam.getExamType())) {
            if (!"clinica".equals(entityType)) {
                throw new InvalidExamDataException("El examen médico debe ser realizado por una entidad tipo CLINICA. Entidad " + 
                                                   exam.getEntityId() + " es de tipo " + entityType + ".");
            }
        } else if ("teorico".equals(exam.getExamType()) || "practico".equals(exam.getExamType())) {
            if (!"autoescuela".equals(entityType)) {
                throw new InvalidExamDataException("Los exámenes teórico/práctico deben ser realizados por una entidad tipo AUTOESCUELA. Entidad " + 
                                                   exam.getEntityId() + " es de tipo " + entityType + ".");
            }
        }
    }

    @Override
    public List<Exam> listAllExams() throws InvalidExamDataException, SQLException {
        String sql = "SELECT * FROM examen ORDER BY fecha DESC, id_examen DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Exam exam = mapResultSetToExam(resultSet);
                exams.add(exam);
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al listar exámenes", e);
        }
        
        return exams;
    }

    @Override
    public Exam getById(Long id) throws SQLException, ExamNotFoundException {
        String sql = "SELECT * FROM examen WHERE id_examen = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToExam(resultSet);
                }
            }
        }
        
        throw new ExamNotFoundException("Examen con ID " + id + " no encontrado");
    }

    @Override
    public Exam save(Exam exam) throws InvalidExamDataException, SQLException {
        // Validar antes de guardar
        validateExamBeforeSave(exam);
        
        String sql = "INSERT INTO examen (tipo_examen, fecha, resultado, id_entidad, id_conductor, examinador) " +
                    "VALUES (?::tipo_examen_enum, ?, ?::resultado_examen_enum, ?, ?, ?)";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, exam.getExamType());
            statement.setDate(2, Date.valueOf(exam.getDate()));
            statement.setString(3, exam.getResult());
            statement.setLong(4, exam.getEntityId());
            statement.setLong(5, exam.getDriverId());
            statement.setString(6, exam.getExaminer());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        exam.setIdExam(generatedKeys.getLong(1));
                        return exam;
                    }
                }
            }
            
            throw new SQLException("No se pudo guardar el examen, no se generó ID");
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Unique constraint violation
                throw new InvalidExamDataException("Ya existe un examen con estos datos", e);
            } else if (e.getSQLState().equals("23514")) { // Check constraint violation
                throw new InvalidExamDataException("Error de validación: " + e.getMessage(), e);
            } else if (e.getSQLState().equals("23503")) { // Foreign key violation
                // Aunque ya validamos, por si acaso
                if (e.getMessage().contains("entidad")) {
                    throw new InvalidExamDataException("La entidad especificada no existe", e);
                } else if (e.getMessage().contains("conductor")) {
                    throw new InvalidExamDataException("El conductor especificado no existe", e);
                }
            }
            throw new InvalidExamDataException("Error al guardar el examen", e);
        }
    }

    @Override
    public Exam update(Exam exam) throws ExamNotFoundException, SQLException, InvalidExamDataException {
        // Validar antes de actualizar
        validateExamBeforeSave(exam);
        
        String sql = "UPDATE examen SET tipo_examen = ?::tipo_examen_enum, fecha = ?, " +
                    "resultado = ?::resultado_examen_enum, id_entidad = ?, " +
                    "id_conductor = ?, examinador = ? WHERE id_examen = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, exam.getExamType());
            statement.setDate(2, Date.valueOf(exam.getDate()));
            statement.setString(3, exam.getResult());
            statement.setLong(4, exam.getEntityId());
            statement.setLong(5, exam.getDriverId());
            statement.setString(6, exam.getExaminer());
            statement.setLong(7, exam.getIdExam());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new ExamNotFoundException("Examen con ID " + exam.getIdExam() + " no encontrado para actualizar");
            }
            
            return exam;
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Unique constraint violation
                throw new InvalidExamDataException("Ya existe un examen con estos datos", e);
            } else if (e.getSQLState().equals("23514")) { // Check constraint violation
                throw new InvalidExamDataException("Error de validación: " + e.getMessage(), e);
            } else if (e.getSQLState().equals("23503")) { // Foreign key violation
                // Aunque ya validamos, por si acaso
                if (e.getMessage().contains("entidad")) {
                    throw new InvalidExamDataException("La entidad especificada no existe", e);
                } else if (e.getMessage().contains("conductor")) {
                    throw new InvalidExamDataException("El conductor especificado no existe", e);
                }
            }
            throw e;
        }
    }

    @Override
    public void delete(Long id) throws SQLException, ExamNotFoundException {
        String sql = "DELETE FROM examen WHERE id_examen = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new ExamNotFoundException("Examen con ID " + id + " no encontrado para eliminar");
            }
        }
    }
    
    @Override
    public boolean existsById(Long id) throws SQLException {
        if (id == null) return false;
        
        String sql = "SELECT COUNT(*) FROM examen WHERE id_examen = ?";
        
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
    public List<Exam> findByDriverId(Long driverId) throws SQLException, InvalidExamDataException {
        // Primero validar que el conductor existe
        if (!driverExists(driverId)) {
            throw new InvalidExamDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM examen WHERE id_conductor = ? ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por conductor", e);
        }
        
        return exams;
    }

    @Override
    public List<Exam> findByEntityId(Long entityId) throws SQLException, InvalidExamDataException {
        // Primero validar que la entidad existe
        if (!entityExists(entityId)) {
            throw new InvalidExamDataException("La entidad con ID " + entityId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM examen WHERE id_entidad = ? ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, entityId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por entidad", e);
        }
        
        return exams;
    }

    @Override
    public List<Exam> findByExamType(String examType) throws SQLException, InvalidExamDataException {
        String sql = "SELECT * FROM examen WHERE tipo_examen = ?::tipo_examen_enum ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, examType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por tipo", e);
        }
        
        return exams;
    }

    @Override
    public List<Exam> findByResult(String result) throws SQLException, InvalidExamDataException {
        String sql = "SELECT * FROM examen WHERE resultado = ?::resultado_examen_enum ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, result);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por resultado", e);
        }
        
        return exams;
    }

    @Override
    public List<Exam> findByDriverAndExamType(Long driverId, String examType) throws SQLException, InvalidExamDataException {
        // Primero validar que el conductor existe
        if (!driverExists(driverId)) {
            throw new InvalidExamDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM examen WHERE id_conductor = ? AND tipo_examen = ?::tipo_examen_enum ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            statement.setString(2, examType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por conductor y tipo", e);
        }
        
        return exams;
    }

    @Override
    public List<Exam> findByDriverAndResult(Long driverId, String result) throws SQLException, InvalidExamDataException {
        // Primero validar que el conductor existe
        if (!driverExists(driverId)) {
            throw new InvalidExamDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM examen WHERE id_conductor = ? AND resultado = ?::resultado_examen_enum ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            statement.setString(2, result);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por conductor y resultado", e);
        }
        
        return exams;
    }

    @Override
    public List<Exam> findBetweenDates(String startDate, String endDate) throws SQLException, InvalidExamDataException {
        String sql = "SELECT * FROM examen WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar exámenes por rango de fechas", e);
        }
        
        return exams;
    }

    @Override
    public int countExamsByDriver(Long driverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM examen WHERE id_conductor = ?";
        
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
    public int countExamsByResult(String result) throws SQLException {
        String sql = "SELECT COUNT(*) FROM examen WHERE resultado = ?::resultado_examen_enum";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, result);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }

    @Override
    public int countExamsByType(String examType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM examen WHERE tipo_examen = ?::tipo_examen_enum";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setString(1, examType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }

    @Override
    public boolean isValidExamForEntityType(String examType, String entityType) throws SQLException {
        // Business rule: medical exams only in clinics, theoretical/practical only in driving schools
        if ("medico".equals(examType)) {
            return "clinica".equals(entityType);
        } else if ("teorico".equals(examType) || "practico".equals(examType)) {
            return "autoescuela".equals(entityType);
        }
        return false;
    }

    @Override
    public int countPassedExamsByDriver(Long driverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM examen WHERE id_conductor = ? AND resultado = 'aprobado'::resultado_examen_enum";
        
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
    public int countFailedExamsByDriver(Long driverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM examen WHERE id_conductor = ? AND resultado = 'reprobado'::resultado_examen_enum";
        
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
    public boolean hasPassedAllRequiredExams(Long driverId) throws SQLException {
        // Check if driver has passed all three types of exams
        String sql = "SELECT COUNT(DISTINCT tipo_examen) FROM examen " +
                    "WHERE id_conductor = ? AND resultado = 'aprobado'::resultado_examen_enum";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) >= 3; // Should have passed medical, theoretical, and practical
                }
            }
        }
        
        return false;
    }

    @Override
    public List<Exam> findLatestExamsByDriver(Long driverId, int limit) throws SQLException, InvalidExamDataException {
        // Primero validar que el conductor existe
        if (!driverExists(driverId)) {
            throw new InvalidExamDataException("El conductor con ID " + driverId + " no existe en la base de datos");
        }
        
        String sql = "SELECT * FROM examen WHERE id_conductor = ? ORDER BY fecha DESC LIMIT ?";
        List<Exam> exams = new ArrayList<>();
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, driverId);
            statement.setInt(2, limit);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Exam exam = mapResultSetToExam(resultSet);
                    exams.add(exam);
                }
            }
        } catch (SQLException e) {
            throw new InvalidExamDataException("Error al buscar últimos exámenes del conductor", e);
        }
        
        return exams;
    }

    private Exam mapResultSetToExam(ResultSet resultSet) throws SQLException {
        Long idExam = resultSet.getLong("id_examen");
        String examType = resultSet.getString("tipo_examen");
        
        Date examDate = resultSet.getDate("fecha");
        String dateStr = examDate != null ? examDate.toString() : null;
        
        String result = resultSet.getString("resultado");
        Long entityId = resultSet.getLong("id_entidad");
        Long driverId = resultSet.getLong("id_conductor");
        String examiner = resultSet.getString("examinador");
        
        Exam exam = new Exam(examType, dateStr, result, entityId, driverId, examiner);
        exam.setIdExam(idExam);
        
        return exam;
    }
}