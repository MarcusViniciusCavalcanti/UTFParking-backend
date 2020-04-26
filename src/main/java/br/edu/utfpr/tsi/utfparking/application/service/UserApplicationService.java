package br.edu.utfpr.tsi.utfparking.application.service;

import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public interface UserApplicationService {
    UserDTO getUserRequest();

    Page<UserDTO> findAllPageableUsers(Pageable pageable);

    UserDTO saveNewUser(InputUserDTO inputUser);

    void deleteById(Long id);

    UserDTO findUserById(Long id);

    UserDTO updateUser(InputUserDTO inputUser, Long id);

    UserDTO updateCar(InputUpdateCarDTO inputUpdateCarDTO, Long id);

    Resource getAvatar(Long id);

    Resource uploadAvatar(MultipartFile file, Long id);
}
