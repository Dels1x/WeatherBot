package ua.delsix.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.delsix.service.units.GeocodingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
class GeocodingServiceTest {
    @Autowired
    private GeocodingService geocodingService;

    @Test
    void getGeocodingResultOdesa() {
        GeocodingResult expectedResult = new GeocodingResult(
                46.4843023,
                30.7322878,
                "UA",
        "Odesa",
        "Одесса"
        );

        Optional<GeocodingResult> geocodingResult = geocodingService.getGeocodingResult("Ukraine", "Odesa");
        assertEquals(expectedResult, geocodingResult.get());
    }

    @Test
    void getGeocodingResultDallas() {
        GeocodingResult expectedResult = new GeocodingResult(
                32.7762719,
                -96.7968559,
                "US",
                "Dallas",
                "Даллас"
        );

        Optional<GeocodingResult> geocodingResult = geocodingService.getGeocodingResult("USA", "Dallas");
        System.out.println(geocodingResult);

        assertEquals(expectedResult, geocodingResult.get());
    }

    @Test
    void getGeocodingResultAmsterdam() {
        GeocodingResult expectedResult = new GeocodingResult(
                52.3727598,
                4.8936041,
                "NL",
                "Amsterdam",
                "Амстердам"
        );

        Optional<GeocodingResult> geocodingResult = geocodingService.getGeocodingResult("Netherlands", "Amsterdam");
        System.out.println(geocodingResult);

        assertEquals(expectedResult, geocodingResult.get());
    }

}