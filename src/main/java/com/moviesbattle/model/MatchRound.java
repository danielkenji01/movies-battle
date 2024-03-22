package com.moviesbattle.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "match_round")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchRound {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Match match;

    private Integer roundNumber;

    private String firstMovieImdb;

    private String secondMovieImdb;

    @Enumerated(EnumType.STRING)
    private RoundStatus status;

    @Enumerated(EnumType.STRING)
    private RoundAnswer answer;

    public void correctAnswer() {
        this.answer = RoundAnswer.CORRECT;
        this.status = RoundStatus.FINISHED;
    }

    public void wrongAnswer() {
        this.answer = RoundAnswer.WRONG;
        this.status = RoundStatus.FINISHED;
    }

}