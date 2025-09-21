package com.aegis.config.scheduling;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/21 15:35
 * @Description: 定时任务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "task.scheduling")
public class SchedulingProperties {

    /**
     * 是否开启定时任务
     */
    private boolean enabled = true;
}
