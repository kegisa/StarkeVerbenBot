package de.viktorlevin.starkeverbenbot.configuration;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class BeanConfiguration {
    @Bean
    public RandomDataGenerator random() {
        return new RandomDataGenerator();
    }

    @Bean
    public Random defaultRandom() {
        return new Random();
    }
}
