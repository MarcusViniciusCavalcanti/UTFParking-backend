package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetterAllTypeUserService {

    public List<TypeUserDTO> get() {
        return Stream.of(TypeUser.values())
            .map(typeUser -> TypeUserDTO.valueOf(typeUser.name()))
            .collect(Collectors.toList());
    }
}
