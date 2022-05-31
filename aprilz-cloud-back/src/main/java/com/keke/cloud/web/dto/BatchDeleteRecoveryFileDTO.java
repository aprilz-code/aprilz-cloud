package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "批量删除回收文件DTO")
public class BatchDeleteRecoveryFileDTO {
    @ApiModelProperty(value = "回收用户文件id")
    private String  recoveryFileIds;
}
