package com.moviesbattle.service;

import java.util.Comparator;
import java.util.List;

import com.moviesbattle.dto.MatchDto;
import com.moviesbattle.dto.mapper.MatchMapper;
import com.moviesbattle.exception.MatchExistsException;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Player;
import com.moviesbattle.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    // Validação da resposta se valor é permitido
    // Adicionar testes unitários
    // Adicionar testes de integração
    // Opcional: adicionar expiração para token

    private final MatchRepository matchRepository;

    private final PlayerService playerService;

    private final MatchMapper matchMapper;

    public Match findByLoggedPlayer() {
        final Player player = playerService.getLoggedUser();

        return matchRepository.findByPlayerAndStatus(player, MatchStatus.IN_PROGRESS).orElseThrow(
                () -> new NotFoundException("Match not found"));
    }

    public List<MatchDto> getLeaderboard() {
        return matchRepository.findAll().stream().sorted(Comparator.comparing(Match::getScore).reversed())
                .map(matchMapper::map).toList();
    }

    public void startMatch() {
        final Player player = playerService.getLoggedUser();

        this.validateExistingMatch(player);

        final Match match = new Match();

        match.setStatus(MatchStatus.IN_PROGRESS);
        match.setCredits(3);
        match.setPlayer(player);
        match.setCorrectAnswers(0);

        this.saveMatch(match);
    }

    public String endMatch() {
        final Match match = this.findByLoggedPlayer();

        match.finish();

        this.saveMatch(match);

        return String.format("Final score: %s", match.getScore());
    }

    public void saveMatch(final Match match) {
        matchRepository.save(match);
    }

    private void validateExistingMatch(final Player player) {
        final boolean exists = matchRepository.existsByPlayerAndStatus(player, MatchStatus.IN_PROGRESS);

        if (exists) {
            throw new MatchExistsException("Match already exists");
        }
    }

}