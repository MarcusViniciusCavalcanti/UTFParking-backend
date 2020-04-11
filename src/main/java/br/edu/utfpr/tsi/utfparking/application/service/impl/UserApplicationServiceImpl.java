package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessDeleteException;
import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessNewUserException;
import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
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
    public UserDTO saveNewUser(InputUserNewDTO inputUser) {
        if (inputUser.getId() != null) {
            throw new IlegalProcessNewUserException("Not process create new user because id is not null, please use another endpoind.");
        }

        return userService.saveNewUser(inputUser);
    }

    @Override
    public void deleteById(Long id) {
        if (getUserRequest().getId().equals(id)) {
            throw new IlegalProcessDeleteException("It is not possible to delete the user from the same session.");
        }

        userService.delete(id);
    }

    @Override
    public UserDTO findUserById(Long id) {
        return userService.findById(id);
    }
}
