package br.edu.utfpr.tsi.utfparking.rest.endpoints;

import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsAdmin;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsEqualsUser;
import br.edu.utfpr.tsi.utfparking.rest.factories.UserRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.rest.representations.UserRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersController {

    private final UserRepresentationFactory userRepresentationFactory;

    private final PagedResourcesAssembler<UserDTO> pagedResourcesAssembler;

    private final UserApplicationService userApplicationService;

    @IsEqualsUser
    @GetMapping(value = "/by-access-card")
    public ResponseEntity<UserRepresentation> getUserByAccessCard() {
        var user = userApplicationService.getUserRequest();
        var factory = new UserRepresentationFactory();
        var userResource = factory.toModel(user);

        return ResponseEntity.ok(userResource);
    }

    @IsAdmin
    @GetMapping
    public ResponseEntity<PagedModel<UserRepresentation>> findAll(Pageable pageable) {
        var page = userApplicationService.findAllPageableUsers(pageable);
        var userRepresentations = pagedResourcesAssembler.toModel(page, userRepresentationFactory);

        return ResponseEntity.ok(userRepresentations);
    }

    @IsAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        userApplicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
