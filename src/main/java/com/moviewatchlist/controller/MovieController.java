package com.moviewatchlist.controller;

import com.moviewatchlist.dto.MovieDTO;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing the movie watchlist.
 * <p>
 * Provides endpoints to add, retrieve, update, and delete movies,
 * as well as to fetch similar movies and update metadata like "watched" status and rating.
 */
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService service;

    /**
     * Constructs the controller with injected movie service.
     *
     * @param service the service layer for movie operations
     */
    public MovieController(MovieService service) {
        this.service = service;
    }

    /**
     * Adds a new movie to the watchlist based on its title.
     *
     * @param dto a DTO containing the title of the movie
     * @return HTTP 201 Created if the movie is successfully added
     */
    @PostMapping
    public ResponseEntity<Void> addMovie(@RequestBody MovieDTO dto) {
        service.addMovie(dto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves a paginated list of movies from the watchlist.
     *
     * @param page the page number (default is 0)
     * @param size the number of elements per page (default is 10)
     * @return a paginated response of movies
     */
    @GetMapping
    public Page<Movie> getAllMovies(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return service.getAllMovies(page, size);
    }

    /**
     * Updates the "watched" status of a specific movie.
     *
     * @param id      the ID of the movie
     * @param watched the new watched status
     * @return HTTP 200 OK on success
     */
    @PutMapping("/{id}/watched")
    public ResponseEntity<Void> updateWatched(@PathVariable Long id, @RequestParam boolean watched) {
        service.updateWatched(id, watched);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the rating of a specific movie.
     *
     * @param id     the ID of the movie
     * @param rating the new rating (1–5)
     * @return HTTP 200 OK on success
     */
    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(@PathVariable Long id, @RequestParam int rating) {
        service.updateRating(id, rating);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a movie from the watchlist.
     *
     * @param id the ID of the movie
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        service.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of similar movie titles based on the original movie ID.
     *
     * @param id the ID of the movie
     * @return a list of similar movie titles
     */
    @GetMapping("/{id}/similar")
    public List<String> getSimilarMovies(@PathVariable Long id) {
        return service.getSimilarMovies(id);
    }
}
