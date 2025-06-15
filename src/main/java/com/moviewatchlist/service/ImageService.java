package com.moviewatchlist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Service for fetching and downloading movie-related images from TMDB API.
 *
 * <p>This class performs the following operations:
 * <ul>
 *   <li>Searches TMDB for a movie ID based on a title.</li>
 *   <li>Retrieves poster/backdrop image metadata for that movie.</li>
 *   <li>Downloads up to 3 images from TMDB and stores them locally on the file system.</li>
 *   <li>Returns the path to the first saved image as a representative image.</li>
 * </ul>
 *
 * <p>Used in conjunction with MovieService to enrich movie entries with visual content.
 */
@Service
public class ImageService {

    /**
     * TMDB API key, injected from application.properties.
     */
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches up to 3 images for a given movie title from TMDB and saves them to disk.
     *
     * @param title the title of the movie to fetch images for
     * @return path to the first image downloaded
     * @throws RuntimeException if any error occurs during the fetch or download process
     */
    public String fetchImage(String title) {
        try {
            // Search for the movie by title
            String searchUrl = String.format(
                    "https://api.themoviedb.org/3/search/movie?query=%s&api_key=%s",
                    title.replace(" ", "+"), tmdbApiKey
            );

            HttpResponse<String> searchResponse = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder().uri(URI.create(searchUrl)).GET().build(),
                          HttpResponse.BodyHandlers.ofString());

            JsonNode searchJson = mapper.readTree(searchResponse.body());
            JsonNode results = searchJson.get("results");

            if (results == null || !results.elements().hasNext())
                throw new RuntimeException("TMDB: Movie not found");

            String movieId = results.get(0).get("id").asText();

            // Fetch images
            String imagesUrl = String.format(
                    "https://api.themoviedb.org/3/movie/%s/images?api_key=%s",
                    movieId, tmdbApiKey
            );

            HttpResponse<String> imagesResponse = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder().uri(URI.create(imagesUrl)).GET().build(),
                          HttpResponse.BodyHandlers.ofString());

            JsonNode imagesJson = mapper.readTree(imagesResponse.body());

            List<String> imagePaths = new ArrayList<>();

            JsonNode backdrops = imagesJson.get("posters");
            if (backdrops != null && backdrops.isArray()) {
                StreamSupport.stream(backdrops.spliterator(), false)
                        .limit(3)
                        .map(node -> node.get("file_path").asText())
                        .forEach(imagePaths::add);
            }

            if (imagePaths.size() < 3) {
                JsonNode posters = imagesJson.get("backdrops");
                if (posters != null && posters.isArray()) {
                    StreamSupport.stream(posters.spliterator(), false)
                            .filter(node -> imagePaths.size() < 3)
                            .map(node -> node.get("file_path").asText())
                            .forEach(imagePaths::add);
                }
            }

            // Download images to local directory
            String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
            File dir = new File("images/" + safeTitle);
            Files.createDirectories(dir.toPath());

            for (int i = 0; i < imagePaths.size(); i++) {
                String path = imagePaths.get(i);
                String imageUrl = "https://image.tmdb.org/t/p/w780" + path;
                InputStream imageStream = URI.create(imageUrl).toURL().openStream();
                File output = new File(dir, "image" + (i + 1) + ".jpg");
                FileUtils.copyInputStreamToFile(imageStream, output);
            }

            return new File(dir, "image1.jpg").getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch image: " + e.getMessage(), e);
        }
    }
}
