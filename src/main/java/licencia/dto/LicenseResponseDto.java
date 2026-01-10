package licencia.dto;

public record LicenseResponseDto(
    Long id,
    Long driverId,
    String licenseType,
    String category,
    String issueDate,
    String expiryDate,
    Integer points,
    String restrictions,
    Boolean renewed
) {
}