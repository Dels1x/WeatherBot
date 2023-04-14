package ua.delsix.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
class WeatherServiceTest {
    @Autowired
    private WeatherService weatherService;

    @Test
    void getCurrentWeatherOdesa() throws IOException {
        String output = weatherService.getCurrentWeather("Ukraine", "Odesa");

        System.out.println(output);
    }

    @Test
    void getSunriseTimeOdesa() throws IOException {
        String output = weatherService.getSunriseTime("Ukraine", "Odesa");

        System.out.println(output);
    }

    @Test
    void getSunsetTimeOdesa() throws IOException {
        String output = weatherService.getSunsetTime("Ukraine", "Odesa");

        System.out.println(output);
    }

}