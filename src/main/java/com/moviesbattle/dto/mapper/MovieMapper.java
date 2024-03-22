package com.moviesbattle.dto.mapper;

import com.moviesbattle.dto.MovieDto;
import com.moviesbattle.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(source = "imdbID", target = "imdb")
    @Mapping(source = "imdbRating", target = "rate")
    @Mapping(source = "imdbVotes", target = "votes", qualifiedByName = "parseVotes")
    @Mapping(source = ".", target = "totalScore", qualifiedByName = "calculateScore")
    Movie map(final MovieDto movieDto);

    @Named("parseVotes")
    static Integer parseVotes(final String votes) {
        return Integer.parseInt(votes.replace(",", ""));
    }

    @Named("calculateScore")
    static double calculateScore(final MovieDto movieDto) {
        final Integer votes = parseVotes(movieDto.imdbVotes());

        return movieDto.imdbRating() * votes;
    }

}
