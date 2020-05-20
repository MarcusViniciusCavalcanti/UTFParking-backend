package br.edu.utfpr.tsi.utfparking.domain.notification.service;

import br.edu.utfpr.tsi.utfparking.domain.notification.model.Message;
import org.springframework.stereotype.Service;

@Service
public interface SendingMessageService {
    void sendBeforeTransactionCommit(Message<?> messageRequest, String topic);
}
