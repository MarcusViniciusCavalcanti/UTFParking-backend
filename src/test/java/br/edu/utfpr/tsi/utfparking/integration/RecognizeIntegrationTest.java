package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.domain.configuration.service.ApplicationConfigService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputPlateRecognizerDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.TypeModeSystem;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecognizeIntegrationTest extends IntegrationTest {

    private static final String URI_RECOGNIZER = "/api/v1/recognizers/send/plate";

    @Autowired
    private ApplicationConfigService applicationConfigService;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) throws UnknownHostException {
        var ipConfig = new InputApplicationConfiguration();
        ipConfig.setModeSystem(TypeModeSystem.NONE);
        var hostAddress = "127.0.0.1";
        ipConfig.setIp(hostAddress);
        applicationConfigService.save(ipConfig);
        super.setUp(restDocumentation);
    }

    @AfterEach
    void teardown() {
        super.tearDown();
    }

    @Test
    void shouldReceivePlate() {
        var fields = new ConstrainedFields(InputPlateRecognizerDTO.class);

        var specification = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(configurer)
                .build();

        var coordinate = new InputPlateRecognizerDTO.Coordinate();
        coordinate.setX(10F);
        coordinate.setY(10F);

        var candidate = new InputPlateRecognizerDTO.Candidate();
        candidate.setConfidence(1F);
        candidate.setMatchesTemplate(1);
        candidate.setPlate("ABCI234");

        var result = new InputPlateRecognizerDTO.Result();
        result.setConfidence(10F);
        result.setMatchesTemplate(10);
        result.setPlate("ABC1234");
        result.setRegion("region");
        result.setRegionConfidence(10);
        result.setCoordinates(List.of(coordinate));
        result.setCandidates(List.of(candidate));

        var recognizerDTO = new InputPlateRecognizerDTO();
        recognizerDTO.setResults(List.of(result));
        recognizerDTO.setCameraId(10);
        recognizerDTO.setEpochTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
        recognizerDTO.setImgHeight(800);
        recognizerDTO.setImgWidth(600);
        recognizerDTO.setProcessingTimeMs(1000F);
        recognizerDTO.setSiteId(UUID.randomUUID().toString());
        recognizerDTO.setUuid(UUID.randomUUID().toString());

        given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("recognizer/send-plate/success",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestFields(
                                fields.withPath("uuid").type(JsonFieldType.STRING).description("Uuid do reconhecimento").optional(),
                                fields.withPath("camera_id").type(JsonFieldType.NUMBER).description("Id da camera").optional(),
                                fields.withPath("site_id").type(JsonFieldType.STRING).description("Id da localização").optional(),
                                fields.withPath("img_width").type(JsonFieldType.NUMBER).description("Resolução da imagem (largura)").optional(),
                                fields.withPath("img_height").type(JsonFieldType.NUMBER).description("Resolução da imagem (altura)").optional(),
                                fields.withPath("epoch_time").type(JsonFieldType.NUMBER).description("Momento do reconhecimento").optional(),
                                fields.withPath("processing_time_ms").type(JsonFieldType.NUMBER).description("Tempo de processamento").optional(),
                                fields.withPath("results").type(JsonFieldType.ARRAY).description("Resultado do reconhecimento").optional(),

                                fields.withPath("results[].plate").type(JsonFieldType.STRING).description("Placa reconhecida"),
                                fields.withPath("results[].confidence").type(JsonFieldType.NUMBER).description("taxa de confidencialidade do reconhecimento"),
                                fields.withPath("results[].matches_template").type(JsonFieldType.NUMBER).description("Id do template para reconhecimento").optional(),
                                fields.withPath("results[].region").type(JsonFieldType.STRING).description("Região").optional(),
                                fields.withPath("results[].region_confidence").type(JsonFieldType.NUMBER).description("Região").optional(),
                                fields.withPath("results[].coordinates").type(JsonFieldType.ARRAY).description("Coordenadas onde foi localizado a placa"),
                                fields.withPath("results[].candidates").type(JsonFieldType.ARRAY).description("Outros candidatos possíveis para reconhecimento").optional(),

                                fields.withPath("results[].coordinates[].x").type(JsonFieldType.NUMBER).description("Coordenadas da onde foi encontrado a placa na imagem").optional(),
                                fields.withPath("results[].coordinates[].y").type(JsonFieldType.NUMBER).description("Coordenadas da onde foi encontrado a placa na imagem").optional(),

                                fields.withPath("results[].candidates[].plate").type(JsonFieldType.STRING).description("Placa").optional(),
                                fields.withPath("results[].candidates[].confidence").type(JsonFieldType.NUMBER).description("Confiabilidade").optional(),
                                fields.withPath("results[].candidates[].matches_template").type(JsonFieldType.NUMBER).description("id do template").optional()
                        ))
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(recognizerDTO)
                .post(URI_RECOGNIZER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.ACCEPTED.value());
    }

    @Test
    void shouldReceivePlateWithoutPlate() {
        var specification = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(configurer)
                .build();

        var coordinate = new InputPlateRecognizerDTO.Coordinate();
        coordinate.setX(10F);
        coordinate.setY(10F);

        var result = new InputPlateRecognizerDTO.Result();
        result.setConfidence(10F);
        result.setMatchesTemplate(10);
        result.setRegion("region");
        result.setRegionConfidence(10);
        result.setCoordinates(List.of(coordinate));

        var recognizerDTO = new InputPlateRecognizerDTO();
        recognizerDTO.setResults(List.of(result));
        recognizerDTO.setCameraId(10);
        recognizerDTO.setEpochTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
        recognizerDTO.setImgHeight(800);
        recognizerDTO.setImgWidth(600);
        recognizerDTO.setProcessingTimeMs(1000F);
        recognizerDTO.setSiteId(UUID.randomUUID().toString());
        recognizerDTO.setUuid(UUID.randomUUID().toString());

        given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("recognizer/send-plate/success",
                        preprocessResponse(
                                prettyPrint(),
                                removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
                        ))
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(recognizerDTO)
                .post(URI_RECOGNIZER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void shouldReceivePlateWithoutConfidence() {
        var specification = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(configurer)
                .build();

        var coordinate = new InputPlateRecognizerDTO.Coordinate();
        coordinate.setX(10F);
        coordinate.setY(10F);

        var result = new InputPlateRecognizerDTO.Result();
        result.setPlate("abc1234");
        result.setMatchesTemplate(10);
        result.setRegion("region");
        result.setRegionConfidence(10);
        result.setCoordinates(List.of(coordinate));

        var recognizerDTO = new InputPlateRecognizerDTO();
        recognizerDTO.setResults(List.of(result));
        recognizerDTO.setCameraId(10);
        recognizerDTO.setEpochTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
        recognizerDTO.setImgHeight(800);
        recognizerDTO.setImgWidth(600);
        recognizerDTO.setProcessingTimeMs(1000F);
        recognizerDTO.setSiteId(UUID.randomUUID().toString());
        recognizerDTO.setUuid(UUID.randomUUID().toString());

        given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("recognizer/send-plate/success",
                        preprocessResponse(
                                prettyPrint(),
                                removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
                        ))
                ).contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(recognizerDTO)
                .post(URI_RECOGNIZER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
