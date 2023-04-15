package ua.delsix.service;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface MainService {
    String processUseRequest(Update update) throws IOException;
}
