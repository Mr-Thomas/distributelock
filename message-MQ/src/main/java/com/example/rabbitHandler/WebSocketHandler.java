package com.example.rabbitHandler;

import com.example.websocket.manager.WsSessionManager;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author :Administrator
 * @description :TODO
 * @date :2022/1/26 11:40
 */

@Component
@Slf4j
public class WebSocketHandler implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String consumerQueue = message.getMessageProperties().getConsumerQueue();
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            String msg = new String(message.getBody());
            log.info("WebSocketHandler==>Queue({})、RoutingKey({})收到消息:{}", consumerQueue, routingKey, msg);
            WebSocketSession webSocketSession = WsSessionManager.WEB_SOCKET_SESSION_POOL.get(routingKey);
            if (ObjectUtils.isNotEmpty(webSocketSession)) {
                webSocketSession.sendMessage(new TextMessage(msg));
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
            e.printStackTrace();
        }
    }
}
