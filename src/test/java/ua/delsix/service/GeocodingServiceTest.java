package ua.delsix.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.delsix.service.geocoding.GeocodingResult;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
class GeocodingServiceTest {
    @Autowired
    private GeocodingService geocodingService;

    @Test
    void getGeocodingResult() throws IOException {
        GeocodingResult expectedResult = new GeocodingResult(
                46.4843023,
                30.7322878,
                "UA",
        "Odesa",
        "Одесса"
        );

        GeocodingResult geocodingResult = geocodingService.getGeocodingResult("Ukraine", "Odesa", 5);
        System.out.println(geocodingResult);

        assertEquals(expectedResult, geocodingResult);
    }

    @Test
    void getGeocodingResultOtherLanguage() throws IOException {
        GeocodingResult expectedResult = new GeocodingResult(
                46.4843023,
                30.7322878,
                "UA",
                "Odesa",
                "Одесса"
        );

        GeocodingResult geocodingResult = geocodingService.getGeocodingResult("Украина", "Одесса", 5);
        System.out.println(geocodingResult);

        assertEquals(expectedResult, geocodingResult);
    }
}