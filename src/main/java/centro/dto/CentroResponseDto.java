package centro.dto;

public record CentroResponseDto(
    Long idCentro,
    String nombre,
    String codigo,
    String direccionPostal,
    String telefono,
    String email,
    String directorGeneral,
    String jefeRRHH,
    String jefeContabilidad,
    String secretarioSindicato,
    String logo
) {}