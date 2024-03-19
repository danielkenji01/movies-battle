package com.moviesbattle.service;

import java.util.Comparator;
import java.util.List;

import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
import com.moviesbattle.exception.MatchExistsException;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchRound;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Movie;
import com.moviesbattle.model.Player;
import com.moviesbattle.model.RoundStatus;
import com.moviesbattle.repository.MatchRepository;
import com.moviesbattle.repository.MatchRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    // Endpoint para ver placar de líderes
    // Endpoint para encerrar partida
    // Encriptar senha
    // Refatorar código ruim
    // Validação da resposta se valor é permitido
    // Adicionar testes unitários
    // Adicionar testes de integração
    // Opcional: adicionar expiração para token


    private final MatchRepository matchRepository;

    private final PlayerService playerService;

    private final MatchRoundRepository matchRoundRepository;

    private final MovieService movieService;

    public void startMatch() {
        final Player player = playerService.getLoggedUser();

        validateExistingMatch(player);

        final Match match = new Match();

        match.setStatus(MatchStatus.IN_PROGRESS);
        match.setCredits(3);
        match.setPlayer(player);
        match.setCorrectAnswers(0);

        matchRepository.save(match);
    }

    public RoundDto nextRound() {
        final Player player = playerService.getLoggedUser();

        final Match match = matchRepository.findByPlayerAndStatus(player, MatchStatus.IN_PROGRESS).orElseThrow(
                () -> new NotFoundException("Match not found"));

        final MatchRound matchRound =
                matchRoundRepository.findByMatchAndStatus(match, RoundStatus.IN_PROGRESS).orElseGet(() -> createRound(match));

        final Movie firstMovie = movieService.findByImdb(matchRound.getFirstMovieImdb());
        final Movie secondMovie = movieService.findByImdb(matchRound.getSecondMovieImdb());

        return new RoundDto(firstMovie.getTitle(), secondMovie.getTitle());
    }

    public String answer(final RoundAnswerDto answerDto) {
        final Player player = playerService.getLoggedUser();

        final Match match = matchRepository.findByPlayerAndStatus(player, MatchStatus.IN_PROGRESS).orElseThrow(
                () -> new NotFoundException("Match not found"));

        final MatchRound matchRound = matchRoundRepository.findByMatchAndStatus(match, RoundStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Round not found"));

        final Movie firstMovie = movieService.findByImdb(matchRound.getFirstMovieImdb());
        final Movie secondMovie = movieService.findByImdb(matchRound.getSecondMovieImdb());

        final int answer = answerDto.getAnswer();
        boolean correct = false;

        if (answer == 1) {
            correct = firstMovie.getTotalScore() > secondMovie.getTotalScore();
        } else if (answer == 2) {
            correct = secondMovie.getTotalScore() > firstMovie.getTotalScore();
        }

        final StringBuilder response = new StringBuilder();

        if (correct) {
            matchRound.correctAnswer();
            match.scored();

            response.append("Your answer is correct!");
        } else {
            matchRound.wrongAnswer();
            match.missed();

            response.append("Your answer is wrong.");

            if (match.getCredits() == 0) {
                match.finish();
                response.append(" You have no more credits. Final score: ").append(match.getCorrectAnswers());
            } else {
                response.append(String.format(" You still have %s credit(s)", match.getCredits()));
            }
        }

        matchRoundRepository.save(matchRound);
        matchRepository.save(match);

        return response.toString();
    }

    public List<Match> getLeaderboard() {
        return matchRepository.findAll().stream().sorted(Comparator.comparing(Match::getScore)).toList();
    }

    private MatchRound createRound(final Match match) {
        final MatchRound matchRound = new MatchRound();

        matchRound.setMatch(match);
        matchRound.setStatus(RoundStatus.IN_PROGRESS);
        matchRound.setFirstMovieImdb(generateUnusedMovie(match).getImdb());
        matchRound.setSecondMovieImdb(generateUnusedMovie(match).getImdb());

        return matchRoundRepository.save(matchRound);
    }

    private Movie generateUnusedMovie(final Match match) {
        final Movie randomMovie = movieService.getRandomMovie();

        final boolean movieBeingUsed =
                matchRoundRepository.isMovieBeingUsed(match, RoundStatus.FINISHED, randomMovie.getImdb());

        if (movieBeingUsed) {
            return generateUnusedMovie(match);
        }

        return randomMovie;
    }

    private void validateExistingMatch(final Player player) {
        final boolean exists = matchRepository.existsByPlayerAndStatus(player, MatchStatus.IN_PROGRESS);

        if (exists) {
            throw new MatchExistsException("Match already exists");
        }
    }

}
