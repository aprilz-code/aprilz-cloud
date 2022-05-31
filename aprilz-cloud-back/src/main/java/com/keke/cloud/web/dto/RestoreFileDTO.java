package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther flk
 * @create 2021/2/24
 */
@Data
@ApiModel(value = "回收文件DTO")
public class RestoreFileDTO {
    @ApiModelProperty(value="删除批次号")
    private String deleteBatchNum;
    @ApiModelProperty(value="文件路径")
    private String filePath;
}
