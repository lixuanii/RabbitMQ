package com.lixuan.rabbitmq.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author lixuan
 * @date 2022-12-02 10:55
 */
@Data
public class MqMessageDTO {

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息体
     */
    private JSONObject msg;
}
