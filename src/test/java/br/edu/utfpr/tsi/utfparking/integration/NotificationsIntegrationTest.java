package br.edu.utfpr.tsi.utfparking.integration;

import br.edu.utfpr.tsi.utfparking.domain.configuration.service.ApplicationConfigService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.TypeModeSystem;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationsIntegrationTest extends IntegrationTest {

    private static final String SUBSCRIBE_CHANGE_CONFIG_ENDPOINT = "/topic/change-config";

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationConfigService applicationConfigService;

    private CompletableFuture<MessageResponse<TypeModeSystem>> completableFuture;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        completableFuture = new CompletableFuture<>();
        super.setUp(restDocumentation);
    }

    @AfterEach
    void teardown() {
        super.tearDown();
    }

    @Disabled
    @Test
    void shouldHaveSubscribeOnConfigTopic() throws InterruptedException, ExecutionException, TimeoutException {
        var stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        var webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.set("Authorization", token);

        var stompSession = stompClient
                .connect("http://localhost:" + port + "/api/v1/ws", webSocketHttpHeaders, new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_CHANGE_CONFIG_ENDPOINT, new ChangeConfigStompFrameHandler());

        var inputApplicationConfiguration = new InputApplicationConfiguration();
        inputApplicationConfiguration.setModeSystem(TypeModeSystem.AUTOMATIC);

        var messageResponse = completableFuture.get(20, SECONDS);

        applicationConfigService.save(inputApplicationConfiguration);

        Assertions.assertEquals(TypeModeSystem.AUTOMATIC, messageResponse.getMessage());

        stompClient.stop();
    }

    private List<Transport> createTransportClient() {
        return List.of(new WebSocketTransport(new StandardWebSocketClient()));
    }

    private class ChangeConfigStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((MessageResponse<TypeModeSystem>) o);
        }
    }

    @Builder
    @Getter
    private static class MessageResponse<T> {

        private T message;

    }
}
