package com.moviesbattle.controller;

import java.util.Optional;


import com.moviesbattle.dto.PlayerDto;
import com.moviesbattle.security.AuthenticateService;
import com.moviesbattle.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Tag(name = "Player", description = "Players API")
public class PlayerController {

    private final PlayerService playerService;

    private final AuthenticateService authenticateService;

    @PostMapping
    @Operation(summary = "Create new player", tags = "Player")
    public ResponseEntity<Void> createPlayer(@Valid @RequestBody final PlayerDto playerDto) {
        playerService.createPlayer(playerDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login with credentials", tags = "Player")
    public ResponseEntity<String> login(@Valid @RequestBody final PlayerDto playerDto) {
        final Optional<String> token = authenticateService.authenticate(playerDto.getUsername(), playerDto.getPassword());

        return token.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
    }

}
