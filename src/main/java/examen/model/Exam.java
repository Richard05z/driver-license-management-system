package examen.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Exam {

    private Long idExam;
    private String examType;       // 'medico', 'teorico', 'practico'
    private String date;           // Format YYYY-MM-DD
    private String result;         // 'aprobado', 'reprobado'
    private Long entityId;         // ID of the entity (clinic or driving school)
    private Long driverId;         // ID of the driver
    private String examiner;       // Name of the examiner

    // Constructor without ID (for creation)
    public Exam(String examType, String date, String result, 
                Long entityId, Long driverId, String examiner) {
        this.examType = examType;
        this.date = date;
        this.result = result;
        this.entityId = entityId;
        this.driverId = driverId;
        this.examiner = examiner;
    }
}