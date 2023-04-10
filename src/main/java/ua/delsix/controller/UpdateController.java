package ua.delsix.controller;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.delsix.utils.MessageUtils;

@Controller
public class UpdateController {
    private TelegramBot telegramBot;

    public void registrateBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        SendMessage sendMessage = MessageUtils.sendMessageBuilder(update,
                update.getMessage().getText());
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
