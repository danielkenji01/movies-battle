package com.moviesbattle.service;

import com.moviesbattle.dto.PlayerDto;
import com.moviesbattle.exception.NotFoundException;
import com.moviesbattle.exception.PlayerAlreadyExistsException;
import com.moviesbattle.model.Player;
import com.moviesbattle.repository.PlayerRepository;
import com.moviesbattle.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public boolean existsPlayers() {
        return playerRepository.count() > 0;
    }

    public void createPlayer(final PlayerDto playerDto) {
        final Player player = new Player();

        if (playerRepository.existsByUsername(playerDto.getUsername())) {
            throw new PlayerAlreadyExistsException();
        }

        player.setPassword(DigestUtils.sha256Hex(playerDto.getPassword()));
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

}
