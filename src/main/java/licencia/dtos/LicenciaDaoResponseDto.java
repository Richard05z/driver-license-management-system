package licencia.dtos;

import java.util.Date;

public record LicenciaDaoResponseDto(
        Long idLicencia,
        Long idConductor,
        String tipoLicencia,
        String categoriaLicencia,
        Date fechaEmision,
        Date fechaVencimiento,
        String restricciones,
        boolean renovada,
        int puntos
) {
}
