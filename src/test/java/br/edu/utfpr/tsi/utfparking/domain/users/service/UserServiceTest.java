package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.exceptions.AuthoritiesNotAllowedException;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.security.factory.AccessCardFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.CarFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.*;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RoleRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final long ID_USER = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private CarFactory carFactory;

    @Mock
    private AccessCardFactory accessCardFactory;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldDeleteUserById() {
        doNothing().when(userRepository).deleteById(eq(ID_USER));

        userService.delete(ID_USER);
        verify(userRepository, times(1)).deleteById(eq(ID_USER));
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(eq(ID_USER))).thenReturn(Optional.of(createMockUser()));
        when(userFactory.createUserDTOByUser(any())).thenReturn(createMockUserDTO());

        var userDTO = userService.findById(ID_USER);

        assertNotNull(userDTO);
        assertEquals(ID_USER, userDTO.getId());
    }

    @Test
    void shouldReturnErrorWhenProfileNotAuthorities() {
        var inputUser = createMockInputUserNewDTO(TypeUser.STUDENTS);
        inputUser.setAuthorities(TypeUser.SERVICE.getAllowedProfiles());


        assertThrows(AuthoritiesNotAllowedException.class, () -> userService.saveNewUser(inputUser));
    }

    @Test
    void shouldHaveCreateNewUser() {
        var inputUser = createMockInputUserNewDTO(TypeUser.SERVICE);

        var role = Role.builder()
                .id(1L)
                .description("description")
                .build();

        AccessCard accessCard = AccessCard.builder()
                .roles(List.of(role))
                .username("username")
                .password("password")
                .id(1L)
                .build();

        Car car = Car.builder()
                .id(1L)
                .model("Model car")
                .plate("abc1234")
                .build();

        User user = User.builder()
                .accessCard(accessCard)
                .typeUser(TypeUser.SERVICE)
                .name("name user")
                .id(1L)
                .car(car)
                .build();

        when(roleRepository.findAllById(any())).thenReturn(List.of(role));
        when(userRepository.save(any())).thenReturn(user);

        when(bCryptPasswordEncoder.encode(any())).thenReturn("1231231231231");

        when(userFactory.createUserByUserDTO(any())).thenCallRealMethod();
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();

        when(carFactory.createCarDTOByUser(any())).thenCallRealMethod();

        when(accessCardFactory.createAccessCardByInputUser(any(), any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUserDTO(any())).thenCallRealMethod();

        userService.saveNewUser(inputUser);

        verify(userRepository, times(1)).save(any());
    }

    private InputUserNewDTO createMockInputUserNewDTO(TypeUser service) {
        var inputUser = new InputUserNewDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setType(TypeUserDTO.valueOf(service.name()));
        inputUser.setName("use name");
        inputUser.setUsername("username");
        inputUser.setPassword("1232312");
        inputUser.setCarModel("Gol");
        inputUser.setCarPlate("abc1234");
        inputUser.setAuthorities(service.getAllowedProfiles());
        return inputUser;
    }

    @Test
    void shouldReturnErrorWheUserNotExist() {
        when(userRepository.findById(eq(ID_USER))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(ID_USER));
    }

    @Test
    void shouldReturnPageUsersDTO() {
        var userList = List.of(
                createMockUser(),
                createMockUser(),
                createMockUser(),
                createMockUser(),
                createMockUser(),
                createMockUser(),
                createMockUser(),
                createMockUser()
        );
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(userList));

        Page<UserDTO> users = userService.findAllPageableUsers(Pageable.unpaged());

        assertEquals(userList.size(), users.getSize());
    }

    private User createMockUser() {
        return User.builder()
                .id(ID_USER)
                .build();
    }

    private UserDTO createMockUserDTO() {
        return UserDTO.builder()
                .typeUser(TypeUserDTO.SERVICE)
                .id(ID_USER)
                .build();
    }

}
