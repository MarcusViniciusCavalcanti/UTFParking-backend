package br.edu.utfpr.tsi.utfparking.domain.recognizer.service;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Coordinate;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Recognize;
import br.edu.utfpr.tsi.utfparking.structure.repositories.CoordinateRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RecognizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecognizeService {

    private final RecognizeRepository recognizeRepository;

    private final CoordinateRepository coordinateRepository;

    public void saveAll(List<Recognize> recognizes) {
        recognizeRepository.saveAll(recognizes);
    }

    public void saveCoordinates(List<Coordinate> coordinates) {
        coordinateRepository.saveAll(coordinates);
    }

    public boolean isEntryOnTenMinutes(String plate) {
        var start = LocalDateTime.now().minusSeconds(10);
        var end = LocalDateTime.now();
        return recognizeRepository.findByPlateAndEpochTimeBetween(plate, start, end).isPresent();
    }
}
