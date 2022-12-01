package com.lixuan.rabbitmq.enums;

/**
 * 消息队列所属模块
 *
 * @author lixuan
 * @date 2022-11-21 16:19
 */
public enum RabbitMqQueuesModelEnum {

    /**
     * 用户模块
     */
    USER("user"),

    ;

    /**
     * 模块
     */
    private final String module;

    RabbitMqQueuesModelEnum(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }
}
