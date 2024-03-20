package com.moviesbattle;

import java.util.Collections;

import com.moviesbattle.dto.MovieDto;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Movie;
import com.moviesbattle.model.Player;

public class Mocks {

    public static Player player() {
        return new Player(1, "john", "12345");
    }

    public static Match match() {
        return new Match(1, player(), 2, 100, MatchStatus.IN_PROGRESS, 2, Collections.emptyList());
    }

    public static Movie movie() {
        final int votes = 100000;
        final double rate = 87.2;
        return new Movie(1, "tt12345", "Spider Man", rate, votes, rate * votes);
    }

    public static MovieDto movieDto() {
        return new MovieDto("Spider Man", 87.2, "100000", "tt12345");
    }

}
