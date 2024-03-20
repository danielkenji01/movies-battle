package com.moviesbattle.controller;

import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
import com.moviesbattle.service.MatchRoundService;
import com.moviesbattle.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    private final MatchRoundService matchRoundService;

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        matchService.startMatch();

        return ResponseEntity.ok("New match started successfully");
    }

    @PostMapping("/end")
    public ResponseEntity<String> end() {
        final String response = matchService.endMatch();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/next-round")
    public ResponseEntity<RoundDto> nextRound() {
        return ResponseEntity.ok(matchRoundService.nextRound());
    }

    @PostMapping("/answer")
    public ResponseEntity<String> answer(@RequestBody final RoundAnswerDto answerDto) {
        return ResponseEntity.ok(matchRoundService.answer(answerDto));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard() {
        return ResponseEntity.ok(matchService.getLeaderboard());
    }

}
