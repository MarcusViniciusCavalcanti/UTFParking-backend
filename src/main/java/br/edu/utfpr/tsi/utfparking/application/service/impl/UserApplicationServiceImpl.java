package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessDeleteException;
import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.domain.users.avatar.service.FileService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.GetterAllTypeUserService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.GetterAllUserService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.ParamsSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserService userService;

    private final FileService fileService;

    private final GetterAllTypeUserService getterAllTypeUserService;

    private final GetterAllUserService getterAllUserService;

    @Override
    public UserDTO getUserRequest() {
        return userService.getUserRequest();
    }

    @Override
    public Page<UserDTO> findAllPageableUsers(ParamsSearchRequestDTO paramsSearchRequestDTO) {
        return getterAllUserService.findAllByFilter(paramsSearchRequestDTO);
    }

    @Override
    public UserDTO saveNewUser(InputUserDTO inputUser) {
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

    @Override
    public UserDTO updateUser(InputUserDTO inputUser, Long id) {
        return userService.editUser(inputUser, id);
    }

    @Override
    public UserDTO updateCar(InputUpdateCarDTO inputUpdateCarDTO, Long id) {
        return userService.updateCar(inputUpdateCarDTO, id);
    }

    @Override
    public List<TypeUserDTO> getAllTypeUser() {
        return getterAllTypeUserService.get();
    }

    @Override
    public Resource getAvatar(Long id) {
        return fileService.getFile(id);
    }

    @Transactional
    @Override
    public Resource uploadAvatar(MultipartFile file, Long id) {
        return fileService.saveDisk(file, id);
    }
}
