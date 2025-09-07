package com.aegis.common.event;

import com.aegis.modules.log.domain.entity.SysOperateLog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/07 16:24
 * @Description: 数据变更发布器
 */
@Component
@RequiredArgsConstructor
public class DataChangePublisher {

    private final ApplicationContext applicationContext;

    public void publishMenuChange(String desc) {
        applicationContext.publishEvent(new DataChangeEvent(DataChangeEvent.Type.MENU, null, desc));
    }

    public void publishWhitelistChange(String desc) {
        applicationContext.publishEvent(new DataChangeEvent(DataChangeEvent.Type.WHITELIST, null, desc));
    }

    public void publishLog(SysOperateLog log) {
        applicationContext.publishEvent(new DataChangeEvent(DataChangeEvent.Type.LOG, log, "记录操作日志"));
    }
}
