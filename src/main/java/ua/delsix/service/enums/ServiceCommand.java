package ua.delsix.service.enums;

import java.util.Objects;

public enum ServiceCommand {
    HELP("/help"),
    WEATHER("/weather", 2),
    FORECAST("/forecast", 2),
    SUNRISE("/sunrise", 2),
    SUNSET("/sunset", 2);


    private final String value;
    private final int argCount;

    ServiceCommand(String value) {
        this(value, 0);
    }

    ServiceCommand(String value, int argCount) {
        this.value = value;
        this.argCount = argCount;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean equals(String value) {
        return this.toString().equals(value);
    }

    public static ServiceCommand fromValue(String value) {
        System.out.println(value);
        for(ServiceCommand command: ServiceCommand.values()) {
            System.out.println(command);
            if(command.value.equals(value))
                return command;
        }

        return null;
    }
}
