package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class UserIntegrationTest extends IntegrationTest {
    private static final String URI_USERS = "/api/v1/users";
    private static final String NAME_USER = "Name";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "12345678";

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);
    }

    @AfterEach
    void teardown() {
        super.tearDown();
    }

    @Test
    void shouldDeleteUser() {
        var inputUser = new InputUserDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setName(NAME_USER);
        inputUser.setUsername(USERNAME);
        inputUser.setPassword(PASSWORD);
        inputUser.setAuthorities(List.of(1L, 2L));

        var userDTO = userService.saveNewUser(inputUser);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/delete/success", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(URI_USERS + "/" + userDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnErrorWhenDeleteYourSelf() {
        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/delete/error", getRequestPreprocessor(), getResponsePreprocessor(),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error."))
                ))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(URI_USERS + "/" + currentUserDTO.getId())
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

    }

}
