package com.moviesbattle.external;

import com.moviesbattle.dto.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${omdb.client.url}", name = "omdbRestClient")
public interface OmdbRestClient {

    @GetMapping
    MovieDto getByImdb(@RequestParam("i") final String imdbId);

}
