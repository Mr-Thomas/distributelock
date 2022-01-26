package com.example.websocket.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/12 11:42
 */
@Component
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WsHandler wsHandler;
    @Autowired
    private WsInterceptor wsInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(wsHandler, "/socket")
                .addInterceptors(wsInterceptor)
                .setAllowedOrigins("*");
    }
}
