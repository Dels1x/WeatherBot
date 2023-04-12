package ua.delsix;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@Log4j
@SpringBootApplication
public class DispatcherApplication {
    public static void main(String[] args) {
        log.debug("Starting...");
        SpringApplication.run(DispatcherApplication.class);
    }
}
