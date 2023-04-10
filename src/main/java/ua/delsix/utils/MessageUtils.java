package ua.delsix.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public final class MessageUtils {
    private MessageUtils() {
    }

    public static SendMessage sendMessageBuilder(Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }
}
