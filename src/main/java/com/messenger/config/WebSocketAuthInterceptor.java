package com.messenger.config;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 13-03-2026
 */

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        log.info("INVOKED INTERCEPTOR");
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Principal principal = accessor.getUser();

            if (principal == null) {
                String userId = firstNativeHeader(accessor, "userId", "phone", "mobNumber", "senderPhone");

                if (userId != null && !userId.isBlank()) {
                    accessor.setUser(() -> userId);
                    log.info("No authentication found. Using CONNECT header user: {}", userId);
                } else {
                    accessor.setUser(() -> "debug-user");
                    log.warn("No authentication found. Using debug-user for testing.");
                }
            }

            log.info("WebSocket CONNECT accepted for user: {}",
                    accessor.getUser().getName());
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            log.info("WebSocket SEND - user: {}, destination: {}, sessionId: {}",
                    accessor.getUser() != null ? accessor.getUser().getName() : "anonymous",
                    accessor.getDestination(),
                    accessor.getSessionId());
        }

        return message;
    }

    private String firstNativeHeader(StompHeaderAccessor accessor, String... headerNames) {
        for (String headerName : headerNames) {
            List<String> values = accessor.getNativeHeader(headerName);
            if (values != null && !values.isEmpty() && values.getFirst() != null && !values.getFirst().isBlank()) {
                return values.getFirst().trim();
            }
        }
        return null;
    }
}
