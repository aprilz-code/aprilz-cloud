package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "批量移动文件DTO")
public class BatchMoveFileDTO {
    @ApiModelProperty(value="文件集合")
    private String files;
    @ApiModelProperty(value="文件路径")
    private String filePath;


}
