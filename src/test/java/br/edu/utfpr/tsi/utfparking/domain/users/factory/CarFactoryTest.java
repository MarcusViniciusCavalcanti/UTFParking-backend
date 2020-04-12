package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CarFactory.class)
class CarFactoryTest {


    @Autowired
    private CarFactory carFactory;

    @Test
    void shouldCreateCarWhitPlateAndModel() {
        var input = CreateMock.createMockInputUserDTO(
                "name user",
                "username",
                "password",
                TypeUserDTO.SERVICE,
                CreateMock.AUTHORITIES_DRIVER,
                "modelCar",
                "ModelPLate"
        );

        var user = CreateMock.createUserDefaultUser();

        Car result = carFactory.createCarByInputUser(input, user);

        assertNotNull(result.getModel());
        assertNotNull(result.getPlate());

        assertEquals(input.getCarModel(), result.getModel());
        assertEquals(input.getCarPlate(), result.getPlate());
    }

    @Test
    void shouldCreateCarWhitPlateAndModelEmpty() {
        var input = CreateMock.createMockInputUserDTO(
                "name user",
                "username",
                "password",
                TypeUserDTO.SERVICE,
                CreateMock.AUTHORITIES_DRIVER,
                null,
                null
        );

        var user = CreateMock.createUserDefaultUser();

        Car result = carFactory.createCarByInputUser(input, user);

        assertNotNull(result.getModel());
        assertNotNull(result.getPlate());

        assertEquals(CarFactory.AUSENTE, result.getModel());
        assertEquals(CarFactory.AUSENTE, result.getPlate());
    }
}
