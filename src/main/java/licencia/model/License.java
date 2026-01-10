package licencia.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class License {

    private Long id;
    private Long driverId;          // Foreign key to Driver
    private String licenseType;     // A, B, C, D, E, F
    private String category;        // camion, moto, automovil, autobus
    private String issueDate;
    private String expiryDate;
    private Integer points;         // 0-20 points
    private String restrictions;    // Any restrictions (glasses, automatic only, etc.)
    private Boolean renewed;        // If the license has been renewed

    public License(Long driverId, String licenseType, String category, String issueDate, 
                   String expiryDate, Integer points, String restrictions, Boolean renewed) {
        this.driverId = driverId;
        this.licenseType = licenseType;
        this.category = category;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.points = points;
        this.restrictions = restrictions;
        this.renewed = renewed;
    }
}