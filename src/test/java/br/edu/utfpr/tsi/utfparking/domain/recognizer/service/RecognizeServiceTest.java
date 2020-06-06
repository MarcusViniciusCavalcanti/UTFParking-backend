package br.edu.utfpr.tsi.utfparking.domain.recognizer.service;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Coordinate;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Recognize;
import br.edu.utfpr.tsi.utfparking.structure.repositories.CoordinateRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RecognizeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RecognizeServiceTest {

    @Mock
    private RecognizeRepository recognizeRepository;

    @Mock
    private CoordinateRepository coordinateRepository;

    @InjectMocks
    private RecognizeService recognizeService;

    @Test
    void shouldReturnTrueWhenExisteEntryBeforeTeenMinute() {
        var plate = "abc1234";
        Mockito.when(recognizeRepository.findByPlateAndEpochTimeBetween(eq(plate), any(), any())).thenReturn(Optional.of(Recognize.builder().build()));

        assertTrue(recognizeService.isVerifier(plate));
    }

    @Test
    void shouldReturnTrueWhenExisteEntryAfterTeenMinute() {
        var plate = "abc1234";
        Mockito.when(recognizeRepository.findByPlateAndEpochTimeBetween(eq(plate), any(), any())).thenReturn(Optional.empty());

        assertFalse(recognizeService.isVerifier(plate));
    }
}
