package com.aegis.modules.project.domain.dto;

import com.aegis.common.domain.dto.PageDTO;
import com.aegis.common.validator.ValidGroup;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Schema(description = "项目信息DTO")
@EqualsAndHashCode(callSuper = true)
public class ProjectDTO extends PageDTO {

    @Schema(description = "主键ID")
    @Null(groups = ValidGroup.Create.class, message = "ID必须为空")
    @NotNull(groups = ValidGroup.Update.class, message = "ID不能为空")
    private Long id;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "项目名称")
    @NotBlank(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "项目名称不能为空")
    private String projectName;

    @Schema(description = "项目管理员")
    private String owner;

}
