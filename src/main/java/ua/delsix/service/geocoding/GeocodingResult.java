package ua.delsix.service.geocoding;

public class GeocodingResult {
    private double lat;
    private double lon;
    private String enCity;
    private String ruCity;

    public GeocodingResult(double lat, double lon, String enCity, String ruCity) {
        this.lat = lat;
        this.lon = lon;
        this.enCity = enCity;
        this.ruCity = ruCity;
    }

    public GeocodingResult() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getEnCity() {
        return enCity;
    }

    public void setEnCity(String enCity) {
        this.enCity = enCity;
    }

    public String getRuCity() {
        return ruCity;
    }

    public void setRuCity(String ruCity) {
        this.ruCity = ruCity;
    }
}
