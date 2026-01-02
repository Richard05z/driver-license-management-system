package conductor.mapper;

import conductor.dto.DriverResponseDto;
import conductor.model.Conductor;

public class DriverMapper {
    public static DriverResponseDto toDriverResponseDto(Conductor conductor){
        return new DriverResponseDto(
                conductor.getId(),
                conductor.getFirstName(),
                conductor.getLastName(),
                conductor.getIdDocument(),
                conductor.getBirthDate(),
                conductor.getAddress(),
                conductor.getPhone(),
                conductor.getEmail(),
                conductor.getLicenseStatus()
        );
    }
}