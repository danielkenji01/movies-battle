package com.moviesbattle.security;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.moviesbattle.service.PlayerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticateServiceTest {

    @Mock
    private PlayerService playerService;

    private MockedStatic<JwtUtil> jwtUtilMockedStatic;

    @InjectMocks
    private AuthenticateService authenticateService;

    @BeforeEach
    void setup() {
        jwtUtilMockedStatic = mockStatic(JwtUtil.class);
    }

    @AfterEach
    void tearDown() {
        jwtUtilMockedStatic.close();
    }

    @Test
    void authenticate_isSuccess() {
        when(playerService.existsByUsername(anyString(), anyString())).thenReturn(true);
        jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(any())).thenReturn("valid_token");

        final Optional<String> token = authenticateService.authenticate("john", "12345");

        assertTrue(token.isPresent());
    }

    @Test
    void authenticate_whenPlayerIsNotValid_shouldReturnEmptyToken() {
        when(playerService.existsByUsername(anyString(), anyString())).thenReturn(false);

        final Optional<String> token = authenticateService.authenticate("john", "12345");

        assertTrue(token.isEmpty());
    }


}