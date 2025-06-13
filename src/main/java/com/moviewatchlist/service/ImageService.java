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

@Service
public class ImageService {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public String fetchImage(String title) {
        try {
            // 1. Поиск фильма на TMDB
            String searchUrl = String.format("https://api.themoviedb.org/3/search/movie?query=%s&api_key=%s",
                    title.replace(" ", "+"), tmdbApiKey);

            HttpResponse<String> searchResponse = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder().uri(URI.create(searchUrl)).GET().build(),
                          HttpResponse.BodyHandlers.ofString());

            JsonNode searchJson = mapper.readTree(searchResponse.body());
            JsonNode results = searchJson.get("results");

            if (results == null || !results.elements().hasNext())
                throw new RuntimeException("TMDB: Movie not found");

            String posterPath = results.get(0).get("poster_path").asText();

            // 2. Скачивание изображения
            String imageUrl = "https://image.tmdb.org/t/p/w780" + posterPath;
            InputStream imageStream = URI.create(imageUrl).toURL().openStream();

            // 3. Сохранение в файловую систему
            String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
            File dir = new File("images/" + safeTitle);
            Files.createDirectories(dir.toPath());

            File output = new File(dir, "poster.jpg");
            FileUtils.copyInputStreamToFile(imageStream, output);

            return output.getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch image: " + e.getMessage(), e);
        }
    }
}
