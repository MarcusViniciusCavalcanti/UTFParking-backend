package br.edu.utfpr.tsi.utfparking.rest.endpoints;

import br.edu.utfpr.tsi.utfparking.application.service.ApplicationConfigurationService;
import br.edu.utfpr.tsi.utfparking.rest.annotations.IsAdmin;
import br.edu.utfpr.tsi.utfparking.rest.erros.exceptions.IlegalRequestBodyException;
import br.edu.utfpr.tsi.utfparking.rest.factories.ApplicationConfigRepresentationFactory;
import br.edu.utfpr.tsi.utfparking.rest.representations.ApplicationConfigurationRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/configurations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigController {

    private final ApplicationConfigurationService applicationConfigurationService;

    private final ApplicationConfigRepresentationFactory applicationConfigRepresentationFactory;

    @IsAdmin
    @GetMapping
    public ResponseEntity<ApplicationConfigurationRepresentation> loadConfig() {
        var config = applicationConfigurationService.loadConfig();
        var representation = applicationConfigRepresentationFactory.toModel(config);
        return ResponseEntity.ok(representation);
    }

    @IsAdmin
    @PostMapping
    public ResponseEntity<ApplicationConfigurationRepresentation> save(@Valid @RequestBody InputApplicationConfiguration input, BindingResult resultSet) {
        if (resultSet.hasErrors()) {
            throw new IlegalRequestBodyException("Change configuration", resultSet);
        }

        var config = applicationConfigurationService.save(input);
        var representation = applicationConfigRepresentationFactory.toModel(config);
        return ResponseEntity.ok(representation);
    }
}
