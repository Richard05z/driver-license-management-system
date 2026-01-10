package centro.mapper;

import centro.dto.CentroResponseDto;
import centro.model.Centro;

public class CentroMapper {
    public static CentroResponseDto toCentroResponseDto(Centro centro) {
        return new CentroResponseDto(
            centro.getIdCentro(),
            centro.getNombre(),
            centro.getCodigo(),
            centro.getDireccionPostal(),
            centro.getTelefono(),
            centro.getEmail(),
            centro.getDirectorGeneral(),
            centro.getJefeRRHH(),
            centro.getJefeContabilidad(),
            centro.getSecretarioSindicato(),
            centro.getLogo()
        );
    }
}