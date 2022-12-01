package com.lixuan.rabbitmq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author lixuan
 * @since 2022-11-21 04:10:45
 */
@Getter
@Setter
@TableName("mq_msg_log")
public class MqMsgLog extends BaseEntity<MqMsgLog> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息体
     */
    @TableField("body")
    private String body;

    /**
     * 推送类型
     */
    @TableField("push_type")
    private String pushType;

    /**
     * 交换机
     */
    @TableField("exchange")
    private String exchange;

    /**
     * 路由key
     */
    @TableField("routing_key")
    private String routingKey;

    /**
     * 队列
     */
    @TableField("queue")
    private String queue;

    /**
     * 状态（0投递中、1投递成功、2投递失败、3消费成功、4消费失败）
     */
    @TableField("status")
    private Integer status;

    /**
     * 投递次数
     */
    @TableField("try_count")
    private Integer tryCount;

    /**
     * 投递失败原因
     */
    @TableField("delivery_failed_reason")
    private String deliveryFailedReason;

    /**
     * 消费失败原因
     */
    @TableField("consumption_failed_reason")
    private String consumptionFailedReason;
}
