package com.aegis.modules.dict.domain;

import com.aegis.common.domain.dto.PageDTO;
import com.aegis.common.validator.ValidGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @Author: xuesong.lei
 * @Date: 2025/9/8 16:17
 * @Description: 字典DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryDTO extends PageDTO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("字典名称")
    @NotBlank(message = "字典名称不能为空", groups = {ValidGroup.Create.class, ValidGroup.Update.class})
    private String dictName;

    @ApiModelProperty("字典类型")
    @NotBlank(message = "字典类型不能为空", groups = {ValidGroup.Create.class, ValidGroup.Update.class})
    private String dictType;

    @ApiModelProperty("字典排序")
    private Integer dictSort;

    @ApiModelProperty("字典标签")
    @NotBlank(message = "字典类型不能为空", groups = {ValidGroup.Create.class, ValidGroup.Update.class})
    private String dictLabel;

    @ApiModelProperty("字典键值")
    @NotBlank(message = "字典键值不能为空", groups = {ValidGroup.Create.class, ValidGroup.Update.class})
    private String dictValue;

    @ApiModelProperty("状态")
    private String status;
}
