package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.LoginDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured3.RestAssuredOperationPreprocessorsConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("h2test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    private static final String FULANO_ADMIN = "fulano_admin";
    private static final String FULANO_USER = "fulano_user";

    private static final String PASSWORD = "1234567";
    private static final String FULANO = "fulano";

    private static final String URI_USERS = "/api/v1/users";
    private static final String URI_LOGIN = "/api/v1/login";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessCardRepository accessCardRepository;

    @Autowired
    private JwtConfiguration jwtConfiguration;

    private RestAssuredOperationPreprocessorsConfigurer configurer;

    @LocalServerPort
    private int port;

    private RequestSpecification specNotAuthentication;
    private RequestSpecification specAuthentication;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.configurer = documentationConfiguration(restDocumentation).operationPreprocessors();
    }

    @AfterEach
    public void deleteAll() {
        userRepository.deleteAll();
        accessCardRepository.deleteAll();
    }

    @Test
    void shouldReturnTokenWhenLoginSuccess() throws Exception {
        var loginDTO = new LoginDTO();
        loginDTO.setUsername(FULANO_ADMIN);
        loginDTO.setPassword(PASSWORD);

        createMockUserAdmin();
        setNotAuthentication();

        given(specNotAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("authenticate/Login/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                prettyPrint(),
                                removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
                        ),
                        responseFields(
                        fieldWithPath("value").type(JsonFieldType.STRING).description("Token gerado."),
                        fieldWithPath("expiration").type(JsonFieldType.NUMBER).description("Timestamp para expiração do Token gerado.")
                        ))
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginDTO)
                .when()
                .post(URI_LOGIN)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnTokenWhenUnauthorized() throws Exception {
        setNotAuthentication();

        given(specNotAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("authenticate/Login/failure/unauthorized",
                        preprocessResponse(
                                prettyPrint(),
                                removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
                        ),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error."))
                        )
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_USERS)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnTokenWhenForbidden() throws Exception {
        var loginDTO = new LoginDTO();
        loginDTO.setUsername(FULANO_USER);
        loginDTO.setPassword(PASSWORD);

        createMockUser();
        setAuthentication(loginDTO);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("authenticate/Login/failure/forbidden",
                        preprocessResponse(
                                prettyPrint(),
                                removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
                        ),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Titulo do error."),
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Código de status HTTP."),
                                fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Timestamp do error."),
                                fieldWithPath("error").type(JsonFieldType.STRING).description("Definição do error."),
                                fieldWithPath("path").type(JsonFieldType.STRING).description("Uri que gerou o error."))
                        )
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_USERS)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private void setNotAuthentication() {
        specNotAuthentication = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(this.configurer)
                .build();
    }

    private void setAuthentication(LoginDTO loginDTO) {
        var authorization = given()
                .basePath(URI_LOGIN)
                .port(port)
                .contentType("application/json")
                .body(loginDTO)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .header(jwtConfiguration.getHeader().getName());

        specAuthentication = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(this.configurer)
                .addHeader(jwtConfiguration.getHeader().getName(), authorization)
                .build();
    }

    private UserDTO createMockUser() {
        return userService.saveNewUser(createNewUser(List.of(1L), FULANO_USER));
    }

    private UserDTO createMockUserAdmin() {
        return userService.saveNewUser(createNewUser(List.of(1L, 2L), FULANO_ADMIN));
    }

    private InputUserDTO createNewUser(List<Long> roles, String username) {
        return CreateMock.createMockInputUserDTO(FULANO, username, PASSWORD, TypeUserDTO.SERVICE, roles);
    }
}
