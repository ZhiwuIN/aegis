package com.aegis.modules.phone.domain.dto;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PhoneNumberImportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("等级")
    private String level;

    @ExcelProperty("备注")
    private String remark;
}
