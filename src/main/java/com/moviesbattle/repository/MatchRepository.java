package com.moviesbattle.repository;

import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    boolean existsByPlayerAndStatus(final Player player, final MatchStatus status);

}
