package com.moviesbattle.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match_rounds")
@Getter
@Setter
public class MatchRounds {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer roundNumber;

    private Integer firstMovieId;

    private Integer secondMovieId;

    private boolean result;

    private RoundStatus status;

}
