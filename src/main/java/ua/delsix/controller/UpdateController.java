package ua.delsix.controller;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.delsix.service.MainService;
import ua.delsix.utils.MessageUtils;

import java.io.IOException;

@Controller
public class UpdateController {
    private TelegramBot telegramBot;
    private final MainService mainService;

    public UpdateController(MainService mainService) {
        this.mainService = mainService;
    }

    public void registrateBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) throws IOException {


        SendMessage sendMessage = MessageUtils.sendMessageBuilder(
                update,
                mainService.processUseRequest(update));
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
