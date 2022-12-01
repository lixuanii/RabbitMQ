package com.lixuan.rabbitmq.util;

import com.lixuan.rabbitmq.enums.RabbitMqExchangesEnum;
import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import com.lixuan.rabbitmq.service.MqMsgLogService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
     * @param exchangesEnum
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

    /**
     * 交换机绑定到队列
     *
     * @return Binding
     * @author lixuan
     * @date 2022/11/22 9:37
     **/
    public static Binding binding() {
        RabbitMqQueuesEnum[] queuesEnums = RabbitMqQueuesEnum.values();
        for (int i = 0; i < queuesEnums.length; i++) {
            final val queuesEnum = queuesEnums[i];
            // 队列绑定交换机
            BindingBuilder.bind(queueDeclare(queuesEnum)).to(exchangeDeclare(queuesEnum.getExchanges()));
        }
        return null;
    }


}
