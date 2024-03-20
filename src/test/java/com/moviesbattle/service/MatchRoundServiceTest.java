package com.moviesbattle.service;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moviesbattle.Mocks;
import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
import com.moviesbattle.exception.AnswerNotValidException;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchRound;
import com.moviesbattle.model.Movie;
import com.moviesbattle.repository.MatchRoundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchRoundServiceTest {

    @Mock
    private MatchRoundRepository matchRoundRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchRoundService matchRoundService;

    @Test
    void nextRound_whenRoundAlreadyExists_shouldReturnIt() {
        final Match mockedMatch = Mocks.match();
        final MatchRound mockedMatchRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.of(mockedMatchRound));
        when(movieService.findByImdb(mockedMatchRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedMatchRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);

        final RoundDto roundDto = matchRoundService.nextRound();

        assertAll(
                () -> assertEquals(mockedFirstMovie.getTitle(), roundDto.firstMovie()),
                () -> assertEquals(mockedSecondMovie.getTitle(), roundDto.secondMovie()),
                () -> verify(matchRoundRepository, never()).save(any(MatchRound.class))
        );
    }

    @Test
    void nextRound_whenRoundDoesNotExists_shouldCreateNewOne() {
        final Match mockedMatch = Mocks.match();
        final MatchRound mockedMatchRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.empty());
        when(matchRoundRepository.save(any(MatchRound.class))).thenReturn(mockedMatchRound);
        when(movieService.findByImdb(mockedMatchRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedMatchRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);
        when(matchRoundRepository.isMovieBeingUsed(any(), any(), any())).thenReturn(false);

        final int[] movieCalls = { 0 };
        final Movie[] movies = { mockedFirstMovie, mockedSecondMovie };
        when(movieService.getRandomMovie()).thenAnswer(invocation -> movies[movieCalls[0]++]);

        final RoundDto roundDto = matchRoundService.nextRound();

        assertAll(
                () -> assertEquals(mockedFirstMovie.getTitle(), roundDto.firstMovie()),
                () -> assertEquals(mockedSecondMovie.getTitle(), roundDto.secondMovie()),
                () -> verify(matchRoundRepository, times(1)).save(any(MatchRound.class)),
                () -> verify(movieService, times(2)).getRandomMovie()
        );
    }

    @Test
    void nextRound_whenRoundDoesNotExistsAndMovieisAlreadyUsed_shouldCreateNewOneWithMultipleCalls() {
        final Match mockedMatch = Mocks.match();
        final MatchRound mockedMatchRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.empty());
        when(matchRoundRepository.save(any(MatchRound.class))).thenReturn(mockedMatchRound);
        when(movieService.findByImdb(mockedMatchRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedMatchRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);

        final int[] movieCalls = { 0 };
        final Movie[] movies = { mockedFirstMovie, mockedFirstMovie, mockedSecondMovie };
        when(movieService.getRandomMovie()).thenAnswer(invocation -> movies[movieCalls[0]++]);

        final int[] movieUsedCalls = { 0 };
        final boolean[] moviesUsed = { true, false, false };
        when(matchRoundRepository.isMovieBeingUsed(any(), any(), any())).thenAnswer(invocation -> moviesUsed[movieUsedCalls[0]++]);

        final RoundDto roundDto = matchRoundService.nextRound();

        assertAll(
                () -> assertEquals(mockedFirstMovie.getTitle(), roundDto.firstMovie()),
                () -> assertEquals(mockedSecondMovie.getTitle(), roundDto.secondMovie()),
                () -> verify(matchRoundRepository, times(1)).save(any(MatchRound.class)),
                () -> verify(movieService, times(3)).getRandomMovie()
        );
    }

    @Test
    void answer_whenMatchDoesNotExists_shouldThrowNotFoundException() {
        when(matchService.findByLoggedPlayer()).thenThrow(new NotFoundException("Match not found"));

        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        assertThrows(NotFoundException.class, () -> matchRoundService.answer(roundAnswerDto));
    }

    @Test
    void answer_whenRoundDoesNotExists_shouldThrowNotFoundException() {
        final Match mockedMatch = Mocks.match();
        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.empty());

        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        final NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> matchRoundService.answer(roundAnswerDto));

        assertEquals("Round not found", notFoundException.getMessage());
    }

    @Test
    void answer_whenAnswerNotValid_shouldThrowAnswerNotValidException() {
        final Match mockedMatch = Mocks.match();
        final MatchRound mockedRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.of(mockedRound));
        when(movieService.findByImdb(mockedRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);

        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(3);

        assertThrows(AnswerNotValidException.class, () -> matchRoundService.answer(roundAnswerDto));
    }

    @Test
    void answer_whenAnswerIsCorrect_shouldReturnCorrectMessage() {
        final Match mockedMatch = Mocks.match();
        final MatchRound mockedRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.of(mockedRound));
        when(movieService.findByImdb(mockedRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);

        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        final String answer = matchRoundService.answer(roundAnswerDto);

        assertEquals("Your answer is correct!", answer);
    }

    @Test
    void answer_whenAnswerIsWrong_shouldReturnWrongMessage() {
        final Match mockedMatch = Mocks.match();
        final MatchRound mockedRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.of(mockedRound));
        when(movieService.findByImdb(mockedRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);

        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(2);

        final String answer = matchRoundService.answer(roundAnswerDto);

        assertEquals("Your answer is wrong. You still have 1 credit(s)", answer);
    }

    @Test
    void answer_whenAnswerIsWrongAndHaveNoMoreCredits_shouldReturnWrongMessageWithFinalScore() {
        final Match mockedMatch = Mocks.match(1, 6);
        final MatchRound mockedRound = Mocks.matchRound();
        final Movie mockedFirstMovie = Mocks.movie("Spider Man", 100000, 87.2);
        final Movie mockedSecondMovie = Mocks.movie("Indiana Jones", 52132, 55);

        when(matchService.findByLoggedPlayer()).thenReturn(mockedMatch);
        when(matchRoundRepository.findByMatchAndStatus(any(), any())).thenReturn(Optional.of(mockedRound));
        when(movieService.findByImdb(mockedRound.getFirstMovieImdb())).thenReturn(mockedFirstMovie);
        when(movieService.findByImdb(mockedRound.getSecondMovieImdb())).thenReturn(mockedSecondMovie);

        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(2);

        final String answer = matchRoundService.answer(roundAnswerDto);

        assertEquals("Your answer is wrong. You have no more credits. Final score: 1200.0", answer);
    }

}