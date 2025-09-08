package com.aegis.modules.dict.service.impl;

import com.aegis.common.constant.CommonConstants;
import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.exception.BusinessException;
import com.aegis.modules.dict.domain.DictionaryDTO;
import com.aegis.modules.dict.domain.entity.Dictionary;
import com.aegis.modules.dict.mapper.DictionaryMapper;
import com.aegis.modules.dict.service.DictionaryService;
import com.aegis.utils.PageUtils;
import com.aegis.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/8 16:08
 * @Description: 字典业务实现层
 */
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryMapper dictionaryMapper;

    @Override
    public PageVO<Dictionary> pageList(DictionaryDTO dto) {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(dto.getDictName()), Dictionary::getDictName, dto.getDictName())
                .like(StringUtils.isNotBlank(dto.getDictType()), Dictionary::getDictType, dto.getDictType())
                .like(StringUtils.isNotBlank(dto.getDictLabel()), Dictionary::getDictLabel, dto.getDictLabel())
                .eq(StringUtils.isNotBlank(dto.getStatus()), Dictionary::getStatus, dto.getStatus());

        return PageUtils.of(dto).paging(dictionaryMapper, queryWrapper);
    }

    @Override
    public Dictionary detail(Long id) {
        return dictionaryMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateStatus(Long id) {
        Dictionary dictionary = dictionaryMapper.selectById(id);

        if (dictionary != null) {
            dictionary.setUpdateBy(SecurityUtils.getUsername());
            dictionary.setStatus(CommonConstants.NORMAL_STATUS.equals(dictionary.getStatus()) ? CommonConstants.DISABLE_STATUS : CommonConstants.NORMAL_STATUS);
            dictionaryMapper.updateById(dictionary);
        }

        return "操作成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) {
        dictionaryMapper.deleteById(id);
        return "操作成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdate(DictionaryDTO dto) {
        if (isContainUpperCase(dto.getDictType())) {
            throw new BusinessException("字典类型必须为大写");
        }
        if (dto.getId() != null) {// 更新

        } else {// 新增

        }

        return "操作成功";
    }

    @Override
    public List<Dictionary> list(String dictType) {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Dictionary::getDictType, dictType);

        return dictionaryMapper.selectList(queryWrapper);
    }

    /**
     * 判断字符串是否全为大写
     *
     * @param str 字符串
     * @return 结果
     */
    private boolean isContainUpperCase(String str) {
        StringBuilder buf = new StringBuilder(str);
        for (int i = 0; i < buf.length(); i++) {
            if (Character.isLowerCase(buf.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
