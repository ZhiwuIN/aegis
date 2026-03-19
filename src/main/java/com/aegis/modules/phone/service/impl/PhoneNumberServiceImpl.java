package com.aegis.modules.phone.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.phone.domain.dto.PhoneNumberDTO;
import com.aegis.modules.phone.domain.dto.PhoneNumberImportDTO;
import com.aegis.modules.phone.domain.entity.PhoneNumber;
import com.aegis.modules.phone.domain.vo.PhoneNumberVO;
import com.aegis.modules.phone.mapper.PhoneNumberMapper;
import com.aegis.modules.phone.service.PhoneNumberConvert;
import com.aegis.modules.phone.service.PhoneNumberService;
import com.aegis.modules.project.domain.entity.Project;
import com.aegis.modules.project.mapper.ProjectMapper;
import com.aegis.modules.user.domain.entity.User;
import com.aegis.modules.user.domain.vo.UserVO;
import com.aegis.modules.user.mapper.UserMapper;
import com.aegis.utils.PageUtils;
import com.aegis.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码业务实现层
 */
@Service
@RequiredArgsConstructor
public class PhoneNumberServiceImpl extends ServiceImpl<PhoneNumberMapper, PhoneNumber> implements PhoneNumberService {

    private final PhoneNumberMapper phoneNumberMapper;

    private final PhoneNumberConvert phoneNumberConvert;

    private final UserMapper userMapper;

    private final ProjectMapper projectMapper;

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
                java.util.List<User> users = userMapper.selectByIds(userIds);
                java.util.Map<Long, String> userNameMap = users.stream()
                        .collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));

                Map<Long, Long> projectMap = users.stream()
                        .filter(user -> user.getProjectId() != null)
                        .collect(java.util.stream.Collectors.toMap(User::getId, User::getProjectId, (a, b) -> a));

                setProjectInfo(projectMap, page.getRecords());


                page.getRecords().forEach(vo -> {
                    if (vo.getOwnerUserId() != null) {
                        vo.setOwnerUsername(userNameMap.get(vo.getOwnerUserId()));
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

        checkPhone(dto, wrapper);

        PhoneNumber phoneNumber = phoneNumberConvert.toPhoneNumber(dto);
        phoneNumber.setOwnerUserId(currentUserId);
        phoneNumber.setCreateBy(currentUserId);

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

        checkPhone(dto, wrapper);

        PhoneNumber update = phoneNumberConvert.toPhoneNumber(dto);
        update.setUpdateBy(currentUserId);
        update.setOwnerUserId(exist.getOwnerUserId());

        phoneNumberMapper.updateById(update);
        return CommonConstants.SUCCESS_MESSAGE;
    }

    private void checkPhone(PhoneNumberDTO dto, LambdaQueryWrapper<PhoneNumber> wrapper) {
        PhoneNumber existByPhone = phoneNumberMapper.selectOne(wrapper);
        if (existByPhone != null) {
            // 已存在时，直接把已有记录的备注抛给前端
            User user = userMapper.selectById(existByPhone.getOwnerUserId());
            Project project = projectMapper.selectById(user.getProjectId());
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String autoRemark = String.format("用户【%s】于【%s】在项目【%s】中已创建手机号【%s】，对应级别为【%s】", user.getUsername(), now, project.getProjectName(),
                    dto.getPhone(), StringUtils.isBlank(existByPhone.getLevel()) ? "空" : existByPhone.getLevel());
            throw new BusinessException(autoRemark);
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importPhoneNumbers(List<PhoneNumberImportDTO> list) {
        Long currentUserId = SecurityUtils.getUserId();

        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("导入的手机号列表不能为空");
        }

        // 1. 提取所有手机号用于批量校验
        List<String> importPhones = list.stream()
                .map(PhoneNumberImportDTO::getPhone)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        if (importPhones.isEmpty()) {
            throw new BusinessException("有效的手机号不能为空");
        }

        // 2. 批量查询已存在的手机号（一次数据库查询）
        LambdaQueryWrapper<PhoneNumber> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PhoneNumber::getPhone, importPhones);
        List<PhoneNumber> existPhones = phoneNumberMapper.selectList(queryWrapper);

        // 3. 如果存在重复，抛出异常（事务会自动回滚）
        if (!existPhones.isEmpty()) {
            // 批量获取归属用户和项目信息（减少数据库查询）
            List<Long> userIds = existPhones.stream()
                    .map(PhoneNumber::getOwnerUserId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, User> userMap = Collections.emptyMap();
            Map<Long, Project> projectMap = Collections.emptyMap();

            if (!userIds.isEmpty()) {
                List<User> users = userMapper.selectByIds(userIds);
                userMap = users.stream()
                        .collect(Collectors.toMap(User::getId, user -> user));

                List<Long> projectIds = users.stream()
                        .map(User::getProjectId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

                if (!projectIds.isEmpty()) {
                    List<Project> projects = projectMapper.selectByIds(projectIds);
                    projectMap = projects.stream()
                            .collect(Collectors.toMap(Project::getId, project -> project));
                }
            }

            // 构建重复手机号的错误信息
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            StringBuilder errorMsg = new StringBuilder("以下手机号已存在，导入失败：\n");

            for (PhoneNumber exist : existPhones) {
                User user = userMap.get(exist.getOwnerUserId());
                Project project = projectMap.get(user != null ? user.getProjectId() : null);

                String userInfo = user != null ? user.getUsername() : "未知";
                String projectName = project != null ? project.getProjectName() : "未知";
                String level = StringUtils.isBlank(exist.getLevel()) ? "空" : exist.getLevel();

                errorMsg.append(String.format(
                    "  - 手机号【%s】：用户【%s】于【%s】在项目【%s】中创建，级别【%s】\n",
                    exist.getPhone(), userInfo, now, projectName, level
                ));
            }

            throw new BusinessException(errorMsg.toString());
        }

        // 4. 批量构建待插入的数据
        List<PhoneNumber> phoneNumbersToInsert = list.stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getPhone()))
                .map(entry -> {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setPhone(entry.getPhone());
                    phoneNumber.setLevel(entry.getLevel());
                    phoneNumber.setRemark(entry.getRemark());
                    phoneNumber.setOwnerUserId(currentUserId);
                    phoneNumber.setCreateBy(currentUserId);
                    return phoneNumber;
                })
                .toList();

        if (phoneNumbersToInsert.isEmpty()) {
            throw new BusinessException("有效的手机号不能为空");
        }

        // 5. 批量插入（使用 MyBatis-Plus 的 saveBatch 方法，默认 batchSize=1000）
        // 如果有异常，事务会自动回滚
        this.saveBatch(phoneNumbersToInsert);

        return String.format("成功导入 %d 条手机号记录", phoneNumbersToInsert.size());
    }

    private void setProjectInfo(Map<Long, Long> projectMap, List<PhoneNumberVO> records) {
        Collection<Long> projectIds = projectMap.values();

        if (projectIds.isEmpty()) {
            return;
        }
        List<Project> projects = projectMapper.selectByIds(projectIds);
        Map<Long, String> projectNameMap = projects.stream()
                .collect(Collectors.toMap(Project::getId, Project::getProjectName, (a, b) -> a));

        for (PhoneNumberVO phoneNumberVO : records) {
            if (phoneNumberVO.getOwnerUserId() != null) {
                Long projectId = projectMap.get(phoneNumberVO.getOwnerUserId());
                phoneNumberVO.setProjectName(projectNameMap.get(projectId));
            }
        }
    }
}
