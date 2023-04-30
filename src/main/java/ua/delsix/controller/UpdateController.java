package ua.delsix.controller;

import lombok.extern.log4j.Log4j;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.delsix.service.MainService;

import java.io.IOException;

@Controller
@Log4j
public class UpdateController {
    private MyTelegramBot myTelegramBot;
    private final MainService mainService;

    public UpdateController(MainService mainService) {
        this.mainService = mainService;
    }

    public void registrateBot(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void processUpdate(Update update) throws IOException, JSONException {
        if(update.hasCallbackQuery()) {
            myTelegramBot.editAnswerMessage(mainService.processForecastCallbackQuery(update.getCallbackQuery()));
        } else if (update.getMessage().hasText()) {
            myTelegramBot.sendAnswerMessage(mainService.processUserCommand(update));
        } else {
            log.debug("Message sent by user is neither CallbackQuery or a message with a text");
        }
    }
}
