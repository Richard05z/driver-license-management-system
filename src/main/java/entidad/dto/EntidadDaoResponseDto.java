package entidad.dto;

public record EntidadDaoResponseDto(
        Long idEntidad,
        String nombre,
        String tipoEntidad,
        String direccion,
        String telefono,
        String email,
        String directorGeneral,
        Long idCentro
) {
}
