package com.moviesbattle.security;

import java.util.Optional;


import com.moviesbattle.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final PlayerService playerService;

    public Optional<String> authenticate(final String username, final String password) {
        if (isValidPlayer(username, password)) {
            return Optional.of(JwtUtil.generateToken(username));
        } else {
            return Optional.empty();
        }
    }

    private boolean isValidPlayer(final String username, final String password) {
        return playerService.existsByUsername(username, password); 
    }

}