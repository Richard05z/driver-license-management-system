package licencia.dtos;

import licencia.vo.CategoriaLicencia;
import licencia.vo.TipoLicencia;

import java.util.Date;

public record LicenciaRequestDto(
        TipoLicencia tipoLicencia,
        CategoriaLicencia categoriaLicencia,
        Date fechaEmision,
        Date fechaVencimiento,
        String restricciones,
        boolean renovada,
        Long idConductor,
        int puntos
) {
}
