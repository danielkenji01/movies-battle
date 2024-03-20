package com.moviesbattle.service;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moviesbattle.Mocks;
import com.moviesbattle.dto.PlayerDto;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.exception.PlayerAlreadyExistsException;
import com.moviesbattle.model.Player;
import com.moviesbattle.repository.PlayerRepository;
import com.moviesbattle.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private MockedStatic<JwtUtil> jwtUtilMockedStatic;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    void setup() {
        jwtUtilMockedStatic = mockStatic(JwtUtil.class);
    }

    @AfterEach
    void tearDown() {
        jwtUtilMockedStatic.close();
    }

    @Test
    void existsPlayers_whenCountIsBiggerThanZero_shouldReturnTrue() {
        when(playerRepository.count()).thenReturn(3L);

        assertTrue(playerService.existsPlayers());
    }

    @Test
    void existsPlayers_whenCountIsZero_shouldReturnFalse() {
        when(playerRepository.count()).thenReturn(0L);

        assertFalse(playerService.existsPlayers());
    }

    @Test
    void createPlayer_isSuccess() {
        final PlayerDto playerDto = new PlayerDto("charles", "testing");

        when(playerRepository.existsByUsername(anyString())).thenReturn(false);

        playerService.createPlayer(playerDto);

        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void createPlayer_whenPlayerAlreadyExists_shouldThrowPlayerAlreadyExistsException() {
        final PlayerDto playerDto = new PlayerDto("charles", "testing");

        when(playerRepository.existsByUsername(anyString())).thenReturn(true);

        assertAll(
                () -> assertThrows(PlayerAlreadyExistsException.class, () -> playerService.createPlayer(playerDto)),
                () -> verify(playerRepository, never()).save(any(Player.class))
        );
    }

    @Test
    void getLoggedUser_isSuccess() {
        final Player mockedPlayer = Mocks.player();

        jwtUtilMockedStatic.when(JwtUtil::getLoggedUser).thenReturn("john");
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.of(mockedPlayer));

        final Player player = playerService.getLoggedUser();

        assertNotNull(player);
    }

    @Test
    void getLoggedUser_whenPlayerDoesNotExists_shouldThrowNotFoundException() {
        jwtUtilMockedStatic.when(JwtUtil::getLoggedUser).thenReturn("joana");
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        final NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> playerService.getLoggedUser());

        assertEquals("Player not found", notFoundException.getMessage());
    }

    @Test
    void findByUsername_isSuccess() {
        final Player mockedPlayer = Mocks.player();

        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.of(mockedPlayer));

        final Player player = playerService.findByUsername("john");

        assertAll(
                () -> assertNotNull(player),
                () -> assertEquals(mockedPlayer.getId(), player.getId())
        );
    }

    @Test
    void findByUsername_whenPlayerDoesNotExists_shouldThrowNotFoundException() {
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        final NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> playerService.findByUsername("joana"));

        assertEquals("Player not found", notFoundException.getMessage());
    }

}