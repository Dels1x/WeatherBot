package ua.delsix.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface MainService {
    SendMessage processUserCommand(Update update) throws IOException;
    SendMessage processForecastCallbackQuery(Update update) throws IOException;
}
