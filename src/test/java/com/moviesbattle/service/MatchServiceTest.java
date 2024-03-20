package com.moviesbattle.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moviesbattle.Mocks;
import com.moviesbattle.exception.MatchExistsException;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.Player;
import com.moviesbattle.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @InjectMocks
    private MatchService matchService;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PlayerService playerService;

    @Test
    void findByLoggedPlayer_isSuccess() {
        final Player mockedPlayer = Mocks.player();
        final Match mockedMatch = Mocks.match();

        when(playerService.getLoggedUser()).thenReturn(mockedPlayer);
        when(matchRepository.findByPlayerAndStatus(any(), any())).thenReturn(Optional.of(mockedMatch));

        final Match match = matchService.findByLoggedPlayer();

        assertAll(
                () -> assertNotNull(match),
                () -> assertEquals(mockedMatch.getId(), match.getId())
        );
    }

    @Test
    void findByLoggedPlayer_whenMatchDoesNotExists_shouldThrowNotFoundException() {
        final Player mockedPlayer = Mocks.player();

        when(playerService.getLoggedUser()).thenReturn(mockedPlayer);
        when(matchRepository.findByPlayerAndStatus(any(), any())).thenReturn(Optional.empty());

        final NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> matchService.findByLoggedPlayer());

        assertEquals("Match not found", notFoundException.getMessage());
    }

    @Test
    void startMatch_isSuccess() {
        final Player mockedPlayer = Mocks.player();

        when(playerService.getLoggedUser()).thenReturn(mockedPlayer);
        when(matchRepository.existsByPlayerAndStatus(any(), any())).thenReturn(false);

        matchService.startMatch();

        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void startMatch_whenMatchAlreadyExists_shouldMatchExistsException() {
        final Player mockedPlayer = Mocks.player();

        when(playerService.getLoggedUser()).thenReturn(mockedPlayer);
        when(matchRepository.existsByPlayerAndStatus(any(), any())).thenReturn(true);

        final MatchExistsException matchExistsException =
                assertThrows(MatchExistsException.class, () -> matchService.startMatch());

        assertEquals("Match already exists", matchExistsException.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void endMatch_isSuccess() {
        final Player mockedPlayer = Mocks.player();
        final Match mockedMatch = Mocks.match();

        when(playerService.getLoggedUser()).thenReturn(mockedPlayer);
        when(matchRepository.findByPlayerAndStatus(any(), any())).thenReturn(Optional.of(mockedMatch));

        final String result = matchService.endMatch();

        assertEquals("Final score: 100.0", result);
    }

    @Test
    void endMatch_whenMatchDoesNotExists_shouldThrowNotFoundException() {
        final Player mockedPlayer = Mocks.player();

        when(playerService.getLoggedUser()).thenReturn(mockedPlayer);
        when(matchRepository.findByPlayerAndStatus(any(), any())).thenReturn(Optional.empty());

        final NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> matchService.endMatch());

        assertEquals("Match not found", notFoundException.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

}