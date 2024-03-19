package com.moviesbattle.service;

import com.moviesbattle.dto.PlayerDto;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.exception.PlayerAlreadyExistsException;
import com.moviesbattle.model.Player;
import com.moviesbattle.repository.PlayerRepository;
import com.moviesbattle.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void createPlayer(final PlayerDto playerDto) {
        final Player player = new Player();

        if (playerRepository.existsByUsername(playerDto.getUsername())) {
            throw new PlayerAlreadyExistsException();
        }

        player.setPassword(playerDto.getPassword());
        player.setUsername(playerDto.getUsername());

        playerRepository.save(player);
    }

    public Player getLoggedUser() {
        final String loggedUser = JwtUtil.getLoggedUser();

        return findByUsername(loggedUser);
    }

    public Player findByUsername(final String username) {
        return playerRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Player not found"));
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
