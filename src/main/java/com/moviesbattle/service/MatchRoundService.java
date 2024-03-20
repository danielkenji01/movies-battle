package com.moviesbattle.service;

import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.dto.RoundDto;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchRound;
import com.moviesbattle.model.Movie;
import com.moviesbattle.model.RoundStatus;
import com.moviesbattle.repository.MatchRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchRoundService {

    private final MatchRoundRepository matchRoundRepository;

    private final MovieService movieService;

    private final MatchService matchService;

    public RoundDto nextRound() {
        final Match match = matchService.findByLoggedPlayer();

        final MatchRound matchRound =
                matchRoundRepository.findByMatchAndStatus(match, RoundStatus.IN_PROGRESS)
                        .orElseGet(() -> createRound(match));

        final Movie firstMovie = movieService.findByImdb(matchRound.getFirstMovieImdb());
        final Movie secondMovie = movieService.findByImdb(matchRound.getSecondMovieImdb());

        return new RoundDto(firstMovie.getTitle(), secondMovie.getTitle());
    }

    @Transactional
    public String answer(final RoundAnswerDto answerDto) {
        final Match match = matchService.findByLoggedPlayer();
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
            builder.append(" You have no more credits. Final score: ").append(match.getScore());
        } else {
            builder.append(String.format(" You still have %s credit(s)", match.getCredits()));
        }

        return builder.toString();
    }

    private void saveMatchAndRound(final Match match, final MatchRound matchRound) {
        try {
            matchRoundRepository.save(matchRound);
            matchService.saveMatch(match);
        } catch (final Exception e) {
            throw new RuntimeException("Error saving match information"); // TODO refactor
        }
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

}