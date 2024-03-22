package com.moviesbattle.controller;

import java.util.function.Consumer;


import com.moviesbattle.MoviesBattleApplication;
import com.moviesbattle.security.JwtUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(classes = { MoviesBattleApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("TEST")
public abstract class AbstractControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    private final UriComponentsBuilder clientDomainBuilder = UriComponentsBuilder.newInstance()
            .scheme("http").host("localhost").path("/api/{uri}");


    protected Consumer<HttpHeaders> setBearerAuth(final String username) {
        return httpHeaders -> httpHeaders.setBearerAuth(JwtUtil.generateToken(username));
    }

    protected final WebTestClient.RequestBodySpec post(final String uri) {
        return request(HttpMethod.POST, uri);
    }

    protected final WebTestClient.RequestHeadersSpec<?> get(final String uri) {
        return request(HttpMethod.GET, uri);
    }

    protected WebTestClient.RequestBodySpec request(final HttpMethod method, final String uri) {
        return webTestClient.method(method)
                .uri(clientDomainBuilder.port(port).buildAndExpand(uri).toUriString())
                .accept(MediaType.APPLICATION_JSON);
    }

}