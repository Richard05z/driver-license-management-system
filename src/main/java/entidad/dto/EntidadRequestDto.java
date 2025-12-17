package entidad.dto;

import entidad.vo.TipoEntidad;

public record EntidadRequestDto(
        String nombre,
        TipoEntidad tipoEntidad,
        String direccion,
        String telefono,
        String email,
        String directorGeneral,
        Long idCentro
) {
}
