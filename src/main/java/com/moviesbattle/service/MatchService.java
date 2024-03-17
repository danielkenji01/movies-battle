package com.moviesbattle.service;

import java.util.Optional;


import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchRound;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Movie;
import com.moviesbattle.model.Player;
import com.moviesbattle.model.RoundAnswer;
import com.moviesbattle.model.RoundStatus;
import com.moviesbattle.repository.MatchRepository;
import com.moviesbattle.repository.MatchRoundRepository;
import com.moviesbattle.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    private final PlayerService playerService;

    private final MatchRoundRepository matchRoundRepository;

    private final MovieService movieService;

    public void startMatch() throws Exception {
        final String loggedUser = JwtUtil.getLoggedUser().orElseThrow(Exception::new);

        final Player player = playerService.findByUsername(loggedUser);

        validateExistingMatch(player);

        final Match match = new Match();

        match.setStatus(MatchStatus.IN_PROGRESS);
        match.setCredits(3);
        match.setPlayer(player);
        match.setScore(0);

        matchRepository.save(match);
    }

    public RoundDto nextRound() throws Exception {
        final String loggedUser = JwtUtil.getLoggedUser().orElseThrow(Exception::new);

        final Player player = playerService.findByUsername(loggedUser); // TODO separate in new service

        final Match match = matchRepository.findByPlayerAndStatus(player, MatchStatus.IN_PROGRESS).orElseThrow(
                ChangeSetPersister.NotFoundException::new);

        final MatchRound matchRound =
                matchRoundRepository.findByMatchAndStatus(match, RoundStatus.IN_PROGRESS).orElseGet(() -> createRound(match));

        final Movie firstMovie = movieService.findByImdb(matchRound.getFirstMovieImdb());
        final Movie secondMovie = movieService.findByImdb(matchRound.getSecondMovieImdb());

        return new RoundDto(firstMovie.getTitle(), secondMovie.getTitle());
    }

    public String answer(final RoundAnswerDto answerDto) throws Exception {

        final String loggedUser = JwtUtil.getLoggedUser().orElseThrow(Exception::new);

        final Player player = playerService.findByUsername(loggedUser); // TODO separate in new service

        final Match match = matchRepository.findByPlayerAndStatus(player, MatchStatus.IN_PROGRESS).orElseThrow(
                ChangeSetPersister.NotFoundException::new);

        final MatchRound matchRound = matchRoundRepository.findByMatchAndStatus(match, RoundStatus.IN_PROGRESS)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

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
            matchRound.setAnswer(RoundAnswer.CORRECT);
            matchRound.setStatus(RoundStatus.FINISHED);

            Integer score = match.getScore();
            match.setScore(score + 1);

            response.append("Your answer is correct!");
        } else {
            matchRound.setAnswer(RoundAnswer.WRONG);
            matchRound.setStatus(RoundStatus.FINISHED);

            final Integer credits = match.getCredits();
            match.setCredits(credits - 1);

            response.append("Your answer is wrong.");

            if (match.getCredits() == 0) {
                match.setStatus(MatchStatus.FINISHED);
                response.append(" You have no more credits. Final score: ").append(match.getScore());
            } else {
                response.append(String.format(" You still have %s credit(s)", match.getCredits()));
            }
        }

        matchRoundRepository.save(matchRound);
        matchRepository.save(match);

        return response.toString();
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
            throw new UnsupportedOperationException();
        }
    }

}
