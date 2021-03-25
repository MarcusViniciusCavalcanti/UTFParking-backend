package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.spec.ParametersGetAllSpec;
import br.edu.utfpr.tsi.utfparking.domain.users.spec.UserSpecifications;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.ParamsSearchRequestDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.specs.BasicSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetterAllUserService {
    private final UserRepository userRepository;
    private final CreatorUserFacade creatorUserFacade;

    @Qualifier(UserSpecifications.QUALIFIER_GET_ALL_SPEC)
    private final BasicSpecification<User, ParametersGetAllSpec> getterAllUserSpec;

    public Page<UserDTO> findAllByFilter(ParamsSearchRequestDTO params) {
        var sort = Sort.by(Sort.Direction.fromString(params.getOrDefaultSortDirection()), params.getOrDefaultSort());
        var pageable = PageRequest.of(params.getOrDefaultPage(), params.getOrDefaultSize(), sort);
        var paramsSpec = ParametersGetAllSpec.builder()
            .name(params.getName())
            .enabled(params.getOrDefaultEnabled())
            .profile(params.getProfile())
            .type(params.getType())
            .build();

        var spec = getterAllUserSpec.filterBy(paramsSpec);

        var userPage = userRepository.findAll(spec, pageable);
        var userDTOS = userPage.getContent().stream()
            .map(creatorUserFacade.createUserDTO())
            .collect(Collectors.toList());

        return new PageImpl<>(userDTOS, pageable, userPage.getTotalElements());
    }
}
