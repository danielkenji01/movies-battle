package com.moviesbattle;

import java.util.ArrayList;
import java.util.List;

import com.moviesbattle.dto.MovieDto;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchRound;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Movie;
import com.moviesbattle.model.Player;
import com.moviesbattle.model.RoundStatus;

public class Mocks {

    public static Player player() {
        return new Player(1, "john", "12345");
    }

    public static Match match() {
        return match(2, 2);
    }

    public static Match match(final int credits, final int roundsPlayed) {
        final List<MatchRound> rounds = new ArrayList<>();

        for (int i = 0; i < roundsPlayed; i++) {
            rounds.add(Mocks.matchRound());
        }

        return new Match(1, player(), 4, 100, MatchStatus.IN_PROGRESS, credits, rounds);
    }

    public static MatchRound matchRound() {
        return new MatchRound(1, match(1, 0), 1, "tt12345", "tt6789", RoundStatus.IN_PROGRESS, null);
    }

    public static Movie movie() {
        return movie("Spider Man", 100000, 87.2);
    }

    public static Movie movie(final String title, final int votes, final double rate) {
        return new Movie(1, "tt12345", title, rate, votes, rate * votes);
    }

    public static MovieDto movieDto() {
        return new MovieDto("Spider Man", 87.2, "100000", "tt12345");
    }

}