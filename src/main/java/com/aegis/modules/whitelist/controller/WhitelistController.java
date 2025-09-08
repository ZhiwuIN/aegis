package com.aegis.modules.whitelist.controller;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.duplicate.PreventDuplicateSubmit;
import com.aegis.common.log.BusinessType;
import com.aegis.common.log.OperationLog;
import com.aegis.common.validator.ValidGroup;
import com.aegis.modules.whitelist.domain.dto.WhitelistDTO;
import com.aegis.modules.whitelist.domain.entity.Whitelist;
import com.aegis.modules.whitelist.service.WhitelistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/8 10:34
 * @Description: 白名单接口
 */
@RestController
@Api(tags = "白名单接口")
@RequiredArgsConstructor
@RequestMapping("/whitelist")
public class WhitelistController {

    private final WhitelistService whitelistService;

    @ApiOperation("分页列表")
    @PostMapping("/pageList")
    public PageVO<Whitelist> pageList(@RequestBody WhitelistDTO dto) {
        return whitelistService.pageList(dto);
    }

    @ApiOperation("详情")
    @GetMapping("/detail")
    public Whitelist detail(@RequestParam("id") Long id) {
        return whitelistService.detail(id);
    }

    @ApiOperation("更新白名单状态")
    @GetMapping("/updateStatus")
    @OperationLog(moduleTitle = "更新白名单状态", businessType = BusinessType.UPDATE)
    public String updateStatus(@RequestParam("id") Long id) {
        return whitelistService.updateStatus(id);
    }

    @ApiOperation("删除白名单")
    @GetMapping("/delete")
    @OperationLog(moduleTitle = "删除白名单", businessType = BusinessType.DELETE)
    public String delete(@RequestParam("id") Long id) {
        return whitelistService.delete(id);
    }

    @ApiOperation("新增或修改白名单")
    @PostMapping("/addOrUpdate")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "新增或修改白名单", businessType = BusinessType.INSERT)
    public String addOrUpdate(@Validated({ValidGroup.Create.class, ValidGroup.Update.class}) @RequestBody WhitelistDTO dto) {
        return whitelistService.addOrUpdate(dto);
    }
}
