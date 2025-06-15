package com.moviewatchlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for transferring movie information between client and server.
 * <p>
 * This class is used to decouple the internal `Movie` entity from the external representation.
 * It can be used in request and response bodies when interacting with the Movie REST API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    /**
     * The title of the movie.
     */
    private String title;

    /**
     * The name of the movie's director.
     */
    private String director;

    /**
     * The year the movie was released.
     */
    private String release_year;

    /**
     * The genre(s) of the movie.
     */
    private String genre;

    /**
     * Flag indicating whether the movie has been watched.
     */
    private boolean watched;

    /**
     * User rating of the movie (typically from 1 to 5).
     */
    private int rating;

    /**
     * Path to the stored image representing the movie (e.g. poster).
     */
    private String imagePath;
}
