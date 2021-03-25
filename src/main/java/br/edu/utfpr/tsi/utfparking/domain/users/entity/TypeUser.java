package br.edu.utfpr.tsi.utfparking.domain.users.entity;

import lombok.Getter;

import java.util.List;

public enum TypeUser {
    STUDENTS(List.of(1L)),
    SPEAKER(List.of(1L)),
    SERVICE(List.of(1L, 2L, 3L));

    @Getter
    private List<Long> allowedProfiles;

    TypeUser(List<Long> allowedProfiles) {
        this.allowedProfiles = allowedProfiles;
    }
}
