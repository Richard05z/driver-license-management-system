package examen.service;

import examen.exception.ExamNotFoundException;
import examen.exception.InvalidExamDataException;
import examen.model.Exam;
import examen.repository.ExamRepositoryInterface;
import examen.validator.ExamValidator;

import java.sql.SQLException;
import java.util.List;

public class ExamService implements ExamServiceInterface {

    private final ExamRepositoryInterface examRepository;

    public ExamService(ExamRepositoryInterface examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public List<Exam> listAllExams() throws InvalidExamDataException, SQLException {
        return this.examRepository.listAllExams();
    }

    @Override
    public Exam getById(Long id) throws SQLException, ExamNotFoundException {
        return this.examRepository.getById(id);
    }

    @Override
    public Exam save(Exam exam) throws InvalidExamDataException, SQLException {
        validateExamData(exam);
        checkExamConstraints(exam);
        return this.examRepository.save(exam);
    }

    @Override
    public void delete(Long id) throws SQLException, ExamNotFoundException {
        this.existsById(id);
        this.examRepository.delete(id);
    }

    @Override
    public Exam update(Exam exam) throws ExamNotFoundException, SQLException, InvalidExamDataException {
        this.existsById(exam.getIdExam());
        validateExamData(exam);
        checkExamConstraints(exam);
        return this.examRepository.update(exam);
    }

    @Override
    public boolean existsById(Long id) throws SQLException, ExamNotFoundException {
        if (!this.examRepository.existsById(id)) {
            throw new ExamNotFoundException("Examen con ID " + id + " no fue encontrado");
        }
        return true;
    }

    @Override
    public List<Exam> findByDriverId(Long driverId) throws SQLException, InvalidExamDataException {
        return this.examRepository.findByDriverId(driverId);
    }

    @Override
    public List<Exam> findByEntityId(Long entityId) throws SQLException, InvalidExamDataException {
        return this.examRepository.findByEntityId(entityId);
    }

    @Override
    public List<Exam> findByExamType(String examType) throws SQLException, InvalidExamDataException {
        ExamValidator.validateExamType(examType);
        return this.examRepository.findByExamType(examType);
    }

    @Override
    public List<Exam> findByResult(String result) throws SQLException, InvalidExamDataException {
        ExamValidator.validateResult(result);
        return this.examRepository.findByResult(result);
    }

    @Override
    public List<Exam> findByDriverAndExamType(Long driverId, String examType) throws SQLException, InvalidExamDataException {
        ExamValidator.validateExamType(examType);
        return this.examRepository.findByDriverAndExamType(driverId, examType);
    }

    @Override
    public List<Exam> findByDriverAndResult(Long driverId, String result) throws SQLException, InvalidExamDataException {
        ExamValidator.validateResult(result);
        return this.examRepository.findByDriverAndResult(driverId, result);
    }

    @Override
    public List<Exam> findBetweenDates(String startDate, String endDate) throws SQLException, InvalidExamDataException {
        ExamValidator.validateDate(startDate);
        ExamValidator.validateDate(endDate);
        return this.examRepository.findBetweenDates(startDate, endDate);
    }

    @Override
    public int countExamsByDriver(Long driverId) throws SQLException {
        return this.examRepository.countExamsByDriver(driverId);
    }

    @Override
    public int countExamsByResult(String result) throws SQLException {
        return this.examRepository.countExamsByResult(result);
    }

    @Override
    public int countExamsByType(String examType) throws SQLException {
        return this.examRepository.countExamsByType(examType);
    }

    @Override
    public int countPassedExamsByDriver(Long driverId) throws SQLException {
        return this.examRepository.countPassedExamsByDriver(driverId);
    }

    @Override
    public int countFailedExamsByDriver(Long driverId) throws SQLException {
        return this.examRepository.countFailedExamsByDriver(driverId);
    }

    @Override
    public boolean hasPassedAllRequiredExams(Long driverId) throws SQLException {
        return this.examRepository.hasPassedAllRequiredExams(driverId);
    }

    @Override
    public boolean isValidExamForEntityType(String examType, String entityType) throws SQLException, InvalidExamDataException {
        ExamValidator.validateExamType(examType);
        ExamValidator.validateEntityType(entityType);
        return this.examRepository.isValidExamForEntityType(examType, entityType);
    }

    @Override
    public boolean canTakeExam(Long driverId, String examType) throws SQLException, InvalidExamDataException {
        ExamValidator.validateExamType(examType);
        return this.examRepository.canTakeExam(driverId, examType);
    }

    @Override
    public List<Exam> getExamHistory(Long driverId) throws SQLException, InvalidExamDataException {
        return this.examRepository.getExamHistory(driverId);
    }

    @Override
    public List<Exam> findLatestExamsByDriver(Long driverId, int limit) throws SQLException, InvalidExamDataException {
        if (limit <= 0) {
            throw new InvalidExamDataException("El límite debe ser mayor que 0");
        }
        return this.examRepository.findLatestExamsByDriver(driverId, limit);
    }

    @Override
    public double getPassRateByExamType(String examType) throws SQLException, InvalidExamDataException {
        ExamValidator.validateExamType(examType);
        int totalExams = this.examRepository.countExamsByType(examType);
        int passedExams = this.examRepository.countExamsByResult("aprobado");
        
        if (totalExams == 0) {
            return 0.0;
        }
        
        return (double) passedExams / totalExams * 100;
    }

    @Override
    public double getPassRateByEntity(Long entityId) throws SQLException, InvalidExamDataException {
        List<Exam> entityExams = this.examRepository.findByEntityId(entityId);
        if (entityExams.isEmpty()) {
            return 0.0;
        }
        
        long passedExams = entityExams.stream()
            .filter(exam -> "aprobado".equals(exam.getResult()))
            .count();
            
        return (double) passedExams / entityExams.size() * 100;
    }

    @Override
    public void validateExamData(Exam exam) throws InvalidExamDataException, SQLException {
        ExamValidator.validate(exam);
    }

    @Override
    public void checkExamConstraints(Exam exam) throws InvalidExamDataException, SQLException {
        // Check if driver exists (would need driver service or repository)
        // Check if entity exists (would need entity service or repository)
        
        // Check business rule: exam type must match entity type
        // This would require additional methods to get entity type by ID
        // For now, we'll trust the database trigger
        
        // Check if driver can take this exam
        if (!this.examRepository.canTakeExam(exam.getDriverId(), exam.getExamType())) {
            throw new InvalidExamDataException("El conductor no puede tomar este tipo de examen");
        }
    }
}