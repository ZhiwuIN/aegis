package com.aegis.modules.notice.service.impl;

import com.aegis.modules.notice.mapper.NoticeMapper;
import com.aegis.modules.notice.mapper.NoticeUserMapper;
import com.aegis.modules.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/10 14:10
 * @Description: 通知业务实现层
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    private final NoticeUserMapper noticeUserMapper;
}
