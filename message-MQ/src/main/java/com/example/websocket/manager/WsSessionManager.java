package com.example.websocket.manager;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :Administrator
 * @description :TODO
 * @date :2022/1/26 13:49
 */
public class WsSessionManager {
    public static final Map<String, WebSocketSession> WEB_SOCKET_SESSION_POOL = new ConcurrentHashMap<>();
}
