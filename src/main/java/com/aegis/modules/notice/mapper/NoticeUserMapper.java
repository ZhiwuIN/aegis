package com.aegis.modules.notice.mapper;

import com.aegis.modules.notice.domain.dto.NoticeUserDTO;
import com.aegis.modules.notice.domain.entity.NoticeUser;
import com.aegis.modules.notice.domain.vo.NoticeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025-09-16 21:39:42
 * @Description: 针对表【t_notice_user(通知接收记录表)】的数据库操作Mapper
 * @Entity: com.aegis.modules.notice.domain.entity.NoticeUser
 */
public interface NoticeUserMapper extends BaseMapper<NoticeUser> {

    void batchInsert(List<NoticeUser> relations);

    List<Long> selectAllUserIds();

    List<Long> selectUserIdsByRoleIds(List<Long> ids);

    List<Long> selectUserIdsByDeptIds(List<Long> ids);

    List<NoticeVO> pageList(@Param("dto") NoticeUserDTO dto, @Param("userId") Long userId);
}




