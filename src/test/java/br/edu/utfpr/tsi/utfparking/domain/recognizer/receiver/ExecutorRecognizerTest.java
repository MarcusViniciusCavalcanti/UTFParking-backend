package br.edu.utfpr.tsi.utfparking.domain.recognizer.receiver;

import br.edu.utfpr.tsi.utfparking.domain.notification.model.TopicApplication;
import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.service.RecognizeService;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.service.CarService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutorRecognizerTest {

    public static final String PLATE = "abc1234";
    @Mock
    private RecognizeService recognizeService;

    @Mock
    private CarService carService;

    @Mock
    private SendingMessageService sendingMessageService;

    @Test
    void shouldHaveNotSendingResultWhenVerifierIsTrue() {
        var executor = new ExecutorRecognizer(recognizeService, carService, sendingMessageService, new InputPlateRecognizerDTO());

        when(recognizeService.isEntryOnTenMinutes(PLATE)).thenReturn(true);

        executor.accept(PLATE);

        verify(sendingMessageService, times(0)).sendBeforeTransactionCommit(any(), anyString());
    }

    @Test
    void shouldSendingNonResult() {
        InputPlateRecognizerDTO result = createDTO(90);

        var abcI123 = "abcI123";
        var userDefaultUser1 = CreateMock.createUserDefaultUser();
        var car1 = Car.builder()
                .model("Model 1")
                .plate(abcI123)
                .user(userDefaultUser1)
                .lastAccess(LocalDateTime.now().minusMinutes(30))
                .build();
        userDefaultUser1.setCar(car1);

        String abcII23 = "abcII23";
        var userDefaultUser2 = CreateMock.createUserDefaultUser();
        var car2 = Car.builder()
                .model("Model 1")
                .plate(abcII23)
                .user(userDefaultUser2)
                .lastAccess(LocalDateTime.now().minusMinutes(30))
                .build();
        userDefaultUser2.setCar(car2);

        String abc12E = "abc12E";
        var userDefaultUser3 = CreateMock.createUserDefaultUser();
        var car3 = Car.builder()
                .model("Model 1")
                .plate(abc12E)
                .user(userDefaultUser3)
                .lastAccess(LocalDateTime.now().minusMinutes(30))
                .build();
        userDefaultUser3.setCar(car3);

        var resulToSending = getResult(99, abc12E);
        result.setResults(List.of(getResult(92, abcI123), getResult(95, abcII23), resulToSending));
        var executor = new ExecutorRecognizer(recognizeService, carService, sendingMessageService, result);

        when(recognizeService.isEntryOnTenMinutes(PLATE)).thenReturn(false);
        when(carService.getCarByPlates(anyList())).thenReturn(List.of(car1, car2, car3));

        executor.accept(PLATE);

        verify(sendingMessageService).sendBeforeTransactionCommit(
                argThat(
                    message -> {
                        var msg = (RecognizerDTO) message.message();

                        var isDrive = msg.getDriver().getCarModel().equals(car3.getModel());
                        var isPlate = msg.getDriver().getPlate().equals(car3.getPlate());

                        return isDrive && isPlate;
                    }),
                argThat(topicName -> topicName.equals(TopicApplication.RECOGNIZER.getTopicName())));
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
        return getResult(confidence, PLATE);
    }

    private InputPlateRecognizerDTO.Result getResult(float confidence, String plate) {
        var result = new InputPlateRecognizerDTO.Result();
        result.setConfidence(confidence);
        result.setMatchesTemplate(0);
        result.setRegion("");
        result.setPlate(plate);

        var coordinate = new InputPlateRecognizerDTO.Coordinate();
        coordinate.setX(218f);
        coordinate.setY(218f);

        result.setCoordinates(List.of(coordinate));

        return result;
    }
}
