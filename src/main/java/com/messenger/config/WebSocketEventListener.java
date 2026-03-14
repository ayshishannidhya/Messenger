package com.messenger.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal principal = headerAccessor.getUser();

        if (principal != null) {
            assert event.getUser() != null;
            registerUser(sessionId, event.getUser().getName());
            log.info("WebSocket connected - sessionId: {}", sessionId);
            log.debug("Total active connections: {}", onlineUsers.size() + 1);
        } else {
            log.warn("WebSocket connected without authenticate user session: {}", sessionId);
        }

    }

    public void registerUser(String sessionId, String userId) {
        onlineUsers.put(sessionId, userId);
        userSessions.put(userId, sessionId);

        log.info("User registered online - userId: {}, sessionId: {}", userId, sessionId);
        log.debug("Total online users: {}", userSessions.size());

        Map<String, Object> onlineMessage = Map.of(
                "type", "ONLINE",
                "userId", userId,
                "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/presence", Optional.of(onlineMessage));
        log.debug("Online status broadcast for userId: {}", userId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String userId = onlineUsers.remove(sessionId);
        if (userId != null) {
            userSessions.remove(userId);

            log.info("WebSocket disconnected - userId: {}, sessionId: {}", userId, sessionId);
            log.debug("Remaining active connections: {}", onlineUsers.size());

            Map<String, Object> offlineMessage = Map.of(
                    "type", "OFFLINE",
                    "userId", userId,
                    "timestamp", System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/presence", Optional.of(offlineMessage));
            log.debug("Offline status broadcast for userId: {}", userId);
        } else {
            log.debug("WebSocket disconnected - sessionId: {} (no user associated)", sessionId);
        }
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();

        log.debug("WebSocket subscription - sessionId: {}, destination: {}", sessionId, destination);
    }

    public boolean isUserOnline(String userId) {
        boolean online = userSessions.containsKey(userId);
        log.debug("User online check - userId: {}, online: {}", userId, online);
        return online;
    }

    public Set<String> getOnlineUsers() {
        log.debug("Getting all online users - count: {}", userSessions.size());
        return userSessions.keySet();
    }

    public String getSessionId(String userId) {
        String sessionId = userSessions.get(userId);
        log.debug("Getting session for userId: {} - sessionId: {}", userId, sessionId);
        return sessionId;
    }
}
