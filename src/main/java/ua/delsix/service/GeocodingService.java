package ua.delsix.service;

import ua.delsix.service.geocoding.GeocodingResult;

import java.io.IOException;

public interface GeocodingService {
    GeocodingResult getGeocodingResult(String county, String city, int limit) throws IOException;

    GeocodingResult getGeocodingResult(String city, int limit);


}
