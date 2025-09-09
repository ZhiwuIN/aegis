package com.aegis.modules.dept.service.impl;

import com.aegis.modules.dept.domain.dto.DeptDTO;
import com.aegis.modules.dept.domain.entity.Dept;
import com.aegis.modules.dept.mapper.DeptMapper;
import com.aegis.modules.dept.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/9 14:28
 * @Description: 部门业务实现层
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<Dept> list(DeptDTO dto) {
        return Collections.emptyList();
    }

    @Override
    public Dept detail(Long id) {
        return null;
    }

    @Override
    public List<Dept> exclude(Long id) {
        return Collections.emptyList();
    }

    @Override
    public String delete(Long id) {
        return "";
    }

    @Override
    public String addOrUpdate(DeptDTO dto) {
        return "";
    }
}
