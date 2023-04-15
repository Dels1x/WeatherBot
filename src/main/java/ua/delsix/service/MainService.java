package ua.delsix.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    String processUseRequest(Update update);
}
