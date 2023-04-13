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
}
