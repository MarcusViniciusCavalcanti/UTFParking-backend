package br.edu.utfpr.tsi.utfparking.domain.recognizer.executor;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

public class NoResult extends ResultHandler {

    public NoResult(ResultHandler next, SendingMessageService sendingMessageService) {
        super(next, sendingMessageService);
    }

    @Override
    public void handleResult(List<ResultRecognizerDTO> results) {
        if (!results.isEmpty() && results.get(0).getCar() == null) {
            var message = RecognizerDTO.getNewInstance(null, results.get(0).getConfidence());
            super.sending(message);
        }

        super.handleResult(results);
    }
}
