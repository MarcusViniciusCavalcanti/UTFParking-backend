package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserService userService;

    @Override
    public UserDTO getUserRequest() {
        return userService.getUserRequest();
    }

    @Override
    public Page<UserDTO> findAllPageableUsers(Pageable pageable) {
        return userService.findAllPageableUsers(pageable);
    }

    @Override
    public UserDTO saveNewUser(InputUserDTO inputUser) {
        return userService.saveNewUser(inputUser);
    }
}
