package com.example;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.example.model.MessageStruct;
import com.example.rabbit.MessageUtil;
import com.example.rabbit.RabbitConsts;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author :Administrator
 * @description :TODO
 * @date :2022/1/22 14:47
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class MessageUtilTest {
    @Autowired
    private MessageUtil messageUtil;

    /**
     * 测试直接模式发送
     */
    @Test
    public void sendDirect() {
//        messageUtil.sendMsg(RabbitConsts.DIRECT_MODE_QUEUE_ONE, JSONUtil.toJsonStr(new MessageStruct("direct message")));
        messageUtil.sendMsg(RabbitConsts.DIRECT_MODE_QUEUE, "direct_routing_key", JSONUtil.toJsonStr(new MessageStruct("direct message-->direct_routing_key")));
    }

    /**
     * 测试分列模式发送
     */
    @Test
    public void sendFanout() {
//        messageUtil.sendMsg(RabbitConsts.FANOUT_MODE_QUEUE, "", JSONUtil.toJsonStr(new MessageStruct("fanout message")));
        messageUtil.sendMsg(RabbitConsts.FANOUT_SOCKET_MODE_QUEUE, "1", JSONUtil.toJsonStr(new MessageStruct("fanout message")));
    }

    /**
     * 测试主题模式发送1
     */
    @Test
    public void sendTopic1() {
        messageUtil.sendMsg(RabbitConsts.TOPIC_MODE_QUEUE, "queue.aaa.bbb", JSONUtil.toJsonStr(new MessageStruct("topic message--queue.aaa.bbb")));
    }

    /**
     * 测试主题模式发送2
     */
    @Test
    public void sendTopic2() {
        messageUtil.sendMsg(RabbitConsts.TOPIC_MODE_QUEUE, "ccc.queue", JSONUtil.toJsonStr(new MessageStruct("topic message--ccc.queue")));
    }

    /**
     * 测试主题模式发送3
     */
    @Test
    public void sendTopic3() {
        messageUtil.sendMsg(RabbitConsts.TOPIC_MODE_QUEUE, "3.queue", JSONUtil.toJsonStr(new MessageStruct("topic message--3.queue")));
    }

    /**
     * 测试延迟队列发送
     */
    /*@Test
    public void sendDelay() {
        rabbitTemplate.convertAndSend(RabbitConsts.DELAY_MODE_QUEUE, RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message, delay 5s, " + DateUtil.date()), message -> {
            message.getMessageProperties().setHeader("x-delay", 5000);
            return message;
        });
        rabbitTemplate.convertAndSend(RabbitConsts.DELAY_MODE_QUEUE, RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message,  delay 2s, " + DateUtil.date()), message -> {
            message.getMessageProperties().setHeader("x-delay", 2000);
            return message;
        });
        rabbitTemplate.convertAndSend(RabbitConsts.DELAY_MODE_QUEUE, RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message,  delay 8s, " + DateUtil.date()), message -> {
            message.getMessageProperties().setHeader("x-delay", 8000);
            return message;
        });
    }*/

}
