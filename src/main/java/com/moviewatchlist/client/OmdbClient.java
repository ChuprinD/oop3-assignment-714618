package com.moviewatchlist.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviewatchlist.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A client for fetching movie data from the OMDb API.
 * <p>
 * This component is responsible for constructing and sending HTTP requests
 * to the OMDb API based on a movie title, and then parsing the JSON response
 * into a {@link Movie} object.
 */
@Component
public class OmdbClient {

    /**
     * The OMDb API key, injected from application properties.
     */
    @Value("${omdb.api.key}")
    private String omdbApiKey;

    /**
     * Jackson object mapper for parsing JSON responses.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches a movie from the OMDb API by its title.
     *
     * @param title The title of the movie to search for.
     * @return A {@link Movie} object populated with the retrieved information.
     * @throws RuntimeException If the movie is not found or the request fails.
     */
    public Movie fetchMovie(String title) {
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
}
