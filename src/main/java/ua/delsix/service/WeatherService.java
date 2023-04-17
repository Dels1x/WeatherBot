package ua.delsix.service;


import java.io.IOException;

public interface WeatherService {
    String getWeather(String country, String city) throws IOException;
    String getWeatherForecast(String country, String city) throws IOException;
    String getSunriseTime(String country, String city) throws IOException;
    String getSunsetTime(String country, String city) throws IOException;
}
