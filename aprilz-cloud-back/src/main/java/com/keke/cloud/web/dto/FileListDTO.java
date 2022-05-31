package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "文件列表DTO")
public class FileListDTO {
    @ApiModelProperty(value = "文件路径")
    private String filePath;
    @ApiModelProperty(value = "当前页码")
    private Long currentPage;
    @ApiModelProperty(value = "一页显示数量")
    private Long pageCount;
}
