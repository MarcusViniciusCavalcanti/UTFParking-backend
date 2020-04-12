package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class UserDTO {
    private Long id;

    private String name;

    private TypeUserDTO typeUser;

    private AccessCardDTO accessCard;

    private CarDTO car;

    public Optional<CarDTO> car() {
        return Optional.ofNullable(car);
    }
}
