package br.edu.utfpr.tsi.utfparking.domain.recognizer.executor;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

public final class ExecutorResult {

    private ResultHandler resultHandler;

    public ExecutorResult(SendingMessageService sendingMessageService) {
        var noResult = new NoResult(null, sendingMessageService);
        var multipleResult = new MultipleResult(noResult, sendingMessageService);
        var oneResult = new OneResult(multipleResult, sendingMessageService);

        initialHandlerResult(oneResult);
    }

    public void sendingResult(List<ResultRecognizerDTO> cars) {
        resultHandler.handleResult(cars);
    }

    protected void initialHandlerResult(ResultHandler handler) {
        resultHandler = handler;
    }
}
