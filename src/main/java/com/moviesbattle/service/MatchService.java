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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public String answer(final RoundAnswerDto answerDto) {
        final Player player = playerService.getLoggedUser();
        final Match match = getInProgressMatch(player);
        final MatchRound matchRound = getInProgressRound(match);

        final Movie firstMovie = movieService.findByImdb(matchRound.getFirstMovieImdb());
        final Movie secondMovie = movieService.findByImdb(matchRound.getSecondMovieImdb());

        final int selectedOption = answerDto.getAnswer();
        boolean correct = checkAnswer(selectedOption, firstMovie, secondMovie);

        String response;

        if (correct) {
            response = handleCorrectAnswer(matchRound, match);
        } else {
            response = handleWrongAnswer(matchRound, match);
        }

        saveMatchAndRound(match, matchRound);

        return response;
    }

    private Match getInProgressMatch(final Player player) {
        return matchRepository.findByPlayerAndStatus(player, MatchStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Match not found"));
    }

    private MatchRound getInProgressRound(final Match match) {
        return matchRoundRepository.findByMatchAndStatus(match, RoundStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Round not found"));
    }

    private boolean checkAnswer(final int selectedOption, final Movie firstMovie, final Movie secondMovie) {
        return selectedOption == 1 ? firstMovie.getTotalScore() > secondMovie.getTotalScore() :
                secondMovie.getTotalScore() > firstMovie.getTotalScore();
    }

    private String handleCorrectAnswer(final MatchRound matchRound, final Match match) {
        matchRound.correctAnswer();
        match.scored();
        return "Your answer is correct!";
    }

    private String handleWrongAnswer(final MatchRound matchRound, final Match match) {
        final StringBuilder builder = new StringBuilder();

        matchRound.wrongAnswer();
        match.missed();
        builder.append("Your answer is wrong.");

        if (match.getCredits() == 0) {
            match.finish();
            builder.append(" You have no more credits. Final score: ").append(match.getCorrectAnswers());
        } else {
            builder.append(String.format(" You still have %s credit(s)", match.getCredits()));
        }

        return builder.toString();
    }

    private void saveMatchAndRound(final Match match, final MatchRound matchRound) {
        try {
            matchRoundRepository.save(matchRound);
            matchRepository.save(match);
        } catch (final Exception e) {
            throw new RuntimeException("Error saving match information");
        }
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
