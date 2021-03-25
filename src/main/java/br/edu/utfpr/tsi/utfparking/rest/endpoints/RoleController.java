package br.edu.utfpr.tsi.utfparking.rest.endpoints;

import br.edu.utfpr.tsi.utfparking.application.service.RoleService;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsAdmin;
import br.edu.utfpr.tsi.utfparking.rest.factories.RoleRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.rest.representations.RoleRepresentation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleController {

    private final RoleService roleService;
    private final RoleRepresentationFactory roleRepresentationFactory;

    @IsAdmin
    @GetMapping
    public ResponseEntity<List<RoleRepresentation>> getAll() {
        final List<RoleRepresentation> roleRepresentations = roleService.getAll().stream()
            .map(roleRepresentationFactory::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(roleRepresentations);
    }
}
