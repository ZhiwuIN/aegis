package com.aegis.modules.notice.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.notice.domain.dto.NoticeDTO;
import com.aegis.modules.notice.domain.entity.Notice;
import com.aegis.modules.notice.mapper.NoticeMapper;
import com.aegis.modules.notice.mapper.NoticeUserMapper;
import com.aegis.modules.notice.service.NoticeConvert;
import com.aegis.modules.notice.service.NoticeService;
import com.aegis.utils.PageUtils;
import com.aegis.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    private final NoticeConvert noticeConvert;

    @Override
    public PageVO<Notice> pageList(NoticeDTO dto) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dto.getNoticeTitle()), Notice::getNoticeTitle, dto.getNoticeTitle())
                .eq(StringUtils.isNotBlank(dto.getNoticeType()), Notice::getNoticeType, dto.getNoticeType())
                .eq(StringUtils.isNotBlank(dto.getStatus()), Notice::getStatus, dto.getStatus())
                .orderByDesc(Notice::getCreateTime);

        return PageUtils.of(dto).paging(noticeMapper, queryWrapper);
    }

    @Override
    public Notice detail(Long id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public String delete(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (!CommonConstants.NORMAL_STATUS.equals(notice.getStatus())) {
            throw new BusinessException("只能删除未发布的通知");
        }

        noticeMapper.deleteById(id);

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public String add(NoticeDTO dto) {
        Notice notice = noticeConvert.toNotice(dto);
        notice.setCreateBy(SecurityUtils.getUserId());

        if (ObjectUtils.isNull(dto.getPublishTime()) || !dto.getPublishTime().after(new Date())) {// 立即发布
            notice.setStatus(CommonConstants.DISABLE_STATUS);
            publishNotice(notice);
        }

        // 处理目标ID列表，转为字符串存储
        String targetIdsString = buildTargetIdsString(notice.getTargetType(), dto.getTargetIds());
        notice.setTargetIds(targetIdsString);

        noticeMapper.insert(notice);

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public String update(NoticeDTO dto) {
        Notice notice = noticeConvert.toNotice(dto);
        notice.setUpdateBy(SecurityUtils.getUserId());

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public String publish(Long id) {

        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    public String revoke(Long id) {

        return CommonConstants.SUCCESS_MESSAGE;
    }

    private void publishNotice(Notice notice) {

    }

    /**
     * 根据目标类型和目标ID列表，返回用于存储的字符串
     *
     * @param targetType 目标类型(1=全部用户,2=指定用户,3=指定角色,4=指定部门)
     * @param targetIds  目标ID列表
     * @return 目标ID字符串，全部用户返回 null
     */
    private String buildTargetIdsString(Integer targetType, List<Long> targetIds) {
        // 1 = 全部用户，不需要存储ID
        if (targetType == 1) {
            return null;
        }

        // 2、3、4 = 指定用户/角色/部门，必须有ID列表
        if (targetIds == null || targetIds.isEmpty()) {
            throw new BusinessException("目标对象ID列表不能为空");
        }

        // 去重并排序，转为逗号分隔字符串
        return targetIds.stream()
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
