package br.edu.utfpr.tsi.utfparking.domain.recognizer.executor;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class MultipleResult extends ResultHandler {

    public MultipleResult(ResultHandler next, SendingMessageService sendingMessageService) {
        super(next, sendingMessageService);
    }

    @Override
    public void handleResult(List<ResultRecognizerDTO> results) {
        if (results.size() > 1) {
            results.stream()
                    .filter(car -> {
                        var lastAccess = car.getCar().getLastAccess();
                        var currentTimeMinusTenMinute = LocalDateTime.now().minusMinutes(10);

                        if (Objects.isNull(lastAccess)) {
                            return false;
                        }

                        return lastAccess.isBefore(currentTimeMinusTenMinute);
                    })
                    .max(Comparator.comparing(ResultRecognizerDTO::getConfidence))
                    .ifPresent(result -> {
                        var dto = RecognizerDTO.getNewInstance(result.getCar(), result.getConfidence());
                        this.sending(dto);
                    });

        }

        super.handleResult(results);
    }
}
