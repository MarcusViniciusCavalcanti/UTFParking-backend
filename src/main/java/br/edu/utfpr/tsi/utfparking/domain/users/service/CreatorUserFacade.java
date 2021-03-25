package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.security.factory.AccessCardFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.CarFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreatorUserFacade {
    private final AccessCardFactory accessCardFactory;

    private final UserFactory userFactory;

    private final CarFactory carFactory;

    public Function<User, UserDTO> createUserDTO() {
        return user -> {
            var userDTO = userFactory.createUserDTOByUser(user);
            var accessCardDTO = accessCardFactory.createAccessCardByUser(user);
            var carDTO = user.car()
                .map(car -> carFactory.createCarDTOByUser(car.getUser()))
                .orElse(null);

            userDTO.setAccessCard(accessCardDTO);
            userDTO.setCar(carDTO);

            return userDTO;
        };
    }
}
