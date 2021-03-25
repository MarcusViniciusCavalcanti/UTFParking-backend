package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.service.RoleService;
import br.edu.utfpr.tsi.utfparking.domain.security.service.GetterRoles;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RolesServiceImpl implements RoleService {

    private final GetterRoles getterRoles;

    @Override
    public List<RoleDTO> getAll() {
        return getterRoles.getAllRoles();
    }

}
