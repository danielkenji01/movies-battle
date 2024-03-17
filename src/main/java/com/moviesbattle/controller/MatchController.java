package com.moviesbattle.controller;

import com.moviesbattle.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/start")
    public ResponseEntity<String> start() throws Exception {
        matchService.startMatch();

        return ResponseEntity.ok("New match started successfully");
    }

}
