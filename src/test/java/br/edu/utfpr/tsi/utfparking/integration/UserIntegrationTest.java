package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.structure.disk.properties.DiskProperties;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.CarRepository;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.key;

public class UserIntegrationTest extends IntegrationTest {

    private static final String URI_USERS = "/api/v1/users";
    private static final String NAME_USER = "Fulano de Tal";
    private static final String USERNAME = "fulano_username";
    private static final String PASSWORD = "12345678";

    @Autowired
    private DiskProperties diskProperties;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);
    }

    @AfterEach
    void teardown() {
        super.tearDown();
    }

    @Test
    void shouldCreateNewUser() {
        var mockInputUserDTO = createMockInputUserDTO();
        var fields = new ConstrainedFields(InputUserDTO.class);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/create/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("self").description("Uri do recurso criado."),
                                linkWithRel("avatar").description("Uri do avatar."),
                                linkWithRel("updateCar").description("Url para alterar os dados do carro do usuário."),
                                linkWithRel("me").description("Uri do recurso criado.")
                        ),
                        requestFields(
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
                                fieldWithPath("accountNonExpired").type(JsonFieldType.BOOLEAN).description("Se a conta esta expirada ou não."),
                                fieldWithPath("accountNonLocked").type(JsonFieldType.BOOLEAN).description("Se a conta esta bloqueadaa ou não."),
                                fieldWithPath("credentialsNonExpired").type(JsonFieldType.BOOLEAN).description("Se as credenciais expirada ou não."),
                                fieldWithPath("enabled").type(JsonFieldType.BOOLEAN).description("Se a conta esta ativa ou não."),
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
                                fieldWithPath("_links.self.href").ignored(),
                                fieldWithPath("_links.avatar.href").ignored(),
                                fieldWithPath("_links.updateCar.href").ignored(),
                                fieldWithPath("_links.me.href").ignored()
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
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
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
    void shouldGetUser() {
        var mockInputUserDTO = createMockInputUserDTO();
        var userDTO = userService.saveNewUser(mockInputUserDTO);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/get/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("self").description("Uri do recurso criado."),
                                linkWithRel("avatar").description("Uri do avatar."),
                                linkWithRel("updateCar").description("Url para alterar os dados do carro do usuário."),
                                linkWithRel("me").description("Uri do recurso criado.")
                        ),
                        pathParameters(
                                parameterWithName("id").description("id do recurso que será recuperado")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_USERS + "/{id}", userDTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("userId", is(userDTO.getId().intValue()))
                .body("name", is(userDTO.getName()))
                .body("car.plate", is(userDTO.getCar().getPlate()))
                .body("car.model", is(userDTO.getCar().getModel()))
                .body("roles", hasSize(userDTO.getAccessCard().getRoles().size()));
    }

    @Test
    void shouldGetCurrentUser() {
        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/me/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("self").description("Uri do recurso criado."),
                                linkWithRel("avatar").description("Uri do avatar."),
                                linkWithRel("updateCar").description("Uri para alterar os dados do carro."),
                                linkWithRel("me").description("Uri do recurso criado.")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_USERS + "/me")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("userId", is(currentUserDTO.getId().intValue()))
                .body("name", is(currentUserDTO.getName()))
                .body("car.plate", is(currentUserDTO.getCar().getPlate()))
                .body("car.model", is(currentUserDTO.getCar().getModel()))
                .body("roles", hasSize(currentUserDTO.getAccessCard().getRoles().size()));
    }

    @Test
    void shouldGetUserWhitErrorUserNotFound() {
        var userIdNotFound = 1000;
        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/get/error", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("id").description("id do recurso que será recuperado")
                        ),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error.")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_USERS + "/{id}", userIdNotFound)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnPageUser() {
        CreateMock.createListInputUsersDTO().forEach(userService::saveNewUser);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/get/page", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("first").description("Uri da primeira página."),
                                linkWithRel("self").description("Uri da página corrente."),
                                linkWithRel("next").description("Uri da próxima página."),
                                linkWithRel("last").description("Uri da última página.")
                        ),
                        requestParameters(
                                parameterWithName("page").description("Número da página de início"),
                                parameterWithName("size").description("Quantidade de recursos a ser exibidos por página"),
                                parameterWithName("sort").description("Referencia do atributo de ordenação.")
                        ),
                        responseFields(
                                beneathPath("page"),
                                fieldWithPath("size").description("Quantidade de recursos retornados na página"),
                                fieldWithPath("totalElements").description("Total de recursos encontrados"),
                                fieldWithPath("totalPages").description("Total de páginas"),
                                fieldWithPath("number").description("Número da página corrente")
                        ),
                        responseBody(
                                beneathPath("_embedded")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParams(
                        "page", 0,
                        "size", 2,
                        "sort", "name,DESC"
                )
                .get(URI_USERS)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldUpdateUser() {
        var mockInputUserDTO = createMockInputUserDTO();
        var fields = new ConstrainedFields(InputUserDTO.class);
        var userDTO = userService.saveNewUser(mockInputUserDTO);

        var anotherName = "Another Name";
        var anotherCarModel = "Another car model";
        var anotherPlate = "abc3454";

        mockInputUserDTO.setName(anotherName);
        mockInputUserDTO.setCarPlate(anotherPlate);
        mockInputUserDTO.setCarModel(anotherCarModel);
        mockInputUserDTO.setType(TypeUserDTO.STUDENTS);
        mockInputUserDTO.setAuthorities(TypeUser.STUDENTS.getAllowedProfiles());

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/update/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("id").description("id do recurso que será atualizado")
                        ),
                        requestFields(
                                fields.withPath("name").description("Nome"),
                                fields.withPath("username").ignored(),
                                fields.withPath("password").ignored(),
                                fields.withPath("authorities").description("Perfils"),
                                fields.withPath("type").description("Tipo do usuário"),
                                fields.withPath("accountNonExpired").description("expira a conta"),
                                fields.withPath("accountNonLocked").description("bloqueia a conta"),
                                fields.withPath("credentialsNonExpired").description("expira as credencias"),
                                fields.withPath("enabled").description("ativa ou desativa a conta"),
                                fields.withPath("carPlate").description("placa do carro"),
                                fields.withPath("carModel").description("modelo do carro")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUserDTO)
                .when()
                .put(URI_USERS + "/{id}", userDTO.getId())
                .then()
                .assertThat()
                    .statusCode(HttpStatus.OK.value())
                    .body("userId", is(userDTO.getId().intValue()))
                    .body("name", is(anotherName))
                    .body("car.plate", is(anotherPlate))
                    .body("car.model", is(anotherCarModel))
                    .body("roles", hasSize(TypeUser.STUDENTS.getAllowedProfiles().size()))
                    .body("roles[0].name", is("ROLE_DRIVER"));
    }

    @Test
    void shouldReturnErrorOfValidationInUpdate() {
        var mockInputUserDTO = createMockInputUserDTO();
        var userDTO = userService.saveNewUser(mockInputUserDTO);

        mockInputUserDTO.setName(null);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/update/error/validation", getRequestPreprocessor(), getResponsePreprocessor(),
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
                .put(URI_USERS + "/{id}", userDTO.getId())
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void shouldDeleteUser() {
        InputUserDTO inputUser = createMockInputUserDTO();

        var userDTO = userService.saveNewUser(inputUser);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/delete/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("id").description("id do recurso que será recuperado")
                        )))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(URI_USERS + "/{id}", userDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnErrorDeleteUserNotFound() {
        var userIdNotFound = 1000000;
        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/delete/error", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(URI_USERS + "/" + userIdNotFound)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
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
    void shouldReturnErrorWhenUpdateCarWithIdIsDiffSessionRequest() {
        var mockInputUpdateCarDTO = CreateMock.createMockInputUpdateCarDTO();

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/update/car/forbidden", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("id").description("id do recurso que será alterado")
                        ),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error.")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUpdateCarDTO)
                .when()
                .patch(URI_USERS + "/{id}/update-car", 10000)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnErrorOfValidationWhenUpdateCar() {
        var mockInputUpdateCarDTO = CreateMock.createMockInputUpdateCarDTO();

        mockInputUpdateCarDTO.setCarModel(null);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/car/error/validation", getRequestPreprocessor(), getResponsePreprocessor(),
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
                .body(mockInputUpdateCarDTO)
                .when()
                .patch(URI_USERS + "/{id}/update-car", currentUserDTO.getId())
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Order(1)
    @Test
    void shouldHaveUploadAvatar() throws URISyntaxException, IOException {
        var avatarDirectory = Path.of(diskProperties.getPath());

        if(!Files.exists(avatarDirectory)) {
            Files.createDirectories(avatarDirectory);
        }

        var resource = this.getClass().getResource("/avatar/1.png");
        var file = new File(resource.toURI());

        given(specAuthentication)
                .filter(document("user/upload", getRequestPreprocessor(), getResponsePreprocessor()))
                .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("files", file)
                .post(URI_USERS + "/{id}/avatar", currentUserDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Order(2)
    @Test
    void shouldHaveDownloadAvatar() {
        given(specAuthentication)
                .filter(document("user/download", getRequestPreprocessor(), getResponsePreprocessor()))
                .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
                .get(URI_USERS + "/{id}/avatar", currentUserDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnSuccessWhenUpdateCar() {
        var mockInputUpdateCarDTO = CreateMock.createMockInputUpdateCarDTO();
        var car = carRepository.findById(currentUserDTO.getCar().getId()).orElseThrow();
        car.setUpdatedAt(LocalDate.now().minusDays(10L));
        carRepository.saveAndFlush(car);

        mockInputUpdateCarDTO.setCarPlate("efg0987");
        mockInputUpdateCarDTO.setCarModel("other model");

        var fields = new ConstrainedFields(InputUpdateCarDTO.class);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/update/car/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("id").description("id do recurso que será alterado")
                        ),
                        requestFields(
                             fields.withPath("carModel").type(JsonFieldType.STRING).description("Valor de alteração do modelo do carro"),
                             fields.withPath("carPlate").type(JsonFieldType.STRING).description("Valor de alteração do placa do carro")
                        ))
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUpdateCarDTO)
                .when()
                .patch(URI_USERS + "/{id}/update-car", currentUserDTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("car.plate", is(mockInputUpdateCarDTO.getCarPlate()))
                .body("car.model", is(mockInputUpdateCarDTO.getCarModel()));
    }

    @Test
    void shouldReturnErrorWhenUpdateCarOnLimit() {
        var mockInputUpdateCarDTO = CreateMock.createMockInputUpdateCarDTO();

        mockInputUpdateCarDTO.setCarPlate("efg0987");
        mockInputUpdateCarDTO.setCarModel("other model");

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("user/update/car/error", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mockInputUpdateCarDTO)
                .when()
                .patch(URI_USERS + "/{id}/update-car", currentUserDTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    private InputUserDTO createMockInputUserDTO() {
        return CreateMock.createMockInputUserDTO(NAME_USER, USERNAME, PASSWORD, TypeUserDTO.SERVICE, TypeUser.SERVICE.getAllowedProfiles(), "Another", "adf12345");
    }
}
