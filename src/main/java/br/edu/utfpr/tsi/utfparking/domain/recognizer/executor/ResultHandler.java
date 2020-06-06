package br.edu.utfpr.tsi.utfparking.domain.recognizer.executor;

import br.edu.utfpr.tsi.utfparking.domain.notification.model.RecognizeMessage;
import br.edu.utfpr.tsi.utfparking.domain.notification.model.TopicApplication;
import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public abstract class ResultHandler {

    private final ResultHandler next;

    private final SendingMessageService sendingMessageService;

    public void handleResult(List<ResultRecognizerDTO> results) {
        if (Objects.nonNull(this.next)) {
            this.getNext().handleResult(results);
        }
    }

    public ResultHandler getNext() {
        return this.next;
    }

    protected void sending(RecognizerDTO message) {
        log.info("{} handling result '{}'", this, message);
        var recognizeMessage = RecognizeMessage.builder().recognizerDTO(message).build();
        sendingMessageService.sendBeforeTransactionCommit(recognizeMessage, TopicApplication.RECOGNIZER.getTopicName());
    }

}
