package br.edu.utfpr.tsi.utfparking.domain.notification.model;

import br.edu.utfpr.tsi.utfparking.structure.dtos.RecognizerDTO;
import lombok.Builder;

@Builder
public class RecognizeMessage  implements Message<RecognizerDTO>{

    private final RecognizerDTO recognizerDTO;

    @Override
    public RecognizerDTO message() {
        return recognizerDTO;
    }
}
