package ua.delsix.service.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResult {
    private double lat;
    private double lon;
    private String countryCode;
    private String enCityName;
    private String ruCityName;
}
