package com.aegis.modules.phone.domain.dto;

import com.aegis.common.domain.dto.PageDTO;
import com.aegis.common.validator.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: xuesong.lei
 * @Date: 2026/3/14
 * @Description: 手机号码DTO
 */
@Data
@Schema(description = "手机号码DTO")
@EqualsAndHashCode(callSuper = true)
public class PhoneNumberDTO extends PageDTO {

    @Schema(description = "主键ID")
    @Null(groups = ValidGroup.Create.class, message = "ID必须为空")
    @NotNull(groups = ValidGroup.Update.class, message = "ID不能为空")
    private Long id;

    @Schema(description = "手机号")
    @NotBlank(groups = {ValidGroup.Create.class, ValidGroup.Update.class}, message = "手机号不能为空")
    private String phone;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "所属项目")
    private String projectName;

    @Schema(description = "等级")
    private String level;

    /**
     * 所属用户ID（管理员查询某个子用户手机数量时使用）
     */
    @Schema(description = "所属用户ID")
    private Long ownerUserId;
}
