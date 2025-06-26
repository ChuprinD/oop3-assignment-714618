package com.moviewatchlist.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class TmdbClient {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches the TMDB movie ID based on the movie title.
     *
     * @param title The movie title to search for
     * @return TMDB movie ID
     */
    public Long fetchTmdbId(String title) {
        try {
            String query = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = String.format("https://api.themoviedb.org/3/search/movie?query=%s&api_key=%s", query, tmdbApiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.path("results");

            if (!results.isArray() || results.isEmpty()) {
                throw new RuntimeException("TMDB: No movie found for title: " + title);
            }

            return results.get(0).get("id").asLong();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch TMDB ID: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches similar movie titles from TMDB using a given TMDB movie ID.
     *
     * @param tmdbId The TMDB movie ID
     * @return List of similar movie titles
     */
    public List<String> fetchSimilarMovies(Long tmdbId) {
        try {
            String url = String.format("https://api.themoviedb.org/3/movie/%d/similar?api_key=%s", tmdbId, tmdbApiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.path("results");

            List<String> similarTitles = new ArrayList<>();
            for (JsonNode node : results) {
                similarTitles.add(node.get("title").asText());
            }

            return similarTitles;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch similar movies: " + e.getMessage(), e);
        }
    }
}
