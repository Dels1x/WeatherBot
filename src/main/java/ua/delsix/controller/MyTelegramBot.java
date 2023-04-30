package ua.delsix.controller;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
@Log4j
public class MyTelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    private final UpdateController updateController;

    @Autowired
    public MyTelegramBot(@Value("${bot.token}") String token,
                         UpdateController updateController) {
        super(token);
        this.updateController = updateController;
    }

    @PostConstruct
    public void registrateBotInUpdateController() {
        updateController.registrateBot(this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update received: "+update);
        try {
            updateController.processUpdate(update);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public void editAnswerMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
