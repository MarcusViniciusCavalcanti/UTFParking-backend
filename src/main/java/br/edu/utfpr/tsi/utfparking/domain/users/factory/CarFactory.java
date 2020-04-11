package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.CarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CarFactory {

    public Car createCarByInputUser(InputUserNewDTO dto, User user) {
        return Car.builder()
                .user(user)
                .plate(dto.getCarPlate().equalsIgnoreCase("ausente") ? dto.getId().toString() : dto.getCarPlate())
                .model(dto.getCarModel())
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
