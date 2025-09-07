package com.aegis.common.event;

import com.aegis.common.constant.RedisConstants;
import com.aegis.config.security.handler.MyFilterInvocationSecurityMetadataSource;
import com.aegis.modules.log.domain.entity.SysOperateLog;
import com.aegis.modules.log.mapper.SysOperateLogMapper;
import com.aegis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author: xuesong.lei
 * @Date: 2025/09/07 16:24
 * @Description: 数据变更监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataChangeListener {

    private final RedisUtils redisUtils;

    private final MyFilterInvocationSecurityMetadataSource securityMetadataSource;

    private final SysOperateLogMapper sysOperateLogMapper;

    @Async
    @EventListener(DataChangeEvent.class)
    public void onDataChange(DataChangeEvent event) {
        switch (event.getType()) {
            case MENU:
                handleMenuChange(event);
                break;
            case WHITELIST:
                handleWhitelistChange(event);
                break;
            case LOG:
                handleLog(event);
                break;
            default:
                log.warn("未知事件类型: {}", event.getType());
        }
    }

    /**
     * 处理菜单变更
     */
    private void handleMenuChange(DataChangeEvent event) {
        redisUtils.delete(RedisConstants.MENUS);
        securityMetadataSource.loadDataSourceAllUrl();
        log.info("菜单数据刷新完成，描述: {}", event.getDescription());
    }

    /**
     * 处理白名单变更
     */
    private void handleWhitelistChange(DataChangeEvent event) {
        redisUtils.delete(RedisConstants.WHITELIST);
        securityMetadataSource.loadDataSourceAllWhitelist();
        log.info("白名单数据刷新完成，描述: {}", event.getDescription());
    }

    /**
     * 处理操作日志入库
     */
    private void handleLog(DataChangeEvent event) {
        SysOperateLog logData = (SysOperateLog) event.getPayload();
        sysOperateLogMapper.insert(logData);
        log.debug("操作日志已保存: {}", logData);
    }
}
