package br.edu.utfpr.tsi.utfparking.domain.notification.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class MessageSendRequestDTO<T extends Message<?>> {

    private T message;

    @Getter
    private String topic;

    public T getValue() {
        return message;
    }
}
