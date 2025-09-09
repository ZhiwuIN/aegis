package com.aegis.modules.dict.controller;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.duplicate.PreventDuplicateSubmit;
import com.aegis.common.log.BusinessType;
import com.aegis.common.log.OperationLog;
import com.aegis.common.validator.ValidGroup;
import com.aegis.modules.dict.domain.DictionaryDTO;
import com.aegis.modules.dict.domain.entity.Dictionary;
import com.aegis.modules.dict.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/8 16:08
 * @Description: 字典接口
 */
@RestController
@Api(tags = "字典接口")
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @ApiOperation("分页列表")
    @PostMapping("/pageList")
    public PageVO<Dictionary> pageList(@RequestBody DictionaryDTO dto) {
        return dictionaryService.pageList(dto);
    }

    @ApiOperation("详情")
    @PostMapping("/detail/{id}")
    public Dictionary detail(@PathVariable("id") Long id) {
        return dictionaryService.detail(id);
    }

    @ApiOperation("更新字典状态")
    @PostMapping("/updateStatus/{id}")
    @OperationLog(moduleTitle = "更新字典状态", businessType = BusinessType.UPDATE)
    public String updateStatus(@PathVariable("id") Long id) {
        return dictionaryService.updateStatus(id);
    }

    @ApiOperation("删除字典")
    @PostMapping("/delete/{id}")
    @OperationLog(moduleTitle = "删除字典", businessType = BusinessType.DELETE)
    public String delete(@PathVariable("id") Long id) {
        return dictionaryService.delete(id);
    }

    @ApiOperation("新增或修改字典")
    @PostMapping("/addOrUpdate")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "新增或修改字典", businessType = BusinessType.INSERT)
    public String addOrUpdate(@Validated({ValidGroup.Update.class, ValidGroup.Create.class,}) @RequestBody DictionaryDTO dto) {
        return dictionaryService.addOrUpdate(dto);
    }

    @ApiOperation("根据类型获取字典列表")
    @GetMapping("/list")
    public List<Dictionary> list(@RequestParam("dictType") String dictType) {
        return dictionaryService.list(dictType);
    }
}
