package com.moviesbattle.controller;

import java.util.Optional;


import com.moviesbattle.dto.LoginDto;
import com.moviesbattle.repository.PlayerRepository;
import com.moviesbattle.security.AuthenticateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerRepository playerRepository;

    private final AuthenticateService authenticateService;

    public PlayerController(PlayerRepository playerRepository, AuthenticateService authenticateService) {
        this.playerRepository = playerRepository;
        this.authenticateService = authenticateService;
    }

    @GetMapping
    public ResponseEntity<?> getPlayers() {
        return ResponseEntity.ok(playerRepository.findAll());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final LoginDto loginDto) {
        final Optional<String> token = authenticateService.authenticate(loginDto.getUsername(), loginDto.getPassword());

        return token.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
    }

}
