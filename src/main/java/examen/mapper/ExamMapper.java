package examen.mapper;

import examen.dto.ExamResponseDto;
import examen.model.Exam;

public class ExamMapper {
    public static ExamResponseDto toExamResponseDto(Exam exam) {
        return new ExamResponseDto(
            exam.getIdExam(),
            exam.getExamType(),
            exam.getDate(),
            exam.getResult(),
            exam.getEntityId(),
            exam.getDriverId(),
            exam.getExaminer()
        );
    }
}