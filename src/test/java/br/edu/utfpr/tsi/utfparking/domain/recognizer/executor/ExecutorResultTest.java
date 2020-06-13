package br.edu.utfpr.tsi.utfparking.domain.recognizer.executor;

import br.edu.utfpr.tsi.utfparking.domain.notification.model.RecognizeMessage;
import br.edu.utfpr.tsi.utfparking.domain.notification.model.TopicApplication;
import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.CarResultDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserCarResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExecutorResultTest {

    @Mock
    private SendingMessageService sendingMessageService;

    @Test
    void shouldSendingRecognizeWithMajorConfidence() {
        var multipleResult = new MultipleResult(null, sendingMessageService);

        var user = UserCarResultDTO.builder()
                .typeUser(TypeUserDTO.STUDENTS)
                .id(1L)
                .authorizedAccess(true)
                .accessNumber(10)
                .name("Name User")
                .build();

        var car = CarResultDTO.builder()
                .user(user)
                .lastAccess(LocalDateTime.now().minusSeconds(1))
                .model("Car Model")
                .plate("abc1234")
                .id(1L)
                .build();

        var userOther = UserCarResultDTO.builder()
                .typeUser(TypeUserDTO.STUDENTS)
                .id(1L)
                .authorizedAccess(true)
                .accessNumber(10)
                .name("Other Name User")
                .build();

        var carOther = CarResultDTO.builder()
                .user(userOther)
                .lastAccess(LocalDateTime.now().minusMinutes(40))
                .model("Other Car Model")
                .plate("abc1235")
                .id(1L)
                .build();

        var result = new ResultRecognizerDTO(car, 89.8F);
        var resultOther = new ResultRecognizerDTO(carOther, 89.9F);

        multipleResult.handleResult(List.of(result, resultOther));

        verify(sendingMessageService).sendBeforeTransactionCommit(
                argThat(message -> ((RecognizeMessage) message).message().getDriver().getUserName().equals(userOther.getName())),
                argThat(topicName -> topicName.equals(TopicApplication.RECOGNIZER.getTopicName()))
        );
    }

    @Test
    public void shouldSendingRecognizeWhenNoIdentifierCarPlateOnDatabase() {
        var noResult = new NoResult(null, sendingMessageService);
        noResult.handleResult(List.of(new ResultRecognizerDTO(null, 10.0F)));

        verify(sendingMessageService).sendBeforeTransactionCommit(
                argThat(message -> !((RecognizeMessage) message).message().getIdentified()),
                argThat(topicName -> topicName.equals(TopicApplication.RECOGNIZER.getTopicName()))
        );
    }

    @Test
    public void shouldSendingRecognizeWhenOneResult() {
        var oneResult = new OneResult(null, sendingMessageService);

        var user = UserCarResultDTO.builder()
                .typeUser(TypeUserDTO.STUDENTS)
                .id(1L)
                .authorizedAccess(true)
                .accessNumber(10)
                .name("Name User")
                .build();

        var car = CarResultDTO.builder()
                .user(user)
                .lastAccess(LocalDateTime.now().minusSeconds(40))
                .model("Car Model")
                .plate("abc1234")
                .id(1L)
                .build();

        oneResult.handleResult(List.of(new ResultRecognizerDTO(car, 10.0F)));

        verify(sendingMessageService).sendBeforeTransactionCommit(
                argThat(message -> ((RecognizeMessage) message).message().getDriver().getUserName().equals(user.getName())),
                argThat(topicName -> topicName.equals(TopicApplication.RECOGNIZER.getTopicName()))
        );
    }

    @Test
    void shouldResultIsEmptyList() {
        var resultResolver = new OneResult(null, sendingMessageService);

        resultResolver.handleResult(List.of());

        verify(sendingMessageService, times(0)).sendBeforeTransactionCommit(any(), anyString());
    }

    @Test
    void shouldSendingMessagenWhenLastAccessIsNull() {
        var multipleResult = new MultipleResult(null, sendingMessageService);

        var user = UserCarResultDTO.builder()
                .typeUser(TypeUserDTO.STUDENTS)
                .id(1L)
                .authorizedAccess(true)
                .accessNumber(10)
                .name("Name User")
                .build();

        var car = CarResultDTO.builder()
                .user(user)
                .lastAccess(LocalDateTime.now().minusSeconds(1))
                .model("Car Model")
                .plate("abc1234")
                .id(1L)
                .build();

        var userOther = UserCarResultDTO.builder()
                .typeUser(TypeUserDTO.STUDENTS)
                .id(1L)
                .authorizedAccess(true)
                .accessNumber(10)
                .name("Other Name User")
                .build();

        var carOther = CarResultDTO.builder()
                .user(userOther)
                .lastAccess(LocalDateTime.now().minusMinutes(40))
                .model("Other Car Model")
                .plate("abc1235")
                .id(1L)
                .build();

        var result = new ResultRecognizerDTO(car, 99.8F);
        var resultOther = new ResultRecognizerDTO(carOther, 89.9F);

        multipleResult.handleResult(List.of(result, resultOther));

        verify(sendingMessageService).sendBeforeTransactionCommit(
                argThat(message -> ((RecognizeMessage) message).message().getDriver().getUserName().equals(userOther.getName())),
                argThat(topicName -> topicName.equals(TopicApplication.RECOGNIZER.getTopicName()))
        );
    }

    @Test
    void shouldResultIsCarNull() {
        var resultResolver = new OneResult(null, sendingMessageService);

        resultResolver.handleResult(List.of(new ResultRecognizerDTO(null, 89.8F)));

        verify(sendingMessageService, times(0)).sendBeforeTransactionCommit(any(), anyString());
    }

    @Test
    void shouldResultIsListEmpty() {
        var resultResolver = new OneResult(null, sendingMessageService);

        resultResolver.handleResult(List.of());

        verify(sendingMessageService, times(0)).sendBeforeTransactionCommit(any(), anyString());
    }
}
