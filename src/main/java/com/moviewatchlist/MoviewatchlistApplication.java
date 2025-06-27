package com.moviewatchlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Entry point of the Movie Watchlist Spring Boot application.
 * <p>
 * This class bootstraps the application context and starts the embedded web server.
 */
@SpringBootApplication
public class MoviewatchlistApplication {

    /**
     * Main method that launches the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(MoviewatchlistApplication.class, args);
    }


    /**
     * Registers a singleton RestTemplate bean in the application context.
     * This allows it to be injected into other components like services.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
