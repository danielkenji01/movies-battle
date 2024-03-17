package com.moviesbattle.service;

import com.moviesbattle.dto.MovieDto;
import com.moviesbattle.external.OmdbRestClient;
import com.moviesbattle.model.Movie;
import com.moviesbattle.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    private final OmdbRestClient omdbRestClient;

    public void create(final String imdb) {
        final Movie movie = new Movie();

        final MovieDto movieDto = omdbRestClient.getByImdb(imdb);

        movie.setImdb(movieDto.imdbID());
        movie.setTitle(movieDto.title());
        movie.setRate(movieDto.imdbRating());
        movie.setVotes(Integer.parseInt(movieDto.imdbVotes().replaceAll(",", "")));
        movie.setTotalScore(movieDto.imdbRating() * movie.getVotes());

        movieRepository.save(movie);
    }

}
