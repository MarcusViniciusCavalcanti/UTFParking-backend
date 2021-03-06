package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessNewUserException;
import br.edu.utfpr.tsi.utfparking.domain.exceptions.AuthoritiesNotAllowedException;
import br.edu.utfpr.tsi.utfparking.domain.exceptions.UsernameExistException;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.factory.AccessCardFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.CarFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.AccessCardDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.CarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.ParamsSearchRequestDTO;
import br.edu.utfpr.tsi.utfparking.structure.exceptions.UpdateCarException;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RoleRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private static final long SEVEM_DAYS = 7L;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AccessCardFactory accessCardFactory;

    private final UserFactory userFactory;

    private final CarFactory carFactory;

    private final BCryptPasswordEncoder encoder;

    public UserDTO getUserRequest() {
        var principal = (AccessCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByAccessCard(principal)
                .map(createUserDTO())
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public UserDTO saveNewUser(InputUserDTO inputUser) {
        var user = getUser(inputUser);
        user.getAccessCard().setPassword(encoder.encode(inputUser.getPassword()));
        setCarIfExist(inputUser, user);

        try {
            var newUser = saveOrUpdate(user);
            return CompletableFuture
                    .supplyAsync(() -> userFactory.createUserDTOByUser(newUser))
                    .thenCombineAsync(executorCreateAccessCardDTO(newUser), executorSetAccessCardToUserDTO())
                    .thenCombineAsync(executorCreateCarDTO(newUser), executorSetCarToUserDTO())
                    .handle((dto, exception) -> {
                        if (exception != null) {
                            throw new IlegalProcessNewUserException(exception);
                        }

                        return dto;
                    })
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new IlegalProcessNewUserException(e);
        }
    }

    @Transactional
    public UserDTO editUser(InputUserDTO inputUser, Long id) {
        return userRepository.findById(id)
                .map(setCarInUser(inputUser))
                .map(updateUser(inputUser))
                .map(createUserDTO())
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public UserDTO updateCar(InputUpdateCarDTO inputUpdateCarDTO, Long id) {
        var user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return user.car()
                .map(car -> {
                    var from = LocalDate.now().minusDays(SEVEM_DAYS);
                    var lastModifier = car.getUpdatedAt();
                    var days = ChronoUnit.DAYS.between(from, lastModifier);

                    if (days > 0) {
                        var msg = String.format("Alteração do carro só pode ser feita uma vez por semana, a alteração será possível em %d", days);
                        throw new UpdateCarException(msg);
                    }

                    return car.getUser();
                })
                .map(setCarInUser(inputUpdateCarDTO))
                .map(createUserDTO())
                .orElseThrow();
    }

    public Page<UserDTO> findAllPageableUsers(ParamsSearchRequestDTO paramsSearchRequestDTO) {
        var sort = Sort.by(Sort.Direction.fromString(paramsSearchRequestDTO.getSortDirection()), paramsSearchRequestDTO.getSort());
        var pageable = PageRequest.of(paramsSearchRequestDTO.getPage(), paramsSearchRequestDTO.getSize());

        var userPage = userRepository.findAll(pageable);
        var userDTOS = userPage.stream()
                .map(createUserDTO())
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOS, pageable, userPage.getTotalElements());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User saveOrUpdate(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameExistException(String.format("Username %s already exist", user.getAccessCard().getUsername()));
        }
    }

    private User getUser(InputUserDTO inputUser) {
        if (inputUser.getType() == TypeUserDTO.STUDENTS) {
            var allowedProfiles = TypeUser.valueOf(inputUser.getType().name()).getAllowedProfiles();

            var size = inputUser.getAuthorities().stream()
                    .filter(value -> !allowedProfiles.contains(value))
                    .count();

            if (size != 0) {
                throw new AuthoritiesNotAllowedException();
            }
        }

        var roles = roleRepository.findAllById(inputUser.getAuthorities());

        var accessCard = accessCardFactory.createAccessCardByInputUser(inputUser, roles);
        var user = userFactory.createUserByUserDTO(inputUser);

        user.setAccessCard(accessCard);

        return user;
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(createUserDTO())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User by: %d not found", id)));
    }

    private void setCarIfExist(InputUserDTO inputUser, User user) {
        var isCarPlate = inputUser.getCarPlate() != null && !inputUser.getCarPlate().isEmpty();
        var isCarModel = inputUser.getCarModel() != null && !inputUser.getCarModel().isEmpty();

        if (isCarPlate || isCarModel) {
            var car = carFactory.createCarByInputUser(inputUser, user);
            user.setCar(car);
        }
    }

    private static BiFunction<UserDTO, CarDTO, UserDTO> executorSetCarToUserDTO() {
        return (userDTO, carDTO) -> {
            userDTO.setCar(carDTO);
            return userDTO;
        };
    }

    private CompletableFuture<CarDTO> executorCreateCarDTO(User newUser) {
        return CompletableFuture.supplyAsync(
                () -> newUser.car().map(car -> carFactory.createCarDTOByUser(newUser)).orElse(null));
    }

    private static BiFunction<UserDTO, AccessCardDTO, UserDTO> executorSetAccessCardToUserDTO() {
        return (userDTO, accessCardDTO) -> {
            userDTO.setAccessCard(accessCardDTO);
            return userDTO;
        };
    }

    private CompletableFuture<AccessCardDTO> executorCreateAccessCardDTO(User newUser) {
        return CompletableFuture.supplyAsync(
                () -> accessCardFactory.createAccessCardByUser(newUser));
    }

    private Function<User, UserDTO> createUserDTO() {
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

    private Function<User, User> setCarInUser(InputUserDTO inputUser) {
        return user -> {
            var car = user.car()
                    .map(updateCar(inputUser.getCarPlate(), inputUser.getCarModel()))
                    .orElse(carFactory.createCarByInputUser(inputUser, user));
            user.setCar(car);
            return user;
        };
    }

    private Function<User, User> setCarInUser(InputUpdateCarDTO inputUser) {
        return user -> {
            var car = user.car()
                    .map(updateCar(inputUser.getCarPlate(), inputUser.getCarModel()))
                    .orElse(carFactory.createCarByInputUser(inputUser, user));
            user.setCar(car);
            return user;
        };
    }

    private static Function<Car, Car> updateCar(String carPlate, String carModel) {
        return mapCar -> {
            mapCar.setPlate(carPlate);
            mapCar.setModel(carModel);
            mapCar.setUpdatedAt(LocalDate.now());
            return mapCar;
        };
    }

    private Function<User, User> updateUser(InputUserDTO inputUser) {
        return user -> {
            user.setName(inputUser.getName());
            user.setTypeUser(TypeUser.valueOf(inputUser.getType().name()));
            user.setAuthorisedAccess(inputUser.isAccountNonLocked() && inputUser.isEnabled());

            var roles = roleRepository.findAllById(inputUser.getAuthorities());

            user.getAccessCard().setUsername(inputUser.getUsername());
            user.getAccessCard().setRoles(roles);
            user.getAccessCard().setAccountNonExpired(inputUser.isAccountNonExpired());
            user.getAccessCard().setAccountNonLocked(inputUser.isAccountNonLocked());
            user.getAccessCard().setCredentialsNonExpired(inputUser.isCredentialsNonExpired());
            user.getAccessCard().setEnabled(inputUser.isEnabled());

            return userRepository.save(user);
        };
    }
}
