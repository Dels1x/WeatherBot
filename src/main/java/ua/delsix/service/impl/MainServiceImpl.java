package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.delsix.service.MainService;
import ua.delsix.service.WeatherService;
import ua.delsix.service.enums.ServiceCommand;
import ua.delsix.utils.MessageUtils;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final WeatherService weatherService;

    public MainServiceImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public SendMessage processUserCommand(Update update) throws IOException {
        String userCommand = update.getMessage().getText();
        List<String> commandList = Arrays.asList(userCommand.split(" "));
        System.out.println("commandList: "+ commandList);
        System.out.println("commandList.get(0): "+ commandList.get(0));
        ServiceCommand command = ServiceCommand.fromValue(commandList.get(0));

        if (command == null) {
            log.debug("Could not find a command to: " + userCommand);
            return MessageUtils.sendMessageBuilder(
                    update,
                    "Command usage: /command <country> <city>");
        }

        switch (command) {
            case HELP -> {
                return MessageUtils.sendMessageBuilder(
                        update,
                        "Available commands:\n\n" +
                                "/weather <country> <city> - get current weather in mentioned place\n" +
                                "/forecast <country> <city> - get weather forecast in mentioned place\n" +
                                "/sunrise <country> <city> - get sunrise time in mentioned place\n" +
                                "/sunset <country> <city> - get sunset time in mentioned place\n");
            }
            case WEATHER -> {
                if (commandList.size() < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /weather <country> <city>");
                }
                return MessageUtils.sendMessageBuilder(
                        update,
                        weatherService.getWeather(commandList.get(1), commandList.get(2)));
            }
            case FORECAST -> {
                log.trace(String.format("MainServiceImpl>:%d - Processing forecast command...:\n",
                        Thread.currentThread().getStackTrace()[1].getLineNumber()));

                if (commandList.size() < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /forecast <country> <city>");
                }

                String country = commandList.get(1);
                String city = commandList.get(2);

                Forecast forecast = weatherService.getWeatherForecast(country, city);

                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                List<InlineKeyboardButton> mainRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                mainRow.add(new InlineKeyboardButton("Today"));
                mainRow.add(new InlineKeyboardButton("Tomorrow"));
                secondRow.add(new InlineKeyboardButton("Day 3"));
                secondRow.add(new InlineKeyboardButton("Day 4"));
                secondRow.add(new InlineKeyboardButton("Day 5"));

                keyboard.add(mainRow);
                keyboard.add(secondRow);

                int counter = 0;

                for(List<InlineKeyboardButton> row: keyboard) {
                    for (InlineKeyboardButton inlineKeyboardButton : row) {
                        inlineKeyboardButton.setCallbackData(
                                        "day"
                                        .concat(String.valueOf(++counter))
                                        .concat("|")
                                        .concat(country)
                                        .concat("|")
                                        .concat(city));
                    }
                }

                SendMessage sendMessage = MessageUtils.sendMessageBuilder(
                        update,
                        forecast.getMessage()
                                .concat("Choose a date using the buttons below"));

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(markup);

                return sendMessage;
            }
            case SUNRISE -> {
                if (commandList.size() < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /sunrise <country> <city>");
                }
                return MessageUtils.sendMessageBuilder(
                        update,
                        weatherService.getSunriseTime(commandList.get(1), commandList.get(2)));
            }
            case SUNSET -> {
                if (commandList.size() < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /sunset <country> <city>");
                }
                return MessageUtils.sendMessageBuilder(
                        update,
                        weatherService.getSunsetTime(commandList.get(1), commandList.get(2)));
            }
        }

        log.error("MainService: could not execute command: "+command);
        return null;
    }

    @Override
    public EditMessageText processForecastCallbackQuery(Update update) throws IOException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        int messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        String[] callbackData = callbackQuery.getData().split("\\|");
        log.trace("MainService: processing user's callback query: "+ Arrays.toString(callbackData));

        String data = callbackData[0];
        String country = callbackData[1];
        String city = callbackData[2];

        if (data.startsWith("day")) {
            Forecast forecast = weatherService.getWeatherForecast(country, city);
            String newMessage = "Weather in: UA, Odesa\n\n" +
                    "Choose a time using the buttons below";

            System.out.println(forecast.getDtSteps());

            long endOfDay = LocalDate.now()
                            .atTime(LocalTime.MAX)
                            .toEpochSecond(ZoneOffset.UTC);

            List<List<Integer>> days = new ArrayList<>();
            for(int i = 0; i < 6; i++) {
                days.add(new ArrayList<>());
            }

            byte counterA = 1, counterB = 0;

            for(Integer dtStep: forecast.getDtSteps()) {
                if(dtStep < endOfDay) {
                    days.get(0).add(dtStep);
                    log.trace("DtStep added to day0: "+dtStep);
                    continue;
                }

                if(counterB == 8) {
                    counterA++;
                    counterB = 0;
                }

                days.get(counterA).add(dtStep);
                counterB++;

                log.trace(String.format(
                                "dtStep - %d " +
                                "counterA - %d " +
                                "counterB - %d\n",
                                dtStep,
                                counterA,
                                counterB
                ));
            }

            log.trace("Days list: "+days);


            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> mainRow = new ArrayList<>();
            List<InlineKeyboardButton> secondRow = new ArrayList<>();
            List<InlineKeyboardButton> cancelRow = new ArrayList<>();
            keyboard.add(mainRow);
            keyboard.add(secondRow);
            keyboard.add(cancelRow);



            switch(data) {
                case "day1" -> {

                }
            }


            int dayNumber = data.charAt(data.length() - 1) - '0' - 1; // gets the last number of data variable
            List<Integer> theDay = days.get(dayNumber);
            int daysSize = theDay.size();
            for(int i = 0; i <  daysSize; i++) {
                Instant instant = Instant.ofEpochSecond(theDay.get(i));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                                .withZone(ZoneId.systemDefault());
                String timeString = formatter.format(instant);

                System.out.println(timeString);

                InlineKeyboardButton button = new InlineKeyboardButton(timeString);
                button.setCallbackData(
                        "time|"
                                .concat(timeString)
                                .concat("|")
                                .concat(data)
                                .concat("|")
                                .concat(country)
                                .concat("|")
                                .concat(city));

                if(i > daysSize / 2) {
                    secondRow.add(button);
                } else {
                    mainRow.add(button);
                }
            }

            InlineKeyboardButton button = new InlineKeyboardButton("Go back");
            button.setCallbackData(
                    "time|"
                            .concat("cancel")
                            .concat("|")
                            .concat(data)
                            .concat("|")
                            .concat(country)
                            .concat("|")
                            .concat(city));

            cancelRow.add(button);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);
            editMessageText.setText(forecast.getMessage()
                    .concat("Choose time using the buttons below"));
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(keyboard);
            editMessageText.setReplyMarkup(inlineKeyboardMarkup);
            return editMessageText;
        }

        return null;
    }
}
