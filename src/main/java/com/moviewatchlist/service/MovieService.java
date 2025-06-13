package com.moviewatchlist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MovieService {
    private final MovieRepository repo;
    private final ImageService imageService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    public MovieService(MovieRepository repo, ImageService imageService) {
        this.repo = repo;
        this.imageService = imageService;
    }

    public void addMovie(String title) {
        Movie movie = fetchFromOMDb(title);
        String imagePath = imageService.fetchImage(title);
        movie.setImagePath(imagePath);
        repo.save(movie);
    }

    public Movie fetchFromOMDb(String title) {
        try {
            String url = String.format("https://www.omdbapi.com/?t=%s&apikey=%s",
                    title.replace(" ", "+"), omdbApiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode json = mapper.readTree(response.body());

            if (json.has("Error")) {
                throw new RuntimeException("Movie not found: " + title);
            }

            return Movie.builder()
                    .title(json.get("Title").asText())
                    .director(json.get("Director").asText())
                    .release_year(json.get("Year").asText())
                    .genre(json.get("Genre").asText())
                    .watched(false)
                    .rating(0)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch from OMDb: " + e.getMessage(), e);
        }
    }

    public Page<Movie> getAllMovies(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    public void updateWatched(Long id, boolean watched) {
        Movie movie = repo.findById(id).orElseThrow();
        movie.setWatched(watched);
        repo.save(movie);
    }

    public void updateRating(Long id, int rating) {
        Movie movie = repo.findById(id).orElseThrow();
        movie.setRating(rating);
        repo.save(movie);
    }

    public void deleteMovie(Long id) {
        repo.deleteById(id);
    }
}
