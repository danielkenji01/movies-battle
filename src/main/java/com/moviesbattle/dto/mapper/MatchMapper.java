package com.moviesbattle.dto.mapper;

import com.moviesbattle.dto.MatchDto;
import com.moviesbattle.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(source = "player.username", target = "player")
    MatchDto map(final Match match);

}