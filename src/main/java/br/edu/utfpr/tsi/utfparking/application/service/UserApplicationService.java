package br.edu.utfpr.tsi.utfparking.application.service;

import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserApplicationService {
    UserDTO getUserRequest();

    Page<UserDTO> findAllPageableUsers(Pageable pageable);

    UserDTO saveNewUser(InputUserNewDTO inputUser);

    void deleteById(Long id);

    UserDTO findUserById(Long id);
}
