package br.edu.utfpr.tsi.utfparking.domain.recognizer.executor;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;

import java.util.List;

public class OneResult extends ResultHandler {

    public OneResult(ResultHandler next, SendingMessageService sendingMessageService) {
        super(next, sendingMessageService);
    }

    @Override
    public void handleResult(List<ResultRecognizerDTO> results) {
        if (results.size() == 1 && results.get(0).getCar() != null) {
            var car = results.get(0).getCar();
            var confidence = results.get(0).getConfidence();
            var dto = RecognizerDTO.getNewInstance(car, confidence);
            super.sending(dto);
        }

        super.handleResult(results);
    }
}
