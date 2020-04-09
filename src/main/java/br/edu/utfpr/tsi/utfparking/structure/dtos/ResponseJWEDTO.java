package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ResponseJWEDTO {
    private String token;
    private Long expiration;
}
