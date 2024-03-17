package com.moviesbattle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match")
@Getter
@Setter
public class Match {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    private Player player;

    @Column(name = "score")
    private Integer score;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "credits")
    private Integer credits;

}
