package examen.service;

import examen.exception.ExamNotFoundException;
import examen.exception.InvalidExamDataException;
import examen.model.Exam;

import java.sql.SQLException;
import java.util.List;

public interface ExamServiceInterface {
    // Basic CRUD operations
    List<Exam> listAllExams() throws InvalidExamDataException, SQLException;
    
    Exam getById(Long id) throws SQLException, ExamNotFoundException;
    
    Exam save(Exam exam) throws InvalidExamDataException, SQLException;
    
    void delete(Long id) throws SQLException, ExamNotFoundException;
    
    Exam update(Exam exam) throws ExamNotFoundException, SQLException, InvalidExamDataException;
    
    boolean existsById(Long id) throws SQLException, ExamNotFoundException;
    
    // Exam-specific queries
    List<Exam> findByDriverId(Long driverId) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByEntityId(Long entityId) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByExamType(String examType) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByResult(String result) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByDriverAndExamType(Long driverId, String examType) throws SQLException, InvalidExamDataException;
    
    List<Exam> findByDriverAndResult(Long driverId, String result) throws SQLException, InvalidExamDataException;
    
    List<Exam> findBetweenDates(String startDate, String endDate) throws SQLException, InvalidExamDataException;
    
    // Statistics methods
    int countExamsByDriver(Long driverId) throws SQLException;
    
    int countExamsByResult(String result) throws SQLException;
    
    int countExamsByType(String examType) throws SQLException;
    
    int countPassedExamsByDriver(Long driverId) throws SQLException;
    
    int countFailedExamsByDriver(Long driverId) throws SQLException;
    
    boolean hasPassedAllRequiredExams(Long driverId) throws SQLException;
    
    // Business logic methods
    boolean isValidExamForEntityType(String examType, String entityType) throws SQLException, InvalidExamDataException;
    
    boolean canTakeExam(Long driverId, String examType) throws SQLException, InvalidExamDataException;
    
    List<Exam> getExamHistory(Long driverId) throws SQLException, InvalidExamDataException;
    
    // Additional methods for reporting and analysis
    List<Exam> findLatestExamsByDriver(Long driverId, int limit) throws SQLException, InvalidExamDataException;
    
    double getPassRateByExamType(String examType) throws SQLException, InvalidExamDataException;
    
    double getPassRateByEntity(Long entityId) throws SQLException, InvalidExamDataException;
    
    // Validation methods
    void validateExamData(Exam exam) throws InvalidExamDataException, SQLException;
    
    void checkExamConstraints(Exam exam) throws InvalidExamDataException, SQLException;
}