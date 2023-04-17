package ua.delsix.service.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private GeocodingResult geocodingResult;
    private double realTemp;
    private double feelsLikeTemp;
    private int humidity;
    private double windSpeed;
    private String weatherName;
    private String weatherDesc;
    private long sunriseTimestamp;
    private long sunsetTimestamp;

    public Weather(GeocodingResult geocodingResult, double realTemp, double feelsLikeTemp, int humidity, double windSpeed, String weatherName, String weatherDesc) {
        this.geocodingResult = geocodingResult;
        this.realTemp = realTemp;
        this.feelsLikeTemp = feelsLikeTemp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherName = weatherName;
        this.weatherDesc = weatherDesc;
    }
}
