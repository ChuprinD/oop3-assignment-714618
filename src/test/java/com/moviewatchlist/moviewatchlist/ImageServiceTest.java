package com.moviewatchlist.moviewatchlist;

import com.moviewatchlist.service.ImageService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit test for {@link ImageService}.
 * Verifies downloading and saving images from the TMDB API using a mocked RestTemplate.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImageServiceTest {

    private ImageService service;
    private RestTemplate restTemplate;

    private final String mockSearchJson = """
        {
          "results": [
            { "id": 123 }
          ]
        }
        """;

    private final String mockImagesJson = """
        {
          "posters": [
            { "file_path": "/image1.jpg" },
            { "file_path": "/image2.jpg" },
            { "file_path": "/image3.jpg" }
          ]
        }
        """;

    private final byte[] fakeImage = new byte[] {
        (byte) 0xFF, (byte) 0xD8, // JPEG SOI marker
        (byte) 0xFF, (byte) 0xD9  // JPEG EOI marker
    };

    /**
     * Sets up the mocked service before each test.
     */
    @BeforeEach
    void setup() {
        restTemplate = Mockito.mock(RestTemplate.class);
        service = new ImageService(restTemplate);

        // Search TMDB
        Mockito.when(restTemplate.getForObject(
                contains("search/movie"), eq(String.class)))
                .thenReturn(mockSearchJson);

        // Image metadata
        Mockito.when(restTemplate.getForObject(
                contains("images"), eq(String.class)))
                .thenReturn(mockImagesJson);

        // Fake image binary
        Mockito.when(restTemplate.getForObject(
                contains("image.tmdb.org"), eq(byte[].class)))
                .thenReturn(fakeImage);
    }

    /**
     * Verifies that 3 image files are downloaded and saved correctly.
     */
    @Test
    void fetchImage_shouldDownloadAndReturnImagePath() throws Exception {
        String title = "Inception";
        String path = service.fetchImage(title);

        File outputDir = new File("images/Inception");
        assertTrue(outputDir.exists(), "Output directory should exist");

        File img1 = new File(outputDir, "image1.jpg");
        File img2 = new File(outputDir, "image2.jpg");
        File img3 = new File(outputDir, "image3.jpg");

        assertTrue(img1.exists(), "image1.jpg should exist");
        assertTrue(img2.exists(), "image2.jpg should exist");
        assertTrue(img3.exists(), "image3.jpg should exist");

        // Validate contents
        assertArrayEquals(fakeImage, Files.readAllBytes(img1.toPath()));
        assertArrayEquals(fakeImage, Files.readAllBytes(img2.toPath()));
        assertArrayEquals(fakeImage, Files.readAllBytes(img3.toPath()));

        // Validate return path
        assertEquals(img1.getAbsolutePath(), path);
    }

    /**
     * Deletes test files after all tests.
     */
    @AfterAll
    void cleanup() throws Exception {
        FileUtils.deleteDirectory(new File("images/Inception"));
    }
}
