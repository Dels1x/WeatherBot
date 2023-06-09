package ua.delsix.service;

import org.json.JSONException;
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
    void getCurrentWeatherOdesa() throws IOException, JSONException {
        String output = weatherService.getWeather("Ukraine", "Odesa");

        System.out.println(output);

    }


    @Test
    void getSunriseTimeOdesa() throws IOException, JSONException {
        String output = weatherService.getSunriseTime("Ukraine", "Odesa");

        System.out.println(output);
    }

    @Test
    void getSunsetTimeOdesa() throws IOException, JSONException {
        String output = weatherService.getSunsetTime("Ukraine", "Odesa");

        System.out.println(output);
    }


}