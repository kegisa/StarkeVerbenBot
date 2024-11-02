package de.viktorlevin.starkeverbenbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class StarkeVerbenBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarkeVerbenBotApplication.class, args);
    }

}
