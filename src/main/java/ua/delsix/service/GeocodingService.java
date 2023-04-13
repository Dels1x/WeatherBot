package ua.delsix.service;

import ua.delsix.service.units.GeocodingResult;

import java.util.List;

public interface GeocodingService {
    GeocodingResult getGeocodingResult(String county, String city, int limit);

    List<GeocodingResult> getGeocodingResult(String city, int limit);


}
