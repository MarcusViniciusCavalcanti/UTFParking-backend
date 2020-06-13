package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Coordinate;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Recognize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RecognizeRepositoryTest {

    @Autowired
    private RecognizeRepository recognizeRepository;

    @Test
    void shouldSaveNewRecognizer() {
        var coordinate = Coordinate.builder()
                .axiosX(10F)
                .axiosY(10F)
                .build();

        var recognize = Recognize.builder()
                .origin("origin")
                .matchesTemplate(10)
                .epochTime(LocalDateTime.now())
                .confidence(90F)
                .cameraId(10)
                .plate("ABC1234")
                .processingTimeMs(20000F)
                .uuid(UUID.randomUUID())
                .coordinates(List.of(coordinate))
                .build();

        assertNotNull(recognizeRepository.save(recognize));
    }

    @Test
    void shouldReturnRecogniser() {
        var coordinate = Coordinate.builder()
                .axiosX(10F)
                .axiosY(10F)
                .build();

        var recognize = Recognize.builder()
                .origin("origin")
                .matchesTemplate(10)
                .epochTime(LocalDateTime.now())
                .confidence(90F)
                .cameraId(10)
                .plate("ABC1234")
                .processingTimeMs(20000F)
                .uuid(UUID.randomUUID())
                .coordinates(List.of(coordinate))
                .build();

        recognizeRepository.save(recognize).getId();

        recognizeRepository.findByPlateAndEpochTimeBetween("ABC1234", LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5))
                .ifPresent(rec -> {
                    assertEquals(recognize.getOrigin(), rec.getOrigin());
                    assertEquals(recognize.getPlate(), rec.getPlate());
                    assertEquals(recognize.getEpochTime(), rec.getEpochTime());
                    assertEquals(recognize.getProcessingTimeMs(), rec.getProcessingTimeMs());
                    assertEquals(recognize.getUuid(), rec.getUuid());
                    assertEquals(recognize.getMatchesTemplate(), rec.getMatchesTemplate());
                    assertEquals(recognize.getCoordinates(), rec.getCoordinates());
                    assertEquals(recognize.getCameraId(), rec.getCameraId());
                    assertEquals(recognize.getConfidence(), rec.getConfidence());
                    assertNotNull(rec.getCreatedAt());
                });

    }
}
