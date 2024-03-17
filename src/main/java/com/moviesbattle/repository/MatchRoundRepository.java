package com.moviesbattle.repository;

import java.util.Optional;

import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchRound;
import com.moviesbattle.model.RoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchRoundRepository extends JpaRepository<MatchRound, Integer> {

    Optional<MatchRound> findByMatchAndStatus(final Match match, final RoundStatus status);

    @Query("SELECT count(e) > 0 FROM MatchRound e where e.match = :match and e.status = :status and ("
            + "e.firstMovieImdb = :movie or e.secondMovieImdb = :movie)")
    boolean isMovieBeingUsed(final Match match, final RoundStatus status,
            final String movie);

}
