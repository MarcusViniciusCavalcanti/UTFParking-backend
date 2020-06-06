package br.edu.utfpr.tsi.utfparking.domain.recognizer.receiver;

import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Coordinate;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Recognize;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.executor.ExecutorResult;
import br.edu.utfpr.tsi.utfparking.domain.recognizer.service.RecognizeService;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.service.CarService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.CarResultDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResultRecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserCarResultDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ExecutorRecognizer implements Consumer<String> {

    private final RecognizeService recognizeService;

    private final CarService carService;

    private final SendingMessageService sendingMessageService;

    private final InputPlateRecognizerDTO dto;

    @Override
    public void accept(String plate) {
        if (!recognizeService.isVerifier(plate)) {
            var recognizes = createRecognizers(dto);

            var coordinates = recognizes.stream()
                    .flatMap(recognize -> recognize.getCoordinates().stream())
                    .collect(Collectors.toList());

            recognizeService.saveCoordinates(coordinates);
            recognizeService.saveAll(recognizes);

            var firstTwoPlace = dto.getResults().stream()
                    .sorted(Comparator.comparing(InputPlateRecognizerDTO.Result::getConfidence).reversed())
                    .collect(Collectors.toList());

            var plates = firstTwoPlace.stream()
                    .map(InputPlateRecognizerDTO.Result::getPlate)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            dto.getResults().stream()
                    .findFirst()
                    .ifPresent(result -> executeResult(plates, result));
        }
    }

    private void executeResult(List<String> plates, InputPlateRecognizerDTO.Result result) {
        var cars = carService.getCarByPlates(plates).stream()
                .map(this::createCarResultDTO)
                .map(car -> new ResultRecognizerDTO(car, result.getConfidence()))
                .collect(Collectors.toList());

        new ExecutorResult(sendingMessageService).sendingResult(
                cars.isEmpty() ? List.of(new ResultRecognizerDTO(null, result.getConfidence())) : cars
        );
    }

    private CarResultDTO createCarResultDTO(Car car) {
        var user = car.getUser();
        var userResultDTO = UserCarResultDTO.builder()
                .accessNumber(user.getNumberAccess())
                .authorizedAccess(user.getAuthorisedAccess())
                .name(user.getName())
                .id(user.getId())
                .typeUser(TypeUserDTO.valueOf(user.getTypeUser().name()))
                .build();

        return CarResultDTO.builder()
                .id(car.getId())
                .model(car.getModel())
                .plate(car.getPlate())
                .lastAccess(car.getLastAccess())
                .user(userResultDTO)
                .build();
    }

    private List<Recognize> createRecognizers(InputPlateRecognizerDTO dto) {
        var cameraId = dto.getCameraId();
        var siteId = dto.getSiteId();
        var processingTimeMs = dto.getProcessingTimeMs();
        var epochTime = dto.getEpochTime();
        var uuid = dto.getUuid();

        return dto.getResults().stream()
                .map(result -> {
                    var plate = result.getPlate();
                    var confidence = result.getConfidence();
                    var matchesTemplate = result.getMatchesTemplate();
                    var coordinates = createCoordinates(result);

                    return Recognize.builder()
                            .cameraId(cameraId)
                            .coordinates(coordinates)
                            .epochTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), TimeZone.getDefault().toZoneId()))
                            .origin(siteId)
                            .plate(plate)
                            .uuid(UUID.nameUUIDFromBytes((System.currentTimeMillis() + " "+ uuid).getBytes()))
                            .processingTimeMs(processingTimeMs)
                            .confidence(confidence)
                            .matchesTemplate(matchesTemplate)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<Coordinate> createCoordinates(InputPlateRecognizerDTO.Result result) {
        return result.getCoordinates().stream()
                .map(coordinate -> Coordinate.builder()
                        .axiosX(coordinate.getX())
                        .axiosY(coordinate.getY())
                        .build())
                .collect(Collectors.toList());
    }
}
