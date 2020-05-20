package br.edu.utfpr.tsi.utfparking.domain.notification.service;

import br.edu.utfpr.tsi.utfparking.domain.notification.model.Message;
import br.edu.utfpr.tsi.utfparking.domain.notification.model.MessageSendRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendingMessageServiceImpl implements SendingMessageService {

    private final SimpMessagingTemplate simpleMessage;

    private final ApplicationContext context;

    public void sendBeforeTransactionCommit(Message<?> messageRequest, String topic) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "sendBeforeTransactionCommit() was called but there is no transaction active.");
        log.info("prepare to sending message {} on topic {}", messageRequest.message(), topic);
        context.publishEvent(MessageSendRequestDTO.builder()
                .message(messageRequest)
                .topic(topic)
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleSendEvent(MessageSendRequestDTO<Message<?>> sendRequest) {
        log.info("Actually sending message to topic {} before commit.", sendRequest.getTopic());
        var url = String.format("/topic%s", sendRequest.getTopic());
        simpleMessage.convertAndSend(url, sendRequest.getValue().message());
    }
}
