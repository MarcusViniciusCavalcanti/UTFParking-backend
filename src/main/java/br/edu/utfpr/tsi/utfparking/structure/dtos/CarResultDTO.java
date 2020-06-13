package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class CarResultDTO extends CarDTO {

    @Getter
    private final UserCarResultDTO user;
}
