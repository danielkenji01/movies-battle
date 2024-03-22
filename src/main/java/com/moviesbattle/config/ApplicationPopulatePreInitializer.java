package com.moviesbattle.config;

import java.io.IOException;
import java.util.Optional;


import com.moviesbattle.dto.PlayerDto;
import com.moviesbattle.external.ImdbScrapper;
import com.moviesbattle.service.PlayerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationPopulatePreInitializer {

    private final Optional<ImdbScrapper> imdbScrapper;

    private final PlayerService playerService;

    @PostConstruct
    public void populateDatabase() {
        imdbScrapper.ifPresent(ImdbScrapper::scrapeMovieTitles);
        this.createPlayers();
    }

    private void createPlayers() {
        if (playerService.existsPlayers()) {
            return;
        }

        final PlayerDto player = new PlayerDto("daniel", "12345");
        final PlayerDto player1 = new PlayerDto("caio", "1234");

        playerService.createPlayer(player);
        playerService.createPlayer(player1);
    }

}
