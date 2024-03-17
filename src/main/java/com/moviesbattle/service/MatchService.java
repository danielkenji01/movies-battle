package com.moviesbattle.service;

import com.moviesbattle.model.Match;
import com.moviesbattle.model.MatchStatus;
import com.moviesbattle.model.Player;
import com.moviesbattle.repository.MatchRepository;
import com.moviesbattle.repository.PlayerRepository;
import com.moviesbattle.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    private final PlayerService playerService;

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

    private void validateExistingMatch(final Player player) {
        final boolean exists = matchRepository.existsByPlayerAndStatus(player, MatchStatus.IN_PROGRESS);

        if (exists) {
            throw new UnsupportedOperationException();
        }
    }

}
