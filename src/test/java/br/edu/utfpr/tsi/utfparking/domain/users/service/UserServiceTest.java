package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.exceptions.AuthoritiesNotAllowedException;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.security.factory.AccessCardFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.CarFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.*;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RoleRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
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
import java.util.stream.Collectors;

import static br.edu.utfpr.tsi.utfparking.utils.CreateMock.createMockInputUpdateCarDTO;
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
    void shouldHaveCreateNewUserWhitCar() {
        var inputUser = createMockInputUserNewDTO(TypeUser.SERVICE);
        var role = CreateMock.createRole(1L, "description");
        var accessCard = CreateMock.createAccessCard(1L, List.of(role), "username", "password");
        var car = CreateMock.createCar(1L, "Model Car", "abc1234");
        var user = CreateMock.createUser(1L, accessCard, TypeUser.SERVICE, "name user", car);

        when(roleRepository.findAllById(any())).thenReturn(List.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("1231231231231");
        when(userFactory.createUserByUserDTO(any())).thenCallRealMethod();
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();
        when(carFactory.createCarDTOByUser(any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByInputUser(any(), any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUser(any())).thenCallRealMethod();

        userService.saveNewUser(inputUser);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldHaveCreateNewUserWithoutCar() {
        var inputUser = createMockInputUserNewDTO(TypeUser.SERVICE);
        var role = CreateMock.createRole(1L, "description");
        var accessCard = CreateMock.createAccessCard(1L, List.of(role), "username", "password");
        var user = CreateMock.createUser(1L, accessCard, TypeUser.SERVICE, "name user", null);

        when(roleRepository.findAllById(any())).thenReturn(List.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("1231231231231");
        when(userFactory.createUserByUserDTO(any())).thenCallRealMethod();
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByInputUser(any(), any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUser(any())).thenCallRealMethod();

        userService.saveNewUser(inputUser);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldHaveUpdateUser() {
        var inputUser = createMockInputUserNewDTO(TypeUser.SERVICE);

        inputUser.setName("Another name");
        inputUser.setType(TypeUserDTO.STUDENTS);
        inputUser.setAuthorities(TypeUser.STUDENTS.getAllowedProfiles());

        var role1 = CreateMock.createRole(1L, "description");
        var role2 = CreateMock.createRole(2L, "description");

        var accessCard = CreateMock.createAccessCard(1L, List.of(role1, role2), "username", "password");
        var accessCardAnother = CreateMock.createAccessCard(1L, List.of(role1), "username", "password");

        var car = CreateMock.createCar(1L, "Model Car", "abc1234");

        var user = CreateMock.createUser(1L, accessCard, TypeUser.SERVICE, "name user", car);
        var anotherUser = CreateMock.createUser(1L, accessCardAnother, TypeUser.STUDENTS, "name user", car);

        when(roleRepository.findAllById(any())).thenReturn(List.of(role1));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUser(any())).thenCallRealMethod();
        when(userRepository.save(any())).thenReturn(anotherUser);

        var userDTO = userService.editUser(inputUser, user.getId());
        var roles = anotherUser.getAccessCard().getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        var roleDTOS = userDTO.getAccessCard().getRoles().stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());

        assertEquals(anotherUser.getName(), userDTO.getName());
        assertEquals(anotherUser.getTypeUser().name(), userDTO.getTypeUser().name());
        assertEquals(roles, roleDTOS);
    }

    @Test
    void shouldHaveUpdateUserWhitCar() {
        var inputUser = createMockInputUserNewDTO(TypeUser.SERVICE);

        inputUser.setName("Another name");
        inputUser.setType(TypeUserDTO.STUDENTS);
        inputUser.setAuthorities(TypeUser.STUDENTS.getAllowedProfiles());

        var role1 = CreateMock.createRole(1L, "description");
        var role2 = CreateMock.createRole(2L, "description");

        var accessCard = CreateMock.createAccessCard(1L, List.of(role1, role2), "username", "password");
        var accessCardAnother = CreateMock.createAccessCard(1L, List.of(role1), "username", "password");

        var car = CreateMock.createCar(1L, "Model Car", "abc1234");
        var carAnother = CreateMock.createCar(1L, "Another Model Car", "abc12e4");

        var user = CreateMock.createUser(1L, accessCard, TypeUser.SERVICE, "name user", car);
        var anotherUser = CreateMock.createUser(1L, accessCardAnother, TypeUser.STUDENTS, "name user", carAnother);

        when(roleRepository.findAllById(any())).thenReturn(List.of(role1));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();
        when(carFactory.createCarDTOByUser(any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUser(any())).thenCallRealMethod();
        when(userRepository.save(any())).thenReturn(anotherUser);

        var userDTO = userService.editUser(inputUser, user.getId());
        var carDTO = userDTO.getCar();

        assertEquals(carAnother.getModel(), carDTO.getModel());
        assertEquals(carAnother.getPlate(), carDTO.getPlate());
    }

    private InputUserDTO createMockInputUserNewDTO(TypeUser service) {
        return CreateMock.createMockInputUserDTO("use name", "username", "1232312",  TypeUserDTO.valueOf(service.name()), service.getAllowedProfiles());
    }

    @Test
    void shouldReturnErrorWheUserNotExist() {
        when(userRepository.findById(eq(ID_USER))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(ID_USER));
    }

    @Test
    void shouldReturnPageUsersDTO() {
        var userList = CreateMock.createListUser();

        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(userList));
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();
        when(carFactory.createCarDTOByUser(any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUser(any())).thenCallRealMethod();

        var users = userService.findAllPageableUsers(Pageable.unpaged());

        assertEquals(userList.size(), users.getSize());
    }

    @Test
    void shouldHaveUpdateCar() {
        var mockInputUpdateCarDTO = createMockInputUpdateCarDTO();
        var role = CreateMock.createRole(1L, "description");
        var car = CreateMock.createCar(1L, "Model Car", "abc1234");

        var accessCard = CreateMock.createAccessCard(1L, List.of(role), "username", "password");
        var user = CreateMock.createUser(1L, accessCard, TypeUser.SERVICE, "name user", car);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userFactory.createUserDTOByUser(any())).thenCallRealMethod();
        when(carFactory.createCarDTOByUser(any())).thenCallRealMethod();
        when(accessCardFactory.createAccessCardByUser(any())).thenCallRealMethod();

        var resultDTO = userService.updateCar(mockInputUpdateCarDTO, user.getId());

        assertEquals(mockInputUpdateCarDTO.getCarModel(), resultDTO.getCar().getModel());
        assertEquals(mockInputUpdateCarDTO.getCarPlate(), resultDTO.getCar().getPlate());
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
