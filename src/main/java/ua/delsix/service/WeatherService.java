package ua.delsix.service;


import ua.delsix.service.units.Weather;

import java.io.IOException;

public interface WeatherService {
    Weather getCurrentWeather(String country, String city, String units) throws IOException;
    Weather getWeatherForecast(String country, String city) throws IOException;
    Weather getSunriseTime(String country, String city) throws IOException;
    Weather getSunsetTime(String country, String city) throws IOException;

}
