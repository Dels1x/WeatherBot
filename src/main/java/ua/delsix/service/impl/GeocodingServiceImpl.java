package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.delsix.service.GeocodingService;
import ua.delsix.service.geocoding.GeocodingResult;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Log4j
@Service
public class GeocodingServiceImpl implements GeocodingService {
    private static final String BASE_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();
    @Value("${weather.api.key}")
    private String apiKey;

    @Override
    public GeocodingResult getGeocodingResult(String country, String city, int limit) throws IOException, JSONException {
        GeocodingResult geocodingResult = new GeocodingResult();
        String countryCode = getCountryCode(country);
        if (countryCode == null) {
            return null;
        }

        System.out.println(apiKey);
        log.trace(String.format("GeocodingService - getGeocodingResult() API call: %s?q=%s,%s&limit=%d&appid=%s",
                BASE_URL, city, countryCode, limit, apiKey));

        Request request = new Request.Builder()
                        .url(String.format("%s?q=%s,%s&limit=%d&appid=%s", BASE_URL, city, countryCode, limit, apiKey))
                        .build();


        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.code() == 200) {
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
        }

        return geocodingResult;
    }

    @Override
    public GeocodingResult getGeocodingResult(String city, int limit) {
        GeocodingResult geocodingResult = new GeocodingResult();
        return null;
    }

    private String getCountryCode(String countryName) {
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            if(l.getDisplayCountry().equalsIgnoreCase(countryName)) {
                return iso;
            }
        }

        log.debug("GeocodingService - getCountryCode() - Didn't find country code to "+countryName);
        log.trace(Locale.getISOCountries());
        return null;
    }
}
