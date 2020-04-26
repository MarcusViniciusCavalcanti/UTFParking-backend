package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnCarByPlate() {
        var roles = roleRepository.findAll();
        var accessCard = CreateMock.createAccessCard(null, roles, "username", "password");
        var car = CreateMock.createCar(null, CreateMock.CAR_MODEL, CreateMock.CAR_PLATE);
        var user = CreateMock.createUser(null, accessCard, TypeUser.STUDENTS, "User name", car);

        userRepository.saveAndFlush(user);

        var cars = carRepository.findAllByPlateIn(List.of(car.getPlate()));

        var result = cars.get(0);

        assertNotNull(result.getId());
        assertEquals(CreateMock.CAR_MODEL, result.getModel());
        assertEquals(CreateMock.CAR_PLATE, result.getPlate());
        assertEquals(LocalDate.now(), result.getCreatedAt());
        assertEquals(LocalDate.now(), result.getUpdatedAt());
    }
}
