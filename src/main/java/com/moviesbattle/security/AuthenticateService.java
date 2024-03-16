package com.moviesbattle.security;

import java.util.Optional;


import com.moviesbattle.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateService {

    private final PlayerRepository playerRepository;

    public Optional<String> authenticate(String username, String password) {
        // Perform authentication logic, e.g., check against database
        if (isValidUser(username, password)) {
            return Optional.of(JwtUtil.generateToken(username));
        } else {
            return Optional.empty();
        }
    }

    private boolean isValidUser(final String username, final String password) {
        // Example authentication logic (replace with your own)
        return playerRepository.existsByUsernameAndPassword(username, password);
    }

    public AuthenticateService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}