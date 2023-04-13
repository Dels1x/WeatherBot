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
    public Weather getCurrentWeather(String country, String city, String units) throws IOException {
        Weather weather = getWeather(country, city, units, 5);

        //TODO implement this method

        return weather;
    }

    @Override
    public Weather getWeatherForecast(String country, String city) {
        //TODO implement this method
        return null;
    }

    @Override
    public Weather getSunriseTime(String country, String city) {
        //TODO implement this method
        return null;
    }

    @Override
    public Weather getSunsetTime(String country, String city) {
        //TODO implement this method
        return null;
    }

    private Weather getWeather(String country, String city, String units, int limit) throws IOException {
        Weather weather = new Weather();
        Optional<GeocodingResult> geocodingResult = geocodingService.getGeocodingResult(country, city, 5);

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
                        units,
                        apiKey));

        Request request = new Request.Builder()
                .url(String.format("%s?lat=%f&lon=%f&units=%s&appid=%s",
                        CURRENT_WEATHER_URL,
                        weather.getGeocodingResult().getLat(),
                        weather.getGeocodingResult().getLon(),
                        units,
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

            weather.setRealTemp(mainObject.getDouble("temp"));
            weather.setFeelsLikeTemp(mainObject.getDouble("feels_like"));
            weather.setHumidity(mainObject.getInt("humidity"));
            weather.setWindSpeed(windObject.getDouble("speed"));
            weather.setWeatherName(weatherObject.getString("main"));
            weather.setWeatherDesc(weatherObject.getString("description"));

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
