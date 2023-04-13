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
import java.util.List;
import java.util.Locale;
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
    public GeocodingResult getGeocodingResult(String country, String city, int limit) {
        GeocodingResult geocodingResult = new GeocodingResult();
        String countryCode = getCountryCode(country);
        if (countryCode == null) {
            return null;
        }

        System.out.println(apiKey);
        log.trace(String.format(
                "GeocodingServiceImpl:%d - API call: %s?q=%s,%s&limit=%d&appid=%s",
                Thread.currentThread().getStackTrace()[1].getLineNumber(),
                BASE_URL,
                city,
                countryCode,
                limit,
                apiKey));

        Request request = new Request.Builder()
                        .url(String.format("%s?q=%s,%s&limit=%d&appid=%s",
                                BASE_URL,
                                city,
                                countryCode,
                                limit,
                                apiKey))
                        .build();


        Call call = client.newCall(request);
        try (Response response = call.execute()) {

            if (response.code() == 200) {
                assert response.body() != null;
                String responseBody = response.body().string();
                JSONArray jsonArray = new JSONArray(responseBody);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject localNames = jsonObject.getJSONObject("local_names");

                    geocodingResult.setLat(jsonObject.getDouble("lat"));
                    geocodingResult.setLon(jsonObject.getDouble("lon"));
                    geocodingResult.setCountryCode(jsonObject.getString("country"));
                    geocodingResult.setEnCityName(localNames.getString("en"));
                    geocodingResult.setRuCityName(localNames.getString("ru"));
                }
            } else {
                log.error(String.format("GeocodingServiceImpl:%d - Response code: %d",
                        Thread.currentThread().getStackTrace()[1].getLineNumber(),
                        response.code()));
            }
        } catch (Exception e) {
            log.error("Response error: "+e);
        }

        return geocodingResult;
    }

    @Override
    public List<GeocodingResult> getGeocodingResult(String city, int limit) {
        GeocodingResult geocodingResult = new GeocodingResult();
        return null;
    }

    private String getCountryCode(String countryName) {
        if(countryName.equals("USA")) return "US";

        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            if(l.getDisplayCountry().equalsIgnoreCase(countryName)) {
                return iso;
            }

            log.trace(l.getDisplayCountry());
        }

        log.trace("Locale.getISOCountries(): "+ Arrays.toString(Locale.getISOCountries()));
        return null;
    }
}
