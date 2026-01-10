package conductor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Driver {

    private Long id;
    private String firstName;
    private String lastName;
    private String idDocument;
    private String birthDate;
    private String address;
    private String phone;
    private String email;
    private String licenseStatus;

    public Driver(String firstName, String lastName, String idDocument, String birthDate, String address, String phone, String email, String licenseStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idDocument = idDocument;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.licenseStatus = licenseStatus;
    }
}