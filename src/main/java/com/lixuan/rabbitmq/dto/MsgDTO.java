package com.lixuan.rabbitmq.dto;

import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import lombok.Data;

/**
 * 消息推送dto
 *
 * @author lixuan
 * @date 2022-12-02 10:08
 */
@Data
public class MsgDTO {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 消息
     */
    private MqMessageDTO message;

    /**
     * 队列
     */
    private RabbitMqQueuesEnum queuesEnum;
}
