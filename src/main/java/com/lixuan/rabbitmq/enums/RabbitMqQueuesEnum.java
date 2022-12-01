package com.lixuan.rabbitmq.enums;

import cn.hutool.extra.spring.SpringUtil;

import java.util.Optional;

/**
 * 队列枚举
 *
 * @author lixuan
 * @date 2022-11-21 16:18
 */
public enum RabbitMqQueuesEnum {

    /**
     * 消息推送
     */
    LOGIN_SMS_USER(
            RabbitMqQueuesModelEnum.USER,
            RabbitMqPushTypeEnum.SMS_TO_USER,
            "20221121",
            RabbitMqExchangesEnum.LOGIN, null
    ),

    /**
     * 积分累加
     */
    LOGIN_INTEGRAL_USER(
            RabbitMqQueuesModelEnum.USER,
            RabbitMqPushTypeEnum.SMS_TO_USER,
            "20221121",
            RabbitMqExchangesEnum.LOGIN, null
    );


    /**
     * 业务模块
     */
    private final RabbitMqQueuesModelEnum model;
    /**
     * 业务名称,多个单词使用逗号隔开
     */
    private final RabbitMqPushTypeEnum pushTypeEnum;
    /**
     * 业务开始日期
     */
    private final String date;
    /**
     * 是否自动 ack  默认否
     */
    private final boolean autoAck;
    /**
     * 所属交换机
     */
    private final RabbitMqExchangesEnum exchanges;
    /**
     * routingKey
     */
    private final String routingKey;
    /**
     * 最大重试次数
     */
    private final int maxRetryCount;
    /**
     * 全部匹配
     */
    private final boolean isWhereAll;
    /**
     * headers内容
     */
    private final String headers;

    /**
     * 构建队列名
     *
     * @return 队列名
     */
    public String buildQueueName() {
        String active = Optional.ofNullable(SpringUtil.getActiveProfile()).orElse("dev");
        StringBuilder queue = new StringBuilder(active).append(".")
                .append(this.getModel().getModule()).append(".")
                .append(this.getPushTypeEnum().getType()).append(".")
                .append(this.getExchanges().getRabbitMqExchangesTypeEnum().getType()).append(".")
                .append(this.getDate());
        if (!RabbitMqExchangesEnum.DEFAULT.equals(this.getExchanges())) {
            queue.append(".").append(this.getExchanges().getName());
        }
        return queue.toString();
    }

    RabbitMqQueuesEnum(RabbitMqQueuesModelEnum model, RabbitMqPushTypeEnum pushTypeEnum, String date, RabbitMqExchangesEnum exchanges, String routingKey) {
        this.model = model;
        this.pushTypeEnum = pushTypeEnum;
        this.date = date;
        this.autoAck = false;
        this.exchanges = exchanges;
        this.routingKey = routingKey;
        this.maxRetryCount = 5;
        this.isWhereAll = false;
        this.headers = null;
    }

    RabbitMqQueuesEnum(RabbitMqQueuesModelEnum model, RabbitMqPushTypeEnum pushTypeEnum, String date, boolean autoAck, RabbitMqExchangesEnum exchanges, String routingKey, int maxRetryCount, boolean isWhereAll, String headers) {
        this.model = model;
        this.pushTypeEnum = pushTypeEnum;
        this.date = date;
        this.autoAck = autoAck;
        this.exchanges = exchanges;
        this.routingKey = routingKey;
        this.maxRetryCount = maxRetryCount;
        this.isWhereAll = isWhereAll;
        this.headers = headers;
    }

    public RabbitMqQueuesModelEnum getModel() {
        return model;
    }

    public RabbitMqPushTypeEnum getPushTypeEnum() {
        return pushTypeEnum;
    }

    public String getDate() {
        return date;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public RabbitMqExchangesEnum getExchanges() {
        return exchanges;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public boolean isWhereAll() {
        return isWhereAll;
    }

    public String getHeaders() {
        return headers;
    }
}
