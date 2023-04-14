package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.delsix.service.GeocodingService;
import ua.delsix.service.WeatherService;
import ua.delsix.service.units.GeocodingResult;
import ua.delsix.service.units.Weather;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Optional;

@Log4j
@Service
public class WeatherServiceImpl implements WeatherService {
    private final GeocodingService geocodingService;
    private static final String CURRENT_WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    @Value("${weather.api.key}")
    private String apiKey;
    private static final OkHttpClient client = new OkHttpClient();

    public WeatherServiceImpl(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @Override
    public String getCurrentWeather(String country, String city) throws IOException {
        Weather weather = getWeatherData(country, city);
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
        //TODO implement this method
        return null;
    }

    @Override
    public String getSunriseTime(String country, String city) throws IOException {
        Weather weather = getWeatherData(country, city);

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

    public Weather getWeatherData(String country, String city) throws IOException {
        Weather weather = new Weather();
        Optional<GeocodingResult> geocodingResult = geocodingService.getGeocodingResult(country, city);

        if(geocodingResult.isPresent()) {
            weather.setGeocodingResult(geocodingResult.get()); // get lat and lon coords from city
        } else {
            //TODO handle the case where geocodingResult is empty
        }

        log.trace(String.format(
                "WeatherServiceImpl:%d - API call:%s?lat=%f&lon=%f&units=%s&appid=%s",
                        Thread.currentThread().getStackTrace()[1].getLineNumber(),
                        CURRENT_WEATHER_URL,
                        weather.getGeocodingResult().getLat(),
                        weather.getGeocodingResult().getLon(),
                        "metric",
                        apiKey));

        Request request = new Request.Builder()
                .url(String.format("%s?lat=%f&lon=%f&units=%s&appid=%s",
                        CURRENT_WEATHER_URL,
                        weather.getGeocodingResult().getLat(),
                        weather.getGeocodingResult().getLon(),
                        "metric",
                        apiKey))
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.code() == 200) {
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            JSONObject weatherObject = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject windObject = jsonObject.getJSONObject("wind");
            JSONObject sysObject = jsonObject.getJSONObject("sys");

            log.trace(jsonObject.toString());

            weather.setRealTemp(mainObject.getDouble("temp"));
            weather.setFeelsLikeTemp(mainObject.getDouble("feels_like"));
            weather.setHumidity(mainObject.getInt("humidity"));
            weather.setWindSpeed(windObject.getDouble("speed"));
            weather.setWeatherName(weatherObject.getString("main"));
            weather.setWeatherDesc(weatherObject.getString("description"));
            weather.setSunriseTimestamp(sysObject.getLong("sunrise"));
            weather.setSunsetTimestamp(sysObject.getLong("sunset"));

        } else {
            log.error(String.format("WeatherServiceImpl:%d - Response code: %d",
                    Thread.currentThread().getStackTrace()[1].getLineNumber(),
                    response.code()));
        }

        log.trace(String.format("WeatherServiceImpl:%d - Weather object: %s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                weather));

        return weather;
    }
}
