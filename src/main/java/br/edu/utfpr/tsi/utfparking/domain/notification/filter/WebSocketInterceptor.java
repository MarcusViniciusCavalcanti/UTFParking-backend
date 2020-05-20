package br.edu.utfpr.tsi.utfparking.domain.notification.filter;

import br.edu.utfpr.tsi.utfparking.domain.security.service.SecurityContextUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSocketInterceptor implements ChannelInterceptor {

    private final SecurityContextUserService securityContextUserService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert accessor != null;
        log.info("message to {}. . .", accessor.getCommand());

        if (StompCommand.CONNECT.equals(accessor.getCommand()) && message.getHeaders().get("simpUser") != null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("message to connect. . .");
            var authorization = accessor.getNativeHeader("Authorization");
            assert authorization != null;
            var token = authorization.get(0).replace("Bearer", "").trim();
            securityContextUserService.receiveTokenToSecurityHolder(token);
        }

        return message;
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        return true;
    }
}
