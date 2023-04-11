package ua.delsix.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.delsix.service.geocoding.GeocodingResult;
import ua.delsix.service.impl.GeocodingServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GeocodingServiceTest {
    private GeocodingService geocodingService;

    @BeforeEach
    public void setUp() {
        geocodingService = new GeocodingServiceImpl();
    }

    @Test
    void getGeocodingResult() throws IOException {
        GeocodingResult geocodingResult = geocodingService.getGeocodingResult("Ukraine", "Odesa", 5);
        System.out.println(geocodingResult);
    }
}