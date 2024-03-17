package com.moviesbattle.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match_rounds")
@Getter
@Setter
public class MatchRound {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Match match;

    private Integer roundNumber;

    private String firstMovieImdb;

    private String secondMovieImdb;

    private RoundStatus status;

    private RoundAnswer answer;

}