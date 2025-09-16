package com.aegis.modules.notice.service.impl;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.modules.notice.domain.dto.NoticeUserDTO;
import com.aegis.modules.notice.domain.vo.NoticeVO;
import com.aegis.modules.notice.mapper.NoticeMapper;
import com.aegis.modules.notice.mapper.NoticeUserMapper;
import com.aegis.modules.notice.service.NoticeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/16 22:00
 * @Description: 通知用户业务实现层
 */
@Service
@RequiredArgsConstructor
public class NoticeUserServiceImpl implements NoticeUserService {

    private final NoticeUserMapper noticeUserMapper;

    private final NoticeMapper noticeMapper;

    @Override
    public PageVO<NoticeVO> pageList(NoticeUserDTO dto) {
        return null;
    }

    @Override
    public NoticeVO detail(Long id) {
        return null;
    }

    @Override
    public Long unreadCount() {
        return 0L;
    }
}
