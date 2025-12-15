package centro.dto;

public record CentroResponseDto(
         String nombre,
         String direccionPostal,
         String codigo,
         String telefono,
         String email,
         String directorGeneral,
         String jefeRRHH,
         String jefeContabilidad,
         String secretarioSindicato,
         String logo
) {
}
