package com.example.websocket.server;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/12 11:36
 */
@Slf4j
@Component
public class WsInterceptor implements HandshakeInterceptor {

    /**
     * 握手前
     *
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @param webSocketHandler
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) {
        log.info("握手开始...");
        Map<String, String> paramMap = HttpUtil.decodeParamMap(serverHttpRequest.getURI().getQuery(), "UTF-8");
        map.put("uid", paramMap.get("uid"));
        return true;
    }

    /**
     * 握手后
     *
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @param webSocketHandler
     * @param e
     */
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
    }
}
