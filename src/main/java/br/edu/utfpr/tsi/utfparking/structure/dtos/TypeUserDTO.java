package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Getter;

@Getter
public enum  TypeUserDTO {
    STUDENTS("Aluno"),
    SERVICE("Servidor");

    private String description;

    TypeUserDTO(String description) {
        this.description = description;
    }
}
