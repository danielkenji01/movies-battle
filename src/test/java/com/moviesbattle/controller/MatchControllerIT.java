package com.moviesbattle.controller;

import java.io.IOException;
import java.util.List;


import static org.hamcrest.Matchers.containsInRelativeOrder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviesbattle.JsonUtils;
import com.moviesbattle.dto.RoundAnswerDto;
import com.moviesbattle.model.Movie;
import com.moviesbattle.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

class MatchControllerIT extends AbstractControllerIT {

    private static final String MATCH_URL = "/match";

    private static final String START_URL = MATCH_URL + "/start";

    private static final String END_URL = MATCH_URL + "/end";

    private static final String NEXT_ROUND_URL = MATCH_URL + "/next-round";

    private static final String ANSWER_URL = MATCH_URL + "/answer";

    @Autowired
    private MovieService movieService;

    @BeforeEach
    void setup() throws IOException {
        final String jsonString = JsonUtils.getMoviesJson();
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Movie> movies = objectMapper.readValue(jsonString, new TypeReference<>() {});
        movieService.createAll(movies);
    }

    @Sql(value = "classpath:sql/match/delete_all_matches.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void start_whenMatchDoesNotExists_isSuccess() {
        post(START_URL)
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
        post(START_URL)
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
        post(END_URL)
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
        post(END_URL)
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

    @Test
    void nextRound_whenMatchDoesNotExists_shouldReturnNotFound() {
        post(NEXT_ROUND_URL)
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Match not found");
    }

    @Sql(value = "classpath:sql/match/create_match.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:sql/match/delete_all_rounds.sql", "classpath:sql/match/delete_match.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void nextRound_whenMatchExists_isSuccess() {
        post(NEXT_ROUND_URL)
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty();
    }

    @Sql(value = "classpath:sql/match/create_match_with_round.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_match_with_round.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void nextRound_whenMatchAndRoundExists_shouldReturnSame() {
        post(NEXT_ROUND_URL)
                .headers(setBearerAuth("daniel"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.firstMovie").isEqualTo("Stand by Me")
                .jsonPath("$.secondMovie").isEqualTo("Pirates of the Caribbean: The Curse of the Black Pearl");
    }

    @Test
    void answer_whenAnswerIsInvalid_shouldReturnBadRequest() {
        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(5);

        post(ANSWER_URL)
                .headers(setBearerAuth("daniel"))
                .bodyValue(roundAnswerDto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Invalid answer. Choose between 1 or 2.");
    }

    @Test
    void answer_whenMatchDoesNotExists_shouldReturnNotFound() {
        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        post(ANSWER_URL)
                .headers(setBearerAuth("daniel"))
                .bodyValue(roundAnswerDto)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Match not found");
    }

    @Sql(value = "classpath:sql/match/create_match.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_match.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void answer_whenRoundDoesNotExists_shouldReturnNotFound() {
        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        post(ANSWER_URL)
                .headers(setBearerAuth("daniel"))
                .bodyValue(roundAnswerDto)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Round not found");
    }

    @Sql(value = "classpath:sql/match/create_match_with_round.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_match_with_round.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void answer_whenCorrect_shouldReturnSuccessResponse() {
        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(2);

        post(ANSWER_URL)
                .headers(setBearerAuth("daniel"))
                .bodyValue(roundAnswerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Your answer is correct!");
    }

    @Sql(value = "classpath:sql/match/create_match_with_round.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/match/delete_match_with_round.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void answer_whenWrong_shouldReturnWrongResponse() {
        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        post(ANSWER_URL)
                .headers(setBearerAuth("daniel"))
                .bodyValue(roundAnswerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Your answer is wrong. You still have 2 credit(s)");
    }

    @Sql(value = "classpath:sql/match/create_match_with_multiple_rounds.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"classpath:sql/match/delete_all_rounds.sql", "classpath:sql/match/delete_match.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void answer_whenWrongAndHaveNoMoreCredits_shouldReturnWrongResponse() {
        final RoundAnswerDto roundAnswerDto = new RoundAnswerDto(1);

        post(ANSWER_URL)
                .headers(setBearerAuth("daniel"))
                .bodyValue(roundAnswerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo("Your answer is wrong. You have no more credits. Final score: 600.0");
    }

}