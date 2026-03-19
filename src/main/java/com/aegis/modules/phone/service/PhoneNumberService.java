package com.aegis.modules.phone.service;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.modules.phone.domain.dto.PhoneNumberDTO;
import com.aegis.modules.phone.domain.dto.PhoneNumberImportDTO;
import com.aegis.modules.phone.domain.vo.PhoneNumberVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码业务层
 */
public interface PhoneNumberService {

    /**
     * 分页列表
     *
     * 管理员：查看所有手机号
     * 子用户：只查看自己创建的手机号
     */
    PageVO<PhoneNumberVO> pageList(PhoneNumberDTO dto);

    /**
     * 详情
     */
    PhoneNumberVO detail(Long id);

    /**
     * 新增手机号
     */
    String add(PhoneNumberDTO dto);

    /**
     * 修改手机号
     */
    String update(PhoneNumberDTO dto);

    /**
     * 删除手机号
     */
    String delete(Long id);

    /**
     * 批量导入手机号（从 Excel 文件）
     */
    String importPhoneNumbers(List<PhoneNumberImportDTO> dto);
}
