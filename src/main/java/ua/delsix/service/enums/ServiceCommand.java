package ua.delsix.service.enums;

public enum ServiceCommand {
    START("/start"),
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
        for(ServiceCommand command: ServiceCommand.values()) {
            if(command.value.equals(value))
                return command;
        }

        return null;
    }
}
