package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.service.RecognizerPlateService;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.receiver.RecognizeReceiver;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecognizerPlateServiceImpl implements RecognizerPlateService {

    private final RecognizeReceiver recognizeReceiver;

    @Override
    public void saveNewRecognizer(InputPlateRecognizerDTO recognizerDTO) {
        recognizeReceiver.receive(recognizerDTO);
    }
}
