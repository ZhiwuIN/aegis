package com.aegis.modules.whitelist.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.event.DataChangePublisher;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.whitelist.domain.dto.WhitelistDTO;
import com.aegis.modules.whitelist.domain.entity.Whitelist;
import com.aegis.modules.whitelist.mapper.WhitelistMapper;
import com.aegis.modules.whitelist.service.WhitelistConvert;
import com.aegis.modules.whitelist.service.WhitelistService;
import com.aegis.utils.PageUtils;
import com.aegis.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/8 10:35
 * @Description: 白名单业务实现层
 */
@Service
@RequiredArgsConstructor
public class WhitelistServiceImpl implements WhitelistService {

    private final WhitelistMapper whitelistMapper;

    private final DataChangePublisher dataChangePublisher;

    private final WhitelistConvert whitelistConvert;

    // 正则：只允许 / 开头，后面可有字母数字、下划线、横杠、单斜杠，最多允许末尾 /** 前缀
    private static final Pattern VALID_PATH_PATTERN = Pattern.compile("^(/[a-zA-Z0-9_-]+)*(?:/\\*{2})?$");

    @Override
    public PageVO<Whitelist> pageList(WhitelistDTO dto) {
        LambdaQueryWrapper<Whitelist> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(StringUtils.isNotBlank(dto.getRequestMethod()), Whitelist::getRequestMethod, dto.getRequestMethod())
                .eq(StringUtils.isNotBlank(dto.getStatus()), Whitelist::getStatus, dto.getStatus())
                .like(StringUtils.isNotBlank(dto.getRequestUri()), Whitelist::getRequestUri, dto.getRequestUri());

        return PageUtils.of(dto).paging(whitelistMapper, queryWrapper);
    }

    @Override
    public Whitelist detail(Long id) {
        return whitelistMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateStatus(Long id) {
        Whitelist whitelist = whitelistMapper.selectById(id);

        if (whitelist != null) {
            whitelist.setUpdateBy(SecurityUtils.getUsername());
            whitelist.setStatus(CommonConstants.NORMAL_STATUS.equals(whitelist.getStatus()) ? CommonConstants.DISABLE_STATUS : CommonConstants.NORMAL_STATUS);
            whitelistMapper.updateById(whitelist);

            // 发布白名单变更事件
            dataChangePublisher.publishWhitelistChange("更新白名单状态,ID: " + id);
        }
        return "操作成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        // 删除白名单
        whitelistMapper.deleteById(id);

        // 发布白名单变更事件
        dataChangePublisher.publishWhitelistChange("删除白名单,ID: " + id);

        return "操作成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdate(WhitelistDTO dto) {
        Whitelist whitelist = whitelistConvert.toWhitelist(dto);

        final String url = validateAndNormalize(whitelist.getRequestUri());

        // 检查是否有重复的路径和方法（排除自身）
        LambdaQueryWrapper<Whitelist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Whitelist::getRequestUri, url)
                .eq(Whitelist::getRequestMethod, whitelist.getRequestMethod().toUpperCase())
                .ne(ObjectUtils.isNotNull(whitelist.getId()), Whitelist::getId, whitelist.getId());

        if (whitelistMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("相同请求路径和方法的白名单已存在");
        }

        if (whitelist.getId() != null) {
            Whitelist existing = whitelistMapper.selectById(whitelist.getId());
            if (existing == null) {
                throw new BusinessException("白名单记录不存在");
            }

            whitelist.setUpdateBy(SecurityUtils.getUsername());
            whitelistMapper.updateById(whitelist);
        } else {
            whitelist.setCreateBy(SecurityUtils.getUsername());
            whitelistMapper.insert(whitelist);
        }

        // 发布白名单变更事件
        dataChangePublisher.publishWhitelistChange("新增或更新白名单,ID: " + (whitelist.getId() != null ? whitelist.getId() : "新建"));

        return "操作成功";
    }

    /**
     * 校验并规范化白名单路径
     *
     * @param path 用户输入的路径
     * @return 规范化后的路径
     */
    private String validateAndNormalize(String path) {
        // 去掉首尾空格，合并连续斜杠
        path = path.trim().replaceAll("/+", "/");

        // 保证以 / 开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // 禁止全路径通配符 /* 或 /** 或 /*** 等
        if (path.matches("/\\*+")) {
            throw new BusinessException("禁止使用全路径通配符");
        }

        // 校验合法字符和合法前缀通配符
        if (!VALID_PATH_PATTERN.matcher(path).matches()) {
            throw new BusinessException("路径包含非法字符或通配符位置不正确");
        }

        // 删除尾部多余斜杠（保留 /**）
        if (path.endsWith("/") && !path.endsWith("/**")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
}
