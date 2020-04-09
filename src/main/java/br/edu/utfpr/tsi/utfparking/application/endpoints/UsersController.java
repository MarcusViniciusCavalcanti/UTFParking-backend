package br.edu.utfpr.tsi.utfparking.application.endpoints;

import br.edu.utfpr.tsi.utfparking.application.factories.UserRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.application.representations.UserRepresentation;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersController {

    private final UserService userService;

    @GetMapping(value = "/by-access-card", produces = "application/hal+json" )
    public ResponseEntity<UserRepresentation> getUserByAccessCard() {
        var user = userService.getUserRequest();
        var factory = new UserRepresentationFactory();
        var userResource = factory.toModel(user);

        return ResponseEntity.ok(userResource);
    }
}
