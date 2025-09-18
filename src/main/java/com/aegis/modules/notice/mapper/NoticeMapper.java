package com.aegis.modules.notice.mapper;

import com.aegis.modules.notice.domain.entity.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025-09-16 21:39:03
 * @Description: 针对表【t_notice(通知公告表)】的数据库操作Mapper
 * @Entity: com.aegis.modules.notice.domain.entity.Notice
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    List<Notice> selectPendingNotices();
}




