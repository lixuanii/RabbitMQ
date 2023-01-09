package com.lixuan.rabbitmq.config;

import cn.hutool.extra.spring.SpringUtil;
import com.lixuan.rabbitmq.enums.RabbitMqExchangesEnum;
import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author lixuan
 * @date 2022-12-02 10:36
 */
@Slf4j
public class RabbitMqConfig2 {

//    @PostConstruct
    private void registerBean() {
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

    /**
     * 队列声明
     *
     * @param queuesEnum 队列枚举
     * @author lixuan
     * @date 2022/11/21 16:35
     **/
    private static Queue queueDeclare(RabbitMqQueuesEnum queuesEnum) {
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
    private static Exchange exchangeDeclare(RabbitMqExchangesEnum exchangesEnum) {
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

}
