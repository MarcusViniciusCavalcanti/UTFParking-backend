package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.structure.repositories.CarRepository;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    private static final String ABC_1234 = "ABC1234";
    private static final String ABC_1235 = "ABC1235";
    private static final long ID_1 = 1L;
    private static final long ID_2L = 2L;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    public void shout_return_car_by_plate() {
        var car1 = Car.builder()
                .lastAccess(LocalDateTime.now())
                .model("model")
                .plate(ABC_1234)
                .id(1L)
                .user(CreateMock.createUserDefaultUser())
                .build();

        var car2 = Car.builder()
                .lastAccess(LocalDateTime.now())
                .model("model")
                .plate(ABC_1235)
                .id(2L)
                .user(CreateMock.createUserDefaultUser())
                .build();

        when(carRepository.findAllByPlateIn(List.of(ABC_1234, ABC_1235))).thenReturn(List.of(car1, car2));

        var result = carService.getCarByPlates(List.of(ABC_1234, ABC_1235));

        var resultCar1 = result.get(0);
        var resultCar2 = result.get(1);

        assertEquals(ABC_1234, resultCar1.getPlate());
        assertEquals(ID_1, resultCar1.getId());

        assertEquals(ABC_1235, resultCar2.getPlate());
        assertEquals(ID_2L, resultCar2.getId());
    }

}
