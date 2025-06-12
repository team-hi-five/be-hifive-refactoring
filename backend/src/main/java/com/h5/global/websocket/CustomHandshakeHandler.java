package com.h5.global.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String userEmail = (String) attributes.get("userEmail");
        if (userEmail == null) {
            return null;
        }
        return new StompPrincipal(userEmail);
    }

    private static class StompPrincipal implements Principal {
        private final String name;
        public StompPrincipal(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }
}
