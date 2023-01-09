package com.lixuan.rabbitmq.enums;

/**
 * 交换机类型
 *
 * @author lixuan
 * @date 2022-11-21 16:28
 */
public enum RabbitMqExchangesTypeEnum {

    /**
     * 点对点模式 简单队列模式（默认交换机） 生产者发送消息给队列只有一个消费者能消费
     */
    DEFAULT("default"),
    /**
     * 广播者，发布订阅 模式（交换机下所有消费者都能进行消费） 消息发送至交换机
     */
    FANOUT("fanout"),

    /**
     * 路由，直连模式（交换机下绑定了routingKey的消费者才能消费）
     */
    DIRECT("direct"),

    /**
     * 主题模式（交换机下绑定routingKey且能匹配规则的消费者才能消费）
     */
    TOPIC("topic"),

    /**
     * 头部（以headers属性对队列进行匹配，能匹配的消费者才能消费）
     */
    HEADERS("headers"),
    ;

    private final String type;

    RabbitMqExchangesTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
