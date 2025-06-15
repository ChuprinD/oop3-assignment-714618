package com.moviewatchlist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviewatchlist.client.OmdbClient;
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
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class responsible for core business logic related to Movie management.
 * <p>
 * This includes:
 * <ul>
 *     <li>Adding a movie to the database</li>
 *     <li>Fetching data from OMDb and TMDB</li>
 *     <li>Updating movie attributes (watched/rating)</li>
 *     <li>Deleting movies</li>
 *     <li>Returning similar movies based on TMDB data</li>
 * </ul>
 */
@Service
public class MovieService {

    private final MovieRepository repo;
    private final ImageService imageService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OmdbClient omdbClient;

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    /**
     * Constructs MovieService with required dependencies.
     *
     * @param repo         movie repository for database access
     * @param imageService service to download movie images
     * @param omdbClient   client to fetch OMDb movie metadata
     */
    public MovieService(MovieRepository repo, ImageService imageService, OmdbClient omdbClient) {
        this.repo = repo;
        this.imageService = imageService;
        this.omdbClient = omdbClient;
    }

    /**
     * Adds a movie by title by calling OMDb and TMDB, storing full data to database.
     *
     * @param title the title of the movie
     */
    public void addMovie(String title) {
        try {
            CompletableFuture<Movie> movieFuture = CompletableFuture.supplyAsync(() -> omdbClient.fetchMovie(title));
            CompletableFuture<String> imageFuture = CompletableFuture.supplyAsync(() -> imageService.fetchImage(title));

            Movie movie = movieFuture.join();
            movie.setImagePath(imageFuture.join());

            repo.save(movie);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add movie: " + e.getMessage(), e);
        }
    }


    /**
     * Fetches a page of movies from the watchlist.
     *
     * @param page page index (zero-based)
     * @param size number of elements per page
     * @return a Page of Movie entities
     */
    public Page<Movie> getAllMovies(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    /**
     * Updates the "watched" flag of a movie.
     *
     * @param id      ID of the movie
     * @param watched new watched status
     */
    public void updateWatched(Long id, boolean watched) {
        Movie movie = repo.findById(id).orElseThrow();
        movie.setWatched(watched);
        repo.save(movie);
    }

    /**
     * Updates the rating of a movie.
     *
     * @param id     ID of the movie
     * @param rating new rating (1–5)
     */
    public void updateRating(Long id, int rating) {
        Movie movie = repo.findById(id).orElseThrow();
        movie.setRating(rating);
        repo.save(movie);
    }

    /**
     * Deletes a movie from the watchlist.
     *
     * @param id ID of the movie
     */
    public void deleteMovie(Long id) {
        repo.deleteById(id);
    }

    /**
     * Returns a list of similar movies based on TMDB API using stored title.
     *
     * @param id ID of the reference movie
     * @return list of titles of similar movies
     */
    public List<String> getSimilarMovies(Long id) {
        Movie movie = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Long tmdbId = fetchTmdbId(movie.getTitle());
        return fetchSimilarFromTMDB(tmdbId);
    }

    /**
     * Searches for a TMDB movie ID by movie title.
     *
     * @param title title of the movie
     * @return TMDB internal movie ID
     */
    public Long fetchTmdbId(String title) {
        try {
            String searchUrl = String.format(
                    "https://api.themoviedb.org/3/search/movie?query=%s&api_key=%s",
                    URLEncoder.encode(title, StandardCharsets.UTF_8), tmdbApiKey);

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder().uri(URI.create(searchUrl)).GET().build(),
                            HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.path("results");

            if (results.isEmpty())
                throw new RuntimeException("No TMDB results");

            return results.get(0).get("id").asLong();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch TMDB ID", e);
        }
    }

    /**
     * Fetches titles of similar movies from TMDB API.
     *
     * @param tmdbId TMDB movie ID
     * @return list of similar movie titles
     */
    public List<String> fetchSimilarFromTMDB(Long tmdbId) {
        try {
            String url = String.format(
                    "https://api.themoviedb.org/3/movie/%d/similar?api_key=%s",
                    tmdbId, tmdbApiKey
            );

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                            HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            List<String> titles = new ArrayList<>();

            for (JsonNode node : root.path("results")) {
                titles.add(node.get("title").asText());
            }

            return titles;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch similar movies", e);
        }
    }
}
