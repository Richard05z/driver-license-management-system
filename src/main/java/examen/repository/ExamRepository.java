package examen.repository;

import examen.exception.ExamNotFoundException;
import examen.exception.InvalidExamDataException;
import examen.model.Exam;
import examen.persistence.ExamDao;

import java.sql.SQLException;
import java.util.List;

public class ExamRepository implements ExamRepositoryInterface {

    private final ExamDao examDao;

    public ExamRepository(ExamDao examDao) throws SQLException {
        this.examDao = examDao;
    }

    @Override
    public List<Exam> listAllExams() throws InvalidExamDataException, SQLException {
        return this.examDao.listAllExams();
    }

    @Override
    public Exam getById(Long id) throws SQLException, ExamNotFoundException {
        return this.examDao.getById(id);
    }

    @Override
    public Exam save(Exam exam) throws InvalidExamDataException, SQLException {
        return this.examDao.save(exam);
    }

    @Override
    public void delete(Long id) throws SQLException, ExamNotFoundException {
        this.examDao.delete(id);
    }

    @Override
    public Exam update(Exam exam) throws ExamNotFoundException, SQLException {
        return this.examDao.update(exam);
    }

    @Override
    public boolean existsById(Long id) throws SQLException {
        return this.examDao.existsById(id);
    }

    @Override
    public List<Exam> findByDriverId(Long driverId) throws SQLException, InvalidExamDataException {
        return this.examDao.findByDriverId(driverId);
    }

    @Override
    public List<Exam> findByEntityId(Long entityId) throws SQLException, InvalidExamDataException {
        return this.examDao.findByEntityId(entityId);
    }

    @Override
    public List<Exam> findByExamType(String examType) throws SQLException, InvalidExamDataException {
        return this.examDao.findByExamType(examType);
    }

    @Override
    public List<Exam> findByResult(String result) throws SQLException, InvalidExamDataException {
        return this.examDao.findByResult(result);
    }

    @Override
    public List<Exam> findByDriverAndExamType(Long driverId, String examType) throws SQLException, InvalidExamDataException {
        return this.examDao.findByDriverAndExamType(driverId, examType);
    }

    @Override
    public List<Exam> findByDriverAndResult(Long driverId, String result) throws SQLException, InvalidExamDataException {
        return this.examDao.findByDriverAndResult(driverId, result);
    }

    @Override
    public List<Exam> findBetweenDates(String startDate, String endDate) throws SQLException, InvalidExamDataException {
        return this.examDao.findBetweenDates(startDate, endDate);
    }

    @Override
    public int countExamsByDriver(Long driverId) throws SQLException {
        return this.examDao.countExamsByDriver(driverId);
    }

    @Override
    public int countExamsByResult(String result) throws SQLException {
        return this.examDao.countExamsByResult(result);
    }

    @Override
    public int countExamsByType(String examType) throws SQLException {
        return this.examDao.countExamsByType(examType);
    }

    @Override
    public boolean isValidExamForEntityType(String examType, String entityType) throws SQLException {
        return this.examDao.isValidExamForEntityType(examType, entityType);
    }

    @Override
    public int countPassedExamsByDriver(Long driverId) throws SQLException {
        return this.examDao.countPassedExamsByDriver(driverId);
    }

    @Override
    public int countFailedExamsByDriver(Long driverId) throws SQLException {
        return this.examDao.countFailedExamsByDriver(driverId);
    }

    @Override
    public boolean hasPassedAllRequiredExams(Long driverId) throws SQLException {
        return this.examDao.hasPassedAllRequiredExams(driverId);
    }

    @Override
    public List<Exam> findLatestExamsByDriver(Long driverId, int limit) throws SQLException, InvalidExamDataException {
        return this.examDao.findLatestExamsByDriver(driverId, limit);
    }

    @Override
    public boolean canTakeExam(Long driverId, String examType) throws SQLException, InvalidExamDataException {
        // Check if driver can take this type of exam
        // For example: cannot retake passed exam, or must pass medical before theoretical, etc.
        List<Exam> previousExams = this.examDao.findByDriverAndExamType(driverId, examType);
        
        // Check if driver has already passed this type of exam
        for (Exam exam : previousExams) {
            if ("aprobado".equals(exam.getResult())) {
                return false; // Already passed this exam type
            }
        }
        
        return true;
    }

    @Override
    public List<Exam> getExamHistory(Long driverId) throws SQLException, InvalidExamDataException {
        // Return all exams for a driver sorted by date
        return this.examDao.findByDriverId(driverId);
    }
}