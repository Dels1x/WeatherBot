package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.delsix.service.MainService;
import ua.delsix.service.WeatherService;
import ua.delsix.service.enums.ServiceCommand;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final WeatherService weatherService;

    public MainServiceImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public String processUseRequest(Update update) throws IOException {
        String userCommand = update.getMessage().getText();
        List<String> commandList = Arrays.asList(userCommand.split(" "));
        System.out.println("commandList: "+ commandList);
        System.out.println("commandList.get(0): "+ commandList.get(0));
        ServiceCommand command = ServiceCommand.fromValue(commandList.get(0));

        if (command == null) {
            log.debug("Could not find a command to: " + userCommand);
            return "Command usage: /command <country> <city>";
        }

        switch(command) {
            case HELP:

                return  "Available commands:\n\n" +
                        "/weather <country> <city> - get current weather in mentioned place\n" +
                        "/forecast <country> <city> - get weather forecast in mentioned place\n" +
                        "/sunrise <country> <city> - get sunrise time in mentioned place\n" +
                        "/sunset <country> <city> - get sunset time in mentioned place\n";
            case WEATHER:
                if (commandList.size() < 3) {
                    return "Usage: /weather <country> <city>";
                }

                return weatherService.getWeather(commandList.get(1), commandList.get(2));
            case FORECAST:
                if (commandList.size() < 3) {
                    return "Usage: /forecast <country> <city>";
                }

                return weatherService.getWeatherForecast(commandList.get(1), commandList.get(2));
            case SUNRISE:
                if (commandList.size() < 3) {
                    return "Usage: /sunrise <country> <city>";
                }

                return weatherService.getSunriseTime(commandList.get(1), commandList.get(2));
            case SUNSET:
                if (commandList.size() < 3) {
                    return "Usage: /sunset <country> <city>";
                }

                return weatherService.getSunsetTime(commandList.get(1), commandList.get(2));

        }

        log.error("MainService: could not execute command: "+command);
        return null;
    }
}
