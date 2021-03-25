package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Getter;

public enum  TypeUserDTO {
    STUDENTS("Estudante"), SERVICE("Servidor"), SPEAKER("Palestrante");

    @Getter
    private final String translaterName;

    private TypeUserDTO(String translaterName) {
        this.translaterName = translaterName;
    }
}
