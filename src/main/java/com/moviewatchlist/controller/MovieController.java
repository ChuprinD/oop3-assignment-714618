package com.moviewatchlist.controller;

import com.moviewatchlist.dto.MovieDTO;
import com.moviewatchlist.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: GET with pagination, PUT /watched, PUT /rating, DELETE
}
