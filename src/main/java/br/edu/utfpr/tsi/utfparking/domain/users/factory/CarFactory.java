package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.CarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import org.springframework.stereotype.Component;

@Component
public class CarFactory {

    public static final String AUSENTE = "ausente";

    public Car createCarByInputUser(InputUserDTO dto, User user) {
        var carPlate = setCarPlate(dto.getCarPlate());
        var carModel = setCarModel(dto.getCarModel());

        return Car.builder()
                .user(user)
                .plate(carPlate)
                .model(carModel)
                .build();
    }

    public Car createCarByInputUser(InputUpdateCarDTO dto, User user) {
        var carPlate = setCarPlate(dto.getCarPlate());
        var carModel = setCarModel(dto.getCarModel());

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

    private String setCarModel(String carModel) {
        return carModel == null || carModel.isBlank() ? AUSENTE : carModel;
    }

    private String setCarPlate(String plate) {
        return plate == null || plate.isBlank() ? AUSENTE : plate;
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
