package com.lixuan.rabbitmq;

import cn.hutool.extra.spring.SpringUtil;
import com.lixuan.rabbitmq.enums.RabbitMqQueuesEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lixuan
 * @date 2022-12-01 18:10
 */
@SpringBootTest
public class RabbitMQTest {

    @Test
    public void getBeanTest() {
        Object bean = SpringUtil.getBean(RabbitMqQueuesEnum.LOGIN_INTEGRAL_USER.buildQueueName());
    }
}
