package com.moviesbattle.security;

import java.util.Optional;


import com.moviesbattle.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final PlayerRepository playerRepository;

    public Optional<String> authenticate(final String username, final String password) {
        if (isValidUser(username, password)) {
            return Optional.of(JwtUtil.generateToken(username));
        } else {
            return Optional.empty();
        }
    }

    private boolean isValidUser(final String username, final String password) {
        return playerRepository.existsByUsernameAndPassword(username, DigestUtils.sha256Hex(password));
    }

}