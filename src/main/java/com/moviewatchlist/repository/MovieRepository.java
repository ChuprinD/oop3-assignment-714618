package com.moviewatchlist.repository;

import com.moviewatchlist.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MovieRepository {
    private final JdbcTemplate jdbc;

    public MovieRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(Movie movie) {
        String sql = "INSERT INTO movies (title, director, year, genre, watched, rating, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, movie.getTitle(), movie.getDirector(), movie.getYear(),
                    movie.getGenre(), movie.isWatched(), movie.getRating(), movie.getImagePath());
    }

    // TODO: findAll, findById, updateWatched, updateRating, delete
}
