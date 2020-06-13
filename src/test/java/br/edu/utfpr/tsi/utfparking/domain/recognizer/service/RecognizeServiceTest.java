package br.edu.utfpr.tsi.utfparking.domain.recognizer.service;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Recognize;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RecognizeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecognizeServiceTest {

    @Mock
    private RecognizeRepository recognizeRepository;

    @InjectMocks
    private RecognizeService recognizeService;

    @Test
    void shouldReturnTrueWhenExisteEntryBeforeTeenMinute() {
        var plate = "abc1234";
        when(recognizeRepository.findByPlateAndEpochTimeBetween(eq(plate), any(), any()))
                .thenReturn(Optional.of(Recognize.builder().build()));

        assertTrue(recognizeService.isEntryOnTenMinutes(plate));
    }

    @Test
    void shouldReturnTrueWhenExisteEntryAfterTeenMinute() {
        var plate = "abc1234";
        when(recognizeRepository.findByPlateAndEpochTimeBetween(eq(plate), any(), any())).thenReturn(Optional.empty());

        assertFalse(recognizeService.isEntryOnTenMinutes(plate));
    }
}
