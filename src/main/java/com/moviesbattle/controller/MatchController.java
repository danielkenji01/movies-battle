package com.moviesbattle.controller;

import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
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

    @PostMapping("/start")
    public ResponseEntity<String> start() throws Exception {
        matchService.startMatch();

        return ResponseEntity.ok("New match started successfully");
    }

    @PostMapping("/next-round")
    public ResponseEntity<RoundDto> nextRound() throws Exception {
        return ResponseEntity.ok(matchService.nextRound());
    }

    @PostMapping("/answer")
    public ResponseEntity<String> answer(@RequestBody final RoundAnswerDto answerDto) throws Exception {
        return ResponseEntity.ok(matchService.answer(answerDto));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard() {
        return ResponseEntity.ok(matchService.getLeaderboard());
    }

}
