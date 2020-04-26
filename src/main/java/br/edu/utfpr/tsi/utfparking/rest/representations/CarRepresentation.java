package br.edu.utfpr.tsi.utfparking.rest.representations;

import br.edu.utfpr.tsi.utfparking.structure.dtos.CarDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarRepresentation extends RepresentationModel<CarRepresentation> {

    private Long carId;

    private String plate;

    private String model;

    private LocalDateTime lastAccess;

    public void copyAttributeBy(CarDTO carDTO) {
        this.carId = carDTO.getId();
        this.plate = carDTO.getPlate();
        this.model = carDTO.getModel();
        this.lastAccess = carDTO.getLastAccess();
    }
}
