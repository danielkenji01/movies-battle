package com.moviesbattle.repository;

import java.util.Optional;

import com.moviesbattle.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    Optional<Movie> findByImdb(final String imdb);

}
