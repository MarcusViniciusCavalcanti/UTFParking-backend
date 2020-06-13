package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.TypeModeSystem;
import br.edu.utfpr.tsi.utfparking.structure.repositories.ApplicationConfigRepository;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ApplicationConfigIntegrationTest extends IntegrationTest{

    private static final String PATH = "/api/v1/configurations";

    @Autowired
    private ApplicationConfigRepository applicationConfigRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);
    }

    @AfterEach
    void teardown() {
        super.tearDown();
        applicationConfigRepository.deleteAll();
    }

    @Test
    void shouldHaveChangeConfigurations() {
        var configuration = CreateMock.createMockInputApplicationConfiguration();
        var fields = new ConstrainedFields(InputApplicationConfiguration.class);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("configurations/create/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("save").description("Url para configurar o sistema."),
                                linkWithRel("load").description("Url para recuperar as configurações do sistema.")
                        ),
                        requestFields(
                                fields.withPath("ip").type(JsonFieldType.STRING).description("Endereço Ip do dispositivo."),
                                fields.withPath("modeSystem").type(JsonFieldType.STRING).description("Modo do sistema.")
                        ),
                        responseFields(
                                fieldWithPath("ip").type(JsonFieldType.STRING).description("Endereço Ip do dispositivo."),
                                fieldWithPath("modeSystem").type(JsonFieldType.STRING).description("Modo do sistema."),
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("Uris relacionadas ao recurso."),
                                fieldWithPath("_links.save.href").ignored(),
                                fieldWithPath("_links.load.href").ignored()
                        )
                ))
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(configuration)
                .post(PATH)
                .then()
                .body("modeSystem", is("AUTOMATIC"))
                .body("ip", is("192.0.0.0"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnConfigurations() {
        var types = Stream.of(TypeModeSystem.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        var msgModelSystem = "Modo do sistema. Valores possíveis: " + types;

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("configurations/load/success", getRequestPreprocessor(), getResponsePreprocessor(),
                        links(halLinks(),
                                linkWithRel("save").description("Url para configurar o sistema."),
                                linkWithRel("load").description("Url para recuperar as configurações do sistema.")
                        ),
                        responseFields(
                                fieldWithPath("ip").type(JsonFieldType.STRING).description("Endereço Ip do dispositivo."),
                                fieldWithPath("modeSystem").type(JsonFieldType.STRING).description(msgModelSystem),
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("Uris relacionadas ao recurso."),
                                fieldWithPath("_links.save.href").ignored(),
                                fieldWithPath("_links.load.href").ignored()
                        )
                ))
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get(PATH)
                .then()
                .body("modeSystem", is("NONE"))
                .body("ip", is("0.0.0.0"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnErrorWhenModelSystemIsNull() {
        var configuration = CreateMock.createMockInputApplicationConfiguration();
        configuration.setModeSystem(null);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(configuration)
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void shouldReturnErrorWhenModelSystemIsInvalidValue() {
        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{ \"modeSystem\": \"other\", \"ip\":\"192.0.0.0\"}")
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void shouldReturnErrorWhenIpIsNull() {
        var configuration = CreateMock.createMockInputApplicationConfiguration();
        configuration.setIp(null);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(configuration)
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @ParameterizedTest
    @MethodSource(value = "providerIpErrors")
    void shouldReturnErrorWhenIpIsInvalid(String ip) {
        var configuration = CreateMock.createMockInputApplicationConfiguration();
        configuration.setIp(ip);

        given(specAuthentication)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(configuration)
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    private static Stream<Arguments> providerIpErrors() {
        return Stream.of(
               Arguments.of("00000000000000"),
               Arguments.of("00.0.00000000000"),
               Arguments.of("000000.00000000"),
               Arguments.of("000.000.000.000.00"),
               Arguments.of("00:00:0000:000000"),
               Arguments.of("000000:00000000"),
               Arguments.of("00:0:000:00000000"),
               Arguments.of("00:00:00:00:000:000:00"),
               Arguments.of("000000:00000:000"),
               Arguments.of("000:000:00000:00:0")
        );
    }
}
