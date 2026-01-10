package examen.dto;

public record ExamResponseDto(
    Long idExam,
    String examType,
    String date,
    String result,
    Long entityId,
    Long driverId,
    String examiner
) {
}