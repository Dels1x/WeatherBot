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

                return processForecastCommand(update, commandList);
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

    private SendMessage processForecastCommand(Update update, List<String> commandList) throws IOException {
        String country = commandList.get(1);
        String city = commandList.get(2);

        Forecast forecast = weatherService.getWeatherForecast(country, city);

        SendMessage sendMessage = MessageUtils.sendMessageBuilder(
                update,
                forecast.getMessage()
                        .concat("Choose a date using the buttons below"));

        sendMessage.setReplyMarkup(forecastKeyboardMarkupFactory(forecast, country, city));

        return sendMessage;
    }

    private EditMessageText processForecastCommand(CallbackQuery callbackQuery, String[] callbackData) throws IOException {
        String country = callbackData[1];
        String city = callbackData[2];

        Forecast forecast = weatherService.getWeatherForecast(country, city);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText(forecast.getMessage()
                .concat("Choose a date using buttons below"));
        editMessageText.setReplyMarkup(forecastKeyboardMarkupFactory(forecast, country, city));

        return editMessageText;
    }

    private InlineKeyboardMarkup forecastKeyboardMarkupFactory(Forecast forecast, String country, String city) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> mainRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        mainRow.add(new InlineKeyboardButton("Today"));
        mainRow.add(new InlineKeyboardButton("Tomorrow"));
        secondRow.add(new InlineKeyboardButton("Day 3"));
        secondRow.add(new InlineKeyboardButton("Day 4"));
        secondRow.add(new InlineKeyboardButton("Day 5"));

        //TODO set appropriate unix date for buttons

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

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    @Override
    public EditMessageText processForecastCallbackQuery(Update update) throws IOException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] callbackData = callbackQuery.getData().split("\\|");
        log.trace("MainService: processing user's callback query: "+ Arrays.toString(callbackData));

        String data = callbackData[0];

        if (data.startsWith("cancel")) {
            log.trace("e");
            return handleCancelButton(callbackQuery);
        } else if(data.startsWith("day")) {
            return handleDayButton(callbackQuery);
        } else if (data.startsWith("time")) {
            return handleTimeButton(callbackQuery);
        }

        return null;
    }

    private EditMessageText handleDayButton(CallbackQuery callbackQuery) throws IOException {
        int messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        String[] callbackData = callbackQuery.getData().split("\\|");

        String data = callbackData[0];
        String country = callbackData[1];
        String city = callbackData[2];

        Forecast forecast = weatherService.getWeatherForecast(country, city);
        String newMessage = "Choose desired time using the buttons below";

        ZoneId zoneId = ZoneId.of("Europe/Kiev");
        LocalDate now = LocalDate.now(zoneId);
        LocalTime endOfDayTime = LocalTime.MAX;
        ZonedDateTime endOfDayZoned = ZonedDateTime.of(now, endOfDayTime, zoneId);
        long endOfDay = endOfDayZoned.toEpochSecond();

        List<List<Integer>> days = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            days.add(new ArrayList<>());
        }

        byte counterA = 1, counterB = 0;

        for(Integer dtStep: forecast.getDtSteps()) {
            if(forecast.getDtSteps()[0] > endOfDay) {
                newMessage = "No avalaible time-stops during this day";
                break;
            }
            if(dtStep < endOfDay - 1) {
                days.get(0).add(dtStep);
                log.trace("DtStep added to day0: "+dtStep);
                System.out.println(endOfDay);
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
                            .concat(data)
                            .concat("|")
                            .concat(country)
                            .concat("|")
                            .concat(city)
                            .concat("|")
                            .concat(String.valueOf(i)));

            if(i > daysSize / 2 - 1) {
                secondRow.add(button);
            } else {
                mainRow.add(button);
            }
        }

        InlineKeyboardButton button = new InlineKeyboardButton("Go back");
        button.setCallbackData(
                "cancel"
                        .concat("|")
                        .concat(country)
                        .concat("|")
                        .concat(city)
                        .concat("|")
                        .concat(data)
                        .concat("|")
                        .concat("time"));

        cancelRow.add(button);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(forecast.getMessage()
                .concat(newMessage));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    private EditMessageText handleCancelButton(CallbackQuery callbackQuery) throws IOException {
        String[] callbackData = callbackQuery.getData().split("\\|");
        log.debug("Processing cancel command: "+ Arrays.toString(callbackData));

        callbackData[0] = callbackData[4];
        String[] newCallbackData = new String[callbackData.length - 1];

        System.arraycopy(callbackData, 0, newCallbackData, 0, newCallbackData.length);

        String data = newCallbackData[0];

        if(data.startsWith("time")) {
            return processForecastCommand(callbackQuery, newCallbackData);
        }

        return null;
    }

    private EditMessageText handleTimeButton(CallbackQuery callbackQuery) {
        return null;
    }

}
