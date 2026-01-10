package licencia.mapper;

import licencia.dto.LicenseResponseDto;
import licencia.model.License;

public class LicenseMapper {
    public static LicenseResponseDto toLicenseResponseDto(License license) {
        return new LicenseResponseDto(
                license.getId(),
                license.getDriverId(),
                license.getLicenseType(),
                license.getCategory(),
                license.getIssueDate(),
                license.getExpiryDate(),
                license.getPoints(),
                license.getRestrictions(),
                license.getRenewed()
        );
    }
}