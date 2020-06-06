package br.edu.utfpr.tsi.utfparking.domain.recognizer.receiver;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.service.RecognizeService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.CarService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecognizeReceiverTest {

    @Mock
    private RecognizeService recognizeService;

    @Mock
    private CarService carService;

    @Mock
    private SendingMessageService sendingMessageService;

    @InjectMocks
    private RecognizeReceiver recognizeReceiver;

    @Test
    void shouldNotExecutingVerificationBecauseResultNotMajor75Confidence() {
        var dto = createDTO(60F);

        recognizeReceiver.receive(dto);

        verify(sendingMessageService, times(0)).sendBeforeTransactionCommit(any(), any());
    }

    @Test
    void shouldExecuteVerificationBecauseResultIsMajor75Confidence() {
        var dto = createDTO(75F);

        recognizeReceiver.receive(dto);

        verify(sendingMessageService, times(1)).sendBeforeTransactionCommit(any(), any());
    }

    @Test
    void shouldExecuteVerificationBecauseResultIsMajor75ConfidenceOnList() {
        var dto = createDTO(75F);

        dto.setResults(List.of(getResult(64F), getResult(62F), getResult(74F), getResult(75F)));

        recognizeReceiver.receive(dto);

        verify(sendingMessageService, times(1)).sendBeforeTransactionCommit(any(), any());
    }

    private InputPlateRecognizerDTO createDTO(float confidence) {
        var plateRecognizerDTO = new InputPlateRecognizerDTO();

        plateRecognizerDTO.setCameraId(1);
        plateRecognizerDTO.setEpochTime(System.currentTimeMillis());
        plateRecognizerDTO.setImgHeight(480);
        plateRecognizerDTO.setImgWidth(640);
        plateRecognizerDTO.setProcessingTimeMs(138.669163f);

        var result = getResult(confidence);
        plateRecognizerDTO.setResults(List.of(result));

        return plateRecognizerDTO;
    }

    private InputPlateRecognizerDTO.Result getResult(float confidence) {
        var result = new InputPlateRecognizerDTO.Result();
        result.setConfidence(confidence);
        result.setMatchesTemplate(0);
        result.setRegion("");
        result.setPlate("AAY5127");

        var coordinate = new InputPlateRecognizerDTO.Coordinate();
        coordinate.setX(218f);
        coordinate.setY(218f);

        result.setCoordinates(List.of(coordinate));

        return result;
    }
}
