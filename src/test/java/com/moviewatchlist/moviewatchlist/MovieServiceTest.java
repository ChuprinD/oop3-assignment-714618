package com.moviewatchlist.moviewatchlist;

import com.moviewatchlist.model.Movie;
import com.moviewatchlist.repository.MovieRepository;
import com.moviewatchlist.service.ImageService;
import com.moviewatchlist.service.MovieService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
}
