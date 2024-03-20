package com.moviesbattle.dto.mapper;

import com.moviesbattle.dto.MatchDto;
import com.moviesbattle.model.Match;
import com.moviesbattle.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(source = "player", target = "player", qualifiedByName = "getPlayerName")
    MatchDto map(final Match match);

    @Named("getPlayerName")
    static String getPlayerName(final Player player) {
        return player.getUsername();
    }

}
