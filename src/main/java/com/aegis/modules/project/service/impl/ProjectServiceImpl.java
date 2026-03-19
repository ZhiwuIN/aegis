package com.aegis.modules.project.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.phone.domain.entity.PhoneNumber;
import com.aegis.modules.project.domain.dto.ProjectDTO;
import com.aegis.modules.project.domain.entity.Project;
import com.aegis.modules.project.domain.vo.ProjectVO;
import com.aegis.modules.project.mapper.ProjectMapper;
import com.aegis.modules.project.service.ProjectConvert;
import com.aegis.modules.project.service.ProjectService;
import com.aegis.modules.user.domain.entity.User;
import com.aegis.modules.user.mapper.UserMapper;
import com.aegis.utils.PageUtils;
import com.aegis.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 项目信息业务实现层
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    private final ProjectConvert projectConvert;

    private final UserMapper userMapper;

    @Override
    public PageVO<ProjectVO> pageList(ProjectDTO dto) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(dto.getProjectName()), Project::getProjectName, dto.getProjectName());

        Long currentUserId = SecurityUtils.getUserId();

        // 非管理员只可以看到自己创建的项目，管理员可以看到所有
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId)) {
            queryWrapper.eq(Project::getCreateBy, currentUserId);
        }

        // 按更新时间降序排列
        queryWrapper.orderByDesc(Project::getUpdateTime);
        PageVO<ProjectVO> page = PageUtils.of(dto)
                .pagingAndConvert(projectMapper, queryWrapper, projectConvert::toProjectVo);

        // 列表中ID转用户名
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            var userIds = page.getRecords().stream()
                    .flatMap(vo -> java.util.stream.Stream.of(vo.getCreateBy(), vo.getUpdateBy()))
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toSet());

            if (!userIds.isEmpty()) {
                java.util.List<User> users = userMapper.selectBatchIds(userIds);
                java.util.Map<Long, String> userNameMap = users.stream()
                        .collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));

                page.getRecords().forEach(vo -> {
                    if (vo.getCreateBy() != null) {
                        vo.setCreateByName(userNameMap.get(vo.getCreateBy()));
                    }
                    if (vo.getUpdateBy() != null) {
                        vo.setUpdateByName(userNameMap.get(vo.getUpdateBy()));
                    }
                    if (vo.getOwner() != null) {
                        vo.setOwnerName(userNameMap.get(vo.getOwner()));
                    }
                });
            }
        }

        return page;
    }

    @Override
    public ProjectVO detail(Long id) {
        Project Project = projectMapper.selectById(id);
        if (Project == null) {
            return null;
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId) && !currentUserId.equals(Project.getCreateBy())) {
            throw new BusinessException("无权查看该项目信息");
        }

        return projectConvert.toProjectVo(Project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(ProjectDTO dto) {
        Long currentUserId = SecurityUtils.getUserId();

        // 项目名称唯一性校验（全局唯一）
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectName, dto.getProjectName());
        checkProjectName(dto, wrapper);

        Project project = projectConvert.toProject(dto);
        project.setOwner(currentUserId);
        project.setCreateBy(currentUserId);

        projectMapper.insert(project);
        return CommonConstants.SUCCESS_MESSAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String update(ProjectDTO dto) {
        Project exist = projectMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException("项目信息记录不存在");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId) && !currentUserId.equals(exist.getOwner())) {
            throw new BusinessException("无权修改该项目");
        }

        // 唯一性校验
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectName, dto.getProjectName())
                .ne(Project::getId, dto.getId());
        checkProjectName(dto, wrapper);

        Project update = projectConvert.toProject(dto);
        update.setUpdateBy(currentUserId);
        update.setOwner(exist.getOwner());

        projectMapper.updateById(update);
        return CommonConstants.SUCCESS_MESSAGE;
    }

    private void checkProjectName(ProjectDTO dto, LambdaQueryWrapper<Project> wrapper) {
        Project existByProjectName = projectMapper.selectOne(wrapper);
        if (existByProjectName != null) {
            // 已存在时，直接把已有记录的信息抛给前端
            User user = userMapper.selectById(existByProjectName.getCreateBy());
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String autoRemark = String.format("用户【%s】在【%s】已创建项目【%s】", user.getUsername(), now, dto.getProjectName());
            throw new BusinessException(autoRemark);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        Project exist = projectMapper.selectById(id);
        if (exist == null) {
            return CommonConstants.SUCCESS_MESSAGE;
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!CommonConstants.SUPER_ADMIN_ID.equals(currentUserId) && !currentUserId.equals(exist.getOwner())) {
            throw new BusinessException("无权删除该项目");
        }

        projectMapper.deleteById(id);
        return CommonConstants.SUCCESS_MESSAGE;
    }
}
