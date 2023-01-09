package com.lixuan.rabbitmq.config;

import cn.hutool.extra.spring.SpringUtil;
import com.lixuan.rabbitmq.enums.RabbitMqExchangesEnum;
import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author lixuan
 * @date 2022-11-22 10:40
 */
@Component
public class RabbitMqConfig {

    /**
     * 动态声明队列
     *
     * @author lixuan
     * @date 2022/11/22 11:22
     **/
    @Bean
    public void createQueue() {
        Arrays.stream(RabbitMqQueuesEnum.values()).toList().stream().filter(Objects::nonNull).forEach(item -> {
            String queueName = item.buildQueueName();
            SpringUtil.registerBean(queueName, new Queue(queueName));
        });
    }


    /**
     * 动态声明交换机
     *
     * @author lixuan
     * @date 2022/11/22 11:22
     **/
    @Bean
    public void createExchange() {
        Arrays.stream(RabbitMqExchangesEnum.values()).filter(Objects::nonNull).forEach(item -> {
            Exchange exchange = switch (item.getRabbitMqExchangesTypeEnum()) {
                default -> ExchangeBuilder.fanoutExchange(item.getName()).build();
                case DIRECT -> ExchangeBuilder.directExchange(item.getName()).build();
                case TOPIC -> ExchangeBuilder.topicExchange(item.getName()).build();
                case HEADERS -> ExchangeBuilder.headersExchange(item.getName()).build();
            };
            SpringUtil.registerBean(item.buildExchangeName(), exchange);
        });
    }

    /**
     * 动态 交换机绑定队列
     *
     * @author lixuan
     * @date 2022/11/22 12:00
     **/
    @Bean
    public void createBinding() {
        Arrays.stream(RabbitMqQueuesEnum.values()).toList().stream().filter(Objects::nonNull).forEach(item -> {
            Queue queue = SpringUtil.getBean(item.buildQueueName(), Queue.class);

            Binding binding = switch (item.getExchanges().getRabbitMqExchangesTypeEnum()) {
                default ->
                        BindingBuilder.bind(queue).to(SpringUtil.getBean(item.getExchanges().buildExchangeName(), FanoutExchange.class));
                case DIRECT ->
                        BindingBuilder.bind(queue).to(SpringUtil.getBean(item.getExchanges().buildExchangeName(), DirectExchange.class)).with(item.getRoutingKey());
                case TOPIC ->
                        BindingBuilder.bind(queue).to(SpringUtil.getBean(item.getExchanges().buildExchangeName(), TopicExchange.class)).with(item.getRoutingKey());
                case HEADERS ->
                        where(headers(queue, SpringUtil.getBean(item.getExchanges().buildExchangeName(), HeadersExchange.class)), item.isWhereAll(), item.getHeaders()).exist();
            };
            SpringUtil.registerBean(item.buildQueueName() + ".binding", binding);
        });
    }

    private BindingBuilder.HeadersExchangeMapConfigurer headers(Queue queue, HeadersExchange headersExchange) {
        return BindingBuilder.bind(queue).to(headersExchange);
    }

    private BindingBuilder.HeadersExchangeMapConfigurer.HeadersExchangeKeysBindingCreator where(BindingBuilder.HeadersExchangeMapConfigurer configurer, boolean isWhereAll, String headers) {
        return isWhereAll ? configurer.whereAll(headers) : configurer.whereAny(headers);
    }

}
