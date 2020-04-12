package br.edu.utfpr.tsi.utfparking.utils;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMock {
    public static final String CAR_MODEL = "Gol";
    public static final String CAR_PLATE = "abc1234";
    public static final List<Long> AUTHORITIES_ADMIN = List.of(1L, 2L);
    public static final List<Long> AUTHORITIES_DRIVER = List.of(1L);
    public static final List<Long> AUTHORITIES_OPERATOR = List.of(1L, 3L);

    public static InputUserDTO createMockInputUserDTO(String name, String username, String pass, TypeUserDTO type, List<Long> roles, String carModel, String carPlate) {
        var inputUser = new InputUserDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setType(type);
        inputUser.setName(name);
        inputUser.setUsername(username);
        inputUser.setPassword(pass);
        inputUser.setCarModel(carModel);
        inputUser.setCarPlate(carPlate);
        inputUser.setAuthorities(roles);

        return inputUser;
    }

    public static InputUserDTO createMockInputUserDTO(String name, String username, String pass, TypeUserDTO type) {
        return createMockInputUserDTO(name, username, pass, type, AUTHORITIES_ADMIN);
    }

    public static InputUserDTO createMockInputUserDTO(String name, String username, String pass, TypeUserDTO type, List<Long> roles) {
        return createMockInputUserDTO(name, username, pass, type, roles, CAR_MODEL, CAR_PLATE);
    }

    public static Role createRole(Long id, String description) {
        return createRole(id, description, "");
    }

    public static Role createRole(Long id, String description, String name) {
        return Role.builder()
                .id(id)
                .description(description)
                .name(name)
                .build();
    }

    public static AccessCard createAccessCard(Long id, List<Role> roles, String username, String password) {
        return AccessCard.builder()
                .roles(roles)
                .username(username)
                .password(password)
                .id(id)
                .build();
    }

    public static Car createCar(Long id, String model, String plate) {
        return Car.builder()
                .id(id)
                .model(model)
                .plate(plate)
                .build();
    }

    public static User createUser(Long id, AccessCard accessCard, TypeUser typeUser, String name, Car car) {
        return User.builder()
                .accessCard(accessCard)
                .typeUser(typeUser)
                .name(name)
                .id(id)
                .car(car)
                .build();
    }

    public static User createUserDefaultUser() {
        var car = CreateMock.createCar(1L, "gol", "abc1234");
        var role = createRole(1L, "name");
        var accessCard = createAccessCard(1L, List.of(role), "username", "password");

        return createUser(1L, accessCard, TypeUser.STUDENTS, "name", car);
    }
}
