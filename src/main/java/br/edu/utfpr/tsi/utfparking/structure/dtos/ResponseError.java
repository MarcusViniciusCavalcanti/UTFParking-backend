package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseError {

    private String title;
    private int statusCode;
    private long timestamp;
    private Object error;
    private String path;
}
