package br.edu.utfpr.tsi.utfparking.structure.dtos.inputs;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InputUpdateCarDTO {

    @NotBlank(message = "Não deve ser nulo ou branco")
    private String carModel;

    @NotBlank(message = "Não deve ser nulo ou branco")
    private String carPlate;

}
