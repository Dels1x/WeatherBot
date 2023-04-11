package ua.delsix.service.impl;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.delsix.service.WeatherService;

@Service
public class WeatherServiceImpl implements WeatherService {
    private static final String ONE_CALL_URL = "https://api.openweathermap.org/data/3.0/onecall?";
    @Value("${weather.api.key}")
    private static String apiKey;
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    public String getCurrentWeather(String city) {
        return null;
    }

    @Override
    public String getWeatherForecast(String city) {
        return null;
    }

    @Override
    public String getSunriseTime(String city) {
        return null;
    }

    @Override
    public String getSunsetTime(String city) {
        return null;
    }
}
