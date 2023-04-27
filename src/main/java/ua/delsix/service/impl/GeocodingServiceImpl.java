package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.delsix.service.GeocodingService;
import ua.delsix.service.units.GeocodingResult;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j
@Service
public class GeocodingServiceImpl implements GeocodingService {
    private static final String BASE_URL = "http://api.openweathermap.org/geo/1.0/direct";
    @Value("${weather.api.key}")
    private String apiKey;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();


    @Override
    public Optional<GeocodingResult> getGeocodingResult(String country, String city) {
        GeocodingResult geocodingResult = new GeocodingResult();
        String countryCode = getCountryCode(country);
        if (countryCode == null) {
            return Optional.empty();
        }

        log.trace(String.format(
                "GeocodingServiceImpl:%d - API call: %s?q=%s,%s&limit=%d&appid=%s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                BASE_URL,
                city,
                countryCode,
                5,
                apiKey));

        Request request = new Request.Builder()
                .url(String.format("%s?q=%s,%s&limit=%d&appid=%s",
                        BASE_URL,
                        city,
                        countryCode,
                        5,
                        apiKey))
                .build();


        Call call = client.newCall(request);
        try (Response response = call.execute()) {

            if (response.code() == 200) {
                assert response.body() != null;
                String responseBody = response.body().string();
                JSONArray jsonArray = new JSONArray(responseBody);
                JSONObject jsonObject;

                if(jsonArray.length() > 1 ){
                    jsonObject = jsonArray.getJSONObject(1);
                } else {
                    jsonObject = jsonArray.getJSONObject(0);
                }
                JSONObject localNames = jsonObject.getJSONObject("local_names");

                geocodingResult.setLat(jsonObject.getDouble("lat"));
                geocodingResult.setLon(jsonObject.getDouble("lon"));
                geocodingResult.setCountryCode(jsonObject.getString("country"));
                geocodingResult.setEnCityName(localNames.getString("en"));
                geocodingResult.setRuCityName(localNames.getString("ru"));

                return Optional.of(geocodingResult);
            } else {
                log.error(String.format("GeocodingServiceImpl:%d - Response code: %d",
                        Thread.currentThread().getStackTrace()[1].getLineNumber(),
                        response.code()));
            }
        } catch (Exception e) {
            log.error("Response error: " + e);
        }

        return Optional.empty();
    }

    private String getCountryCode(String countryName) {
        if (countryName.equals("USA")) return "US";
        if (countryName.equals("UK")) return "GB";

        if (Arrays.asList(Locale.getISOCountries()).contains(countryName)) {
            return countryName;
        }

        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            if (l.getDisplayCountry().equalsIgnoreCase(countryName)) {
                return iso;
            }
        }

        log.trace("Locale.getISOCountries(): " + Arrays.toString(Locale.getISOCountries()));
        return null;
    }
}
