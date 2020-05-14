package br.edu.utfpr.tsi.utfparking.domain.notification.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageResponse<T> {

    private T message;

}
