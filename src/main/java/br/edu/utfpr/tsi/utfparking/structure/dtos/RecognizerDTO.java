package br.edu.utfpr.tsi.utfparking.structure.dtos;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class RecognizerDTO {

    private Boolean identified;

    private Boolean authorize;

    private Driver driver;

    private Float tax;

    public static RecognizerDTO getNewInstance(CarResultDTO car, Float tax) {
        var recognizerDTO = new RecognizerDTO();
        recognizerDTO.setTax(tax);

        if (car != null) {
            var model = car.getModel();
            var lastAccess = car.getLastAccess() == null ? LocalDateTime.now() : car.getLastAccess();
            var id = car.getUser().getId();
            var name = car.getUser().getName();
            var type = car.getUser().getTypeUser().name();
            var accessNumber = car.getUser().getAccessNumber();
            var authorized = car.getUser().getAuthorizedAccess();
            var plate = car.getPlate();

            var driver = new Driver();
            driver.userId = id;
            driver.carModel = model;
            driver.plate = plate;
            driver.lastAccess = String.format("%d/%d/%d", lastAccess.getDayOfMonth(), lastAccess.getMonthValue() + 1, lastAccess.getYear());
            driver.lastHoursAccess = String.format("%d : %d", lastAccess.getHour(), lastAccess.getMinute());
            driver.userName = name;
            driver.userAvatar = "/user/avatar/" + id.toString();
            driver.type = type;
            driver.authorizedAccess = authorized;
            driver.accessNumber = accessNumber;

            recognizerDTO.setDriver(driver);
            recognizerDTO.setAuthorize(car.getUser().getAuthorizedAccess());
            recognizerDTO.setIdentified(true);
        } else {
            recognizerDTO.setIdentified(false);
        }

        return recognizerDTO;
    }

    @Data
    public static class Driver {
        private Long userId;
        private String userName;
        private String type;
        private String userAvatar;
        private String lastAccess;
        private String lastHoursAccess;
        private String carModel;
        private String plate;
        private Boolean authorizedAccess;
        private Integer accessNumber;
    }
}
