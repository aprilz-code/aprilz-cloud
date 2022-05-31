package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "删除回收文件DTO")
public class DeleteRecoveryFileDTO {
    @ApiModelProperty(value = "回收文件id")
    private Long recoveryFileId;

}
