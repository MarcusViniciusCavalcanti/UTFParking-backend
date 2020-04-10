package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.LoginDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    public static final String FULANO_ADMIN = "fulano_admin";
    public static final String PASSWORD = "1234567";
    public static final String FULANO = "fulano";

    @Autowired
    private UserService userService;

    private String baseUrl = "/api/v1/login";

    @Autowired
    private WebApplicationContext context;

    private RequestSpecification spec;

    @LocalServerPort
    private int port;

    protected ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {

        this.spec = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void shouldReturnTokenWhenLoginSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(FULANO_ADMIN);
        loginDTO.setPassword(PASSWORD);

        createMockUser();

        String header = RestAssured.given(spec)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("Login"))
                .body(loginDTO)
                .when()
                .post(baseUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .header("Authorization");

        System.out.println(header);


//        mockMvc.perform(RestDocumentationRequestBuilders.get(baseUrl)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(content))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("value", notNullValue()))
//                .andDo(document("login",
//                        preprocessResponse(prettyPrint()),
//                        requestHeaders(
//                                headerWithName("Access-Control-Expose-Headers").description("XSRF-TOKEN"),
//                                headerWithName("Authorization").description("Token da sessão e.g JWT")
//                        ),
//                        responseFields(
//                                fieldWithPath("value").description("Token da sessão e.g JWT"),
//                                fieldWithPath("expiration").description("Data de expiração do token")
//                        )
//                ));
    }

    private UserDTO createMockUser() {
        var inputUser = new InputUserDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setName(FULANO);
        inputUser.setUsername(FULANO_ADMIN);
        inputUser.setPassword(PASSWORD);
        inputUser.setAuthorities(List.of(1L, 2L));

        return userService.saveNewUser(inputUser);
    }
}
