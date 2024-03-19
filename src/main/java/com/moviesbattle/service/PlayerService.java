package com.moviesbattle.service;

import com.moviesbattle.model.Player;
import com.moviesbattle.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player findByUsername(final String username) throws ChangeSetPersister.NotFoundException {
        return playerRepository.findByUsername(username).orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    @PostConstruct
    public void createPlayers() {
        if (playerRepository.count() > 0) {
            return;
        }

        final Player player = new Player();

        player.setPassword("12345");
        player.setUsername("daniel");

        playerRepository.save(player);

        final Player player1 = new Player();
        player1.setUsername("caio");
        player1.setPassword("123");

        playerRepository.save(player1);
    }

}
