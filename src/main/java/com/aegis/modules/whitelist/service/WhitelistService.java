package com.aegis.modules.whitelist.service;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.modules.whitelist.domain.dto.WhitelistDTO;
import com.aegis.modules.whitelist.domain.entity.Whitelist;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/8 10:35
 * @Description: 白名单业务层
 */
public interface WhitelistService {

    /**
     * 分页列表
     *
     * @param dto 查询参数
     * @return 白名单分页列表
     */
    PageVO<Whitelist> pageList(WhitelistDTO dto);

    /**
     * 更新白名单状态
     *
     * @param id 白名单ID
     * @return 响应消息
     */
    String updateStatus(Long id);

    /**
     * 删除白名单
     *
     * @param id 白名单ID
     * @return 响应消息
     */
    String delete(Long id);

    /**
     * 新增或修改白名单
     *
     * @param dto 白名单DTO
     * @return 响应消息
     */
    String addOrUpdate(WhitelistDTO dto);
}
