package br.edu.utfpr.tsi.utfparking.domain.notification.filter;

import br.edu.utfpr.tsi.utfparking.domain.security.service.SecurityContextUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class WebSocketInterceptorTest {

    @Mock
    private SecurityContextUserService securityContextUserService;

    @Mock
    private MessageChannel messageChannel;

    @InjectMocks
    private WebSocketInterceptor webSocketInterceptor;

    @Test
    void shouldAuthenticatedReceiveMessageConnectUserIsAuthenticated() {
        var simpMessageHeaderAccessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        simpMessageHeaderAccessor.setSessionId("systemTest");
        simpMessageHeaderAccessor.setDestination("/topic/topic");
        simpMessageHeaderAccessor.setLeaveMutable(true);

        simpMessageHeaderAccessor.setUser(new UserTest());

        simpMessageHeaderAccessor.addNativeHeader("Authorization", "Bearer");

        String payload = "message";
        var genericMessage = new GenericMessage<>(payload, simpMessageHeaderAccessor.getMessageHeaders());

        var message = webSocketInterceptor.preSend(genericMessage, messageChannel);

        assertEquals(payload, message.getPayload());

    }

    @Test
    void shouldSendMessageAuthenticatedReceiveMessageConnectUserIsAuthenticated() {
        var simpMessageHeaderAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        simpMessageHeaderAccessor.setSessionId("systemTest");
        simpMessageHeaderAccessor.setDestination("/topic/topic");
        simpMessageHeaderAccessor.setLeaveMutable(true);

        simpMessageHeaderAccessor.setUser(new UserTest());

        simpMessageHeaderAccessor.addNativeHeader("Authorization", "Bearer");

        String payload = "message";
        var genericMessage = new GenericMessage<>(payload, simpMessageHeaderAccessor.getMessageHeaders());

        var message = webSocketInterceptor.preSend(genericMessage, messageChannel);

        assertEquals(payload, message.getPayload());

    }

    @Test
    void shouldAuthenticateMessageWhenSendMessageWhitBearer() {
        var simpMessageHeaderAccessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        simpMessageHeaderAccessor.setSessionId("systemTest");
        simpMessageHeaderAccessor.setDestination("/topic/topic");
        simpMessageHeaderAccessor.setLeaveMutable(true);

        simpMessageHeaderAccessor.addNativeHeader("Authorization", "Bearer");

        var payload = "message";
        var genericMessage = new GenericMessage<>(payload, simpMessageHeaderAccessor.getMessageHeaders());

        doNothing().when(securityContextUserService).receiveTokenToSecurityHolder(any());

        var message = webSocketInterceptor.preSend(genericMessage, messageChannel);

        assertEquals(payload, message.getPayload());

    }

    @Test
    void shouldReturnNullPointerWhenHeaderNotInstanceStompAccessor() {
        var simpMessageHeaderAccessor = StompHeaderAccessor.create(SimpMessageType.CONNECT);

        var payload = "message";
        var genericMessage = new GenericMessage<>(payload, simpMessageHeaderAccessor.getMessageHeaders());

        assertThrows(IllegalStateException.class, () -> webSocketInterceptor.preSend(genericMessage, messageChannel));
    }

    @Test
    void shouldSendMessageAuthenticateMessageWhenSendMessageWhitBearer() {
        var simpMessageHeaderAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        simpMessageHeaderAccessor.setSessionId("systemTest");
        simpMessageHeaderAccessor.setDestination("/topic/topic");
        simpMessageHeaderAccessor.setLeaveMutable(true);

        simpMessageHeaderAccessor.addNativeHeader("Authorization", "Bearer");

        var payload = "message";
        var genericMessage = new GenericMessage<>(payload, simpMessageHeaderAccessor.getMessageHeaders());

        var message = webSocketInterceptor.preSend(genericMessage, messageChannel);

        assertEquals(payload, message.getPayload());

    }

    @Test
    void shouldMessageWhenWithoutHeaderAutorization() {
        var simpMessageHeaderAccessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        simpMessageHeaderAccessor.setSessionId("systemTest");
        simpMessageHeaderAccessor.setDestination("/topic/topic");
        simpMessageHeaderAccessor.setLeaveMutable(true);


        var payload = "message";
        var genericMessage = new GenericMessage<>(payload, simpMessageHeaderAccessor.getMessageHeaders());

        assertThrows(IllegalStateException.class, () -> webSocketInterceptor.preSend(genericMessage, messageChannel));
    }

    @Test
    void shouldReturnTrue() {
        assertTrue(webSocketInterceptor.preReceive(null));
    }

    private static class UserTest implements Principal {
        @Override
        public String getName() {
            return "UserTest";
        }
    }
}
