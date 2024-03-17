package com.moviesbattle.service;

import com.moviesbattle.model.Player;
import com.moviesbattle.repository.PlayerRepository;
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

}
