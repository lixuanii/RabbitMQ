package com.lixuan.rabbitmq.service.impl;

import com.lixuan.rabbitmq.entity.MqMsgLog;
import com.lixuan.rabbitmq.mapper.MqMsgLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lixuan.rabbitmq.service.MqMsgLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixuan
 * @since 2022-11-21 04:10:45
 */
@Service
public class MqMsgLogServiceImpl extends ServiceImpl<MqMsgLogMapper, MqMsgLog> implements MqMsgLogService {

}
