package br.edu.utfpr.tsi.utfparking.rest.endpoints;

import br.edu.utfpr.tsi.utfparking.application.service.RecognizerPlateService;
import br.edu.utfpr.tsi.utfparking.rest.erros.exceptions.IlegalRequestBodyException;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/recognizers")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecognizerController {

    private final RecognizerPlateService recognizerPlateService;

    @PostMapping("/send/plate")
    public ResponseEntity<String> identifierPlate(@Valid @RequestBody InputPlateRecognizerDTO dto, BindingResult resultSet) {
        if (resultSet.hasErrors()) {
            throw new IlegalRequestBodyException("Change configuration", resultSet);
        }

        recognizerPlateService.saveNewRecognizer(dto);
        return ResponseEntity.accepted().build();
    }
}
