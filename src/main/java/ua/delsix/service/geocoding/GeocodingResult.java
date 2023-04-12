package ua.delsix.service.geocoding;

import lombok.Data;
@Data
public class GeocodingResult {
    private double lat;
    private double lon;
    private String countryCode;
    private String enCityName;
    private String ruCityName;

    public GeocodingResult(double lat, double lon, String countryCode, String enCityName, String ruCityName) {
        this.lat = lat;
        this.lon = lon;
        this.countryCode = countryCode;
        this.enCityName = enCityName;
        this.ruCityName = ruCityName;
    }

    public GeocodingResult() {
    }
}
