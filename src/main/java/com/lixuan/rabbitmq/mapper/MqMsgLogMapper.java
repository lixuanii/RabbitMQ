package com.lixuan.rabbitmq.mapper;

import com.lixuan.rabbitmq.entity.MqMsgLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lixuan
 * @since 2022-11-21 04:10:45
 */
@Mapper
public interface MqMsgLogMapper extends BaseMapper<MqMsgLog> {

}
