package com.moviesbattle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "player")
@Entity
@Getter
@Setter
public class Player {

    @Id
    @GeneratedValue
    private Integer id;

    private String username;

    private String password;

}