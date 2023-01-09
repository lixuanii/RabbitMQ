package com.lixuan.rabbitmq.util;

import com.lixuan.rabbitmq.dto.MsgDTO;
import com.lixuan.rabbitmq.entity.MqMsgLog;
import com.lixuan.rabbitmq.enums.MsgLogStatusEnum;
import com.lixuan.rabbitmq.enums.RabbitMqExchangesTypeEnum;
import com.lixuan.rabbitmq.service.MqMsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private MqMsgLogService msgLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 消息推送
     *
     * @param dto                消息dto
     * @param pushToExchangeFlag 是否推送到交换机标识
     * @author lixuan
     * @date 2022/12/2 10:26
     **/
    public void pushMsg(MsgDTO dto, boolean pushToExchangeFlag) {
        boolean flag = dto == null || dto.getMessage() == null || dto.getMessage().getMsgId() == null || dto.getMessage().getMsg() == null || dto.getUserId() == null || dto.getQueuesEnum() == null || dto.getQueuesEnum().getExchanges() == null || dto.getQueuesEnum().getPushTypeEnum() == null;
        if (flag) {
            log.error("RabbitMqUtil.pushMsg.MsgDTO error,MsgDTO:{}", dto);
            return;
        }
        MqMsgLog msgLog = this.buildMsgLog(dto);
        // 消息落库
        if (msgLogService.save(msgLog)) {
            try {
                // 推送消息 ,消息推送至交换机,消息推送到队列
                if (pushToExchangeFlag) {
                    this.pushMsgToExchange(dto);
                } else {
                    this.pushMsgToQueue(dto);
                }
                log.info("消息投递成功");
                // 消息数据更新
                msgLog.setStatus(MsgLogStatusEnum.DELIVERY_SUCCESS.getStatus());
                msgLogService.updateById(msgLog);
            } catch (Exception e) {
                log.error("消息投递失败,MsgDTO:{},error:{}", dto, e.getMessage());
                // 消息数据更新
                msgLog.setStatus(MsgLogStatusEnum.DELIVERY_FAILED.getStatus());
                msgLog.setDeliveryFailedReason(e.getMessage());
                msgLogService.updateById(msgLog);
            }
        } else {
            log.error("消息落库失败,入参MsgDTO:{},构建实例msgLog:{}", dto, msgLog);
        }
    }


    // TODO: 2022/12/2 消息发布，有待考究

    private void pushMsgToExchange(MsgDTO dto) {
        if (dto == null || dto.getQueuesEnum() == null || dto.getQueuesEnum().getExchanges() == null) {
            log.error("RabbitMqUtil.pushMsgToExchange.MqExchangesEnum is null");
            throw new RuntimeException("RabbitMqUtil.pushMsgToExchange.MqExchangesEnum is null");
        } else if (!RabbitMqExchangesTypeEnum.FANOUT.getType().equals(dto.getQueuesEnum().getExchanges().getRabbitMqExchangesTypeEnum().getType())) {
            log.error("RabbitMqUtil.pushMsgToExchange.MqExchangesEnum.Type is not FANOUT");
            throw new RuntimeException("RabbitMqUtil.pushMsgToExchange.MqExchangesEnum.Type is not FANOUT");
        }
        CorrelationData data = new CorrelationData();
        data.setId(dto.getMessage().getMsgId());
        rabbitTemplate.setExchange(dto.getQueuesEnum().getExchanges().getName());
        rabbitTemplate.send("", new Message(dto.toString().getBytes()), data);
    }


    private void pushMsgToQueue(MsgDTO dto) {
    }


    private MqMsgLog buildMsgLog(MsgDTO dto) {
        MqMsgLog msgLog = new MqMsgLog();
        if (dto == null || dto.getQueuesEnum() == null || dto.getMessage() == null) {
            log.error("RabbitMqUtil.pushMsg.MsgDTO is Null");
            return msgLog;
        }
        msgLog.setMsgId(dto.getMessage().getMsgId());
        msgLog.setBody(dto.getMessage().getMsg().toString());
        msgLog.setPushType(dto.getQueuesEnum().getPushTypeEnum().getType());
        msgLog.setExchange(dto.getQueuesEnum().getExchanges().buildExchangeName());
        msgLog.setRoutingKey(dto.getQueuesEnum().getRoutingKey());
        msgLog.setQueue(dto.getQueuesEnum().buildQueueName());
        msgLog.setStatus(MsgLogStatusEnum.DELIVERY.getStatus());
        msgLog.setTryCount(0);
        return msgLog;
    }


    // TODO: 2022/12/1 监听消息并消费
    //  统一的消费流程
    //  但是 每个消费者的实现逻辑不一致
    //  抽取流程，不同消费者不同实现

}
