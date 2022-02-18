package com.example.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author :Administrator
 * @description :TODO
 * @date :2022/1/24 18:23
 */
@Component
@Slf4j
public class MessageUtil implements RabbitTemplate.ReturnCallback, RabbitTemplate.ConfirmCallback {

    private RabbitTemplate rabbitTemplate;

    public MessageUtil(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setConfirmCallback(this::confirm);
        rabbitTemplate.setReturnCallback(this::returnedMessage);
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMsg4Peer(String trialNo, Object message) {
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_PEER_EXCHANGE, RabbitConsts.TOPIC_PEER_ROUTING_KEY.replace("#", trialNo), message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                String peer_uuid = UUID.randomUUID().toString();
                message.getMessageProperties().setHeader("PEER_UUID", peer_uuid);
                return message;
            }
        });
    }

    public void sendMsg4Channel(String trialId, Object message) {
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_CHANNEL_EXCHANGE, RabbitConsts.TOPIC_CHANNEL_ROUTING_KEY.replace("#", trialId), message, new CorrelationData(UUID.randomUUID().toString()));
    }

    public void sendMsg(String exchange, String routingKey, Object object) {
        rabbitTemplate.convertAndSend(exchange, routingKey, object, new CorrelationData(UUID.randomUUID().toString()));
    }

    public void sendMsg(String queueName, Object object) {
        rabbitTemplate.convertAndSend(queueName, object, new CorrelationData(UUID.randomUUID().toString()));
    }

    /**
     * confirm机制只保证消息到达exchange，不保证消息可以路由到正确的queue,如果exchange错误，就会触发confirm机制
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (!ack) {
            log.error("rabbitmq confirm fail,cause:{}", cause);
        }
    }

    /**
     * Return 消息机制用于处理一个不可路由的消息。在某些情况下，如果我们在发送消息的时候，当前的 exchange 不存在或者指定路由 key 路由不到，这个时候我们需要监听这种不可达的消息
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("rabbitMQ fail=>exchange({}),routingKey({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
    }
}
