package ua.delsix.service;

import ua.delsix.service.units.GeocodingResult;

import java.util.List;
import java.util.Optional;

public interface GeocodingService {
    Optional<GeocodingResult> getGeocodingResult(String county, String city);
}
