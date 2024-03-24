package com.moviesbattle.service;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moviesbattle.Mocks;
import com.moviesbattle.dto.MovieDto;
import com.moviesbattle.dto.mapper.MovieMapper;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.external.OmdbRestClient;
import com.moviesbattle.model.Movie;
import com.moviesbattle.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private OmdbRestClient omdbRestClient;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    @Test
    void create_isSuccess() {
        final MovieDto movieDto = Mocks.movieDto();
        final Movie movie = Mocks.movie();

        when(omdbRestClient.getByImdb(anyString())).thenReturn(movieDto);
        when(movieMapper.map(any())).thenReturn(movie);

        movieService.create("tt12345");

        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void findByImdb_isSuccess() {
        final Movie mockedMovie = Mocks.movie();

        when(movieRepository.findByImdb(anyString())).thenReturn(Optional.of(mockedMovie));

        final Movie movie = movieService.findByImdb("tt12345");

        assertAll(
                () -> assertNotNull(movie),
                () -> assertEquals(mockedMovie.getId(), movie.getId())
        );
    }

    @Test
    void findByImdb_whenMovieDoesNotExists_shouldThrowNotFoundException() {
        when(movieRepository.findByImdb(anyString())).thenReturn(Optional.empty());

        final NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> movieService.findByImdb("tt12345"));

        assertEquals("Movie not found", notFoundException.getMessage());
    }

    @Test
    void getRandomMovie_isSuccess() {
        final Movie mockedMovie = Mocks.movie();

        when(movieRepository.count()).thenReturn(1L);
        when(movieRepository.findById(any())).thenReturn(Optional.of(mockedMovie));

        final Movie randomMovie = movieService.getRandomMovie();

        assertAll(
                () -> assertNotNull(randomMovie),
                () -> assertEquals(mockedMovie.getId(), randomMovie.getId())
        );
    }

    @Test
    void getRandomMovie_whenMovieDoesNotExists_shouldReturnNull() {
        when(movieRepository.count()).thenReturn(100L);
        when(movieRepository.findById(any())).thenReturn(Optional.empty());

        final Movie randomMovie = movieService.getRandomMovie();

        assertNull(randomMovie);
    }

    @Test
    void existsMovie_whenCountIsBiggerThanZero_shouldReturnTrue() {
        when(movieRepository.count()).thenReturn(3L);

        assertTrue(movieService.existsMovie());
    }

    @Test
    void existsMovie_whenCountIsZero_shouldReturnFalse() {
        when(movieRepository.count()).thenReturn(0L);

        assertFalse(movieService.existsMovie());
    }
}