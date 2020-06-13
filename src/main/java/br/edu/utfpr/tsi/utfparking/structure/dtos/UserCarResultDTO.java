package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCarResultDTO {

    private final Long id;

    private final String name;

    private final TypeUserDTO typeUser;

    private final Boolean authorizedAccess;

    private final Integer accessNumber;

}
