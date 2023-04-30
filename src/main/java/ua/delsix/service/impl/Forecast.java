package ua.delsix.service.impl;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.delsix.service.units.Weather;

import java.util.TreeMap;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Forecast {
    private TreeMap<Integer, Weather> forecast = new TreeMap<>();
    private int[] dtSteps;
    private String message;

    public Forecast(TreeMap<Integer, Weather> forecast, String message) {
        this.forecast = forecast;
        this.message = message;
        this.dtSteps = forecast.keySet().stream().mapToInt(Integer::intValue).toArray();
    }
}
