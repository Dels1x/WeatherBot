package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.delsix.exceptions.ApiException;
import ua.delsix.service.GeocodingService;
import ua.delsix.service.WeatherService;
import ua.delsix.service.units.GeocodingResult;
import ua.delsix.service.units.Weather;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.*;

@Log4j
@Service
public class WeatherServiceImpl implements WeatherService {
    private final GeocodingService geocodingService;
    private static final String CURRENT_WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";
    @Value("${weather.api.key}")
    private String apiKey;
    private static final OkHttpClient client = new OkHttpClient();

    public WeatherServiceImpl(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @Override
    public String getWeather(String country, String city) throws IOException {
        Weather weather = getWeatherData(country, city);
        assert weather != null;
        GeocodingResult geocodingResult = weather.getGeocodingResult();

        String output = String.format(
                "Weather in: %s, %s\n\n" +
                "Weather: %s: %s\n" +
                "Temperature: %.2f (feels like %.2f)\n" +
                "Wind: %.2fm/s\n" +
                "Humidity: %d",
                geocodingResult.getCountryCode(), geocodingResult.getEnCityName(),
                weather.getWeatherName(), weather.getWeatherDesc(),
                weather.getRealTemp(), weather.getRealTemp(),
                weather.getWindSpeed(),
                weather.getHumidity());

        log.trace(String.format("WeatherServiceImpl:%d - Output:\n%s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                output));

        return output;
    }

    @Override
    public String getWeatherForecast(String country, String city) throws IOException {
        Map<Integer, Weather> forecast = getForecast(country, city);
        assert forecast != null;
        Set<Integer> keys = forecast.keySet();
        String output = "";

        System.out.println(keys);
        return forecast.toString();
    }

    @Override
    public String getSunriseTime(String country, String city) throws IOException {
        Weather weather = getWeatherData(country, city);

        assert weather != null;
        LocalDateTime sunriseTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(weather.getSunriseTimestamp()),
                ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String sunriseString = sunriseTime.format(formatter);
        String output = "The time of sunrise is: ".concat(sunriseString);

        log.trace(String.format("WeatherServiceImpl:%d - Output:\n%s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                output));

        return output;
    }

    @Override
    public String getSunsetTime(String country, String city) throws IOException {
        Weather weather = getWeatherData(country, city);

        assert weather != null;
        LocalDateTime sunsetTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(weather.getSunsetTimestamp()),
                ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String sunriseString = sunsetTime.format(formatter);
        String output = "The time of sunrise is: ".concat(sunriseString);

        log.trace(String.format("WeatherServiceImpl:%d - Output:\n%s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                output));

        return output;
    }

    private Weather getWeatherData(String country, String city) throws IOException {
        Weather weather;
        GeocodingResult geocodingResult = geocodingService.getGeocodingResult(country, city)
                .orElse(null);

        if (geocodingResult == null) {
            log.debug("Could not find coords to: " + country + " " + city);
            return null;
        }

        log.trace(String.format(
                "WeatherServiceImpl:%d - API call:%s?lat=%f&lon=%f&units=%s&appid=%s",
                        Thread.currentThread().getStackTrace()[1].getLineNumber(),
                        CURRENT_WEATHER_URL,
                        geocodingResult.getLat(),
                        geocodingResult.getLon(),
                        "metric",
                        apiKey));

        Request request = new Request.Builder()
                .url(String.format("%s?lat=%f&lon=%f&units=%s&appid=%s",
                        CURRENT_WEATHER_URL,
                        geocodingResult.getLat(),
                        geocodingResult.getLon(),
                        "metric",
                        apiKey))
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.code() == 200) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            log.trace(jsonObject.toString());

            weather = getWeatherDataFromJSON(jsonObject, geocodingResult);
            weather.setSunriseTimestamp(jsonObject.getJSONObject("sys").getLong("sunrise"));
            weather.setSunsetTimestamp(jsonObject.getJSONObject("sys").getLong("sunset"));

        } else {
            log.error(String.format("WeatherServiceImpl:%d - Response code: %d",
                    Thread.currentThread().getStackTrace()[1].getLineNumber(),
                    response.code()));
            throw new ApiException("API returned non-200 status code", response.code());
        }

        log.trace(String.format("WeatherServiceImpl:%d - Weather object: %s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                weather));

        return weather;
    }

    private Map<Integer, Weather> getForecast(String country, String city) throws IOException {
        Map<Integer, Weather> forecast = new TreeMap<>();
        GeocodingResult geocodingResult = geocodingService.getGeocodingResult(country, city)
                .orElse(null);

        if (geocodingResult == null) {
            log.debug("Could not find coords to: " + country + " " + city);
            return null;
        }

        log.trace(String.format(
                "WeatherServiceImpl:%d - API call:%s?lat=%f&lon=%f&units=%s&appid=%s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                FORECAST_URL,
                geocodingResult.getLat(),
                geocodingResult.getLon(),
                "metric",
                apiKey));

        Request request = new Request.Builder()
                .url(String.format("%s?lat=%f&lon=%f&units=%s&appid=%s",
                        FORECAST_URL,
                        geocodingResult.getLat(),
                        geocodingResult.getLon(),
                        "metric",
                        apiKey))
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.code() == 200) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray dtList = jsonObject.getJSONArray("list");

            log.trace(jsonObject.toString());

            for(int i = 0; i < dtList.length(); i++) {
                JSONObject object = dtList.getJSONObject(i);
                forecast.put(object.getInt("dt"), getWeatherDataFromJSON(object, geocodingResult));
            }

        } else {
            log.error(String.format("WeatherServiceImpl:%d - Response code: %d",
                    Thread.currentThread().getStackTrace()[1].getLineNumber(),
                    response.code()));
            throw new ApiException("API returned non-200 status code", response.code());
        }

        log.trace(String.format("WeatherServiceImpl:%d - Weather List: %s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                forecast));

        return forecast;
    }

    private Weather getWeatherDataFromJSON(JSONObject object, GeocodingResult geocodingResult) {
        Weather weather = new Weather();
        weather.setRealTemp(object.getJSONObject("main").getDouble("temp"));
        weather.setFeelsLikeTemp(object.getJSONObject("main").getDouble("feels_like"));
        weather.setHumidity(object.getJSONObject("main").getInt("humidity"));
        weather.setWindSpeed(object.getJSONObject("wind").getDouble("speed"));
        weather.setWeatherName(object.getJSONArray("weather").getJSONObject(0).getString("main"));
        weather.setWeatherDesc(object.getJSONArray("weather").getJSONObject(0).getString("description"));
        weather.setGeocodingResult(geocodingResult);

        return weather;
    }
}
