package com.moviesbattle.controller;

import java.util.List;


import com.moviesbattle.dto.MatchDto;
import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
import com.moviesbattle.service.MatchRoundService;
import com.moviesbattle.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Match", description = "Match API")
@SecurityRequirement(name = "bearerAuth")
public class MatchController {

    private final MatchService matchService;

    private final MatchRoundService matchRoundService;

    @PostMapping("/start")
    @Operation(summary = "Start new match", tags = "Match")
    public ResponseEntity<String> start() {
        matchService.startMatch();

        return ResponseEntity.ok("New match started successfully");
    }

    @PostMapping("/end")
    @Operation(summary = "End match", tags = "Match")
    public ResponseEntity<String> end() {
        final String response = matchService.endMatch();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/next-round")
    @Operation(summary = "Start next round", tags = "Match")
    public ResponseEntity<RoundDto> nextRound() {
        return ResponseEntity.ok(matchRoundService.nextRound());
    }

    @PostMapping("/answer")
    @Operation(summary = "Answer of current round", tags = "Match")
    public ResponseEntity<String> answer(@Valid @RequestBody final RoundAnswerDto answerDto) {
        return ResponseEntity.ok(matchRoundService.answer(answerDto));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", tags = "Match")
    public ResponseEntity<List<MatchDto>> leaderboard() {
        return ResponseEntity.ok(matchService.getLeaderboard());
    }

}
