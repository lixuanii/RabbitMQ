package com.lixuan.rabbitmq.util;

import cn.hutool.extra.spring.SpringUtil;
import com.lixuan.rabbitmq.enums.RabbitMqExchangesEnum;
import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import com.lixuan.rabbitmq.service.MqMsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Objects;


/**
 * MQ工具类
 *
 * @author lixuan
 * @date 2022-11-21 16:17
 */
@Component
@Slf4j
public class RabbitMqUtil {

    @Autowired
    private MqMsgLogService mqMsgLogService;


    /**
     * 队列声明
     *
     * @param queuesEnum 队列枚举
     * @author lixuan
     * @date 2022/11/21 16:35
     **/
    public static Queue queueDeclare(RabbitMqQueuesEnum queuesEnum) {
        if (null == queuesEnum) {
            log.error("RabbitMqUtil.queueDeclare.error,mqQueuesEnum isNull");
            return null;
        }
        String queueName = queuesEnum.buildQueueName();
        return new Queue(queueName);
    }

    /**
     * 声明交换机
     *
     * @param exchangesEnum 交换机类型
     * @return Exchange
     * @author lixuan
     * @date 2022/11/22 9:32
     **/
    public static Exchange exchangeDeclare(RabbitMqExchangesEnum exchangesEnum) {
        if (null == exchangesEnum) {
            log.error("RabbitMqUtil.exchangeDeclare.error,mqExchangesEnum isNull");
            return null;
        }
        return switch (exchangesEnum.getRabbitMqExchangesTypeEnum()) {
            case TOPIC -> new TopicExchange(exchangesEnum.getName());
            case DIRECT -> new DirectExchange(exchangesEnum.getName());
            default -> new FanoutExchange(exchangesEnum.getName());
        };
    }

    @PostConstruct
    public void registerBean() {
        RabbitMqQueuesEnum[] queuesEnums = RabbitMqQueuesEnum.values();
        // 注册交换机
        Arrays.stream(RabbitMqExchangesEnum.values()).filter(Objects::nonNull).forEach(item -> SpringUtil.registerBean(item.getName(), exchangeDeclare(item)));
        for (RabbitMqQueuesEnum queuesEnum : queuesEnums) {
            Queue queue = queueDeclare(queuesEnum);
            Exchange exchange = exchangeDeclare(queuesEnum.getExchanges());
            if (queue == null || exchange == null) {
                continue;
            }
            String queueName = queuesEnum.buildQueueName();
            // 注册队列
            SpringUtil.registerBean(queueName, queue);
            // 队列绑定交换机
            SpringUtil.registerBean(queueName + "_binding", BindingBuilder.bind(queue).to(exchange));
        }

    }

}
