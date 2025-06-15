package com.moviewatchlist.moviewatchlist;

import com.moviewatchlist.client.OmdbClient;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.repository.MovieRepository;
import com.moviewatchlist.service.ImageService;
import com.moviewatchlist.service.MovieService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MovieService} using Mockito.
 * <p>
 * This test class verifies the behavior of core service methods:
 * adding a movie, deleting it, updating its watched status,
 * and updating the rating.
 */
@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository repository;

    @Mock
    private ImageService imageService;

    @Mock
    private OmdbClient omdbClient;

    @InjectMocks
    private MovieService service;

    /**
     * Verifies that {@link MovieService#addMovie(String)} correctly
     * calls the repository's save method when valid data is returned
     * from external clients.
     */
    @Test
    void testAddMovieShouldCallRepository() {
        String title = "Inception";
        Movie dummy = Movie.builder()
                        .title(title)
                        .release_year("2010")
                        .director("Nolan")
                        .genre("Sci-Fi")
                        .watched(false)
                        .rating(0)
                        .build();

        when(omdbClient.fetchMovie(title)).thenReturn(dummy);
        when(imageService.fetchImage(title)).thenReturn("mock/path.jpg");

        service.addMovie(title);

        verify(repository).save(any(Movie.class));
    }

    /**
     * Verifies that {@link MovieService#deleteMovie(Long)} correctly
     * invokes repository deletion.
     */
    @Test
    void testDeleteMovie() {
        service.deleteMovie(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    /**
     * Verifies that {@link MovieService#updateWatched(Long, boolean)}
     * updates the movie's watched status and persists the change.
     */
    @Test
    void testUpdateWatched() {
        Movie movie = Movie.builder().id(1L).watched(false).build();
        when(repository.findById(1L)).thenReturn(Optional.of(movie));

        service.updateWatched(1L, true);
        verify(repository).save(movie);
        assertTrue(movie.isWatched());
    }

    /**
     * Verifies that {@link MovieService#updateRating(Long, int)}
     * updates the rating of a movie and saves it.
     */
    @Test
    void testUpdateRating() {
        Movie movie = Movie.builder().id(1L).rating(0).build();
        when(repository.findById(1L)).thenReturn(Optional.of(movie));

        service.updateRating(1L, 4);
        verify(repository).save(movie);
        assertEquals(4, movie.getRating());
    }

        /**
     * Verifies that {@link MovieService#getAllMovies(int, int)}
     * correctly returns a paginated list of movies as expected
     * from the repository. This ensures that pagination parameters
     * are handled properly and passed through to the data layer.
     */
    @Test
    void testGetAllMoviesReturnsPagedResult() {
        List<Movie> movieList = List.of(
                Movie.builder().title("Inception").build(),
                Movie.builder().title("Interstellar").build());
        Page<Movie> page = new PageImpl<>(movieList);

        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<Movie> result = service.getAllMovies(0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals("Inception", result.getContent().get(0).getTitle());
        verify(repository).findAll(PageRequest.of(0, 10));
    }

    /**
     * Verifies that {@link MovieService#getSimilarMovies(Long)} fetches
     * a list of similar movie titles by retrieving the TMDB ID and calling
     * the TMDB similar movies endpoint. Uses a spy to mock internal
     * calls to {@code fetchTmdbId} and {@code fetchSimilarFromTMDB}.
     */
    @Test
    void testGetSimilarMoviesReturnsList() {
        Long movieId = 1L;
        String title = "Inception";
        Movie movie = Movie.builder().id(movieId).title(title).build();

        when(repository.findById(movieId)).thenReturn(Optional.of(movie));

        MovieService spyService = spy(service);

        doReturn(123L).when(spyService).fetchTmdbId(title);
        doReturn(List.of("Tenet", "The Prestige")).when(spyService).fetchSimilarFromTMDB(123L);

        List<String> result = spyService.getSimilarMovies(movieId);

        assertEquals(2, result.size());
        assertTrue(result.contains("Tenet"));
        assertTrue(result.contains("The Prestige"));
    }
}
