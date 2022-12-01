package com.lixuan.rabbitmq.enums;

/**
 * 推送类型枚举
 *
 * @author lixuan
 * @date 2022-11-21 16:19
 */
public enum RabbitMqPushTypeEnum {

    SMS_TO_USER("push.sms.toUser"),
    LOGIN_INTEGRAL_USER("login.integral.user"),

    ;

    private final String type;


    RabbitMqPushTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
