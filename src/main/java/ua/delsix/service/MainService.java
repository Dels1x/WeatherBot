package ua.delsix.service;

import org.json.JSONException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface MainService {
    SendMessage processUserCommand(Update update) throws IOException, JSONException;
    EditMessageText processForecastCallbackQuery(CallbackQuery callbackQuery) throws IOException, JSONException;
}
