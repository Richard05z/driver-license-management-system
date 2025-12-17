package conductor.dto;

public record DriverResponseDto(
    Integer id,
    String firstName,
    String lastName,
    String idDocument,
    String birthDate,
    String address,
    String phone,
    String email,
    String licenseStatus
) {
}