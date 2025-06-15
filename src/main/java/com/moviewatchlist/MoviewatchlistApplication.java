package com.moviewatchlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
