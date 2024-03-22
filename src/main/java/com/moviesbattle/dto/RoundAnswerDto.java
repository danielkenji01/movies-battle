package com.moviesbattle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoundAnswerDto {

    @Min(value = 1, message = "Invalid answer. Choose between 1 or 2.")
    @Max(value = 2, message = "Invalid answer. Choose between 1 or 2.")
    private int answer;

}
