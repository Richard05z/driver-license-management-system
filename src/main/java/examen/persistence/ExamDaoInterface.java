package examen.persistence;

import examen.exception.ExamNotFoundException;
import examen.exception.InvalidExamDataException;
import examen.model.Exam;

import java.sql.SQLException;
import java.util.List;

public interface ExamDaoInterface {
    // Basic CRUD operations
    List<Exam> listAllExams() throws InvalidExamDataException, SQLException;
    
    Exam getById(Long id) throws SQLException, ExamNotFoundException;
    
    Exam save(Exam exam) throws InvalidExamDataException, SQLException;
    
    void delete(Long id) throws SQLException, ExamNotFoundException;
    
    Exam update(Exam exam) throws ExamNotFoundException, SQLException, InvalidExamDataException;
    
    boolean existsById(Long id) throws SQLException;
    
    // Exam-specific queries
    List<Exam> findByDriverId(Long driverId) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByEntityId(Long entityId) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByExamType(String examType) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByResult(String result) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByDriverAndExamType(Long driverId, String examType) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByDriverAndResult(Long driverId, String result) throws SQLException, InvalidExamDataException;
    
    List<Exam> findBetweenDates(String startDate, String endDate) throws SQLException, InvalidExamDataException;
    
    int countExamsByDriver(Long driverId) throws SQLException;
    
    int countExamsByResult(String result) throws SQLException;
    
    int countExamsByType(String examType) throws SQLException;
    
    // Validation method for business rule (clinic vs driving school)
    boolean isValidExamForEntityType(String examType, String entityType) throws SQLException;
    
    // Statistics methods
    int countPassedExamsByDriver(Long driverId) throws SQLException;
    
    int countFailedExamsByDriver(Long driverId) throws SQLException;
    
    boolean hasPassedAllRequiredExams(Long driverId) throws SQLException;
    
    // For reporting
    List<Exam> findLatestExamsByDriver(Long driverId, int limit) throws SQLException, InvalidExamDataException;
}