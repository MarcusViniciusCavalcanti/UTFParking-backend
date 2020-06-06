package br.edu.utfpr.tsi.utfparking.domain.recognizer.receiver;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.service.RecognizeService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.CarService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecognizeReceiver {

    private final RecognizeService recognizeService;

    private final CarService carService;

    private final SendingMessageService sendingMessageService;

    @Transactional
    public void receive(InputPlateRecognizerDTO dto) {
        dto.getResults().stream()
                .filter(result -> result.getConfidence() >= 75.0F)
                .findFirst()
                .map(InputPlateRecognizerDTO.Result::getPlate)
                .ifPresent(new ExecutorRecognizer(recognizeService, carService, sendingMessageService, dto));
    }
}
