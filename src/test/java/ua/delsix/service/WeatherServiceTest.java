package ua.delsix.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.delsix.service.units.Weather;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
class WeatherServiceTest {
    @Autowired
    private WeatherService weatherService;

    @Test
    void getCurrentWeatherOdesa() throws IOException {
        // how do I make test to something that is not static and changes over time lol
        Weather output = weatherService.getCurrentWeather("Ukraine", "Odesa", "metric");

        System.out.println(output);
    }

}