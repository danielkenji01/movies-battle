package com.moviesbattle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovieDto(@JsonProperty("Title") String title, double imdbRating, String imdbVotes, String imdbID) {

}