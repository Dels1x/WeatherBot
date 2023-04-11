package ua.delsix.service;


public interface WeatherService {
    String getCurrentWeather(String city);
    String getWeatherForecast(String city);
    String getSunriseTime(String city);
    String getSunsetTime(String city);
}
