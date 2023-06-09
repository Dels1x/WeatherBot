package ua.delsix.service.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONException;
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
import ua.delsix.service.units.Weather;
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
    public SendMessage processUserCommand(Update update) throws IOException, JSONException {
        String userCommand = update.getMessage().getText();
        if (userCommand.contains("Great Britain")) {
            userCommand = userCommand.replace("Great Britain", "GB");
        } else if (userCommand.contains("United Kingdom")) {
            userCommand = userCommand.replace("United Kingdom", "GB");
        }

        String[] commandList = userCommand.split(" ");
        String country = "";
        String city = "";

        ServiceCommand command = ServiceCommand.fromValue(commandList[0]);

        if(commandList.length >= 3) {
            String[] countryAndCity = getCountryAndCity(commandList);
            country = countryAndCity[0];
            city = countryAndCity[1];
        }

        if (command == null) {
            log.debug("Could not find a command to: " + userCommand);
            return MessageUtils.sendMessageBuilder(
                    update,
                    "Command usage: /command <country> <city>");
        }

        switch (command) {
            case START -> {
                return MessageUtils.sendMessageBuilder(
                        update,
                        """
                                Welcome to the Weather Bot!
                                
                                To learn about the functionality of this bot, simply type in "/help" or use built-in\
                                 telegram menu in the bottom-left corner.
                                """
                );
            }
            case HELP -> {
                return MessageUtils.sendMessageBuilder(
                        update,
                        """
                                Available commands:

                                /weather <country> <city> - get current weather in mentioned place
                                /forecast <country> <city> - get weather forecast in mentioned place
                                /sunrise <country> <city> - get sunrise time in mentioned place
                                /sunset <country> <city> - get sunset time in mentioned place
                                """);
            }
            case WEATHER -> {
                if (commandList.length < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /weather <country> <city>");
                }
                return MessageUtils.sendMessageBuilder(
                        update,
                        weatherService.getWeather(country, city));
            }
            case FORECAST -> {
                log.trace(String.format("MainServiceImpl>:%d - Processing forecast command...:\n",
                        Thread.currentThread().getStackTrace()[1].getLineNumber()));

                if (commandList.length < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /forecast <country> <city>");
                }

                return processForecastCommand(update, commandList);
            }
            case SUNRISE -> {
                if (commandList.length < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /sunrise <country> <city>");
                }
                return MessageUtils.sendMessageBuilder(
                        update,
                        weatherService.getSunriseTime(country, city));
            }
            case SUNSET -> {
                if (commandList.length < 3) {
                    return MessageUtils.sendMessageBuilder(
                            update,
                            "Usage: /sunset <country> <city>");
                }
                return MessageUtils.sendMessageBuilder(
                        update,
                        weatherService.getSunsetTime(country, city));
            }
        }

        log.error("MainService: could not execute command: " + command);
        return null;
    }

    @Override
    public EditMessageText processForecastCallbackQuery(CallbackQuery callbackQuery) throws IOException, JSONException {
        String[] callbackData = callbackQuery.getData().split("\\|");
        log.trace("MainService: processing user's callback query: " + Arrays.toString(callbackData));

        String data = callbackData[0];

        if (data.startsWith("cancel")) {
            return handleCancelButton(callbackQuery);
        } else if (data.startsWith("day")) {
            return handleDayButton(callbackQuery, callbackData);
        } else if (data.startsWith("time")) {
            return handleTimeButton(callbackQuery);
        }

        return null;
    }

    private SendMessage processForecastCommand(Update update, String[] commandList) throws IOException, JSONException {
        String[] countryAndCity = getCountryAndCity(commandList);
        String country = countryAndCity[0];
        String city = countryAndCity[1];

        Forecast forecast = weatherService.
                getWeatherForecast(country, city);

        SendMessage sendMessage = MessageUtils.sendMessageBuilder(
                update,
                forecast.getMessage()
                        .concat("Choose a date using the buttons below"));

        sendMessage.setReplyMarkup(forecastKeyboardMarkupFactory(country, city));

        return sendMessage;
    }

    private EditMessageText processForecastCommand(CallbackQuery callbackQuery, String[] callbackData) throws IOException, JSONException {
        String country = callbackData[1];
        String city = callbackData[2];

        log.debug("processForecastCommand callbackQuery: " + Arrays.toString(callbackData));

        Forecast forecast = weatherService.getWeatherForecast(country, city);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText(forecast.getMessage()
                .concat("Choose a date using buttons below"));
        editMessageText.setReplyMarkup(forecastKeyboardMarkupFactory(country, city));

        return editMessageText;
    }

    private InlineKeyboardMarkup forecastKeyboardMarkupFactory(String country, String city) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> mainRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        LocalDate date;
        String formattedDate;
        mainRow.add(new InlineKeyboardButton("Today"));
        mainRow.add(new InlineKeyboardButton("Tomorrow"));
        date = LocalDate.now().plusDays(2);
        formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM"));
        secondRow.add(new InlineKeyboardButton(formattedDate));
        date = LocalDate.now().plusDays(3);
        formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM"));
        secondRow.add(new InlineKeyboardButton(formattedDate));
        date = LocalDate.now().plusDays(4);
        formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM"));
        secondRow.add(new InlineKeyboardButton(formattedDate));

        keyboard.add(mainRow);
        keyboard.add(secondRow);

        int counter = 0;

        for (List<InlineKeyboardButton> row : keyboard) {
            for (InlineKeyboardButton inlineKeyboardButton : row) {
                inlineKeyboardButton.setCallbackData(
                        "day"
                                .concat(String.valueOf(counter++))
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

    private EditMessageText handleDayButton(CallbackQuery callbackQuery, String[] newCallbackData) throws IOException, JSONException {
        log.debug("handleDayButton callbackQuery: " + Arrays.toString(newCallbackData));

        String country = newCallbackData[1];
        String city = newCallbackData[2];

        Forecast forecast = weatherService.getWeatherForecast(country, city);
        String newMessage = "Choose desired time using the buttons below";

        return buildEditMessageText(callbackQuery, newCallbackData, forecast, newMessage);
    }

    private EditMessageText handleCancelButton(CallbackQuery callbackQuery) throws IOException, JSONException {
        String[] callbackData = callbackQuery.getData().split("\\|");
        log.debug("Processing cancel command: " + Arrays.toString(callbackData));

        callbackData[0] = callbackData[4];
        String[] newCallbackData = new String[callbackData.length - 1];

        System.arraycopy(callbackData, 0, newCallbackData, 0, newCallbackData.length);

        log.info("handleCancelButton - newCallbackData: " + Arrays.toString(callbackData));

        if (newCallbackData[3].startsWith("time")) {
            return handleDayButton(callbackQuery, newCallbackData);
        } else if (newCallbackData[0].startsWith("day")) {
            return processForecastCommand(callbackQuery, newCallbackData);
        }

        return null;
    }

    private EditMessageText handleTimeButton(CallbackQuery callbackQuery) throws IOException, JSONException {
        log.debug("Processing time button request");
        int messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        String[] callbackData = callbackQuery.getData().split("\\|");

        String data = callbackData[0];
        String country = callbackData[1];
        String city = callbackData[2];
        int dayNumber = Integer.parseInt(callbackData[3]);
        int timeNumber = Integer.parseInt(callbackData[4]);

        Forecast forecast = weatherService.getWeatherForecast(country, city);

        ZoneId zoneId = ZoneId.of("Europe/Kyiv");
        LocalDate now = LocalDate.now(zoneId);
        ZonedDateTime endOfDayZoned = ZonedDateTime.of(now, LocalTime.MAX, zoneId);
        long endOfDay = endOfDayZoned.toEpochSecond();

        if (dayNumber == 0 && forecast.getDtSteps()[0] > endOfDay) {
            handleDayButton(callbackQuery, callbackData);
        }

        List<List<Integer>> days = getDays(forecast);

        Integer selectedTimeUnix = days.get(dayNumber + 1).get(timeNumber);
        Weather weather = forecast.getForecast().get(selectedTimeUnix);

        log.trace("Weather: " + weather);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        keyboard.add(cancelRow);

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
                        .concat("day".concat(String.valueOf(dayNumber))));

        cancelRow.add(button);

        Instant instant = Instant.ofEpochSecond(selectedTimeUnix);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(zoneId);
        String timeString = formatter.format(instant);

        String newMessage = String.format(
                """
                        Weather in: %s, %s at %s

                        Weather: %s: %s
                        Temperature: %.2f (feels like %.2f)
                        Wind: %.2fm/s
                        Humidity: %d""",
                weather.getGeocodingResult().getCountryCode(), weather.getGeocodingResult().getEnCityName(), timeString,
                weather.getWeatherName(), weather.getWeatherDesc(),
                weather.getRealTemp(), weather.getFeelsLikeTemp(),
                weather.getWindSpeed(),
                weather.getHumidity());

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newMessage);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    private String[] getCountryAndCity(String[] commandList) {
        String country = commandList[1];
        StringBuilder sb = new StringBuilder();
        String currentString;
        for (int i = 2; i < commandList.length; i++) {
            currentString = commandList[i];
            if(currentString.matches("^[a-zA-Z]*$")) {
                sb.append(currentString.concat(" "));
            }
        }

        String city = sb.toString().trim();

        return new String[]{country, city};
    }

    private EditMessageText handleNoTimeStops(CallbackQuery callbackQuery, Forecast forecast, String[] callbackData) {
        String newMessage = "No available time-stops during this day";
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        return returnWithCancelButton(callbackQuery, callbackData, keyboard, forecast.getMessage(), newMessage);
    }

    private long getEndOfDay() {
        ZoneId zoneId = ZoneId.of("Europe/Kyiv");
        LocalDate now = LocalDate.now(zoneId);
        ZonedDateTime endOfDayZoned = ZonedDateTime.of(now, LocalTime.MAX, zoneId);
        return endOfDayZoned.toEpochSecond();
    }

    private List<List<Integer>> getDays(Forecast forecast) {
        long endOfDay = getEndOfDay();
        List<List<Integer>> days = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            days.add(new ArrayList<>());
        }

        byte counterA = 1, counterB = 0;

        for (Integer dtStep : forecast.getDtSteps()) {
            if (dtStep < endOfDay) {
                days.get(0).add(dtStep);
                log.trace("DtStep added to day0: " + dtStep);
                continue;
            }

            if (counterB == 8) {
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

        log.trace("Days list: " + days);

        return days;
    }

    private EditMessageText returnWithCancelButton(CallbackQuery callbackQuery, String[] callbackData, List<List<InlineKeyboardButton>> keyboard, String messageA, String messageB) {
        String data = callbackData[0];
        String country = callbackData[1];
        String city = callbackData[2];

        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        keyboard.add(cancelRow);

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
                        .concat("day"));

        cancelRow.add(button);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText(messageA
                .concat(messageB));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    private EditMessageText buildEditMessageText(CallbackQuery callbackQuery, String[] callbackData, Forecast forecast, String message) {
        log.debug("CallbackData: "+ Arrays.toString(callbackData));
        String data = callbackData[0];
        String country = callbackData[1];
        String city = callbackData[2];
        int dayNumber = data.charAt(data.length() - 1) - '0'; // gets the last number of data variable aka day number
        long endOfDay = getEndOfDay();

        if (dayNumber == 0 && forecast.getDtSteps()[0] > endOfDay) {
            return handleNoTimeStops(callbackQuery, forecast, callbackData);
        }

        List<List<Integer>> days = getDays(forecast);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> mainRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        keyboard.add(mainRow);
        keyboard.add(secondRow);

        List<Integer> theDay = days.get(dayNumber);
        int daysSize = theDay.size();
        for (int i = 0; i < daysSize; i++) {
            Instant instant = Instant.ofEpochSecond(theDay.get(i));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneId.of("Europe/Kyiv"));
            String timeString = formatter.format(instant);

            InlineKeyboardButton button = new InlineKeyboardButton(timeString);
            button.setCallbackData(
                    "time|"
                            .concat(country)
                            .concat("|")
                            .concat(city)
                            .concat("|")
                            .concat(String.valueOf(Integer.parseInt(data.substring(data.length() - 1)))
                                    .concat("|")
                                    .concat(String.valueOf(i))));

            if (i > daysSize / 2 - 1) {
                secondRow.add(button);
            } else {
                mainRow.add(button);
            }
        }

        return returnWithCancelButton(callbackQuery, callbackData, keyboard, forecast.getMessage(), message);
    }
}
