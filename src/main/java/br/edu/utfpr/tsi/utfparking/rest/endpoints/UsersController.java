package br.edu.utfpr.tsi.utfparking.rest.endpoints;

import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsAdmin;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsEqualsUser;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsOperatorOrAdmin;
import br.edu.utfpr.tsi.utfparking.rest.erros.exceptions.IlegalRequestBodyException;
import br.edu.utfpr.tsi.utfparking.rest.factories.UserRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.rest.representations.UserRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersController {

    private final UserRepresentationFactory userRepresentationFactory;

    private final PagedResourcesAssembler<UserDTO> pagedResourcesAssembler;

    private final UserApplicationService userApplicationService;

    @GetMapping(value = "/me")
    public ResponseEntity<UserRepresentation> getUserByAccessCard() {
        return unwrap(userApplicationService.getUserRequest());
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

    @IsOperatorOrAdmin
    @GetMapping("/{id}")
    public ResponseEntity<UserRepresentation> findById(@PathVariable("id") Long id) {
        return unwrap(userApplicationService.findUserById(id));
    }

    @IsAdmin
    @PostMapping
    public ResponseEntity<UserRepresentation> createNew(@Valid @RequestBody InputUserDTO inputUserDTO, BindingResult resultSet) {
        if (resultSet.hasErrors()) {
            throw new IlegalRequestBodyException("New User", resultSet);
        }

        var userDTO = userApplicationService.saveNewUser(inputUserDTO);
        var userRepresentation = userRepresentationFactory.toModel(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRepresentation);
    }

    @IsAdmin
    @PutMapping("/{id}")
    public ResponseEntity<UserRepresentation> editUser(@PathVariable("id") Long id, @Valid @RequestBody InputUserDTO inputUserDTO, BindingResult resultSet) {
        if (resultSet.hasErrors()) {
            throw new IlegalRequestBodyException("Edit User", resultSet);
        }

        var userDTO = userApplicationService.updateUser(inputUserDTO, id);
        return unwrap(userDTO);
    }

    @IsEqualsUser
    @PatchMapping("/{id}/update-car")
    public ResponseEntity<UserRepresentation> changePlate(@PathVariable("id") Long id, @Valid @RequestBody InputUpdateCarDTO inputUpdateCarDTO, BindingResult resultSet) {
        if (resultSet.hasErrors()) {
            throw new IlegalRequestBodyException("Update Car", resultSet);
        }

        var userDto = userApplicationService.updateCar(inputUpdateCarDTO, id);

        return unwrap(userDto);
    }

    private ResponseEntity<UserRepresentation> unwrap(UserDTO user) {
        var userRepresentation = userRepresentationFactory.toModel(user);
        return ResponseEntity.ok(userRepresentation);
    }
}
