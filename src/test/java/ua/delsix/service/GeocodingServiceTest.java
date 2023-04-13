package ua.delsix.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.delsix.service.units.GeocodingResult;

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

        GeocodingResult geocodingResult = geocodingService.getGeocodingResult("Ukraine", "Odesa", 5);
        assertEquals(expectedResult, geocodingResult);
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

        GeocodingResult geocodingResult = geocodingService.getGeocodingResult("USA", "Dallas", 5);
        System.out.println(geocodingResult);

        assertEquals(expectedResult, geocodingResult);
    }

    @Test
    void getGeocodingResultAmsterdam() {
        GeocodingResult expectedResult = new GeocodingResult(
                52.37454030000001,
                4.897975505617977,
                "NL",
                "Amsterdam",
                "Амстердам"
        );

        GeocodingResult geocodingResult = geocodingService.getGeocodingResult("Netherlands", "Amsterdam", 5);
        System.out.println(geocodingResult);

        assertEquals(expectedResult, geocodingResult);
    }

}