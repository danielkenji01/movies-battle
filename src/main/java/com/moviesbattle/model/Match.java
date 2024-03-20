package com.moviesbattle.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "match")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Player player;

    private Integer correctAnswers;

    private double score = 0;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private Integer credits;

    @OneToMany(mappedBy = "match")
    private List<MatchRound> rounds;

    public void scored() {
        this.correctAnswers += 1;
        calculateScore();
    }

    public void missed() {
        this.credits -= 1;
        calculateScore();
    }

    public void calculateScore() {
        final int wrongAnswers = rounds.size() - correctAnswers;

        this.score = (((double) correctAnswers / wrongAnswers) * 100) * rounds.size();
    }

    public void finish() {
        this.credits = 0;
        this.status = MatchStatus.FINISHED;
    }

}
