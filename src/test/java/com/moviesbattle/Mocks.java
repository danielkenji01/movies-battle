package com.moviesbattle;

import java.util.Collections;


import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Player;

public class Mocks {

    public static Player player() {
        return new Player(1, "john", "12345");
    }

    public static Match match() {
        return new Match(1, player(), 2, 100, MatchStatus.IN_PROGRESS, 2, Collections.emptyList());
    }

}
