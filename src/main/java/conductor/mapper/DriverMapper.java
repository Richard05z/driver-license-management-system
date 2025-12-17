package conductor.mapper;

import conductor.dto.DriverResponseDto;
import conductor.model.Driver;

public class DriverMapper {
    public static DriverResponseDto toDriverResponseDto(Driver driver){
        return new DriverResponseDto(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getIdDocument(),
                driver.getBirthDate(),
                driver.getAddress(),
                driver.getPhone(),
                driver.getEmail(),
                driver.getLicenseStatus()
        );
    }
}