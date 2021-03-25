package br.edu.utfpr.tsi.utfparking.rest.endpoints;

import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsAdmin;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsEqualsUser;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsOperatorOrAdmin;
import br.edu.utfpr.tsi.utfparking.rest.erros.exceptions.IlegalRequestBodyException;
import br.edu.utfpr.tsi.utfparking.rest.factories.TypeUserRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.rest.factories.UserRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.rest.representations.TypeUserRepresentation;
import br.edu.utfpr.tsi.utfparking.rest.representations.UserRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.ParamsSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersController {

    private final UserRepresentationFactory userRepresentationFactory;

    private final PagedResourcesAssembler<UserDTO> pagedResourcesAssembler;

    private final UserApplicationService userApplicationService;

    private final TypeUserRepresentationFactory typeUserRepresentationFactory;

    @GetMapping(value = "/me")
    public ResponseEntity<UserRepresentation> getUserByAccessCard() {
        return unwrap(userApplicationService.getUserRequest());
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<Resource> upload(@RequestParam("files") MultipartFile file, @PathVariable("id") Long id) {
        var avatar =  userApplicationService.uploadAvatar(file, id);
        return getResponseEntityToFile(avatar);
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<Resource> downloadAvatar(@PathVariable("id") Long id) {
        var avatar = userApplicationService.getAvatar(id);
        return getResponseEntityToFile(avatar);
    }

    @IsAdmin
    @GetMapping
    public ResponseEntity<Page<UserRepresentation>> findAll(ParamsSearchRequestDTO params) {
        var page = userApplicationService.findAllPageableUsers(params);

        var listUserPresentation = page.getContent().stream()
            .map(userRepresentationFactory::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(listUserPresentation, page.getPageable(), page.getTotalElements()));
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
    public ResponseEntity<UserRepresentation> changeCar(@PathVariable("id") Long id, @Valid @RequestBody InputUpdateCarDTO inputUpdateCarDTO, BindingResult resultSet) {
        if (resultSet.hasErrors()) {
            throw new IlegalRequestBodyException("Update Car", resultSet);
        }

        var userDto = userApplicationService.updateCar(inputUpdateCarDTO, id);
        return unwrap(userDto);
    }

    @IsAdmin
    @GetMapping("/types")
    public ResponseEntity<List<TypeUserRepresentation>> getAllTypeUser() {
        List<TypeUserRepresentation> types = userApplicationService.getAllTypeUser().stream()
            .map(typeUserRepresentationFactory::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(types);
    }

    private ResponseEntity<UserRepresentation> unwrap(UserDTO user) {
        var userRepresentation = userRepresentationFactory.toModel(user);
        return ResponseEntity.ok(userRepresentation);
    }

    private static ResponseEntity<Resource> getResponseEntityToFile(Resource avatar) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + avatar.getFilename() + "\"")
                .body(avatar);
    }
}
