package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.LoginDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.restassured3.RestAssuredOperationPreprocessorsConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    private static final String FULANO_ADMIN = "fulano_admin";
    private static final String PASSWORD = "1234567";
    private static final String FULANO = "fulano";
    private static final String URI_LOGIN = "/api/v1/login";

    @Autowired
    protected UserService userService;

    @Autowired
    private AccessCardRepository accessCardRepository;

    @Autowired
    private JwtConfiguration jwtConfiguration;

    private RestAssuredOperationPreprocessorsConfigurer configurer;

    @LocalServerPort
    private int port;

    protected RequestSpecification specAuthentication;

    protected ObjectMapper mapper = new ObjectMapper();

    protected UserDTO currentUserDTO;

    private String token;


    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.configurer = documentationConfiguration(restDocumentation).operationPreprocessors();
        var loginDTO = new LoginDTO();
        loginDTO.setUsername(FULANO_ADMIN);
        loginDTO.setPassword(PASSWORD);

        createMockUser();
        setAuthentication(loginDTO);
    }

    public void tearDown() {
        accessCardRepository.deleteAll();
    }

    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    protected <T> List<T> mapListFromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    protected OperationResponsePreprocessor getResponsePreprocessor() {
        return preprocessResponse(
                prettyPrint(),
                removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
        );
    }

    protected OperationRequestPreprocessor getRequestPreprocessor() {
        return preprocessRequest(prettyPrint(), removeHeaders("Host", "Authorization"));
    }

    private void setAuthentication(LoginDTO loginDTO) {
        token = given()
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
                .addHeader(jwtConfiguration.getHeader().getName(), token)
                .build();
    }

    private void createMockUser() {
        var inputUser = new InputUserDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setName(FULANO);
        inputUser.setUsername(FULANO_ADMIN);
        inputUser.setPassword(PASSWORD);
        inputUser.setAuthorities(List.of(1L, 2L));

        currentUserDTO = userService.saveNewUser(inputUser);
    }
}
