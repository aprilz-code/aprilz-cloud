package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "回收文件列表DTO")
public class RecoveryFileListDTO {
    @ApiModelProperty(value = "当前页码")
    private Long currentPage;
    @ApiModelProperty(value = "一页显示数量")
    private Long pageCount;
}
