package com.moviewatchlist.controller;

import com.moviewatchlist.dto.MovieDTO;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> addMovie(@RequestBody MovieDTO dto) {
        service.addMovie(dto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public Page<Movie> getAllMovies(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return service.getAllMovies(page, size);
    }

    @PutMapping("/{id}/watched")
    public ResponseEntity<Void> updateWatched(@PathVariable Long id, @RequestParam boolean watched) {
        service.updateWatched(id, watched);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(@PathVariable Long id, @RequestParam int rating) {
        service.updateRating(id, rating);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        service.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
