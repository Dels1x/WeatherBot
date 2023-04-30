package ua.delsix.service;


import org.json.JSONException;
import ua.delsix.service.impl.Forecast;

import java.io.IOException;

public interface WeatherService {
    String getWeather(String country, String city) throws IOException, JSONException;
    Forecast getWeatherForecast(String country, String city) throws IOException, JSONException;
    String getSunriseTime(String country, String city) throws IOException, JSONException;
    String getSunsetTime(String country, String city) throws IOException, JSONException;
}
