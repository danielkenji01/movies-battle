package com.moviesbattle.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue
    private Integer id;

    private String imdb;

    private String title;

    private double rate;

    private int votes;

    private double totalScore;

}