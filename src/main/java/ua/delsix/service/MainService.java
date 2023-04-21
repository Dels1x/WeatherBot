package ua.delsix.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface MainService {
    SendMessage processUserCommand(Update update) throws IOException;
    EditMessageText processForecastCallbackQuery(Update update) throws IOException;
}
