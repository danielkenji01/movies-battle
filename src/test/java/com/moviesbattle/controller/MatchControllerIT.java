package com.moviesbattle.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviesbattle.MoviesBattleApplication;
import com.moviesbattle.model.Movie;
import com.moviesbattle.security.JwtUtil;
import com.moviesbattle.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(classes = { MoviesBattleApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("TEST")
class MatchControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Autowired
    private MovieService movieService;

    @BeforeEach
    void setup() {
        String jsonString;
        try (FileReader reader = new FileReader("C:/Users/CTW02596/IdeaProjects/movies-battle/src/test/resources/movies.json")) {
            int charCount;
            StringBuilder content = new StringBuilder();
            while ((charCount = reader.read()) != -1) {
                content.append((char) charCount);
            }
            jsonString = content.toString();

            final ObjectMapper objectMapper = new ObjectMapper();
            final List<Movie> movies = objectMapper.readValue(jsonString, new TypeReference<List<Movie>>() {});
            movieService.createAll(movies);
        } catch (final IOException exception) {

        }
    }

    @Test
    void start_whenMatchDoesNotExists_isSuccess() {
        webTestClient.post()
                .uri(UriComponentsBuilder.newInstance().scheme("http").host("localhost").path("/api/match/start").port(port).toUriString())
                .headers(setBasicAuth("daniel"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("New match started successfully");
    }

    @Sql(value = "classpath:sql/create_match.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/delete_match.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void start_whenMatchAlreadyExists_shouldReturnConflict() {
        webTestClient.post()
                .uri(UriComponentsBuilder.newInstance().scheme("http").host("localhost").path("/api/match/start").port(port).toUriString())
                .headers(setBasicAuth("daniel"))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Match already exists");
    }

    private final Consumer<HttpHeaders> setBasicAuth(final String username) {
        return httpHeaders -> httpHeaders.setBearerAuth(JwtUtil.generateToken(username));
    }

}
