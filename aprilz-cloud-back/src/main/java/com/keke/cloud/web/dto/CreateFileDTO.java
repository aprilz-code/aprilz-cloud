package com.keke.cloud.web.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "创建文件DTO")
public class CreateFileDTO {
    @ApiModelProperty(value="文件名")
    private String fileName;
    @ApiModelProperty(value="文件路径")
    private String filePath;
}
