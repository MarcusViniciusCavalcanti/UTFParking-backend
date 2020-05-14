package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationConfigDTO {

    private String modeSystem;

    private String ip;
}
