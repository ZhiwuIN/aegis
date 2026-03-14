package com.aegis.modules.phone.controller;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.duplicate.PreventDuplicateSubmit;
import com.aegis.common.log.BusinessType;
import com.aegis.common.log.OperationLog;
import com.aegis.common.validator.ValidGroup;
import com.aegis.modules.phone.domain.dto.PhoneNumberDTO;
import com.aegis.modules.phone.domain.vo.PhoneNumberVO;
import com.aegis.modules.phone.service.PhoneNumberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码接口
 */
@RestController
@Tag(name = "手机号码接口")
@RequiredArgsConstructor
@RequestMapping("/phone")
public class PhoneNumberController {

    private final PhoneNumberService phoneNumberService;

    @Operation(summary = "分页列表")
    @GetMapping("/pageList")
    public PageVO<PhoneNumberVO> pageList(PhoneNumberDTO dto) {
        return phoneNumberService.pageList(dto);
    }

    @Operation(summary = "详情")
    @GetMapping("/detail/{id}")
    public PhoneNumberVO detail(@PathVariable("id") Long id) {
        return phoneNumberService.detail(id);
    }

    @Operation(summary = "新增手机号")
    @PostMapping("/add")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "新增手机号", businessType = BusinessType.INSERT)
    public String add(@Validated(ValidGroup.Create.class) @RequestBody PhoneNumberDTO dto) {
        return phoneNumberService.add(dto);
    }

    @Operation(summary = "修改手机号")
    @PutMapping("/update")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "修改手机号", businessType = BusinessType.UPDATE)
    public String update(@Validated(ValidGroup.Update.class) @RequestBody PhoneNumberDTO dto) {
        return phoneNumberService.update(dto);
    }

    @Operation(summary = "删除手机号")
    @DeleteMapping("/delete/{id}")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "删除手机号", businessType = BusinessType.DELETE)
    public String delete(@PathVariable("id") Long id) {
        return phoneNumberService.delete(id);
    }
}
