package com.keke.cloud.web.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "批量删除文件DTO")
public class BatchDeleteFileDTO {
    @ApiModelProperty(value="文件集合")
    private String files;



}
