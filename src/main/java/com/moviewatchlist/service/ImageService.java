package com.moviewatchlist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for fetching movie-related images from TMDB (The Movie Database).
 * <p>
 * This service performs the following:
 * <ul>
 *     <li>Searches for a movie ID on TMDB using a movie title.</li>
 *     <li>Retrieves image metadata (posters and backdrops) for that movie.</li>
 *     <li>Downloads up to 3 images and stores them locally in the file system.</li>
 *     <li>Returns the path to the first downloaded image.</li>
 * </ul>
 */
@Service
public class ImageService {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs the ImageService with a {@link RestTemplate} dependency.
     *
     * @param restTemplate Spring-managed HTTP client used for API calls
     */
    public ImageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches up to 3 images for a given movie title using the TMDB API.
     * Downloads the images and stores them in a local directory named after the sanitized title.
     * Returns the absolute path to the first downloaded image, which serves as the representative image.
     *
     * @param title The movie title to search for
     * @return Absolute path to the first downloaded image
     * @throws RuntimeException if the movie is not found or image retrieval fails
     */
    public String fetchImage(String title) {
        try {
            // 1. Search for movie ID by title
            String encodedTitle = UriUtils.encode(title, StandardCharsets.UTF_8);
            String searchUrl = String.format(
                    "https://api.themoviedb.org/3/search/movie?query=%s&api_key=%s",
                    encodedTitle, tmdbApiKey);

            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode searchJson = mapper.readTree(searchResponse);
            JsonNode results = searchJson.path("results");

            if (!results.elements().hasNext()) {
                throw new RuntimeException("TMDB: Movie not found");
            }

            String movieId = results.get(0).get("id").asText();

            // 2. Fetch images metadata
            String imagesUrl = String.format(
                    "https://api.themoviedb.org/3/movie/%s/images?api_key=%s",
                    movieId, tmdbApiKey);

            String imagesResponse = restTemplate.getForObject(imagesUrl, String.class);
            JsonNode imagesJson = mapper.readTree(imagesResponse);

            List<String> imagePaths = new ArrayList<>();

            // Collect poster paths
            JsonNode posters = imagesJson.path("posters");
            posters.elements().forEachRemaining(node -> {
                if (imagePaths.size() < 3) {
                    imagePaths.add(node.path("file_path").asText());
                }
            });

            // Fallback: add backdrops if not enough posters
            if (imagePaths.size() < 3) {
                JsonNode backdrops = imagesJson.path("backdrops");
                backdrops.elements().forEachRemaining(node -> {
                    if (imagePaths.size() < 3) {
                        imagePaths.add(node.path("file_path").asText());
                    }
                });
            }

            if (imagePaths.isEmpty()) {
                throw new RuntimeException("No images found for movie");
            }

            // 3. Download images to local file system
            String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
            File dir = new File("images/" + safeTitle);
            Files.createDirectories(dir.toPath());

            for (int i = 0; i < imagePaths.size(); i++) {
                String path = imagePaths.get(i);
                String imageUrl = "https://image.tmdb.org/t/p/w780" + path;

                byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
                File output = new File(dir, "image" + (i + 1) + ".jpg");
                FileUtils.writeByteArrayToFile(output, imageBytes);
            }

            // 4. Return path to first image
            return new File(dir, "image1.jpg").getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch image: " + e.getMessage(), e);
        }
    }
}
