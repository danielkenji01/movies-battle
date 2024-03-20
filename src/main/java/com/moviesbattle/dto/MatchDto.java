package com.moviesbattle.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class MatchDto {

    private Integer id;

    private double score;

    private Integer correctAnswers;

    private String player;

}