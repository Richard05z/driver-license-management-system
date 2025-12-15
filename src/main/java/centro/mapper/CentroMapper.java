package centro.mapper;

import centro.dto.CentroResponseDto;
import centro.model.Centro;

public class CentroMapper {
    public static CentroResponseDto toCentroResponseDto(Centro centro){
        return new CentroResponseDto(
                centro.getNombre(),
                centro.getDireccionPostal(),
                centro.getTelefono(),
                centro.getEmail(),
                centro.getCodigo(),
                centro.getDirectorGeneral(),
                centro.getJefeRRHH(),
                centro.getJefeContabilidad(),
                centro.getSecretarioSindicato(),
                centro.getLogo()
        );
    }
}
