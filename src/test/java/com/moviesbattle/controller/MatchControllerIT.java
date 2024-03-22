package com.moviesbattle.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


import static org.hamcrest.Matchers.containsInRelativeOrder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviesbattle.model.Movie;
import com.moviesbattle.security.JwtUtil;
import com.moviesbattle.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

class MatchControllerIT extends AbstractControllerIT {

    @Autowired
    private MovieService movieService;

    @BeforeEach
    void setup() {
        String jsonString;
        try (FileReader reader = new FileReader(
                "C:/Users/CTW02596/IdeaProjects/movies-battle/src/test/resources/movies.json")) {
            int charCount;
            StringBuilder content = new StringBuilder();
            while ((charCount = reader.read()) != -1) {
                content.append((char) charCount);
            }
            jsonString = content.toString();

            final ObjectMapper objectMapper = new ObjectMapper();
            final List<Movie> movies = objectMapper.readValue(jsonString, new TypeReference<List<Movie>>() {
            });
            movieService.createAll(movies);
        } catch (final IOException exception) {

        }
    }

    @Test
    void start_whenMatchDoesNotExists_isSuccess() {
        post("/match/start")
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("New match started successfully");
    }

    @Sql(value = "classpath:sql/match/create_match.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_match.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void start_whenMatchAlreadyExists_shouldReturnConflict() {
        post("/match/start")
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Match already exists");
    }

    @Test
    void end_whenMatchDoesNotExists_shouldReturnNotFound() {
        post("/match/end")
                .headers(setBearerAuth("caio"))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Match not found");
    }

    @Sql(value = "classpath:sql/match/create_match_to_finish.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_match_to_finish.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void end_whenMatchExists_shouldReturnFinalResponse() {
        post("/match/end")
                .headers(setBearerAuth("phillip"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Final score: 100.0");
    }

    @Sql(value = "classpath:sql/match/create_matches_leaderboard.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_matches_leaderboard.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void leaderboard_shouldReturnInCorrectOrder() {
        get("/match/leaderboard")
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.*").isNotEmpty()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$.[*].id", containsInRelativeOrder(999, 998, 1000));
    }

    @Test
    void leaderboard_whenThereIsNotMatches_shouldReturnEmpty() {
        get("/match/leaderboard")
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isEmpty();
    }

}
