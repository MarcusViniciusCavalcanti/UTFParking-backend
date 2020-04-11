package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CarDTO {

    private Long id;

    private String plate;

    private String model;

    private LocalDateTime lastAccess;

}
