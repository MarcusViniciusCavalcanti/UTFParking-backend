package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.CarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarFactory {

    public static final String AUSENTE = "ausente";

    public Car createCarByInputUser(InputUserDTO dto, User user) {
        String carPlate = dto.getCarPlate() == null || dto.getCarPlate().isBlank() ? AUSENTE : dto.getCarPlate();
        String carModel = dto.getCarModel() == null ||  dto.getCarModel().isBlank() ? AUSENTE :  dto.getCarModel();

        return Car.builder()
                .user(user)
                .plate(carPlate)
                .model(carModel)
                .build();
    }

    public CarDTO createCarDTOByCar(Car car) {
        return CarDTO.builder()
                .id(car.getId())
                .model(car.getModel())
                .plate(car.getPlate())
                .build();
    }

    public CarDTO createCarDTOByUser(User user) {
        Car car = user.getCar();
        return CarDTO.builder()
                .id(car.getId())
                .model(car.getModel())
                .plate(car.getPlate())
                .lastAccess(car.getLastAccess())
                .build();
    }
}
