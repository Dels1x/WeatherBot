package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.delsix.service.GeocodingService;
import ua.delsix.service.geocoding.GeocodingResult;

import java.io.IOException;
import java.util.Locale;

@Log4j
@Service
public class GeocodingServiceImpl implements GeocodingService {
    private static final String BASE_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private static final OkHttpClient client = new OkHttpClient();
    @Value("${weather.api.key}")
    private static String apiKey;

    @Override
    public GeocodingResult getGeocodingResult(String country, String city, int limit) throws IOException {
        GeocodingResult geocodingResult = new GeocodingResult();
        String countryCode = getCountryCode(country);

        Request request = new Request.Builder()
                        .url(String.format("%s?q=%s,%s&limit=%d&appid=%s", BASE_URL, city, countryCode, limit, apiKey))
                        .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if(response.code() == 200) {
            ResponseBody responseBody = response.body();
            JSONArray jsonArray = new JSONArray(responseBody);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                System.out.println(jsonObject);
            }
        }

        return null;
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

        log.debug("Didn't find country code to "+countryName);
        return null;
    }
}
