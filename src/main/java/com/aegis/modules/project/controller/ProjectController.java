package com.aegis.modules.project.controller;

import com.aegis.common.domain.vo.PageVO;
import com.aegis.common.duplicate.PreventDuplicateSubmit;
import com.aegis.common.log.BusinessType;
import com.aegis.common.log.OperationLog;
import com.aegis.common.validator.ValidGroup;
import com.aegis.modules.project.domain.dto.ProjectDTO;
import com.aegis.modules.project.domain.vo.ProjectVO;
import com.aegis.modules.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 项目信息接口
 */
@RestController
@Tag(name = "项目信息接口")
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "分页列表")
    @GetMapping("/pageList")
    public PageVO<ProjectVO> pageList(ProjectDTO dto) {
        return projectService.pageList(dto);
    }

    @Operation(summary = "详情")
    @GetMapping("/detail/{id}")
    public ProjectVO detail(@PathVariable("id") Long id) {
        return projectService.detail(id);
    }

    @Operation(summary = "新增项目信息")
    @PostMapping("/add")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "新增项目信息", businessType = BusinessType.INSERT)
    public String add(@Validated(ValidGroup.Create.class) @RequestBody ProjectDTO dto) {
        return projectService.add(dto);
    }

    @Operation(summary = "修改项目信息")
    @PutMapping("/update")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "修改项目信息", businessType = BusinessType.UPDATE)
    public String update(@Validated(ValidGroup.Update.class) @RequestBody ProjectDTO dto) {
        return projectService.update(dto);
    }

    @Operation(summary = "删除项目信息")
    @DeleteMapping("/delete/{id}")
    @PreventDuplicateSubmit
    @OperationLog(moduleTitle = "删除项目信息", businessType = BusinessType.DELETE)
    public String delete(@PathVariable("id") Long id) {
        return projectService.delete(id);
    }
}
