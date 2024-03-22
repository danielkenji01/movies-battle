package com.moviesbattle.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


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

    private final Consumer<HttpHeaders> setBearerAuth(final String username) {
        return httpHeaders -> httpHeaders.setBearerAuth(JwtUtil.generateToken(username));
    }

}
