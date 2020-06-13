package br.edu.utfpr.tsi.utfparking.application.service;

import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import org.springframework.stereotype.Service;

@Service
public interface RecognizerPlateService {

    void saveNewRecognizer(InputPlateRecognizerDTO recognizerDTO);
}
