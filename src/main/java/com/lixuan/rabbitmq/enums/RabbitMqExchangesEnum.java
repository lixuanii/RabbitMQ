package com.lixuan.rabbitmq.enums;

import cn.hutool.extra.spring.SpringUtil;

import java.util.Optional;

/**
 * 交换机枚举
 *
 * @author lixuan
 * @date 2022-11-21 16:19
 */
public enum RabbitMqExchangesEnum {

    /**
     * 默认 路由模式
     */
    DEFAULT("default", RabbitMqExchangesTypeEnum.DIRECT),

    /**
     * 登录
     */
    LOGIN("login", RabbitMqExchangesTypeEnum.FANOUT),


    ;

    private final String name;

    private final RabbitMqExchangesTypeEnum rabbitMqExchangesTypeEnum;

    public String buildExchangeName() {
        return Optional.ofNullable(SpringUtil.getActiveProfile()).orElse("dev") + "." + this.getName() + ".exchange";
    }

    RabbitMqExchangesEnum(String name, RabbitMqExchangesTypeEnum rabbitMqExchangesTypeEnum) {
        this.name = name;
        this.rabbitMqExchangesTypeEnum = rabbitMqExchangesTypeEnum;
    }

    public String getName() {
        return name;
    }

    public RabbitMqExchangesTypeEnum getRabbitMqExchangesTypeEnum() {
        return rabbitMqExchangesTypeEnum;
    }
}
