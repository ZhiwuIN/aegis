package com.aegis.modules.phone.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.phone.domain.dto.PhoneNumberDTO;
import com.aegis.modules.phone.domain.entity.PhoneNumber;
import com.aegis.modules.phone.domain.vo.PhoneNumberVO;
import com.aegis.modules.phone.mapper.PhoneNumberMapper;
import com.aegis.modules.phone.service.PhoneNumberConvert;
import com.aegis.modules.phone.service.PhoneNumberService;
import com.aegis.modules.user.domain.entity.User;
import com.aegis.modules.user.mapper.UserMapper;
import com.aegis.utils.PageUtils;
import com.aegis.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码业务实现层
 */
@Service
@RequiredArgsConstructor
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private final PhoneNumberMapper phoneNumberMapper;

    private final PhoneNumberConvert phoneNumberConvert;

    private final UserMapper userMapper;

    @Override
    public PageVO<PhoneNumberVO> pageList(PhoneNumberDTO dto) {
        LambdaQueryWrapper<PhoneNumber> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(dto.getPhone()), PhoneNumber::getPhone, dto.getPhone());
        queryWrapper.eq(StringUtils.isNotBlank(dto.getLevel()), PhoneNumber::getLevel, dto.getLevel());

        Long currentUserId = SecurityUtils.getUserId();

        // 管理员（ID=1）可以看到所有用户的手机号，子用户只能看到自己创建的
        if (CommonConstants.SUPER_ADMIN_ID.equals(currentUserId)) {
            if (ObjectUtils.isNotNull(dto.getOwnerUserId())) {
                // 管理员可根据子用户ID筛选
                queryWrapper.eq(PhoneNumber::getOwnerUserId, dto.getOwnerUserId());
            }
        } else {
            queryWrapper.eq(PhoneNumber::getOwnerUserId, currentUserId);
        }

        PageVO<PhoneNumberVO> page = PageUtils.of(dto)
                .pagingAndConvert(phoneNumberMapper, queryWrapper, phoneNumberConvert::toPhoneNumberVo);

        // 列表中ID转用户名
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            var userIds = page.getRecords().stream()
                    .flatMap(vo -> java.util.stream.Stream.of(vo.getOwnerUserId(), vo.getCreateBy(), vo.getUpdateBy()))
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toSet());

            if (!userIds.isEmpty()) {
                java.util.List<User> users = userMapper.selectBatchIds(userIds);
                java.util.Map<Long, String> userNameMap = users.stream()
                        .collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));

                java.util.Map<Long, String> projectNameMap = users.stream()
                        .collect(java.util.stream.Collectors.toMap(User::getId, User::getProjectName));

                page.getRecords().forEach(vo -> {
                    if (vo.getOwnerUserId() != null) {
                        vo.setOwnerUsername(userNameMap.get(vo.getOwnerUserId()));
                        vo.setProjectName(projectNameMap.get(vo.getOwnerUserId()));
                    }
                    if (vo.getCreateBy() != null) {
                        vo.setCreateByName(userNameMap.get(vo.getCreateBy()));
                    }
                    if (vo.getUpdateBy() != null) {
                        vo.setUpdateByName(userNameMap.get(vo.getUpdateBy()));
                    }
                });
            }
        }

        return page;
    }

    @Override
    public PhoneNumberVO detail(Long id) {
        PhoneNumber phoneNumber = phoneNumberMapper.selectById(id);
        if (phoneNumber == null) {
            return null;
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId) && !currentUserId.equals(phoneNumber.getOwnerUserId())) {
            throw new BusinessException("无权查看该手机号");
        }

        return phoneNumberConvert.toPhoneNumberVo(phoneNumber);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(PhoneNumberDTO dto) {
        Long currentUserId = SecurityUtils.getUserId();

        // 手机号唯一性校验（全局唯一）
        LambdaQueryWrapper<PhoneNumber> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhoneNumber::getPhone, dto.getPhone());
        PhoneNumber existByPhone = phoneNumberMapper.selectOne(wrapper);
        if (existByPhone != null) {
            // 已存在时，直接把已有记录的备注抛给前端
            User user = userMapper.selectById(existByPhone.getOwnerUserId());
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String autoRemark = String.format("用户【%s】在【%s】已创建手机号【%s】，对应级别为【%s】", user.getUsername(), now,
                    dto.getPhone(), StringUtils.isBlank(existByPhone.getLevel()) ? "空" : existByPhone.getLevel());
            throw new BusinessException(autoRemark);
        }

        PhoneNumber phoneNumber = phoneNumberConvert.toPhoneNumber(dto);
        phoneNumber.setOwnerUserId(currentUserId);
        phoneNumber.setCreateBy(currentUserId);

        // 备注自动填充：例如 “用户【张三】在【2026-03-14 10:00:00】创建手机号【13800000000】”
        String username = SecurityUtils.getCurrentUser().getUsername();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String autoRemark = String.format("用户【%s】在【%s】创建手机号【%s】", username, now, dto.getPhone());
        if (StringUtils.isNotBlank(dto.getRemark())) {
            phoneNumber.setRemark(dto.getRemark() + "；" + autoRemark);
        } else {
            phoneNumber.setRemark(autoRemark);
        }

        phoneNumberMapper.insert(phoneNumber);
        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String update(PhoneNumberDTO dto) {
        PhoneNumber exist = phoneNumberMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException("手机号记录不存在");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId) && !currentUserId.equals(exist.getOwnerUserId())) {
            throw new BusinessException("无权修改该手机号");
        }

        // 唯一性校验
        LambdaQueryWrapper<PhoneNumber> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhoneNumber::getPhone, dto.getPhone())
                .ne(PhoneNumber::getId, dto.getId());
        PhoneNumber existByPhone = phoneNumberMapper.selectOne(wrapper);
        if (existByPhone != null) {
            String remark = existByPhone.getRemark();
            throw new BusinessException(remark != null ? remark : "手机号已存在");
        }

        PhoneNumber update = phoneNumberConvert.toPhoneNumber(dto);
        update.setUpdateBy(currentUserId);
        update.setOwnerUserId(exist.getOwnerUserId());

        // 备注重新生成：例如 “用户【张三】在【2026-03-14 11:00:00】更新手机号【13800000000】”
        String username = SecurityUtils.getCurrentUser().getUsername();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String autoRemark = String.format("用户【%s】在【%s】更新手机号【%s】", username, now, dto.getPhone());
        if (StringUtils.isNotBlank(dto.getRemark())) {
            update.setRemark(dto.getRemark() + "；" + autoRemark);
        } else {
            update.setRemark(autoRemark);
        }

        phoneNumberMapper.updateById(update);
        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        PhoneNumber exist = phoneNumberMapper.selectById(id);
        if (exist == null) {
            return CommonConstants.SUCCESS_MESSAGE;
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId) && !currentUserId.equals(exist.getOwnerUserId())) {
            throw new BusinessException("无权删除该手机号");
        }

        phoneNumberMapper.deleteById(id);
        return CommonConstants.SUCCESS_MESSAGE;
    }
}
