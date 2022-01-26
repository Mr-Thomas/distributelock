package com.example.websocket.server;


import cn.hutool.http.HttpUtil;
import com.example.config.RedisUtil;
import com.example.model.MessageStruct;
import com.example.rabbit.RabbitConsts;
import com.example.websocket.manager.WsSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/12 11:24
 */
@Component
@Slf4j
public class WsHandler extends TextWebSocketHandler {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * socket 建立成功事件
     * 等于onOpen事件
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WsSessionManager.WEB_SOCKET_SESSION_POOL.put(session.getAttributes().get("trialId").toString() + "_" + session.getAttributes().get("trialNo").toString(), session);
        log.warn("与终端设备握手成功...");
    }

    /**
     * 接收消息事件
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        log.info("server 送的String消息 {}", payload);
        session.sendMessage(new TextMessage(session.getAttributes().get("trialNo").toString()));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // 获得客户端传来的消息
        ByteBuffer payload = message.getPayload();
    }

    /**
     * socket 断开连接时
     * 等于onClose事件
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        WsSessionManager.WEB_SOCKET_SESSION_POOL.remove(session.getAttributes().get("trialId").toString() + "_" + session.getAttributes().get("trialNo").toString());
    }

}
