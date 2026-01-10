package examen.controller;

import examen.dto.ExamResponseDto;
import examen.exception.ExamNotFoundException;
import examen.exception.InvalidExamDataException;
import examen.mapper.ExamMapper;
import examen.model.Exam;
import examen.service.ExamServiceInterface;
import examen.validator.ExamValidator;

import java.sql.SQLException;
import java.util.List;

public class ExamController {
    private final ExamServiceInterface examService;

    public ExamController(ExamServiceInterface examService) {
        this.examService = examService;
    }

    public Exam addExam(Exam exam) throws InvalidExamDataException, SQLException {
        validateExamNotNull(exam);
        ExamValidator.validate(exam);
        return this.examService.save(exam);
    }

    public List<ExamResponseDto> getAllExams() throws InvalidExamDataException, SQLException {
        return this.examService.listAllExams().stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public ExamResponseDto getExamResponseById(Long id) throws InvalidExamDataException, SQLException, ExamNotFoundException {
        validateIdNotNull(id);
        return ExamMapper.toExamResponseDto(this.examService.getById(id));
    }

    public List<ExamResponseDto> getExamsByDriverId(Long driverId) throws InvalidExamDataException, SQLException {
        validateIdNotNull(driverId);
        return this.examService.findByDriverId(driverId).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getExamsByEntityId(Long entityId) throws InvalidExamDataException, SQLException {
        validateIdNotNull(entityId);
        return this.examService.findByEntityId(entityId).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getExamsByType(String examType) throws InvalidExamDataException, SQLException {
        validateTextNotNull(examType, "Exam type");
        ExamValidator.validateExamType(examType);
        return this.examService.findByExamType(examType).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getExamsByResult(String result) throws InvalidExamDataException, SQLException {
        validateTextNotNull(result, "Exam result");
        ExamValidator.validateResult(result);
        return this.examService.findByResult(result).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getExamsByDriverAndExamType(Long driverId, String examType) 
            throws InvalidExamDataException, SQLException {
        validateIdNotNull(driverId);
        validateTextNotNull(examType, "Exam type");
        ExamValidator.validateExamType(examType);
        return this.examService.findByDriverAndExamType(driverId, examType).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getExamsByDriverAndResult(Long driverId, String result) 
            throws InvalidExamDataException, SQLException {
        validateIdNotNull(driverId);
        validateTextNotNull(result, "Exam result");
        ExamValidator.validateResult(result);
        return this.examService.findByDriverAndResult(driverId, result).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getExamsBetweenDates(String startDate, String endDate) 
            throws InvalidExamDataException, SQLException {
        validateTextNotNull(startDate, "Start date");
        validateTextNotNull(endDate, "End date");
        ExamValidator.validateDateRange(startDate, endDate);
        return this.examService.findBetweenDates(startDate, endDate).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public Exam updateExam(Exam exam) throws InvalidExamDataException, SQLException, ExamNotFoundException {
        validateExamNotNull(exam);
        validateIdNotNull(exam.getIdExam());
        return this.examService.update(exam);
    }

    public void deleteExam(Long id) throws InvalidExamDataException, SQLException, ExamNotFoundException {
        validateIdNotNull(id);
        examService.delete(id);
    }

    public boolean checkExamExistsById(Long id) throws SQLException, ExamNotFoundException, InvalidExamDataException {
        validateIdNotNull(id);
        return this.examService.existsById(id);
    }

    public int countExamsByDriver(Long driverId) throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        return this.examService.countExamsByDriver(driverId);
    }

    public int countExamsByResult(String result) throws SQLException, InvalidExamDataException {
        validateTextNotNull(result, "Exam result");
        ExamValidator.validateResult(result);
        return this.examService.countExamsByResult(result);
    }

    public int countExamsByType(String examType) throws SQLException, InvalidExamDataException {
        validateTextNotNull(examType, "Exam type");
        ExamValidator.validateExamType(examType);
        return this.examService.countExamsByType(examType);
    }

    public int countPassedExamsByDriver(Long driverId) throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        return this.examService.countPassedExamsByDriver(driverId);
    }

    public int countFailedExamsByDriver(Long driverId) throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        return this.examService.countFailedExamsByDriver(driverId);
    }

    public boolean hasPassedAllRequiredExams(Long driverId) throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        return this.examService.hasPassedAllRequiredExams(driverId);
    }

    public boolean isValidExamForEntityType(String examType, String entityType) 
            throws SQLException, InvalidExamDataException {
        validateTextNotNull(examType, "Exam type");
        validateTextNotNull(entityType, "Entity type");
        return this.examService.isValidExamForEntityType(examType, entityType);
    }

    public boolean canTakeExam(Long driverId, String examType) 
            throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        validateTextNotNull(examType, "Exam type");
        return this.examService.canTakeExam(driverId, examType);
    }

    public List<ExamResponseDto> getExamHistory(Long driverId) 
            throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        return this.examService.getExamHistory(driverId).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public List<ExamResponseDto> getLatestExamsByDriver(Long driverId, int limit) 
            throws SQLException, InvalidExamDataException {
        validateIdNotNull(driverId);
        if (limit <= 0) {
            throw new InvalidExamDataException("Limit must be greater than 0");
        }
        return this.examService.findLatestExamsByDriver(driverId, limit).stream()
                .map(ExamMapper::toExamResponseDto)
                .toList();
    }

    public double getPassRateByExamType(String examType) throws SQLException, InvalidExamDataException {
        validateTextNotNull(examType, "Exam type");
        ExamValidator.validateExamType(examType);
        return this.examService.getPassRateByExamType(examType);
    }

    public double getPassRateByEntity(Long entityId) throws SQLException, InvalidExamDataException {
        validateIdNotNull(entityId);
        return this.examService.getPassRateByEntity(entityId);
    }

    public void validateExamData(Exam exam) throws InvalidExamDataException, SQLException {
        validateExamNotNull(exam);
        this.examService.validateExamData(exam);
    }

    public void checkExamConstraints(Exam exam) throws InvalidExamDataException, SQLException {
        validateExamNotNull(exam);
        this.examService.checkExamConstraints(exam);
    }

    // Métodos auxiliares de validación
    private void validateExamNotNull(Exam exam) throws InvalidExamDataException {
        if (exam == null) {
            throw new InvalidExamDataException("Exam cannot be null");
        }
    }

    private void validateIdNotNull(Long id) throws InvalidExamDataException {
        if (id == null) {
            throw new InvalidExamDataException("ID cannot be null");
        }
    }

    private void validateTextNotNull(String text, String fieldName) throws InvalidExamDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidExamDataException(fieldName + " cannot be null or empty");
        }
    }
}