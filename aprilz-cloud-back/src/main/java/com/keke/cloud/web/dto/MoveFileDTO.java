package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "移动文件DTO")
public class MoveFileDTO {

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "旧文件名")
    private String oldFilePath;
    @ApiModelProperty(value = "扩展名")
    private String extendName;

}
