package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.factory.AccessCardFactory;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.AccessCardDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RoleRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AccessCardRepository accessCardRepository;

    private final AccessCardFactory accessCardFacade;

    private final UserFactory userFacade;

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
    public UserDTO saveNewUser(InputUserDTO inputUser) {
        var user = getUser(inputUser);
        user.getAccessCard().setPassword(encoder.encode(inputUser.getPassword()));

        var newUser = userRepository.save(user);
        return userFacade.createUserDTOByUser(newUser);
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

    private User getUser(InputUserDTO inputUser) {
        var roles = roleRepository.findAllById(inputUser.getAuthorities());

        var accessCard = accessCardFacade.createAccessCardByInputUser(inputUser, roles);
        var user = userFacade.createUserByUserDTO(inputUser);

        user.setAccessCard(accessCard);
        return user;
    }

    public Page<UserDTO> findAllPageableUsers(Pageable pageable) {
        var userPage = userRepository.findAll(pageable);
        var userDTOS = userPage.stream()
                .map(userFacade::createUserDTOByUser)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOS, pageable, userPage.getTotalElements());
    }
}
