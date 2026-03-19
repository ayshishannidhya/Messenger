package com.messenger.config;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.util.Set;

/**
 * Some generic WebSocket clients send STOMP frames without a trailing NUL byte.
 * This decorator normalizes such frames so Spring's STOMP decoder can parse them.
 */
@Component
public class StompFrameTerminationDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private static final Set<String> STOMP_COMMANDS = Set.of(
            "CONNECT", "STOMP", "SUBSCRIBE", "UNSUBSCRIBE", "SEND",
            "DISCONNECT", "ACK", "NACK", "BEGIN", "COMMIT", "ABORT"
    );

    @Override
    public @NonNull WebSocketHandler decorate(@NonNull WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void handleMessage(@NonNull WebSocketSession session,
                                      @NonNull WebSocketMessage<?> message) throws Exception {
                if (message instanceof TextMessage textMessage) {
                    String payload = textMessage.getPayload();
                    String normalizedPayload = normalizeFrame(payload);
                    if (normalizedPayload != null) {
                        super.handleMessage(session, new TextMessage(normalizedPayload));
                        return;
                    }
                }
                super.handleMessage(session, message);
            }
        };
    }

    private String normalizeFrame(String payload) {
        if (payload == null || payload.isBlank() || payload.indexOf('\u0000') >= 0) {
            return null;
        }

        String normalized = payload.replace("\r\n", "\n");
        normalized = trimStompFrameIndentation(normalized);
        String firstLine = normalized.lines().findFirst().orElse("").trim().toUpperCase();

        if (!STOMP_COMMANDS.contains(firstLine)) {
            return null;
        }

        // Postman/Insomnia often send raw frames without the required header/body separator.
        if (!normalized.contains("\n\n")) {
            normalized = normalized + "\n\n";
        }

        return normalized + "\u0000";
    }

    private String trimStompFrameIndentation(String payload) {
        String[] lines = payload.split("\n", -1);
        StringBuilder builder = new StringBuilder(payload.length());
        boolean inBody = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String normalizedLine = inBody ? line : trimLeadingWhitespace(line);

            if (!inBody && normalizedLine.isEmpty()) {
                inBody = true;
            }

            builder.append(normalizedLine);
            if (i < lines.length - 1) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    private String trimLeadingWhitespace(String value) {
        int index = 0;
        while (index < value.length() && Character.isWhitespace(value.charAt(index))) {
            index++;
        }
        return value.substring(index);
    }
}
