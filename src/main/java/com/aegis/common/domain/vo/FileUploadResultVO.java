package com.aegis.common.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: xuesong.lei
 * @Date: 2025/08/27 21:24
 * @Description: 文件上传结果VO
 */
@Data
@Builder
@ApiModel("文件上传结果VO")
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResultVO {

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * 原始文件名称
     */
    @ApiModelProperty("原始文件名称")
    private String originalFileName;

    /**
     * 文件后缀
     */
    @ApiModelProperty("文件后缀")
    private String suffix;

    /**
     * 文件存储路径
     */
    @ApiModelProperty("文件存储路径")
    private String filePath;

    /**
     * 文件访问URL
     */
    @ApiModelProperty("文件访问URL")
    private String fileUrl;

    /**
     * 文件大小，单位字节
     */
    @ApiModelProperty("文件大小，单位字节")
    private Long fileSize;

    /**
     * 文件类型
     */
    @ApiModelProperty("文件类型")
    private String contentType;

    /**
     * 存储平台
     */
    @ApiModelProperty("存储平台")
    private String platform;

    /**
     * 上传时间
     */
    @ApiModelProperty("上传时间")
    private LocalDateTime uploadTime;

    /**
     * 文件MD5值
     */
    @ApiModelProperty("文件MD5值")
    private String md5;
}
