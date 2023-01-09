package com.lixuan.rabbitmq.enums;

/**
 * 消息日志状态枚举
 *
 * @author lixuan
 * @date 2023-01-09 10:43
 */
public enum MsgLogStatusEnum {
    /**
     * 0 投递中
     */
    DELIVERY(0),
    /**
     * 1 投递成功
     */
    DELIVERY_SUCCESS(1),
    /**
     * 2 投递失败
     */
    DELIVERY_FAILED(2),
    /**
     * 3 消费成功
     */
    CONSUMPTION_SUCCESS(3),
    /**
     * 4 消费失败
     */
    CONSUMPTION_FAILED(4),

    ;


    private final Integer status;

    MsgLogStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
