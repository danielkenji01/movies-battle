package com.moviesbattle.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;

import com.moviesbattle.Mocks;
import com.moviesbattle.dto.PlayerDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

class PlayerControllerIT extends AbstractControllerIT {

    @Test
    void createPlayer_isCreated() {
        final PlayerDto playerDto = new PlayerDto("charles", "testingpassword");

        post("/players")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void createPlayer_whenPlayerAlreadyExists_shouldReturnConflict() {
        final PlayerDto playerDto = new PlayerDto("daniel", "testingpassword");

        post("/players")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Player already exists");
    }

    @Test
    void createPlayer_withoutUsername_shouldReturnBadRequest() {
        final PlayerDto playerDto = new PlayerDto("   ", "testingpassword");

        post("/players")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.[0]")
                .isEqualTo("Username is mandatory");
    }

    @Test
    void createPlayer_withoutPassword_shouldReturnBadRequest() {
        final PlayerDto playerDto = new PlayerDto("charles", null);

        post("/players")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.[0]")
                .isEqualTo("Password is mandatory");
    }
    @Test
    void createPlayer_withoutUsernameAndPassword_shouldReturnBadRequest() {
        final PlayerDto playerDto = new PlayerDto(null, " ");

        post("/players")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.[*]", containsInAnyOrder("Username is mandatory", "Password is mandatory"));
    }

    @Sql(value = "classpath:sql/player/create_player.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/player/delete_player.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void login_withValidCredentials_isSuccess() {
        final PlayerDto playerDto = new PlayerDto("phillip", "12345");

        post("/players/login")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isNotEmpty();
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() {
        final PlayerDto playerDto = new PlayerDto("notanuser", "32480912");

        post("/players/login")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Invalid credentials");
    }

    @Test
    void login_withoutUsername_shouldReturnBadRequest() {
        final PlayerDto playerDto = new PlayerDto(" ", "32480912");

        post("/players/login")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.[0]")
                .isEqualTo("Username is mandatory");
    }

    @Test
    void login_withoutPassword_shouldReturnBadRequest() {
        final PlayerDto playerDto = new PlayerDto("notanuser", null);

        post("/players/login")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.[0]")
                .isEqualTo("Password is mandatory");
    }

    @Test
    void login_withoutUsernameAndPassword_shouldReturnBadRequest() {
        final PlayerDto playerDto = new PlayerDto(null, "    ");

        post("/players/login")
                .bodyValue(playerDto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.[*]", containsInAnyOrder("Username is mandatory", "Password is mandatory"));
    }

}
