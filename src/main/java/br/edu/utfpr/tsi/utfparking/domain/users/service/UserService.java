package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.exceptions.AuthoritiesNotAllowedException;
import br.edu.utfpr.tsi.utfparking.domain.exceptions.UsernameExistException;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.factory.AccessCardFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.CarFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.*;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RoleRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AccessCardFactory accessCardFactory;

    private final UserFactory userFactory;

    private final CarFactory carFactory;

    private final BCryptPasswordEncoder encoder;

    public UserDTO getUserRequest() {
        var principal = (AccessCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByAccessCard(principal)
                .map(user -> UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .accessCard(createAccessCardDTO(user, createRoleDTOS(user)))
                .build())
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public UserDTO saveNewUser(InputUserNewDTO inputUser) {
        var user = getUser(inputUser);
        user.getAccessCard().setPassword(encoder.encode(inputUser.getPassword()));

        setCarIfExist(inputUser, user);

        try {
            User newUser = saveOrUpdate(user);
            return CompletableFuture
                    .supplyAsync(() -> userFactory.createUserDTOByUser(newUser))
                    .thenCombineAsync(executorCreateAccessCardDTO(newUser), executorSetAccessCardToUserDTO())
                    .thenCombineAsync(executorCreateCarDTO(newUser), executorSetCarToUserDTO())
                    .handle((dto, exception) -> {
                        if (exception != null) {
                            throw new RuntimeException(exception);
                        }

                        return dto;
                    })
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Page<UserDTO> findAllPageableUsers(Pageable pageable) {
        var userPage = userRepository.findAll(pageable);
        var userDTOS = userPage.stream()
                .map(userFactory::createUserDTOByUser)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOS, pageable, userPage.getTotalElements());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public Boolean existUserByUsername(String username) {
        return userRepository.existsUserByAccessCardUsername(username);
    }

    private User saveOrUpdate(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameExistException(String.format("Username %s already exist", user.getAccessCard().getUsername()));
        }
    }

    private AccessCardDTO createAccessCardDTO(User user, List<RoleDTO> roles) {
        return AccessCardDTO.builder()
                .accountNonExpired(user.getAccessCard().isAccountNonExpired())
                .accountNonExpired(user.getAccessCard().isAccountNonExpired())
                .credentialsNonExpired(user.getAccessCard().isCredentialsNonExpired())
                .enabled(user.getAccessCard().isEnabled())
                .roles(roles)
                .build();
    }

    private List<RoleDTO> createRoleDTOS(User user) {
        return user.getAccessCard().getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .description(role.getDescription())
                        .name(role.getName())
                        .build()
                ).collect(Collectors.toList());
    }

    private User getUser(InputUserNewDTO inputUser) {
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
                .map(userFactory::createUserDTOByUser)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User by: %d not found", id)));
    }

    private void setCarIfExist(InputUserNewDTO inputUser, User user) {
        if ((inputUser.getCarPlate() != null && !inputUser.getCarPlate().isEmpty()) ||
                (inputUser.getCarModel() != null && !inputUser.getCarModel().isEmpty())) {
            var car = carFactory.createCarByInputUser(inputUser, user);
            user.setCar(car);
        }
    }

    private BiFunction<UserDTO, CarDTO, UserDTO> executorSetCarToUserDTO() {
        return (userDTO, carDTO) -> {
            userDTO.setCar(carDTO);
            return userDTO;
        };
    }

    private CompletableFuture<CarDTO> executorCreateCarDTO(User newUser) {
        return CompletableFuture.supplyAsync(
                () -> newUser.car().map(car -> carFactory.createCarDTOByUser(newUser)).orElse(null));
    }

    private BiFunction<UserDTO, AccessCardDTO, UserDTO> executorSetAccessCardToUserDTO() {
        return (userDTO, accessCardDTO) -> {
            userDTO.setAccessCard(accessCardDTO);
            return userDTO;
        };
    }

    private CompletableFuture<AccessCardDTO> executorCreateAccessCardDTO(User newUser) {
        return CompletableFuture.supplyAsync(
                () -> accessCardFactory.createAccessCardByUserDTO(newUser));
    }
}
