package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "删除文件DTO")
public class DeleteFileDTO {
    @ApiModelProperty(value = "用户文件id")
    private Long userFileId;
    @ApiModelProperty(value = "文件路径")
    private String filePath;
    @ApiModelProperty(value = "文件名")
    private String fileName;
    @ApiModelProperty(value = "是否是目录")
    private Integer isDir;
}
