package com.example.rabbit;

import com.example.rabbitHandler.ChannelMessageHandler;
import com.example.rabbitHandler.PeerMessageHandler;
import com.example.rabbitHandler.WebSocketHandler;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

/**
 * @author :Administrator
 * @description :RabbitMQ配置，主要是配置队列，如果提前存在该队列，可以省略本配置类
 * @date :2022/1/22 14:24
 * Fanout：广播，将消息交给所有绑定到交换机的队列
 * Direct：定向，把消息交给符合指定routing key 的队列
 * Topic：通配符，把消息交给符合routing pattern（路由模式） 的队列
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    @Value("${server.port}")
    private String port;
    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private PeerMessageHandler peerMessageHandler;

    @Autowired
    private ChannelMessageHandler channelMessageHandler;

    /*@Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        // rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        // rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message));
        return rabbitTemplate;
    }*/

    //在container内将queue和listener绑定
    @Bean
    public SimpleMessageListenerContainer orderShowMessageListenerContainer(CachingConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames(socketQueueSuffix(RabbitConsts.FANOUT_SOCKET_QUEUE));
        container.setMessageListener(webSocketHandler);
        return container;
    }

    /**
     * 直接模式队列1
     */
    @Bean
    public Queue directOneQueue() {
        return new Queue(RabbitConsts.DIRECT_MODE_QUEUE_ONE);
    }

    /**
     * 队列2
     */
    @Bean
    public Queue queueTwo() {
        return new Queue(RabbitConsts.QUEUE_TWO);
    }

    /**
     * 队列3
     */
    @Bean
    public Queue queueThree() {
        return new Queue(RabbitConsts.QUEUE_THREE);
    }

    @Bean
    public Queue socketQueue() {
        return new Queue(socketQueueSuffix(RabbitConsts.FANOUT_SOCKET_QUEUE));
    }

    @Bean
    public FanoutExchange fanoutSocketExchange() {
        return new FanoutExchange(RabbitConsts.FANOUT_SOCKET_MODE_QUEUE);
    }

    @Bean
    public Binding topicSocketBinding(Queue socketQueue, FanoutExchange fanoutSocketExchange) {
        return BindingBuilder.bind(socketQueue).to(fanoutSocketExchange);
    }

    protected String socketQueueSuffix(String keyName) {
        String queueName = "";
        try {
            queueName = keyName + "." + InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            queueName = keyName + "." + UUID.randomUUID().toString().replace("-", "");
        }
        return queueName;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitConsts.DIRECT_MODE_QUEUE);
    }

    @Bean
    public Binding directBinding(Queue directOneQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(directOneQueue).to(directExchange).with(RabbitConsts.DIRECT_ROUTING_KEY);
    }

    /**
     * 分列模式队列
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitConsts.FANOUT_MODE_QUEUE);
    }

    /**
     * 分列模式绑定队列1
     *
     * @param directOneQueue 绑定队列1
     * @param fanoutExchange 分列模式交换器
     */
    @Bean
    public Binding fanoutBinding1(Queue directOneQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(directOneQueue).to(fanoutExchange);
    }

    /**
     * 分列模式绑定队列2
     *
     * @param queueTwo       绑定队列2
     * @param fanoutExchange 分列模式交换器
     */
    @Bean
    public Binding fanoutBinding2(Queue queueTwo, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueTwo).to(fanoutExchange);
    }

    /**
     * 主题模式队列
     * <li>路由格式必须以 . 分隔，比如 user.email 或者 user.aaa.email</li>
     * <li>通配符 * ，代表一个占位符，或者说一个单词，比如路由为 user.*，那么 user.email 可以匹配，但是 user.aaa.email 就匹配不了</li>
     * <li>通配符 # ，代表一个或多个占位符，或者说一个或多个单词，比如路由为 user.#，那么 user.email 可以匹配，user.aaa.email 也可以匹配</li>
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitConsts.TOPIC_MODE_QUEUE);
    }


    /**
     * 主题模式绑定分列模式
     *
     * @param fanoutExchange 分列模式交换器
     * @param topicExchange  主题模式交换器
     */
    @Bean
    public Binding topicBinding1(FanoutExchange fanoutExchange, TopicExchange topicExchange) {
        return BindingBuilder.bind(fanoutExchange).to(topicExchange).with(RabbitConsts.TOPIC_ROUTING_KEY_ONE);
    }

    /**
     * 主题模式绑定队列2
     *
     * @param queueTwo      队列2
     * @param topicExchange 主题模式交换器
     */
    @Bean
    public Binding topicBinding2(Queue queueTwo, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueTwo).to(topicExchange).with(RabbitConsts.TOPIC_ROUTING_KEY_TWO);
    }

    /**
     * 主题模式绑定队列3
     *
     * @param queueThree    队列3
     * @param topicExchange 主题模式交换器
     */
    @Bean
    public Binding topicBinding3(Queue queueThree, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueThree).to(topicExchange).with(RabbitConsts.TOPIC_ROUTING_KEY_THREE);
    }

    /**
     * 延迟队列
     */
//    @Bean
    public Queue delayQueue() {
        return new Queue(RabbitConsts.DELAY_QUEUE, true);
    }

    /**
     * 延迟队列交换器, x-delayed-type 和 x-delayed-message 固定
     */
//    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = Maps.newHashMap();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(RabbitConsts.DELAY_MODE_QUEUE, "x-delayed-message", true, false, args);
    }

    /**
     * 延迟队列绑定自定义交换器
     *
     * @param delayQueue    队列
     * @param delayExchange 延迟交换器
     */
//    @Bean
    public Binding delayBinding(Queue delayQueue, CustomExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with(RabbitConsts.DELAY_QUEUE).noargs();
    }


    @Bean
    public Queue peerQueue() {
        return new Queue(socketQueueSuffix(RabbitConsts.TOPIC_PEER_QUEUE));
    }

    @Bean
    public TopicExchange topicPeerExchange() {
        return new TopicExchange(RabbitConsts.TOPIC_PEER_EXCHANGE);
    }

    @Bean
    public Binding topicPeerBinding(Queue peerQueue, TopicExchange topicPeerExchange) {
        return BindingBuilder.bind(peerQueue).to(topicPeerExchange).with(RabbitConsts.TOPIC_PEER_ROUTING_KEY);
    }


    @Bean
    public SimpleMessageListenerContainer peerSimpleMessageListenerContainer(CachingConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames(socketQueueSuffix(RabbitConsts.TOPIC_PEER_QUEUE));
        container.setMessageListener(peerMessageHandler);
        return container;
    }


    @Bean
    public Queue channelQueue() {
        return new Queue(socketQueueSuffix(RabbitConsts.TOPIC_CHANNEL_QUEUE));
    }

    @Bean
    public TopicExchange topicChannelExchange() {
        return new TopicExchange(RabbitConsts.TOPIC_CHANNEL_EXCHANGE);
    }

    @Bean
    public Binding topicChannelBinding(Queue channelQueue, TopicExchange topicChannelExchange) {
        return BindingBuilder.bind(channelQueue).to(topicChannelExchange).with(RabbitConsts.TOPIC_CHANNEL_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer channelSimpleMessageListenerContainer(CachingConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames(socketQueueSuffix(RabbitConsts.TOPIC_CHANNEL_QUEUE));
        container.setMessageListener(channelMessageHandler);
        return container;
    }
}
