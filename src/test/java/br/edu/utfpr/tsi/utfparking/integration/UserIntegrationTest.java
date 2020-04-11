package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.key;

public class UserIntegrationTest extends IntegrationTest {
    private static final String URI_USERS = "/api/v1/users";
    private static final String NAME_USER = "Fulano de Tal";
    private static final String USERNAME = "fulano_username";
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
        InputUserNewDTO inputUser = createMockInputUserDTO();

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

    @Test
    void shouldCreateNewUser() {
        var mockInputUserDTO = createMockInputUserDTO();
        var fields = new ConstrainedFields(InputUserNewDTO.class);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/create/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("self").description("Uri do recurso criado. ")
                        ),
                        requestFields(
                                fields.withPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("Id do usuário, utilizado apenas para editar")
                                        .attributes(key("Optional").value("true"))
                                        .optional(),
                                fields.withPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("Nome que será cadastrado."),
                                fields.withPath("username")
                                        .type(JsonFieldType.STRING)
                                        .description("Username para autenticação."),
                                fields.withPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("Senha."),
                                fields.withPath("authorities")
                                        .type(JsonFieldType.ARRAY)
                                        .description("Perfil de cadastrado do usuário."),
                                fields.withPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("Tipo do usuário, Aluno (STUDENT) ou Servidor (SERVICE), aceita apenas estes valores."),
                                fields.withPath("accountNonExpired")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("Expira ou não a conta"),
                                fields.withPath("accountNonLocked")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("Bloquear ou não a conta"),
                                fields.withPath("credentialsNonExpired")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("Expira as credenciais ou não a conta"),
                                fields.withPath("enabled")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("Ativa a conta ou não a conta"),
                                fields.withPath("carPlate")
                                        .type(JsonFieldType.STRING)
                                        .attributes(key("Optional").value("true"))
                                        .description("Placa do carro que será cadastradas para o acesso do usuário"),
                                fields.withPath("carModel")
                                        .type(JsonFieldType.STRING)
                                        .attributes(key("Optional").value("true"))
                                        .description("Modelo do carro que será cadastradas para o acesso do usuário")
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("Id do usuário."),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("Nome."),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("Tipo do usuário, Aluno (STUDENT) ou Servidor (SERVICE)."),
                                fieldWithPath("roles").type(JsonFieldType.ARRAY).description("Os perfils de autorização do usuário. <<roles>>"),
                                fieldWithPath("car").type(JsonFieldType.OBJECT).description("Carro autorizado para entrada do usuário. <<cars>>."),
                                fieldWithPath("roles[].roleId").ignored(),
                                fieldWithPath("roles[].name").ignored(),
                                fieldWithPath("roles[].description").ignored(),
                                fieldWithPath("car.carId").ignored(),
                                fieldWithPath("car.plate").ignored(),
                                fieldWithPath("car.model").ignored(),
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("Uris relacionadas ao recurso."),
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("Uri recurso.")
                        )
                ))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUserDTO)
                .when()
                .post(URI_USERS)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldReturnErrorOfValidation() {
        var mockInputUserDTO = createMockInputUserDTO();

        mockInputUserDTO.setName(null);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/create/error/validation", getRequestPreprocessor(), getResponsePreprocessor(),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).description("Definição do error."),
                                fieldWithPath("error.fieldErrors[].message").ignored(),
                                fieldWithPath("error.fieldErrors[].field").ignored(),
                                fieldWithPath("error.fieldErrors[].code").ignored(),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error."))
                        )
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUserDTO)
                .when()
                .post(URI_USERS)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnErrorWhenUsernameExist() {
        var mockInputUserDTO = createMockInputUserDTO();
        mockInputUserDTO.setUsername(FULANO_ADMIN);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/create/error/username", getRequestPreprocessor(), getResponsePreprocessor(),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error."))
                        )
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUserDTO)
                .when()
                .post(URI_USERS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnErrorWhenIdExist() {
        var mockInputUserDTO = createMockInputUserDTO();
        mockInputUserDTO.setId(10L);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/create/error/idExist", getRequestPreprocessor(), getResponsePreprocessor(),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error."))
                        )
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUserDTO)
                .when()
                .post(URI_USERS)
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    private InputUserNewDTO createMockInputUserDTO() {
        var inputUser = new InputUserNewDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setType(TypeUserDTO.SERVICE);
        inputUser.setName(NAME_USER);
        inputUser.setUsername(USERNAME);
        inputUser.setPassword(PASSWORD);
        inputUser.setCarModel("Gol");
        inputUser.setCarPlate("abc1234");
        inputUser.setAuthorities(List.of(1L, 2L));

        return inputUser;
    }



}
