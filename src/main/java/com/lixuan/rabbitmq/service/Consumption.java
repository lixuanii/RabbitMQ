package com.lixuan.rabbitmq.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lixuan.rabbitmq.dto.MqMessageDTO;
import com.lixuan.rabbitmq.dto.MsgDTO;
import com.lixuan.rabbitmq.entity.MqMsgLog;
import com.lixuan.rabbitmq.enums.MsgLogStatusEnum;
import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 统一消费者
 *
 * @author lixuan
 * @date 2023-01-09 10:59
 */
@Slf4j
@Component
public abstract class Consumption {

    @Autowired
    private MqMsgLogService mqMsgLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    static final String[] QUEUE = Arrays.stream(RabbitMqQueuesEnum.values()).filter(Objects::nonNull).map(RabbitMqQueuesEnum::buildQueueName).filter(StrUtil::isNotBlank).toList().toArray(new String[RabbitMqQueuesEnum.values().length]);

    private static final String QUEUE_NAME = RabbitMqQueuesEnum.LOGIN_SMS_USER.buildQueueName();

    @RabbitListeners(RabbitListener = QUEUE)
    public void basicConsumer(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        if (message == null || message.getBody() == null) {
            return;
        }
        MsgDTO dto = JSONUtil.toBean(new String(message.getBody()), MsgDTO.class);
        if (dto == null) {
            log.error("Consumption.basicConsumer dto isNull,message:{}", message);
            return;
        }
        RabbitMqQueuesEnum queuesEnum = dto.getQueuesEnum();
        if (queuesEnum == null) {
            log.error("Consumption.basicConsumer queuesEnum isNull,dto:{}", dto);
            return;
        }
        String queue = queuesEnum.buildQueueName();
        boolean autoAck = queuesEnum.isAutoAck();
        AtomicReference<String> msgId = new AtomicReference<>();

        try {
            // 消费处理
            this.consumption(dto);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            int retryCount = queuesEnum.getMaxRetryCount();
            int realRetryCount = (int) headers.getOrDefault("retry-count", 0);
            if (retryCount <= 0) {

            }
            if (realRetryCount++ < retryCount) {
                headers.put("retry-count", realRetryCount);
                // 更新次数记录
                // 应答
                channel.basicAck(tag, false);
                // 消息重新发送到MQ
                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().contentType("application/json").headers(headers).build();
                channel.basicPublish(message.getMessageProperties().getReceivedExchange(),
                        message.getMessageProperties().getReceivedRoutingKey(), basicProperties,
                        message.getBody());
            } else {

            }

        }
    }


    public void consumption(MsgDTO dto) {
        MqMessageDTO msg = dto.getMessage();
        RabbitMqQueuesEnum queuesEnum = dto.getQueuesEnum();
        if (msg == null || queuesEnum == null) {
            log.error("Consumption.consumption msg isNull or queuesEnum isNull,dto:{}", dto);
            return;
        }
        if (msg.getMsg() == null || StrUtil.isBlank(msg.getMsgId())) {
            log.error("Consumption.consumption msg.getMsg() isNull or msg.getMsgId() isNull,msg:{},dto:{}", msg, dto);
            return;
        }
        try {
            // 实际消费
            this.doConsumption(msg);
        } catch (Exception e) {
            // 消息消费失败
            log.error("消息消费失败,e:", e);
            mqMsgLogService.update(new LambdaUpdateWrapper<MqMsgLog>().set(MqMsgLog::getMsgId, msg.getMsg())
                    .set(MqMsgLog::getStatus, MsgLogStatusEnum.CONSUMPTION_FAILED.getStatus())
                    .set(MqMsgLog::getConsumptionFailedReason, e.getMessage()));
        }

    }


    /**
     * 消息消费
     *
     * @param dto 消息dto
     * @author lixuan
     * @date 2023/1/9 10:59
     **/
    protected abstract void doConsumption(MqMessageDTO dto);
}
