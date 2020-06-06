package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class CarDTO {

    private Long id;

    private String plate;

    private String model;

    private LocalDateTime lastAccess;

}
