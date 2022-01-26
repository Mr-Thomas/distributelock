package com.example.rabbit;

/**
 * @author :Administrator
 * @description :TODO
 * @date :2022/1/22 14:29
 */
public interface RabbitConsts {
    /**
     * 直接模式1
     */
    String DIRECT_MODE_QUEUE_ONE = "queue.direct.1";

    /**
     * 队列2
     */
    String QUEUE_TWO = "queue.2";

    /**
     * 队列3
     */
    String QUEUE_THREE = "3.queue";

    String DIRECT_MODE_QUEUE = "direct.mode";

    String FANOUT_SOCKET_QUEUE = "socket.queue";

    /**
     * 分列模式
     */
    String FANOUT_MODE_QUEUE = "fanout.mode";

    String FANOUT_SOCKET_MODE_QUEUE = "fanout.socket.mode";

    /**
     * 主题模式
     */
    String TOPIC_MODE_QUEUE = "topic.mode";

    String DIRECT_ROUTING_KEY = "direct_routing_key";

    /**
     * 路由1
     */
    String TOPIC_ROUTING_KEY_ONE = "queue.#";

    /**
     * 路由2
     */
    String TOPIC_ROUTING_KEY_TWO = "*.queue";

    /**
     * 路由3
     */
    String TOPIC_ROUTING_KEY_THREE = "3.queue";

    /**
     * 延迟队列
     */
    String DELAY_QUEUE = "delay.queue";

    /**
     * 延迟队列交换器
     */
    String DELAY_MODE_QUEUE = "delay.mode";

    /**
     * 点对点交换机
     */
    String TOPIC_PEER_EXCHANGE = "peer.exchange";
    //点对点队列
    String TOPIC_PEER_QUEUE = "peer.queue";
    //点对点路由key
    String TOPIC_PEER_ROUTING_KEY = "peer.#";

    /**
     * 频道交换机
     */
    String TOPIC_CHANNEL_EXCHANGE = "channel.exchange";
    //频道队列
    String TOPIC_CHANNEL_QUEUE = "channel.queue";
    //频道路由key
    String TOPIC_CHANNEL_ROUTING_KEY = "channel.#";

}
