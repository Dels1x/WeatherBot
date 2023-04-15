package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.delsix.service.MainService;
import ua.delsix.service.enums.ServiceCommand;

import java.util.Arrays;
import java.util.List;


@Log4j
@Service
public class MainServiceImpl implements MainService {
    @Override
    public String processUseRequest(Update update) {
        String userCommand = update.getMessage().getText();
        List<String> commandList = Arrays.asList(userCommand.split("\\s{2,}"));
        ServiceCommand command = ServiceCommand.fromValue(commandList.get(0));

        if (command == null) {
            log.debug("Could not find a command to: " + userCommand);
            return "Command usage: /command <country> <city>";
        }

        switch(command) {
            case HELP:
                //TODO handle HELP command
                break;
            case WEATHER:
                if (commandList.size() < 3) {
                    return "Usage: /weather <country> <city>";
                }
                //TODO handle WEATHER command
                break;
            case FORECAST:
                if (commandList.size() < 3) {
                    return "Usage: /forecast <country> <city>";
                }
                //TODO handle FORECAST command
                break;
            case SUNRISE:
                if (commandList.size() < 3) {
                    return "Usage: /sunrise <country> <city>";
                }
                //TODO handle SUNRISE command
                break;
            case SUNSET:
                if (commandList.size() < 3) {
                    return "Usage: /sunset <country> <city>";
                }
                //TODO handle SUNSET command
                break;

        }
    }
}
